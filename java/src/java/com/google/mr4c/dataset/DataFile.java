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

package com.google.mr4c.dataset;

import com.google.mr4c.sources.BytesDataFileSource;
import com.google.mr4c.sources.DataFileSink;
import com.google.mr4c.sources.DataFileSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.IOUtils;

public class DataFile {

	private String m_contentType;
	private DataFileSource m_src;
	private DataFileSink m_sink;

	// create new
	public DataFile(byte[] bytes, String contentType) {
		this(new BytesDataFileSource(bytes), contentType);
	}

	public DataFile(DataFileSource src, String contentType) {
		m_src = src;
		m_contentType = contentType;
	}

	// create to load
	public DataFile(String contentType) {
		m_contentType = contentType;
	}

	public DataFile() {
	}

	public String getContentType() {
		return m_contentType;
	}

	public DataFileSource getFileSource() {
		return m_src;
	}

	public DataFileSink getFileSink() {
		return m_sink;
	}

	public void setFileSink(DataFileSink sink) {
		m_sink = sink;
	}

	public synchronized byte[] getBytes() throws IOException {
		assertHasSource();
		return m_src.getFileBytes();
	}

	public synchronized InputStream getInputStream() throws IOException {
		assertHasSource();
		return m_src.getFileInputStream();
	}
			
	public synchronized void setFileSource(DataFileSource src) {
		m_src = src;
	}

	public synchronized void setBytes(byte[] bytes) {
		assertHasNoSource();
		m_src = new BytesDataFileSource(bytes);
	} 

	public boolean hasContent() {
		return m_src!=null;
	}

	public synchronized void release() {
		if ( m_src!=null ) {
			m_src.release();
			m_src=null;
		}
	}

	public synchronized String getFileName() {
		assertHasSource();
		return m_src.getFileName();
	}
	
	private void assertHasSource() {
		if ( m_src==null ) {
			throw new IllegalStateException("DataFile doesn't have a DataFileSource");
		}
	}

	private void assertHasNoSource() {
		if ( m_src!=null ) {
			throw new IllegalStateException("DataFile already has a DataFileSource");
		}
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		DataFile file = (DataFile) obj;
		if ( !equalsIgnoreContent(file) ) return false;
		try {
			if ( !compareContent(this,file) ) return false;
		} catch ( IOException ioe) {
			throw new RuntimeException(ioe); // tunnelling exception
		}
		return true;
	}

	public boolean equalsIgnoreContent(DataFile file) {
		if ( !m_contentType.equals(file.m_contentType) ) return false;
		return true;
	}

	private static boolean compareContent(DataFile file1, DataFile file2) throws IOException {
		if ( file1.hasContent()!=file2.hasContent() ) {
			return false;
		} else if ( !file1.hasContent() ) {
			return true; // neither has content
		} else {
			return IOUtils.contentEquals(
				file1.getInputStream(),
				file2.getInputStream()
			);
		}
	}
				
	public int hashCode() {
		return m_contentType.hashCode(); // shouldn't be a key!
	}

}

