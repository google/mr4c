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

import com.google.mr4c.algorithm.Algorithm;
import com.google.mr4c.algorithm.AlgorithmEnvironment;
import com.google.mr4c.algorithm.Algorithms;
import com.google.mr4c.config.ConfigDescriptor;
import com.google.mr4c.config.ConfigLoader;
import com.google.mr4c.config.algorithm.AlgorithmConfig;
import com.google.mr4c.config.execution.DatasetConfig;
import com.google.mr4c.config.execution.ExecutionConfig;
import com.google.mr4c.content.ContentFactories;
import com.google.mr4c.serialize.ConfigSerializer;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.serialize.param.ParameterizedConfigSerializer;
import com.google.mr4c.sources.DatasetSource;
import com.google.mr4c.sources.DatasetSources;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;

public class ConfiguredExecutionSource implements ExecutionSource {

	private ConfigDescriptor m_confFile;
	private ExecutionConfig m_exeConfig;
	private AlgorithmConfig m_algoConfig;
	private Algorithm m_algorithm;
	private Map<String,DatasetSource> m_inputSrcs = new HashMap<String,DatasetSource>();
	private Map<String,DatasetSource> m_outputSrcs = new HashMap<String,DatasetSource>();
	private Map<DatasetSource.SourceType,Set<String>> m_outputNameMap;

	protected static final Logger s_log = MR4CLogging.getLogger(ConfiguredExecutionSource.class);

	public ConfiguredExecutionSource(ConfigDescriptor confFile) {
		m_confFile = confFile;
	}

	public ConfiguredExecutionSource(URI confFile) {
		this(new ConfigDescriptor(confFile));
	}

	public synchronized void loadConfigs() throws IOException {
		loadExecutionConfig();
		loadAlgorithmConfig();
		populateSourceMaps();
	}

	private void loadExecutionConfig() throws IOException {
		ConfigSerializer ser = SerializerFactories.getSerializerFactory("application/json").createConfigSerializer(); // assume json config for now
		ser = new ParameterizedConfigSerializer(ser);
		ConfigLoader loader = new ConfigLoader(m_confFile, "execution config", s_log);
		Reader reader = loader.load();
		try {
			m_exeConfig = ser.deserializeExecutionConfig(reader);
		} finally {
			reader.close();
		}
	}

	// Doing this because we need to be able to sort by type
	// Can't do that with a lazy load approach
	private void populateSourceMaps() throws IOException {
		for (String name : getInputDatasetNames() ) {
			getInputDatasetSource(name);
		}
		for (String name : getOutputDatasetNames() ) {
			getOutputDatasetSource(name);
		}
		m_outputNameMap = SourceUtils.sortSourceNamesByType(m_outputSrcs);
	}

	private void loadAlgorithmConfig() throws IOException {
		ConfigDescriptor algoDesc = m_exeConfig.getAlgorithmConfig();
		m_algoConfig = Algorithms.getAlgorithmConfig(algoDesc);
	}

	public AlgorithmConfig getAlgorithmConfig() {
		return m_algoConfig;
	}

    public Algorithm getAlgorithm() throws IOException {
        return getAlgorithm( new AlgorithmEnvironment() );
    }

	public synchronized Algorithm getAlgorithm( AlgorithmEnvironment env ) throws IOException {
		if ( m_algorithm==null ) {
			m_algorithm = Algorithms.getAlgorithm(m_algoConfig, env );
		}
		return m_algorithm;
	}

	public synchronized DatasetSource getInputDatasetSource(String name) throws IOException {
		DatasetSource src = m_inputSrcs.get(name);
		if ( src==null ) {
			DatasetConfig datasetConfig = m_exeConfig.getInputDataset(name);
			if ( datasetConfig==null ) {
				throw new IllegalArgumentException(String.format("No source config for input dataset named [%s]", name));
			}
			src = DatasetSources.getDatasetSource(datasetConfig);
			m_inputSrcs.put(name, src);
		}
		return src;
	}

	public Set<String> getInputDatasetNames() {
		return m_exeConfig.getInputDatasetNames();
	}
	
	public synchronized DatasetSource getOutputDatasetSource(String name) throws IOException {
		DatasetSource src = m_outputSrcs.get(name);
		if ( src==null ) {
			DatasetConfig datasetConfig = m_exeConfig.getOutputDataset(name);
			if ( datasetConfig==null ) {
				throw new IllegalArgumentException(String.format("No source config for output dataset named [%s]", name));
			}
			src = DatasetSources.getDatasetSource(datasetConfig);
			m_outputSrcs.put(name, src);
		}
		return src;
	}

	public Set<String> getOutputDatasetNames() {
		return m_exeConfig.getOutputDatasetNames();
	}

	public Set<String> getOutputDatasetNames(DatasetSource.SourceType type) {
		Set<String> names = m_outputNameMap.get(type);
		return names==null ?
			Collections.<String>emptySet() :
			names;
	}

	public Properties getConfigParams() {
		return m_exeConfig.getParameters();
	}

}

