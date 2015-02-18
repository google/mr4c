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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public abstract class HadoopFileSource implements FileSource {

	protected Path m_dir;
	protected FileSystem m_fs;
	protected boolean m_flat;

	protected HadoopFileSource(FileSystem fs, Path dir) {
		this(fs, dir, true);
	}

	protected HadoopFileSource(FileSystem fs, Path dir, boolean flat) {
		m_fs = fs;
		m_dir = dir;
		m_flat = flat;
	}

	public List<String> getAllFileNames() throws IOException {
		return getAllFileNames(m_dir);
	}

	private List<String> getAllFileNames(Path dir) throws IOException {
		List<String> names = new ArrayList<String>();
		FileStatus[] files = m_fs.listStatus(dir);
		if ( files==null ) {
			throw new FileNotFoundException(String.format("[%s] is not an existing directory", dir));
		}
		for ( FileStatus status : files ) {
			if ( status.isDirectory() && !m_flat ) {
				names.addAll(getAllFileNames(status.getPath()));
			} else {
				String name = m_dir.toUri().relativize(status.getPath().toUri()).getPath();
				names.add(name);
			}
		}
		return names;
	}


	public DataFileSource getFileSource(String fileName) throws IOException {
		return new URIDataFileSource(toURI(fileName), fileName);
	}

	public boolean fileExists(String fileName) throws IOException {
		return m_fs.exists(toPath(fileName));
	}

	public DataFileSource getFileSourceOnlyIfExists(String fileName) throws IOException {
		return fileExists(fileName) ?
			getFileSource(fileName) :
			null;
	}

	public DataFileSink getFileSink(String fileName) throws IOException {
		return new URIDataFileSink(toURI(fileName), fileName);
	}

	private Path toPath(String fileName) throws IOException {
		return new Path(m_dir, fileName);
	}
		
	private URI toURI(String fileName) throws IOException {
		return toPath(fileName).toUri();
	}

	public void close() {}

	public void ensureExists() throws IOException {
		if ( !m_fs.mkdirs(m_dir) ) {
			throw new IOException(String.format("Couldn't create directory [%s]", m_dir) );
		}
	}

	public synchronized void clear() throws IOException {
		for ( FileStatus status : m_fs.listStatus(m_dir) ) {
			if ( !m_fs.delete(status.getPath(), !m_flat) ) {
				throw new IOException(String.format("Couldn't delete [%s]", status.getPath().toString()));
			}
		}
	}

	public String getDescription() {
		return m_dir.toUri().toString();
	}

}

