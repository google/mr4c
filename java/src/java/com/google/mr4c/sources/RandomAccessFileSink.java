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

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
  * Wrapper around a DataFileSink to allow random access
*/
public class RandomAccessFileSink implements RandomAccessible {

	private DataFileSink m_sink;
	private File m_file;
	private RandomAccessFile m_raf;
	private boolean m_staged;

	public RandomAccessFileSink(DataFileSink sink) {
		m_sink = sink;
	}

	public DataFileSink getSink() {
		return m_sink;
	}

	public synchronized RandomAccessFile getRandomAccess() throws IOException {
		ensureRandomAccess();
		return m_raf;
	}

	public synchronized void close() throws IOException {
		if ( m_raf!=null ) {
			m_raf.close();
			m_raf=null;
			if ( m_staged ) {
				copyFromLocal();
			}
			m_file=null;
		}
	}

	public boolean isStaged() {
		return m_staged;
	}

	public boolean isWritable() {
		return true;
	}

	public String getDescription() {
		return m_sink.getDescription();
	}

	private void ensureRandomAccess() throws IOException {
		if ( m_raf!=null ) {
			return;
		}
		ensureLocalFile();
		m_raf = new RandomAccessFile(m_file, "rw");
	}

	private void ensureLocalFile() throws IOException {
		if ( m_file!=null ) {
			return;
		}
		m_file = m_sink.getLocalFile();
		if ( m_file==null ) {
			createLocalFile();
		} else {
			ContentFactories.ensureParentExists(m_file.toURI());
		}
	}

	private void createLocalFile() throws IOException {
		m_file = File.createTempFile("mr4c", "random");
		m_file.deleteOnExit();
		m_staged = true;
	} 

	private void copyFromLocal() throws IOException {
		OutputStream output=null;
		try {
			output = m_sink.getFileOutputStream();
			ContentFactories.readContent(m_file.toURI(), output);
		} finally {
			if ( output!=null ) { 
				output.close();
			}
		}
	} 

}


