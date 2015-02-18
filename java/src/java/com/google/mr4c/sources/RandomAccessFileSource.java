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
import java.io.InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
  * Wrapper around a DataFileSource to allow random access
*/
public class RandomAccessFileSource implements RandomAccessible {

	private DataFileSource m_src;
	private File m_file;
	private RandomAccessFile m_raf;
	private boolean m_staged;

	public RandomAccessFileSource(DataFileSource src) {
		m_src = src;
	}

	public DataFileSource getSource() {
		return m_src;
	}

	public synchronized RandomAccessFile getRandomAccess() throws IOException {
		ensureRandomAccess();
		return m_raf;
	}

	public synchronized void close() throws IOException {
		if ( m_raf!=null ) {
			m_raf.close();
			m_raf=null;
			m_file=null;
			m_staged=false;
		}
	}

	public boolean isStaged() {
		return m_staged;
	}

	public boolean isWritable() {
		return false;
	}

	public String getDescription() {
		return m_src.getDescription();
	}

	private void ensureRandomAccess() throws IOException {
		if ( m_raf!=null ) {
			return;
		}
		ensureLocalFile();
		m_raf = new RandomAccessFile(m_file, "r");
	}

	private void ensureLocalFile() throws IOException {
		if ( m_file!=null ) {
			return;
		}
		m_file = m_src.getLocalFile();
		if ( m_file==null ) {
			copyToLocal();
		}
	}

	private void copyToLocal() throws IOException {
		File file = File.createTempFile("mr4c", "random");
		file.deleteOnExit();
		InputStream input=null;
		try {
			input = m_src.getFileInputStream();
			ContentFactories.writeContent(file.toURI(), input);
			m_file = file;
			m_staged = true;
		} finally {
			if ( input!=null ) { 
				input.close();
			}
		}
	} 

}

