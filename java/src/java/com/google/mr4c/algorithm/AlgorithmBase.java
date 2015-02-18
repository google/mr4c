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

import com.google.mr4c.config.algorithm.AlgorithmConfig;

import java.io.IOException;

public abstract class AlgorithmBase implements Algorithm {

	private AlgorithmConfig m_config;
	private AlgorithmEnvironment m_env;
	private AlgorithmSchema m_schema;

	protected AlgorithmBase() {}
	
	public AlgorithmConfig getAlgorithmConfig() {
		return m_config;
	}

	public void setAlgorithmConfig(AlgorithmConfig config) {
		m_config = config;
	}

	public AlgorithmEnvironment getAlgorithmEnvironment() {
		return m_env;
	}

	public void setAlgorithmEnvironment(AlgorithmEnvironment env) {
		m_env = env;
	}

	public AlgorithmSchema getAlgorithmSchema() {
		return m_schema;
	}

	public void setAlgorithmSchema(AlgorithmSchema schema) {
		m_schema = schema;
	}

}

