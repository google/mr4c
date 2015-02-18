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

import com.google.mr4c.content.RelativeContentFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class DiskFileSource implements FileSource {

	protected File m_dir;
	protected boolean m_flat;

	public DiskFileSource(File dir) {
		this(dir, true);
	}

	public DiskFileSource(File dir, boolean flat) {
		m_dir = dir;
		m_flat = flat;
	}

	public List<String> getAllFileNames() throws IOException {
		return getAllFileNames(m_dir);
	}

	private List<String> getAllFileNames(File dir) throws IOException {
		List<String> names = new ArrayList<String>();
		File[] files = dir.listFiles();
		if ( files==null ) {
			throw new FileNotFoundException(String.format("[%s] is not an existing directory", dir));
		}
		for ( File file : files ) {
			if ( !file.isHidden() ) {
				if ( file.isDirectory() && !m_flat ) {
					names.addAll(getAllFileNames(file));
				} else {
					names.add(RelativeContentFactory.toRelativeFilePath(file, m_dir));
				}
			}
		}
		return names;
	}

	public DataFileSource getFileSource(String fileName) throws IOException {
		return new URIDataFileSource(toURI(fileName), fileName, toFile(fileName));
	}

	public boolean fileExists(String fileName) throws IOException {
		return toFile(fileName).exists();
	}

	public DataFileSource getFileSourceOnlyIfExists(String fileName) throws IOException {
		return fileExists(fileName) ?
			getFileSource(fileName) :
			null;
	}

	public DataFileSink getFileSink(String fileName) throws IOException {
		return new URIDataFileSink(toURI(fileName), fileName, toFile(fileName));
	}

	private File toFile(String fileName) {
		return new File(m_dir, fileName);
	}

	private URI toURI(String fileName) throws IOException {
		return toURI(toFile(fileName));
	}

	public void close() {} // nothing to do

	public synchronized void ensureExists() throws IOException {
		if( m_dir.exists() ) {
			return;
		}
		if ( !m_dir.mkdirs() ) {
			throw new IOException("Couldn't create directory " + m_dir );
		}
	}

	public void clear() throws IOException {
		clear(m_dir);
	}

	private void clear(File dir) throws IOException {
		if ( !dir.isDirectory() ) {
			return;
		}
		for ( File file : dir.listFiles() ) {
			if ( !m_flat ) {
				clear(file);
			}
			if ( !file.delete() ) {
				throw new IOException(String.format("Couldn't delete [%s]", file.toString()));
			}
		}
	}

	public String getDescription() {
		return m_dir.toString();
	}

	protected URI toURI(File file) {
		return file.toURI();
	}

}

