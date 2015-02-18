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

package com.google.mr4c.sources;

import com.google.mr4c.content.ContentFactories;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.hadoop.fs.BlockLocation;

public class BytesDataFileSource extends AbstractDataFileSource {

	private byte[] m_bytes;

	public BytesDataFileSource(byte[] bytes) {
		if ( bytes==null ) {
			throw new IllegalArgumentException("Byte array is null");
		}
		m_bytes = bytes;
	}

	public synchronized long getFileSize() throws IOException {
		assertNotReleased();
		return m_bytes.length;
	}

	public synchronized InputStream getFileInputStream() throws IOException {
		assertNotReleased();
		return new ByteArrayInputStream(m_bytes);
	}

	public synchronized byte[] getFileBytes() throws IOException {
		assertNotReleased();
		return m_bytes;
	}

	public synchronized void getFileBytes(ByteBuffer buf) throws IOException {
		assertNotReleased();
		buf.put(m_bytes);
		if ( buf.hasArray() ) {
			m_bytes = buf.array();
		}
	}

	public synchronized void release() {
		m_bytes=null;
	}

	private void assertNotReleased() {
		if ( m_bytes==null ) {
			throw new IllegalStateException("Bytes have already been released");
		}
	}

	public String getDescription() {
		return "in memory file cache";
	}

}
