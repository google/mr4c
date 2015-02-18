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
import java.io.OutputStream;
import java.net.URI;

import org.slf4j.Logger;

public class URIDataFileSink extends AbstractDataFileSink {

	protected static final Logger s_log = MR4CLogging.getLogger(URIDataFileSink.class);

	private URI m_uri;
	private String m_name;
	private File m_file;

	public URIDataFileSink(URI uri) {
		m_uri = uri;
	}

	public URIDataFileSink(URI uri, String name) {
		m_uri = uri;
		m_name = name;
	}

	public URIDataFileSink(URI uri, String name, File file) {
		m_uri = uri;
		m_name = name;
		m_file = file;
	}

	public OutputStream getFileOutputStream() throws IOException {
		s_log.debug("Creating stream for writing file content to [{}]", m_uri);
		ContentFactories.ensureParentExists(m_uri);
		return ContentFactories.getOutputStreamForContent(m_uri);
	}

	public void writeFile(byte[] bytes) throws IOException {
		s_log.debug("Writing {} bytes of file content to [{}]", bytes.length, m_uri);
		ContentFactories.ensureParentExists(m_uri);
		ContentFactories.writeContent(m_uri, bytes);
	}

	public void writeFile(InputStream input) throws IOException {
		s_log.debug("Writing file content from stream to [{}]", m_uri);
		ContentFactories.ensureParentExists(m_uri);
		ContentFactories.writeContent(m_uri, input);
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

}
