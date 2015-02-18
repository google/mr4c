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
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.Properties;

import org.apache.hadoop.fs.Path;

public interface ContentFactory {

	Reader readContentAsReader(URI uri) throws IOException;

	String readContentAsString(URI uri) throws IOException;

	InputStream readContentAsStream(URI uri) throws IOException;

	byte[] readContentAsBytes(URI uri) throws IOException;

	long getContentLength(URI uri) throws IOException;

	Properties readContentAsProperties(URI uri) throws IOException;

	void readContent(URI uri, Writer writer) throws IOException;

	void readContent(URI uri, OutputStream output) throws IOException;


	void writeContent(URI uri, String text) throws IOException;

	void writeContent(URI uri, byte[] bytes) throws IOException;

	void writeContent(URI uri, Reader reader) throws IOException;

	void writeContent(URI uri, InputStream input) throws IOException;

	void writeContent(URI uri, Properties props) throws IOException;

	Writer getWriterForContent(URI uri) throws IOException;

	OutputStream getOutputStreamForContent(URI uri) throws IOException;

	void ensureParentExists(URI uri) throws IOException;

	boolean exists(URI uri) throws IOException;

	Path toPath(URI uri);

	boolean deleteContent(URI uri) throws IOException;

}


