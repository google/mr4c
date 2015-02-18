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

class TestResourceInfo implements ResourceInfo {

	private String m_name;
	private Category m_category;
	private String m_minConf;
	private String m_maxConf;
	private String m_hadoop;
	private String m_maxHadoop;

	TestResourceInfo(
		String name,
		Category category
	) {
		m_name = name;
		m_category = category;
		m_minConf = name + ".min";
		m_maxConf = name + ".max";
		m_hadoop = "hadoop." + name;
		m_maxHadoop = m_hadoop + ".max";
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

