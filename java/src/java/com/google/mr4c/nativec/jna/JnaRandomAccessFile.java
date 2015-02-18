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

import com.google.mr4c.nativec.ExternalRandomAccessFileSource;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.MR4CRandomAccessFileClosePtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.MR4CRandomAccessFileGetLocationPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.MR4CRandomAccessFileGetSizePtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.MR4CRandomAccessFileReadPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.MR4CRandomAccessFileSetLocationPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.MR4CRandomAccessFileSetSizePtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.MR4CRandomAccessFileWritePtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalRandomAccessFileSourcePtr;
import com.google.mr4c.nativec.jna.lib.CExternalRandomAccessFileCallbacksStruct;
import com.google.mr4c.sources.DataFileSource;
import com.google.mr4c.sources.RandomAccessFileSource;
import com.google.mr4c.sources.RandomAccessible;
import com.google.mr4c.util.MR4CLogging;

import com.sun.jna.Pointer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;

/*package*/ class JnaRandomAccessFile {

	protected static final Logger s_log = MR4CLogging.getLogger(JnaRandomAccessFile.class);

	static Mr4cLibrary s_lib = Mr4cLibrary.INSTANCE;

	private RandomAccessible m_rand;
	private RandomAccessFile m_randFile;
	private FileChannel m_chan;
	private CExternalRandomAccessFileCallbacksStruct m_callbacks;


	/*package*/ JnaRandomAccessFile(RandomAccessible rand) {
		m_rand = rand;
		m_callbacks = new CExternalRandomAccessFileCallbacksStruct();
		buildReadCallback();
		buildWriteCallback();
		buildGetLocationCallback();
		buildSetLocationCallback();
		buildSetLocationFromEndCallback();
		buildSkipForwardCallback();
		buildSkipBackwardCallback();
		buildGetSizeCallback();
		buildSetSizeCallback();
		buildCloseCallback();
	}

	/*package*/ RandomAccessible getRandomAccessible() {
		return m_rand;
	}

	/*package*/ CExternalRandomAccessFileCallbacksStruct getCallbacks() {
		return m_callbacks;
	}

	private void buildReadCallback() {
		m_callbacks.readCallback = new MR4CRandomAccessFileReadPtr() {
			public byte apply(Pointer bufPtr, NativeSize num, NativeSizeByReference read) {
				return doRead(bufPtr, num, read);
			}
		};
	}

	private void buildWriteCallback() {
		m_callbacks.writeCallback = new MR4CRandomAccessFileWritePtr() {
			public byte apply(Pointer bufPtr, NativeSize num) {
				return doWrite(bufPtr, num);
			}
		};
	}

	private void buildGetLocationCallback() {
		m_callbacks.getLocationCallback = new MR4CRandomAccessFileGetLocationPtr() {
			public byte apply(NativeSizeByReference loc) {
				return doGetLocation(loc);
			}
		};
	}

	private void buildSetLocationCallback() {
		m_callbacks.setLocationCallback = new MR4CRandomAccessFileSetLocationPtr() {
			public byte apply(NativeSize loc) {
				return doSetLocation(loc);
			}
		};
	}

	private void buildSetLocationFromEndCallback() {
		m_callbacks.setLocationFromEndCallback = new MR4CRandomAccessFileSetLocationPtr() {
			public byte apply(NativeSize loc) {
				return doSetLocationFromEnd(loc);
			}
		};
	}

	private void buildSkipForwardCallback() {
		m_callbacks.skipForwardCallback = new MR4CRandomAccessFileSetLocationPtr() {
			public byte apply(NativeSize loc) {
				return doSkipForward(loc);
			}
		};
	}

	private void buildSkipBackwardCallback() {
		m_callbacks.skipBackwardCallback = new MR4CRandomAccessFileSetLocationPtr() {
			public byte apply(NativeSize loc) {
				return doSkipBackward(loc);
			}
		};
	}

	private void buildGetSizeCallback() {
		m_callbacks.getSizeCallback = new MR4CRandomAccessFileGetSizePtr() {
			public byte apply(NativeSizeByReference size) {
				return doGetSize(size);
			}
		};
	}

	private void buildSetSizeCallback() {
		m_callbacks.setSizeCallback = new MR4CRandomAccessFileSetSizePtr() {
			public byte apply(NativeSize size) {
				return doSetSize(size);
			}
		};
	}

	private void buildCloseCallback() {
		m_callbacks.closeCallback = new MR4CRandomAccessFileClosePtr() {
			public void apply() {
				doClose();
			}
		};
	}

	private synchronized void initRandomAccessIfNecessary() throws IOException {
		if ( m_randFile==null ) {
			m_randFile = m_rand.getRandomAccess();
		}
		if ( m_chan==null ) {
			m_chan = m_randFile.getChannel();
		}
	}
		
	private synchronized byte doRead(Pointer bufPtr, NativeSize num, NativeSizeByReference read) {
		try {
			initRandomAccessIfNecessary();
			int size = num.intValue();
			ByteBuffer buf = bufPtr.getByteBuffer(0, size);
			int numRead = m_chan.read(buf);
			read.setValue(new NativeSize(numRead));
			return 1;
		} catch ( Exception e ) {
			handleError("reading", e);
			return 0;
		}
	}

	private synchronized byte doWrite(Pointer bufPtr, NativeSize num) {
		try {
			assertWritable();
			initRandomAccessIfNecessary();
			int size = num.intValue();
			ByteBuffer buf = bufPtr.getByteBuffer(0, size);
			long numWritten = m_chan.write(buf);
			if ( numWritten!=size ) {
				throw new IOException("Not all bytes written");
			}
			return 1;
		} catch ( Exception e ) {
			handleError("writing", e);
			return 0;
		}
	}

	private synchronized byte doGetLocation(NativeSizeByReference loc) {
		try {
			initRandomAccessIfNecessary();
			long location = m_chan.position();
			loc.setValue(new NativeSize(location));
			return 1;
		} catch ( Exception e ) {
			handleError("getting location", e);
			return 0;
		}
	}

	private synchronized byte doSetLocation(NativeSize loc) {
		try {
			initRandomAccessIfNecessary();
			long location = loc.longValue();
			m_chan.position(location);
			return 1;
		} catch ( Exception e ) {
			handleError("setting location", e);
			return 0;
		}
	}

	private synchronized byte doSetLocationFromEnd(NativeSize loc) {
		try {
			initRandomAccessIfNecessary();
			long location = loc.longValue();
			m_chan.position(m_chan.size()-location);
			return 1;
		} catch ( Exception e ) {
			handleError("setting location from end", e);
			return 0;
		}
	}

	private synchronized byte doSkipForward(NativeSize loc) {
		try {
			initRandomAccessIfNecessary();
			long location = loc.longValue();
			m_chan.position(m_chan.position()+location);
			return 1;
		} catch ( Exception e ) {
			handleError("skipping forward", e);
			return 0;
		}
	}

	private synchronized byte doSkipBackward(NativeSize loc) {
		try {
			initRandomAccessIfNecessary();
			long location = loc.longValue();
			m_chan.position(m_chan.position()-location);
			return 1;
		} catch ( Exception e ) {
			handleError("skipping backward", e);
			return 0;
		}
	}

	private synchronized byte doGetSize(NativeSizeByReference size) {
		try {
			initRandomAccessIfNecessary();
			size.setValue(new NativeSize(m_chan.size()));
			return 1;
		} catch ( Exception e ) {
			handleError("getting size", e);
			return 0;
		}
	}

	private synchronized byte doSetSize(NativeSize size) {
		try {
			assertWritable();
			initRandomAccessIfNecessary();
			m_randFile.setLength(size.longValue());
			return 1;
		} catch ( Exception e ) {
			handleError("setting size", e);
			return 0;
		}
	}


	private synchronized void doClose() {
		try {
			if ( m_rand!=null ) {
				s_log.info("Closing {} byte random access file [{}]" , m_randFile.length(), m_rand.getDescription());
				m_rand.close();
			}
			m_randFile=null;
			m_chan=null;
		} catch ( Exception e ) {
			handleError("closing file", e);
		}
	}

	private void assertWritable() {
		if ( !m_rand.isWritable() ) {
			throw new IllegalStateException(String.format("Tried to write read-only file [%s]", m_rand.getDescription()));
		}
	}

	private void handleError(String operation, Exception e) {
		String msg = String.format("Error %s for %s", operation, m_rand.getDescription());
		s_log.error(msg, e);
	}

}

