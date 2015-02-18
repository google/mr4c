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

package com.google.mr4c.config.execution;

import com.google.mr4c.config.ConfigDescriptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Properties;

public class ExecutionConfig {

	private ConfigDescriptor algoConfig;
	private Map<String,DatasetConfig> inputs = Collections.synchronizedMap( new HashMap<String,DatasetConfig>() );
	private Map<String,DatasetConfig> outputs = Collections.synchronizedMap( new HashMap<String,DatasetConfig>() );
	private Properties params = new Properties();


	// for gson	
	private ExecutionConfig() {}

	public ExecutionConfig(ConfigDescriptor algoConfig) {
		this.algoConfig = algoConfig;
	}

	public ConfigDescriptor getAlgorithmConfig() {
		return this.algoConfig;
	}

	public void addInputDataset(String name, DatasetConfig dataset) {
		this.inputs.put(name,dataset);
	}

	public DatasetConfig getInputDataset(String name) {
		return this.inputs.get(name);
	}

	public Set<String> getInputDatasetNames() {
		return this.inputs.keySet();
	}

	public void addOutputDataset(String name, DatasetConfig dataset) {
		this.outputs.put(name,dataset);
	}

	public DatasetConfig getOutputDataset(String name) {
		return this.outputs.get(name);
	}

	public Set<String> getOutputDatasetNames() {
		return this.outputs.keySet();
	}

	public Properties getParameters() {
		return this.params;
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		ExecutionConfig config = (ExecutionConfig) obj;
		if ( !algoConfig.equals(config.algoConfig) ) return false;
		if ( !inputs.equals(config.inputs) ) return false;
		if ( !outputs.equals(config.outputs) ) return false;
		if ( !params.equals(config.params) ) return false;
		return true; 
	}

	public int hashCode() {
		return algoConfig.hashCode() +
			inputs.hashCode() +
			outputs.hashCode() +
			params.hashCode();
	}

}
