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

package com.google.mr4c.algorithm;

import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyFilter;
import com.google.mr4c.keys.Keyspace;
import com.google.mr4c.sources.DataFileSource;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class AlgorithmData {

	private Properties m_config;
	private Keyspace m_keyspace;
	private Map<String,Dataset> m_inputDatasets = Collections.synchronizedMap( new HashMap<String,Dataset>() );
	private Map<String,Dataset> m_outputDatasets = Collections.synchronizedMap( new HashMap<String,Dataset>() );
	private Set<String> m_excludedInputs = Collections.synchronizedSet( new HashSet<String>() );

	public AlgorithmData(Keyspace keyspace, Properties config) {
		m_keyspace = keyspace;
		m_config = config;
	}

	public AlgorithmData() {
		this(new Keyspace(), new Properties());
	}

	public Properties getConfig() {
		return m_config;
	}

	public void setConfig(Properties config) {
		m_config = config;
	}

	public Keyspace getKeyspace() {
		return m_keyspace;
	}

	public void setKeyspace(Keyspace keyspace) {
		m_keyspace = keyspace;
	}

	public void generateKeyspaceFromInputDatasets() {
		m_keyspace.addKeys(getAllInputKeys(true));
	}

	public Set<DataKey> getAllInputKeys() {
		return getAllInputKeys(false);
	}

	private Set<DataKey> getAllInputKeys(boolean exclude) {
		Set<DataKey> keys = new HashSet<DataKey>();
		for ( String name : m_inputDatasets.keySet() ) {
			if ( exclude && m_excludedInputs.contains(name) ) {
				continue;
			}
			Dataset dataset = m_inputDatasets.get(name);
			keys.addAll(dataset.getAllFileKeys());
			keys.addAll(dataset.getAllMetadataKeys());
		}
		return keys;
	}

	public Dataset getInputDataset(String name) {
		if ( !hasInputDataset(name) ) {
			throw new IllegalArgumentException(String.format("No input dataset named [%s]", name));
		}
		return m_inputDatasets.get(name);
	}

	public boolean hasInputDataset(String name) {
		return m_inputDatasets.containsKey(name);
	}

	public Set<String> getInputDatasetNames() {
		return m_inputDatasets.keySet();
	}

	public void addInputDataset(String name, Dataset dataset) {
		if ( m_inputDatasets.containsKey(name) ) {
			throw new IllegalStateException(String.format("Already have input dataset named [%s]", name));
		}
		m_inputDatasets.put(name,dataset);
	}

	public void excludeInputDatasetFromKeyspace(String name) {
		m_excludedInputs.add(name);
	}

	public Set<String> getExcludedInputDatasets() {
		return m_excludedInputs;
	}

	public Dataset getOutputDataset(String name) {
		if ( !hasOutputDataset(name) ) {
			throw new IllegalArgumentException(String.format("No output dataset named [%s]", name));
		}
		return m_outputDatasets.get(name);
	}

	public boolean hasOutputDataset(String name) {
		return m_outputDatasets.containsKey(name);
	}

	public Set<String> getOutputDatasetNames() {
		return m_outputDatasets.keySet();
	}

	public void addOutputDataset(String name, Dataset dataset) {
		if ( m_outputDatasets.containsKey(name) ) {
			throw new IllegalStateException(String.format("Already have output dataset named [%s]", name));
		}
		m_outputDatasets.put(name,dataset);
	}

	public void releaseInputs() {
		for ( Dataset dataset : m_inputDatasets.values() ) {
			dataset.release();
		}
	}

	public void releaseOutputs() {
		for ( Dataset dataset : m_outputDatasets.values() ) {
			dataset.release();
		}
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		AlgorithmData algoData = (AlgorithmData) obj;
		if ( !m_keyspace.equals(algoData.m_keyspace) ) return false;
		if ( !m_config.equals(algoData.m_config) ) return false;
		if ( !m_inputDatasets.equals(algoData.m_inputDatasets) ) return false;
		if ( !m_outputDatasets.equals(algoData.m_outputDatasets) ) return false;
		if ( !m_excludedInputs.equals(algoData.m_excludedInputs) ) return false;
		return true; 
	}

	public int hashCode() {
		// this is expensive, just doing because we overrode equals
		// this class shouldn't be used as a key to begin with
		return m_keyspace.hashCode() +
			m_config.hashCode() +
			m_inputDatasets.hashCode() +
			m_outputDatasets.hashCode();
	}

	public AlgorithmData slice(DataKeyFilter filter) {
		AlgorithmData slice = new AlgorithmData();
		for ( String excluded : m_excludedInputs ) {
			slice.excludeInputDatasetFromKeyspace(excluded);
		}
		for ( String name : getInputDatasetNames() ) {
			Dataset dataset = getInputDataset(name);
			slice.addInputDataset(name, dataset.slice(filter));
		}
		for ( String name : getOutputDatasetNames() ) {
			Dataset dataset = getOutputDataset(name);
			slice.addOutputDataset(name, dataset.slice(filter));
		}
		Properties config = slice.getConfig();
		config.putAll(getConfig());
		slice.generateKeyspaceFromInputDatasets();
		return slice;
	}

	public Set<DataKey> getDependentKeys(DataKeyDimension dim) {
		Set<DataKey> keys = new HashSet<DataKey>();
		for ( String name : getInputDatasetNames() ) {
			Dataset dataset = getInputDataset(name);
			keys.addAll(dataset.getDependentKeys(dim));
		}
		return keys;
	}

	public Collection<DataFileSource> getInputDataFileSources(boolean exclude) {
		Collection<DataFileSource> sources = new ArrayList<DataFileSource>();
		for ( String name : getInputDatasetNames() ) {
			if ( exclude && m_excludedInputs.contains(name) ) {
				continue;
			}
			Dataset dataset = getInputDataset(name);
			sources.addAll(dataset.getDataFileSources());
		}
		return sources;
	}

	public Set<DataKey> getExcludedInputDatasetKeys() {
		Set<DataKey> keys = new HashSet<DataKey>();
		for ( String name : m_excludedInputs ) {
			Dataset dataset = getInputDataset(name);
			keys.addAll(dataset.getAllFileKeys());
			keys.addAll(dataset.getAllMetadataKeys());
		}
		return keys;
	}

}

