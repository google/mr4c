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

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.Properties;

public abstract class AbstractContentFactory implements ContentFactory {

	public Reader readContentAsReader(URI uri) throws IOException {
		return new InputStreamReader(readContentAsStream(uri));
	}

	public String readContentAsString(URI uri) throws IOException {
		Reader reader = readContentAsReader(uri);
		try {
			return IOUtils.toString(reader);
		} finally {
			reader.close();
		}
	}

	public abstract InputStream readContentAsStream(URI uri) throws IOException;

	public byte[] readContentAsBytes(URI uri) throws IOException {
		InputStream input = readContentAsStream(uri);
		try {
			return IOUtils.toByteArray(input);
		} finally { 
			input.close();
		}
	}

	public Properties readContentAsProperties(URI uri) throws IOException {
		Properties props = new Properties();
		Reader reader = readContentAsReader(uri);
		try {
			props.load(reader);
			return props;
		} finally {
			reader.close();
		}
	}

	public void readContent(URI uri, Writer writer) throws IOException {
		Reader reader = readContentAsReader(uri);
		try {
			IOUtils.copy(reader, writer);
		} finally {
			reader.close();
		}
	}

	public void readContent(URI uri, OutputStream output) throws IOException {
		InputStream input = readContentAsStream(uri);
		try {
			IOUtils.copy(input, output);
		} finally {
			input.close();
		}
	}


	public void writeContent(URI uri, String text) throws IOException {
		Writer writer = getWriterForContent(uri);
		try {
			writer.write(text);
		} finally {
			writer.close();
		}
	}
		

	public void writeContent(URI uri, byte[] bytes) throws IOException {
		OutputStream output = getOutputStreamForContent(uri);
		try {
			output.write(bytes);
		} finally {
			output.close();
		}
	}

	public void writeContent(URI uri, Reader reader) throws IOException {
		Writer writer = getWriterForContent(uri);
		try {
			IOUtils.copy(reader,writer);
		} finally {
			writer.close();
		}
	}

	public void writeContent(URI uri, InputStream input) throws IOException {
		OutputStream output = getOutputStreamForContent(uri);
		try {
			IOUtils.copy(input,output);
		} finally {
			output.close();
		}
	}

	public void writeContent(URI uri, Properties props) throws IOException {
		Writer writer = getWriterForContent(uri);
		try {
			props.store(writer,"");
		} finally {
			writer.close();
		}
	}

	public Writer getWriterForContent(URI uri) throws IOException {
		return new OutputStreamWriter(getOutputStreamForContent(uri));
	}

	public abstract OutputStream getOutputStreamForContent(URI uri) throws IOException;

}


