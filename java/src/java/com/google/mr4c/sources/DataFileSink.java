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

public interface DataFileSink {

	OutputStream getFileOutputStream() throws IOException;

	void writeFile(byte[] bytes) throws IOException;

	void writeFile(InputStream input) throws IOException;

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
