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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
  * Aggregator of various file sources into a single read-only source of files
*/
public class AggregateFileSource implements FileSource {

	private List<FileSource> m_sources;

	public AggregateFileSource(List<FileSource> sources) {
		m_sources = sources;
	}

	public List<String> getAllFileNames() throws IOException {
		List<String> names = new ArrayList<String>();
		for ( FileSource src : m_sources ) {
			List<String> newNames = src.getAllFileNames();
			Collection<String> dups= CollectionUtils.intersection(names, newNames);
			if ( !dups.isEmpty() ) {
				throw new IllegalStateException("Found the following repeated file names: " + dups);
			}
			names.addAll(newNames);
		}
		return names;
	}

	public DataFileSource getFileSource(String fileName) throws IOException {
		DataFileSource src = getFileSourceOnlyIfExists(fileName);
		if ( src==null ) {
			throw new IllegalArgumentException(String.format("No file named [%s] found", fileName));
		}
		return src;
	}
	

	public boolean fileExists(String fileName) throws IOException {
		for ( FileSource src : m_sources ) {
			if ( src.fileExists(fileName) ) {
				return true;
			}
		}
		return false;
	}

	public DataFileSource getFileSourceOnlyIfExists(String fileName) throws IOException {
		for ( FileSource src : m_sources ) {
			if ( src.fileExists(fileName) ) {
				return src.getFileSource(fileName);
			}
		}
		return null; 
	}

	public DataFileSink getFileSink(String fileName) throws IOException {
		throw new UnsupportedOperationException("File sinks not avaiable from aggregate file source");
	}

	public void close() throws IOException {
		for ( FileSource src : m_sources ) {
			src.close();
		}
	}

	public void ensureExists() throws IOException {
		for ( FileSource src : m_sources ) {
			src.ensureExists();
		}
	}

	public void clear() throws IOException {
		throw new UnsupportedOperationException("Clear not avaiable for aggregate file source");
	}

	public String getDescription() {
		return String.format("Aggregate of %d file sources", m_sources.size());
	}

}

