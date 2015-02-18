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

import com.google.mr4c.nativec.ExternalDataFileSink;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.MR4CCloseDataSinkPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.MR4CDataSinkWritePtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalDataFileSinkPtr;
import com.google.mr4c.nativec.jna.lib.CExternalDataSinkCallbacksStruct;
import com.google.mr4c.sources.DataFileSink;
import com.google.mr4c.util.MR4CLogging;

import com.sun.jna.Pointer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import org.slf4j.Logger;

public class JnaDataFileSink implements ExternalDataFileSink {

	protected static final Logger s_log = MR4CLogging.getLogger(JnaDataFileSink.class);

	static Mr4cLibrary s_lib = Mr4cLibrary.INSTANCE;

	private DataFileSink m_sink;
	private CExternalDataSinkCallbacksStruct m_callbacks;
	private WritableByteChannel m_chan;
	private CExternalDataFileSinkPtr m_nativeSink;


	/*package*/ JnaDataFileSink(DataFileSink sink) {
		m_sink = sink;
		m_callbacks = new CExternalDataSinkCallbacksStruct();
		buildWriteCallback();
		buildCloseCallback();
		m_nativeSink = s_lib.CExternalDataFileSink_newDataFileSink(m_callbacks);
		JnaUtils.protectSink(m_sink, this); 
	}

	/*package*/ JnaDataFileSink(CExternalDataFileSinkPtr nativeSink) {
		m_nativeSink = nativeSink;
	}


	public DataFileSink getSink() {
		return m_sink;
	}

	private void buildWriteCallback() {
		m_callbacks.writeCallback = new MR4CDataSinkWritePtr() {
			public byte apply(Pointer buf, NativeSize num) {
				return doWrite(buf, num);
			}
		};
	}

	private void buildCloseCallback() {
		m_callbacks.closeCallback = new MR4CCloseDataSinkPtr() {
			public void apply() {
				doClose();
			}
		};
	}

	private void initStreamIfNecessary() throws IOException {
		if ( m_chan!=null ) {
			return;
		}
		m_chan = Channels.newChannel(m_sink.getFileOutputStream());
	}
		
	private synchronized byte doWrite(Pointer bufPtr, NativeSize num) {
		try {
			initStreamIfNecessary();
			int size = num.intValue();
			ByteBuffer buf = bufPtr.getByteBuffer(0, size);
			int written = m_chan.write(buf);
			if ( written!=size ) {
				throw new IOException(String.format("Expected %d bytes to be written, only wrote %d bytes", size, written));
			} 
			return (byte)1;
		} catch ( Exception e ) {
			s_log.error("Error writing to " +  m_sink.getDescription(), e);
			return (byte)0;
		}
	}

	private synchronized void doClose() {
		if ( m_chan==null ) {
			return;
		}
		s_log.info("Closing stream for file [{}]" , m_sink.getDescription());
		try {
			m_chan.close();
		} catch (IOException ioe) {
			s_log.warn("Error closing stream for " +  m_sink.getDescription(), ioe);
		}
	}
		
	/*package*/ CExternalDataFileSinkPtr getNativeSink() {
		return m_nativeSink;
	}

	/*package*/ static CExternalDataFileSinkPtr toNative(ExternalDataFileSink sink) {
		JnaDataFileSink jnaSink = (JnaDataFileSink) sink;
		return jnaSink==null ? null : jnaSink.getNativeSink();
	}
	
	/*package*/ static JnaDataFileSink fromNative(CExternalDataFileSinkPtr nativeSink) {
		return nativeSink==null ? null : new JnaDataFileSink(nativeSink);
	}

}
