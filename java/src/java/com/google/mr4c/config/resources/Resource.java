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

package com.google.mr4c.config.resources;

import com.google.mr4c.config.category.Category;
import com.google.mr4c.config.category.HadoopConfig;

public enum Resource implements ResourceInfo {

	CORES(
		"cores",
		Category.HADOOP,
		HadoopConfig.PROP_MIN_CORES,
		HadoopConfig.PROP_MAX_CORES,
		"mapreduce.map.cpu.vcores",
		"yarn.scheduler.maximum-allocation-vcores"
	),

	MEMORY(
		"memory",
		Category.HADOOP,
		HadoopConfig.PROP_MIN_MEMORY,
		HadoopConfig.PROP_MAX_MEMORY,
		"mapreduce.map.memory.mb",
		"yarn.scheduler.maximum-allocation-mb"
	);

	private String m_name;
	private Category m_category;
	private String m_minConf;
	private String m_maxConf;
	private String m_hadoop;
	private String m_maxHadoop;


	private Resource(
		String name,
		Category category,
		String minConf,
		String maxConf,
		String hadoop,
		String maxHadoop
	) {
		m_name = name;
		m_category = category;
		m_minConf = minConf;
		m_maxConf = maxConf;
		m_hadoop = hadoop;
		m_maxHadoop = maxHadoop;
	}

	public String getResourceName() {
		return m_name;
	}

	public Category getConfigCategory() {
		return m_category;
	}

	public String getMinConfigName() {
		return m_minConf;
	}

	public String getMaxConfigName() {
		return m_maxConf;
	}

	public String getHadoopName() {
		return m_hadoop;
	}

	public String getMaxHadoopName() {
		return m_maxHadoop;
	}


}
