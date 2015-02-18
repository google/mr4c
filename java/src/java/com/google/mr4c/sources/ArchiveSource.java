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

/**
  * Represents an archive of files.  The archive has the following semantics:
  * <ol>
  * <li> Files are divided into regular files and metadata files</li>
  * <li> Must call startWrite() at the beginning of a write operation, and finishWrite() at the end </li>
  * <li> Starting a write auto-closes any resources held for read </li>
  * <li> Writes always overwrite existing files </li>
  * <li> Reads are not expected to work during writes</li>
  * <li> Calls to getFileSink() are only required to work during a write</li>
  * </ol>
*/
public interface ArchiveSource {

	List<String> getAllFileNames() throws IOException;

	List<String> getAllMetadataFileNames() throws IOException;

	DataFileSource getFileSource(String fileName) throws IOException;

	/**
	  * Check if the file exists
	*/
	boolean fileExists(String fileName) throws IOException;

	/**
	  * Will return null if the file does not exist
	*/
	DataFileSource getFileSourceOnlyIfExists(String fileName) throws IOException;

	DataFileSource getMetadataFileSource(String fileName) throws IOException;

	DataFileSink getFileSink(String fileName) throws IOException;

	DataFileSink getMetadataFileSink(String fileName) throws IOException;

	void startWrite() throws IOException;

	void finishWrite() throws IOException;

	/**
	  * releases any held resources for reading the file
	*/
	void close() throws IOException;


	/**
	  * deletes all files in this source
	*/
	void clear() throws IOException;

	boolean exists() throws IOException;

	String getDescription();
}

