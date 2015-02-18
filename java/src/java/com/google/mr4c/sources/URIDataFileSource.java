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
import com.google.mr4c.util.MR4CLogging;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.slf4j.Logger;

public class URIDataFileSource extends AbstractDataFileSource {

	protected static final Logger s_log = MR4CLogging.getLogger(URIDataFileSource.class);
	private static Configuration s_config = new Configuration(); // hopefully we can just reuse this over and over

	private URI m_uri;
	private String m_name;
	private File m_file;
	private DataFileSource m_cache;

	public URIDataFileSource(URI uri) {
		m_uri = uri;
	}

	public URIDataFileSource(URI uri, String name) {
		m_uri = uri;
		m_name = name;
	}

	public URIDataFileSource(URI uri, String name, File file) {
		m_uri = uri;
		m_name = name;
		m_file = file;
	}

	public long getFileSize() throws IOException {
		if ( m_cache==null ) {
			return ContentFactories.getContentLength(m_uri);
		} else {
			return m_cache.getFileSize();
		}
	}

	public InputStream getFileInputStream() throws IOException {
		if ( m_cache==null ) {
			s_log.debug("Creating stream for reading file content from [{}]", m_uri);
			return ContentFactories.readContentAsStream(m_uri);
		} else {
			s_log.debug("Creating stream for reading file content cached from [{}]", m_uri);
			return m_cache.getFileInputStream();
		}
	}

	public byte[] getFileBytes() throws IOException {
		loadBytesIfNecessary();
		return m_cache.getFileBytes();
	}

	private synchronized void loadBytesIfNecessary() throws IOException {
		if ( m_cache==null ) {
			s_log.debug("Reading file content from [{}]", m_uri);
			byte[] bytes = ContentFactories.readContentAsBytes(m_uri);
			s_log.debug("Read {} bytes from [{}]", bytes.length, m_uri);
			m_cache = new BytesDataFileSource(bytes);
		}
	}

	public void getFileBytes(ByteBuffer buf) throws IOException {
		loadBytesIfNecessary();
		m_cache.getFileBytes(buf);
	}
		

	public synchronized void release() {
		if ( m_cache!=null ) {
			m_cache.release();
		}
		m_cache=null;
	}

	@Override public String getFileName() {
		return m_name;
	}

	@Override public File getLocalFile() {
		return m_file;
	}

	public String getDescription() {
		return m_uri.toString();
	}

	@Override public BlockLocation[] getBlockLocation() throws IOException {
		URI uri = ContentFactories.scrubURI(m_uri);
		FileSystem fs = FileSystem.get(uri,s_config);
		Path path = new Path(uri);
		FileStatus status = fs.getFileStatus(path);
		return fs.getFileBlockLocations(status, 0, status.getBlockSize());
	}

}
