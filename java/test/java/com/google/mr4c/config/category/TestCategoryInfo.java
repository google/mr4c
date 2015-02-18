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

package com.google.mr4c.config.category;

class TestCategoryInfo implements CategoryInfo {

	private String m_name;
	private String m_prop;
	private String m_propPrefix;
	private String m_argPrefix;
	private String m_envVar;
	private boolean m_default;

	TestCategoryInfo(
		String name,
		String envVar
	) {
		this(name, envVar, false);
	}

	TestCategoryInfo(
		String name,
		String envVar,
		boolean isDefault
	) {
		m_name = name;
		m_envVar = envVar;
		m_prop = "mr4ctest." + name;
		m_propPrefix = m_prop + ".";
		m_argPrefix = "-" + name.toUpperCase();
		m_default = isDefault;
	}

	public String getCategoryName() {
		return m_name;
	}

	public String getCategoryProperty() {
		return m_prop;
	}

	public String getArgsPrefix() {
		return m_argPrefix;
	}

	public String getPropertiesPrefix() {
		return m_propPrefix;
	}

	public String getEnvironmentVariable() {
		return m_envVar;
	}

	public boolean isDefaultCategory() {
		return m_default;
	}

}

