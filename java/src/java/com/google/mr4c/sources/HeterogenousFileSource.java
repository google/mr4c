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

import com.google.mr4c.content.ContentFactories;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
  * Aggregator of various URIs into a coherent source of files
*/
public class HeterogenousFileSource implements FileSource {

	private Map<String,URI> m_files;

	public HeterogenousFileSource(Map<String,URI> files) {
		m_files = files;
	}

	public List<String> getAllFileNames() throws IOException {
		List<String> names = new ArrayList<String>();
		for ( String name : m_files.keySet() ) {
			URI uri = m_files.get(name);
			if ( ContentFactories.exists(uri) ) {
				names.add(name);
			}
		}
		Collections.sort(names);
		return names;
	}

	public DataFileSource getFileSource(String fileName) throws IOException {
		URI uri = m_files.get(fileName);
		if ( uri==null ) {
			throw new IllegalArgumentException(String.format("No file named [%s] in this source", fileName));
		}
		return new URIDataFileSource(uri);
	}

	public boolean fileExists(String fileName) throws IOException {
		return m_files.containsKey(fileName);
	}

	public DataFileSource getFileSourceOnlyIfExists(String fileName) throws IOException {
		return fileExists(fileName) ?
			getFileSource(fileName) :
			null;
	}

	public DataFileSink getFileSink(String fileName) throws IOException {
		URI uri = m_files.get(fileName);
		if ( uri==null ) {
			throw new IllegalArgumentException(String.format("No file named [%s] in this source", fileName));
		}
		return new URIDataFileSink(uri);
	}

	public void close() {}

	public void ensureExists() throws IOException {
		// need to make sure the parent of each URI exists
		for ( URI uri : m_files.values() ) {
			ContentFactories.ensureParentExists(uri);
		}
	}

	public synchronized void clear() throws IOException {
		for ( URI uri : m_files.values() ) {
			if ( ContentFactories.exists(uri) && !ContentFactories.deleteContent(uri) ) {
				throw new IOException(String.format("Couldn't delete [%s]", uri.toString()));
			}
		}
	}

	public String getDescription() {
		return String.format("Heterogenous file source with [%d] files" , m_files.size());
	}

}

