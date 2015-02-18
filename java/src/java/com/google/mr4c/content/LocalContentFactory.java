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
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.fs.Path;

public class LocalContentFactory extends AbstractContentFactory implements ContentFactory {

	public InputStream readContentAsStream(URI uri) throws IOException {
		File file = new File(uri);
		return new FileInputStream(file);
	}

	public long getContentLength(URI uri) throws IOException {
		File file = new File(uri);
		return file.length();
	}

	public OutputStream getOutputStreamForContent(URI uri) throws IOException {
		File file = new File(uri);
		return new FileOutputStream(file);
	}

	public void ensureParentExists(URI uri) throws IOException {
		File file = new File(uri);
		ensureParentExists(file);
	}

	/*package*/ static synchronized void ensureParentExists(File file) throws IOException {
		File dir = file.getParentFile();
		if( dir.exists() ) {
			return;
		}
		if ( !dir.mkdirs() ) {
			throw new IOException("Couldn't create directory " + dir );
		}
	}

	public boolean exists(URI uri) throws IOException {
		File file = new File(uri);
		return file.exists();
	}

	public boolean deleteContent(URI uri) throws IOException {
		File file = new File(uri);
		return file.delete();
	}

	public Path toPath(URI uri) {
		return new Path(uri);
	}

}


