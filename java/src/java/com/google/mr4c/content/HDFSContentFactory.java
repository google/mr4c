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

package com.google.mr4c.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSContentFactory extends AbstractContentFactory implements ContentFactory {

	private Configuration m_config = new Configuration();
	private String m_scheme;

	public HDFSContentFactory(String scheme) {
		m_scheme=scheme;
	}

	public Path toPath(URI uri) {
		if ( !uri.getScheme().equals(m_scheme) ) {
			throw new IllegalArgumentException(String.format("Expecting %s URI [%s]", m_scheme, uri));
		}
		return new Path(uri);
	}
	public InputStream readContentAsStream(URI uri) throws IOException {
		Path path = toPath(uri);
		FileSystem fs = FileSystem.get(uri,m_config);
		return fs.open(path);
	}

	public long getContentLength(URI uri) throws IOException {
		Path path = toPath(uri);
		FileSystem fs = FileSystem.get(uri,m_config);
		FileStatus file = fs.getFileStatus(path);
		return file.getLen();
	}

	public OutputStream getOutputStreamForContent(URI uri) throws IOException {
		Path path = toPath(uri);
		FileSystem fs = FileSystem.get(uri,m_config);
		return fs.create(path);
	}

	public void ensureParentExists(URI uri) throws IOException {
		// HDFS creates directories as needed
	}

	public boolean exists(URI uri) throws IOException {
		Path path = toPath(uri);
		FileSystem fs = FileSystem.get(uri,m_config);
		return fs.exists(path);
	}

	public boolean deleteContent(URI uri) throws IOException {
		Path path = toPath(uri);
		FileSystem fs = FileSystem.get(uri,m_config);
		return fs.delete(path, false);
	}
}


