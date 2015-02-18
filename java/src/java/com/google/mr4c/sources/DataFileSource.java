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

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.hadoop.fs.BlockLocation;

public interface DataFileSource {

	/**
	  * Returns the number of bytes in the file.  Should return -1 if this is unknown.  Caching of content may occur as a result of this call.
	*/
	long getFileSize() throws IOException;

	/**
	  * Returns a stream for reading from this source.  No caching of
	  * content is expected as a result of this call.
	*/
	InputStream getFileInputStream() throws IOException;

	/**
	  * Returns the actual file bytes.  A DataFileSource may cache the
	  * bytes between calls.
	*/
	byte[] getFileBytes() throws IOException;

	/**
	  * Put file bytes in the provided byte buffer.  This buffer may be
	  * used to replace an existing cache.
	*/
	void getFileBytes(ByteBuffer buf) throws IOException;

	/**
	  * Release any memory currently holding file content.  Subsequent
	  * calls to access file content will fail if the source has no way to
	  * recover the file content.
	*/
	void release();

	/**
	  * Returns the block locations for this file.  May return an empty
	  * array if this information is not known.
	*/
	BlockLocation[] getBlockLocation() throws IOException;

	/**
	  * Returns the name of the file in a file system.  If there is no actual file system, may return null
	*/
	String getFileName();

	/**
	  * Returns the file on local disk, or null if not on a local disk
	*/
	File getLocalFile();

	String getDescription();

}
