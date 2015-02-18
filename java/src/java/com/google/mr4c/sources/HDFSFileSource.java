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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSFileSource extends HadoopFileSource {

	private Path m_dir;
	private FileSystem m_fs;

	public static HDFSFileSource create(Path dir) throws IOException{
		return create(dir, true);
	}

	public static HDFSFileSource create(Path dir, boolean flat) throws IOException{

		return create(dir.getFileSystem(new Configuration()), dir, flat);
	}

	public static HDFSFileSource create(FileSystem fs, Path dir, boolean flat) {
		Path root = new Path(fs.getUri());
		dir = new Path(root, dir);
		return new HDFSFileSource(fs, dir, flat);
	}

	protected HDFSFileSource(FileSystem fs, Path dir, boolean flat) {
		super(fs, dir, flat);
	}

}
