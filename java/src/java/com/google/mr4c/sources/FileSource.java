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

import java.io.IOException;
import java.net.URI;
import java.util.List;

public interface FileSource {

	List<String> getAllFileNames() throws IOException;

	DataFileSource getFileSource(String fileName) throws IOException;

	/**
	  * Check if the file exists
	*/
	boolean fileExists(String fileName) throws IOException;

	/**
	  * Will return null if the file does not exist
	*/
	DataFileSource getFileSourceOnlyIfExists(String fileName) throws IOException;

	DataFileSink getFileSink(String fileName) throws IOException;

	/**
	  * Releases any resources held by this source.  For some sources, this may be needed to commit writes.
	*/
	void close() throws IOException;

	/**
	  * creates the necessary directories/files/etc before accessing this source
	*/
	void ensureExists() throws IOException;

	/**
	  * deletes all files in this source
	*/
	void clear() throws IOException;

	String getDescription();

}

