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

import com.google.mr4c.config.category.MR4CConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class S3ContentFactory extends AbstractContentFactory implements ContentFactory {

	private static Configuration s_config = new Configuration();
	private static S3Credentials s_cred;
	private String m_scheme;

	public S3ContentFactory(String scheme) {
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
		return getFileSystem(uri).open(path);
	}

	public long getContentLength(URI uri) throws IOException {
		Path path = toPath(uri);
		FileSystem fs = getFileSystem(uri);
		FileStatus file = fs.getFileStatus(path);
		return file.getLen();
	}

	public OutputStream getOutputStreamForContent(URI uri) throws IOException {
		Path path = toPath(uri);
		return getFileSystem(uri).create(path);
	}

	public void ensureParentExists(URI uri) throws IOException {
		// S3 creates directories as needed
	}

	public boolean exists(URI uri) throws IOException {
		Path path = toPath(uri);
		return getFileSystem(uri).exists(path);
	}

	public boolean deleteContent(URI uri) throws IOException {
		Path path = toPath(uri);
		return getFileSystem(uri).delete(path, false);
	}

	public static FileSystem getFileSystem(URI uri) throws IOException {
		ensureCredentials();
		return FileSystem.get(uri,s_config);
	}

	public static S3Credentials getCredentials() {
		return s_cred;
	}

	public synchronized static void setCredentials(S3Credentials cred) {
		s_cred = cred;
		cred.applyTo(s_config);
	}

	private synchronized static void ensureCredentials() {
		if ( s_cred!=null && s_cred.isValid() ) {
			return;
		}

		S3Credentials cred = S3Credentials.extractFrom(MR4CConfig.getDefaultInstance());
		if ( cred!=null && cred.isValid() ) {
			setCredentials(cred);
			return;
		}

		cred = S3Credentials.extractFrom(s_config);
		if ( cred!=null && cred.isValid() ) {
			setCredentials(cred);
			return;
		}

		throw new IllegalStateException("No S3 credentials found in MR4C configuration or Hadoop configuration");

	}
			
}
