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

package com.google.mr4c.dataset;

import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyFilter;
import com.google.mr4c.keys.DataKeyUtils;
import com.google.mr4c.keys.HasDimensionFilter;
import com.google.mr4c.metadata.MetadataKeyExtractor;
import com.google.mr4c.metadata.MetadataMap;
import com.google.mr4c.sources.DataFileSource;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dataset {

	private Map<DataKey,DataFile> m_files = Collections.synchronizedMap( new HashMap<DataKey,DataFile>() );
	private Map<DataKey,MetadataMap> m_meta = Collections.synchronizedMap( new HashMap<DataKey,MetadataMap>() );
	private DatasetContext m_context;
	private Object m_token = new Object();

	public Dataset() {
		m_context = new NullDatasetContext();
	}

	/**
	  * Object to use in a WeakHashMap.  We do not want equals() called on this by a Map!
	*/
	public Object getToken() {
		return m_token;
	}
	
	public void setContext(DatasetContext context) {
		m_context = context;
	}

	public synchronized DataFile getFile(DataKey key) {
		DataFile file = m_files.get(key);
		if ( file==null ) {
			file = findFile(key);
			if ( file!=null ) {
				addFile(key,file);
			}
		}
		return file;
	}

	public String getFileName(DataKey key) {
		try {
			return m_context.getFileName(key);
		} catch ( IOException ioe ) {
			throw new IllegalStateException(ioe);
		}
	}

	public boolean isOutput() {
		return m_context.isOutput();
	}

	public boolean isQueryOnly() {
		return m_context.isQueryOnly();
	}

	private DataFile findFile(DataKey key) {
		try {
			return m_context.findFile(key);
		} catch ( IOException ioe ) {
			throw new IllegalStateException(ioe);
		}
	}
			

	public Set<DataKey> getAllFileKeys() {
		return m_files.keySet();
	}

	public boolean hasFiles() {
		return !m_files.isEmpty();
	}

	public synchronized void addFile(DataKey key, DataFile file) {
		if ( m_files.containsKey(key) ) {
			throw new IllegalArgumentException(String.format("Tried to add two files for key [%s]", key));
		}
		m_files.put(key,file);
		if ( m_context.isOutput() ) {
			try {
				m_context.addFile(key, file);
			} catch ( IOException ioe ) {
				throw new IllegalStateException(ioe);
			}
		}
	}

	public MetadataMap getMetadata(DataKey key) {
		return m_meta.get(key);
	}

	public Set<DataKey> getAllMetadataKeys() {
		return m_meta.keySet();
	}

	public boolean hasMetadata() {
		return !m_meta.isEmpty();
	}

	public synchronized void addMetadata(DataKey key, MetadataMap meta) {
		if ( m_meta.containsKey(key) ) {
			throw new IllegalArgumentException(String.format("Tried to add two metadata maps for key [%s]", key));
		}
		m_meta.put(key,meta);
	}

	public boolean isEmpty() {
		return !hasMetadata() && !hasFiles();
	}
	
	public synchronized void release() {
		for ( DataFile file : m_files.values() ) {
			file.release();
		}
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		Dataset dataset = (Dataset) obj;
		if ( !m_meta.equals(dataset.m_meta) ) return false;
		if ( !m_files.equals(dataset.m_files) ) return false;
		return true; 
	}

	// for testing use only
	public boolean equalsIgnoreFileContent(Dataset dataset) {
		if ( !m_meta.equals(dataset.m_meta) ) return false;
		if ( !m_files.keySet().equals(dataset.m_files.keySet()) ) return false;
		for ( DataKey key : m_files.keySet() ) {
			DataFile file1 = m_files.get(key);
			DataFile file2 = dataset.m_files.get(key);
			if ( !file1.equalsIgnoreContent(file2) ) {
				return false;
			}
		}
		return true; 
	}

	public int hashCode() {
		// this is expensive, just doing because we overrode equals
		// this class shouldn't be used as a key to begin with
		return m_files.hashCode() + m_meta.hashCode();
	}

	public static Dataset combineSlices(Dataset ... slices) {
		return combineSlices(Arrays.asList(slices));
	}

	public static Dataset combineSlices(Collection<Dataset> slices) {
		Dataset combined = new Dataset();
		for ( Dataset slice : slices ) {
			combined.addSlice(slice);
		}
		return combined;
	}

	public void addSlice(Dataset slice) {
		for ( DataKey key : slice.getAllFileKeys() ) {
			DataFile file = slice.getFile(key);
			addFile(key,file);
		}
		for ( DataKey key : slice.getAllMetadataKeys() ) {
			MetadataMap meta = slice.getMetadata(key);
			addMetadata(key,meta);
		}
	}

	public Dataset slice(DataKeyFilter filter) {
		Dataset slice = new Dataset();
		synchronized (m_files) {
			for ( DataKey key : getAllFileKeys() ) {
				DataFile file = getFile(key);
				if ( filter.filter(key) ) {
					slice.addFile(key,file);
				}
			}
		}
		synchronized (m_meta) {
			for ( DataKey key : getAllMetadataKeys() ) {
				MetadataMap meta = getMetadata(key);
				if ( filter.filter(key) ) {
					slice.addMetadata(key,meta);
				}
			}
		}
		slice.setContext(m_context);
		return slice;
	}

	/**
	  * Returns only the data file part of this dataset
	*/
	public Dataset toFilesOnly() {
		Dataset dataset = new Dataset();
		synchronized (m_files) {
			for ( DataKey key : getAllFileKeys() ) {
				DataFile file = getFile(key);
				dataset.addFile(key,file);
			}
		}
		dataset.setContext(m_context);
		return dataset;
	}

	/**
	  * Returns only the metadata part of this dataset
	*/
	public Dataset toMetadataOnly() {
		Dataset dataset = new Dataset();
		synchronized (m_meta) {
			for ( DataKey key : getAllMetadataKeys() ) {
				MetadataMap meta = getMetadata(key);
				dataset.addMetadata(key,meta);
			}
		}
		dataset.setContext(m_context);
		return dataset;
	}

	/**
	  * returns all keys that are part of metadata AND include the specified dimension
	*/
	public Set<DataKey> getDependentKeys(DataKeyDimension dim) {
		Set<DataKey> metaKeys = new HashSet<DataKey>();
		synchronized (m_meta) {
			for ( MetadataMap map : m_meta.values() ) {
				metaKeys.addAll(MetadataKeyExtractor.findKeys(map));
			}
		}
		HasDimensionFilter filter = new HasDimensionFilter(dim);
		return DataKeyUtils.filter(filter,metaKeys);
	}

	public Collection<DataFileSource> getDataFileSources() {
		Collection<DataFileSource> sources = new ArrayList<DataFileSource>();
		synchronized (m_files) {
			for ( DataFile file : m_files.values() ) {
				DataFileSource src = file.getFileSource();
				sources.add(src);
			}
		}
		return sources;
	}
		

	private static class NullDatasetContext implements DatasetContext {

		public DataFile findFile(DataKey key) throws IOException {
			return null;
		}

		public boolean isOutput() {
			return false;
		}

		public boolean isQueryOnly() {
			return false;
		}

		public void addFile(DataKey key, DataFile file) {
			throw new IllegalStateException("Can't add file in NullDatasetContext");
		}

		public String getFileName(DataKey key) {
			return null;
		}

	}

}

