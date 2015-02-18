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

import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.Category;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AlgorithmEnvironment {

	private MR4CConfig m_bbConf;
	private Map<EnvironmentSet,Properties> m_propSets = new HashMap<EnvironmentSet,Properties>();

	public AlgorithmEnvironment() {
		this(MR4CConfig.getDefaultInstance());
	}

	public AlgorithmEnvironment(MR4CConfig bbConf) {
		m_bbConf = bbConf;
		addMR4CRuntime();
		addJavaSystem();
		addCustom();
		addRaw();
	}

	public Properties getPropertySet(EnvironmentSet envSet) {
		return m_propSets.get(envSet);
	}

	private void addMR4CRuntime() {
		addAsCategory(EnvironmentSet.RUNTIME, Category.RUNTIME);
	}

	private void addJavaSystem() {
		m_propSets.put(EnvironmentSet.JAVA, System.getProperties());
	}

	private void addCustom() {
		addAsCategory(EnvironmentSet.CUSTOM, Category.CUSTOM);
	}

	private void addRaw() {
		addAsCategory(EnvironmentSet.RAW, Category.RAW);
	}

	private void addAsCategory(EnvironmentSet env, Category category) {
		m_propSets.put(env, getCategoryProperties(category));
	}

	private Properties getCategoryProperties(Category category) {
		return m_bbConf.getCategory(category).getProperties(false);
	}

}
