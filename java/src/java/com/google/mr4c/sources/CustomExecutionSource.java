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
import com.google.mr4c.config.algorithm.AlgorithmConfig;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class CustomExecutionSource implements ExecutionSource {

	private Algorithm m_algo;
	private Map<String,DatasetSource> m_inputs = new HashMap<String,DatasetSource>();
	private Map<String,DatasetSource> m_outputs = new HashMap<String,DatasetSource>();
	private Properties m_params = new Properties();

	public CustomExecutionSource(Algorithm algo) {
		m_algo = algo;
	}

	public AlgorithmConfig getAlgorithmConfig() {
		return m_algo.getAlgorithmConfig();
	}

    public Algorithm getAlgorithm( AlgorithmEnvironment env ) {
        m_algo.setAlgorithmEnvironment( env );
        return m_algo;
    }

	public Algorithm getAlgorithm() {
		return m_algo;
	}

	public DatasetSource getInputDatasetSource(String name) throws IOException {
		if ( !m_inputs.containsKey(name) ) {
			throw new IllegalArgumentException(String.format("No input dataset named [%s]", name));
		}
		return m_inputs.get(name);
	}

	public Set<String> getInputDatasetNames() {
		return m_inputs.keySet();
	}

	public void addInputSource(String name, DatasetSource src) {
		m_inputs.put(name,src);
	}
	
	public DatasetSource getOutputDatasetSource(String name) throws IOException {
		if ( !m_outputs.containsKey(name) ) {
			throw new IllegalArgumentException(String.format("No output dataset named [%s]", name));
		}
		return m_outputs.get(name);
	}

	public Set<String> getOutputDatasetNames() {
		return m_outputs.keySet();
	}

	public void addOutputSource(String name, DatasetSource src) {
		m_outputs.put(name,src);
	}
	
	public Properties getConfigParams() {
		return m_params;
	}

	public Set<String> getOutputDatasetNames(DatasetSource.SourceType type) {
		// not super efficient, but we don't have a lifecycle that allows us to know that nothing else is being added
		Map<DatasetSource.SourceType,Set<String>> outputNameMap = SourceUtils.sortSourceNamesByType(m_outputs);
		Set<String> names = outputNameMap.get(type);
		return names==null ?
			Collections.<String>emptySet() :
			names;
	}

}

