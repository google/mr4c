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

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.NullOutputStream;

public class NullDataFileSink extends AbstractDataFileSink {

	public OutputStream getFileOutputStream() {
		return new NullOutputStream();
	}

	public void writeFile(byte[] bytes) {}

	public void writeFile(InputStream input) {} 
	// NOTE: a proper implementation should read and discard all the bytes.
	// As long as we don't have a dependency on the stream pointer reaching
	// the end, doing nothing should be fine.

	public String getDescription() {
		return "null data file sink";
	}

}
