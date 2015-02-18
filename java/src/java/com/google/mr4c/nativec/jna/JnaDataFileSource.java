/**
  * Copyright 2014 Google Inc. All rights reserved.
  * 
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  * 
  *     http://www.apache.org/licenses/LICENSE-2.0
  * 
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
*/

package com.google.mr4c.nativec.jna;

import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.ochafik.lang.jnaerator.runtime.NativeSizeByReference;

import com.google.mr4c.nativec.ExternalDataFileSource;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.MR4CDataSourceReadPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.MR4CDataSourceSkipPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.MR4CGetDataSourceBytesPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.MR4CGetDataSourceSizePtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.MR4CReleaseDataSourcePtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalDataFileSourcePtr;
import com.google.mr4c.nativec.jna.lib.CExternalDataSourceCallbacksStruct;
import com.google.mr4c.sources.AbstractDataFileSource;
import com.google.mr4c.sources.BytesDataFileSource;
import com.google.mr4c.sources.DataFileSource;
import com.google.mr4c.util.MR4CLogging;
import com.google.mr4c.util.ByteBufferInputStream;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

import java.io.InputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;

import org.apache.hadoop.fs.BlockLocation;

import org.slf4j.Logger;

public class JnaDataFileSource implements ExternalDataFileSource {

	protected static final Logger s_log = MR4CLogging.getLogger(JnaDataFileSource.class);

	static Mr4cLibrary s_lib = Mr4cLibrary.INSTANCE;

	private DataFileSource m_src;
	private CExternalDataSourceCallbacksStruct m_callbacks;
	private DisposableMemory m_memory;
	private ByteBuffer m_buf;
	private long m_size = -1;
	private PushbackInputStream m_stream;
	private ReadableByteChannel m_chan;
	private boolean m_eof;
	private CExternalDataFileSourcePtr m_nativeSrc;


	/*package*/ JnaDataFileSource(DataFileSource src) {

		m_src = src;
		m_callbacks = new CExternalDataSourceCallbacksStruct();
		buildBytesCallback();
		buildSizeCallback();
		buildReadCallback();
		buildSkipCallback();
		buildReleaseCallback();
		m_nativeSrc = s_lib.CExternalDataFileSource_newDataFileSource(m_callbacks);
		JnaUtils.protectSource(m_src, this);
	}

	/*package*/ JnaDataFileSource(CExternalDataFileSourcePtr nativeSrc) {
		m_nativeSrc = nativeSrc;
		m_src = new SimpleSource();
	}

	public DataFileSource getSource() {
		return m_src;
	}

	private void buildBytesCallback() {
		m_callbacks.getBytesCallback = new MR4CGetDataSourceBytesPtr() {
			public Pointer apply() {
				return doGetBytes();
			}
		};
	}

	private void buildSizeCallback() {
		m_callbacks.getSizeCallback = new MR4CGetDataSourceSizePtr() {
			public byte apply(NativeSizeByReference size) {
				return doGetSize(size);
			}
		};
	}

	private void buildReadCallback() {
		m_callbacks.readCallback = new MR4CDataSourceReadPtr() {
			public byte apply(Pointer bufPtr, NativeSize num, NativeSizeByReference read) {
				return doRead(bufPtr, num, read);
			}
		};
	}

	private void buildSkipCallback() {
		m_callbacks.skipCallback = new MR4CDataSourceSkipPtr() {
			public byte apply(NativeSize num, NativeSizeByReference skipped) {
				return doSkip(num, skipped);
			}
		};
	}

	private void buildReleaseCallback() {
		m_callbacks.releaseCallback = new MR4CReleaseDataSourcePtr() {
			public void apply() {
				doRelease();
			}
		};
	}

	private void initSizeIfNecessary() throws IOException {
		if ( m_size!=-1 ) {
			return;
		}
		m_size = m_src.getFileSize();
		if ( m_size==-1 ) {
			initBuffer();
		}
	}
		
	private void initBufferIfNecessary() throws IOException {
		if ( m_buf!=null ) {
			return;
		}
		initBuffer();
	}

	private void initBuffer() throws IOException {
		m_size = m_src.getFileSize();
		if ( m_size==-1 ) {
			initBufferFromBytes();
		} else {
			initBufferFromChannel();
		}
	}

	private void initBufferFromBytes() throws IOException {
		byte[] bytes = m_src.getFileBytes();
		m_memory = new DisposableMemory(bytes.length);
		m_buf = m_memory.getByteBuffer(0,bytes.length);
		m_buf.put(bytes);
		m_size = bytes.length;
	}

	private void initBufferFromChannel() throws IOException {
		m_memory = new DisposableMemory(m_size);
		InputStream stream = null;
		ReadableByteChannel chan = null;
		try {
			stream = m_src.getFileInputStream();
			chan = Channels.newChannel(stream);
			m_buf = m_memory.getByteBuffer(0,m_size);
			int read = chan.read(m_buf);
			if ( read!=m_size ) {
				throw new IllegalStateException(String.format("Expected %s bytes, read %s bytes", m_size, read));
			}
		} finally {
			if ( chan!=null ) {
				chan.close();
			} else if ( stream!=null ) {
				stream.close();
			}
		}
	}
		
	private void initStreamIfNecessary() throws IOException {
		if ( m_stream!=null ) {
			return;
		}
		m_stream = new PushbackInputStream(m_src.getFileInputStream());
		m_chan = Channels.newChannel(m_stream);
		m_eof = false;
	}
		
	private synchronized Pointer doGetBytes() {
		try {
			initBufferIfNecessary();
			return m_memory;
		} catch ( Exception e ) {
			s_log.error("Error accessing " +  m_src.getDescription(), e);
			return null;
		}
	}
		
	private synchronized byte doGetSize(NativeSizeByReference size) {
		try {
			initSizeIfNecessary();
			size.setValue(new NativeSize(m_size));
			return 1;
		} catch ( Exception e ) {
			s_log.error("Error accessing " +  m_src.getDescription(), e);
			return 0;
		}
	}

	private synchronized byte doRead(Pointer bufPtr, NativeSize num, NativeSizeByReference read) {
		try {
			initStreamIfNecessary();
			if ( m_eof ) {
				read.setValue(new NativeSize(0));
				return 1;
			}
			int size = num.intValue();
			ByteBuffer buf = bufPtr.getByteBuffer(0, size);
			int numRead = m_chan.read(buf);
			if ( numRead<=0 ) {
				// reached EOF
				m_eof=true;
				numRead=0;
			}
			read.setValue(new NativeSize(numRead));
			return 1;
		} catch ( Exception e ) {
			s_log.error("Error accessing " +  m_src.getDescription(), e);
			return 0;
		}
	}

	private synchronized byte doSkip(NativeSize num, NativeSizeByReference skipped) {
		try {
			initStreamIfNecessary();
			if ( m_eof ) {
				skipped.setValue(new NativeSize(0));
				return 1;
			}
			long size = num.longValue();
			long numSkipped = m_stream.skip(size);
			// InputStream just keeps skipping at EOF
			// Need to check if there are any bytes, or it could skip forever
			int next = m_stream.read();
			if ( next==-1 ) {
				m_eof = true;
			} else {
				m_stream.unread(next);
			}
			skipped.setValue(new NativeSize(numSkipped));
			return 1;
		} catch ( Exception e ) {
			s_log.error("Error accessing " +  m_src.getDescription(), e);
			return 0;
		}
	}

	private synchronized void doRelease() {
		releaseMemory();
		releaseStream();
		m_src.release();
	}

	private void releaseMemory() {
		if ( m_memory!=null ) {
			s_log.info("Freeing {} byte file [{}]" , m_size, m_src.getDescription());
			m_memory.publicDispose();
		}
		m_buf = null;
		m_memory = null;
	}
		
	private void releaseStream() {
		if ( m_chan==null ) {
			return;
		}
		s_log.info("Closing stream for file [{}]" , m_src.getDescription());
		try { 
			m_chan.close();
			m_chan=null;
			m_stream=null;
			m_eof=false;
		} catch (IOException ioe) {
			s_log.warn("Error closing stream for " +  m_src.getDescription(), ioe);
		}
	}
		
	/*package*/ CExternalDataFileSourcePtr getNativeSource() {
		return m_nativeSrc;
	}

	/*package*/ static CExternalDataFileSourcePtr toNative(ExternalDataFileSource src) {
		JnaDataFileSource jnaSrc = (JnaDataFileSource) src;
		return jnaSrc==null ? null : jnaSrc.getNativeSource();
	}
	
	/*package*/ static JnaDataFileSource fromNative(CExternalDataFileSourcePtr nativeSrc) {
		return nativeSrc==null ? null : new JnaDataFileSource(nativeSrc);
	}

	// NOTE: JNA normally won't free the native memory until the Memory finalizer runs.
	// Its pretty easy for the GC to go a very long time without that happening.
	// This class exposes the protected dispose method. 

	private static class DisposableMemory extends Memory {
	
		DisposableMemory(long length) {
			super(length);
		}

		public void publicDispose() {
			dispose();
		}
	}

	// This class allows delaying asking for bytes from the native source
	private class SimpleSource extends AbstractDataFileSource {

		private DataFileSource m_delegate;

		public InputStream getFileInputStream() throws IOException {
			long size = getFileSize();
			Pointer ptr = getPointer();
			ByteBuffer buf = ptr.getByteBuffer(0, size);
			return new ByteBufferInputStream(buf);
		}

		public synchronized long getFileSize() throws IOException {
			return m_delegate==null ? 
				getSizeHelper() :
				m_delegate.getFileSize();
		}

		public synchronized byte[] getFileBytes() throws IOException {
			ensureSource();
			return m_delegate.getFileBytes();
		}

		public synchronized void getFileBytes(ByteBuffer buf) throws IOException {
			ensureSource();
			m_delegate.getFileBytes(buf);
		}
	
		public synchronized void release() {
			if ( m_delegate!=null ) {
				m_delegate.release();
				m_delegate=null;
			}
		}

		public String getDescription() {
			return "Simple JNA source wrapper";
		}

		private void ensureSource() {
			if ( m_delegate!=null ) {
				return;
			}
			long size = getSizeHelper();
			Pointer ptr = getPointer();
			byte[] bytes = ptr.getByteArray(0, (int)size);
			m_delegate = new BytesDataFileSource(bytes);
		}

		private long getSizeHelper() {
			return s_lib.CExternalDataFileSource_getSize(m_nativeSrc).intValue();
		}

		private Pointer getPointer() {
			return s_lib.CExternalDataFileSource_getBytes(m_nativeSrc);
		}

	}

}

