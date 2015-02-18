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

import com.google.mr4c.content.S3ContentFactory;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class S3FileSource extends HadoopFileSource {

	private Path m_dir;
	private FileSystem m_fs;

	public static S3FileSource create(URI uri) throws IOException {
		return create(uri, true);
	}

	public static S3FileSource create(URI uri, boolean flat) throws IOException {
		FileSystem fs = S3ContentFactory.getFileSystem(uri);
		Path dir = new Path(uri);
		return new S3FileSource(fs, dir, flat);
	}

	protected S3FileSource(FileSystem fs, Path dir, boolean flat) {
		super(fs, dir, flat);
	}

}
