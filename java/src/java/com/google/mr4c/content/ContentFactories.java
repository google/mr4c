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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import org.apache.hadoop.fs.Path;

public abstract class ContentFactories {

	private static Map<String,ContentFactory> s_factories = new HashMap<String,ContentFactory>();

	static {
		s_factories.put("file", new LocalContentFactory());
		s_factories.put("hdfs", new HDFSContentFactory("hdfs"));
		s_factories.put("rel", new RelativeContentFactory());
		s_factories.put("s3", new S3ContentFactory("s3"));
		s_factories.put("s3n", new S3ContentFactory("s3n"));
	}


	public static ContentFactory getContentFactory(URI uri) {
		return getContentFactory(uri.getScheme());
	}

	public static ContentFactory getContentFactory(String scheme) {
		ContentFactory factory = s_factories.get(scheme);
		if ( factory==null ) {
			throw new IllegalArgumentException("No content factory for scheme=["+scheme+"]");
		}
		return factory;
	}
	
	public static Reader readContentAsReader(URI uri) throws IOException {
		uri = scrubURI(uri);
		return getContentFactory(uri).readContentAsReader(uri);
	}

	public static String readContentAsString(URI uri) throws IOException {
		uri = scrubURI(uri);
		return getContentFactory(uri).readContentAsString(uri);
	}

	public static InputStream readContentAsStream(URI uri) throws IOException {
		uri = scrubURI(uri);
		return getContentFactory(uri).readContentAsStream(uri);
	}

	public static byte[] readContentAsBytes(URI uri) throws IOException {
		uri = scrubURI(uri);
		return getContentFactory(uri).readContentAsBytes(uri);
	}

	public static long getContentLength(URI uri) throws IOException {
		uri = scrubURI(uri);
		return getContentFactory(uri).getContentLength(uri);
	}

	public static Properties readContentAsProperties(URI uri) throws IOException {
		uri = scrubURI(uri);
		return getContentFactory(uri).readContentAsProperties(uri);
	}

	public static void readContent(URI uri, Writer writer) throws IOException {
		uri = scrubURI(uri);
		getContentFactory(uri).readContent(uri,writer);
	}

	public static void readContent(URI uri, OutputStream output) throws IOException {
		uri = scrubURI(uri);
		getContentFactory(uri).readContent(uri,output);
	}


	public static void writeContent(URI uri, String text) throws IOException {
		uri = scrubURI(uri);
		getContentFactory(uri).writeContent(uri,text);
	}

	public static void writeContent(URI uri, byte[] bytes) throws IOException {
		uri = scrubURI(uri);
		getContentFactory(uri).writeContent(uri,bytes);
	}

	public static void writeContent(URI uri, Reader reader) throws IOException {
		uri = scrubURI(uri);
		getContentFactory(uri).writeContent(uri,reader);
	}

	public static void writeContent(URI uri, InputStream input) throws IOException {
		uri = scrubURI(uri);
		getContentFactory(uri).writeContent(uri,input);
	}

	public static void writeContent(URI uri, Properties props) throws IOException {
		uri = scrubURI(uri);
		getContentFactory(uri).writeContent(uri,props);
	}

	public static Writer getWriterForContent(URI uri) throws IOException {
		uri = scrubURI(uri);
		return getContentFactory(uri).getWriterForContent(uri);
	}

	public static OutputStream getOutputStreamForContent(URI uri) throws IOException {
		uri = scrubURI(uri);
		return getContentFactory(uri).getOutputStreamForContent(uri);
	}

	public static void ensureParentExists(URI uri) throws IOException {
		uri = scrubURI(uri);
		getContentFactory(uri).ensureParentExists(uri);
	}

	public static boolean exists(URI uri) throws IOException {
		uri = scrubURI(uri);
		return getContentFactory(uri).exists(uri);
	}

	public static Path toPath(URI uri) {
		uri = scrubURI(uri);
		return getContentFactory(uri).toPath(uri);
	}

	public static boolean deleteContent(URI uri) throws IOException {
		uri = scrubURI(uri);
		return getContentFactory(uri).deleteContent(uri);
	}

	/**
	  * Turns a URI that is just a file path or a "rel" URI into a real file URI.
	*/
	public static URI scrubURI(URI uri) {
		String scheme = uri.getScheme();
		String path = uri.getSchemeSpecificPart();
		if ( StringUtils.isEmpty(scheme) ) {
			// its a file path
			if ( path.startsWith("/") ) {
				// its absolute
				return new File(path).toURI();
			} else {
				// its relative
				return new File(RelativeContentFactory.getWorkingDirectory(), path).toURI();
			}
		} else if ( scheme.equals("rel") ) {
			return RelativeContentFactory.toFile(uri).toURI();
		} else	{
			return uri;
		}
	}

}


