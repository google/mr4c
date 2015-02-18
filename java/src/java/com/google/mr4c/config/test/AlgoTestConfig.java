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

package com.google.mr4c.config.test;

import com.google.mr4c.config.ConfigDescriptor;
import com.google.mr4c.config.diff.DiffConfig;
import com.google.mr4c.config.execution.ExecutionConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

public class AlgoTestConfig {

	private ConfigDescriptor exeConfig;
	private Map<String,DiffConfig> outputs = Collections.synchronizedMap( new HashMap<String,DiffConfig>() );

	// for gson	
	private AlgoTestConfig() {}

	public AlgoTestConfig(ConfigDescriptor exeConfig) {
		this.exeConfig = exeConfig;
	}

	public ConfigDescriptor getExecutionConfig() {
		return this.exeConfig;
	}

	public void addOutputDiff(String name, DiffConfig diff) {
		this.outputs.put(name,diff);
	}

	public DiffConfig getOutputDiff(String name) {
		return this.outputs.get(name);
	}

	public Set<String> getOutputDiffNames() {
		return this.outputs.keySet();
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		AlgoTestConfig config = (AlgoTestConfig) obj;
		if ( !ObjectUtils.equals(exeConfig, config.exeConfig) ) return false;
		if ( !ObjectUtils.equals(outputs, config.outputs) ) return false;
		return true; 
	}

}
