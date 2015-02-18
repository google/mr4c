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

import java.util.Properties;

class TestCategorySystemData implements CategorySystemData {

	private Properties m_fromDep;
	private Properties m_fromSys;
	private Properties m_fromExt;

	TestCategorySystemData(
		Properties fromDep,
		Properties fromSys,
		Properties fromExt
	) {
		m_fromDep = fromDep;
		m_fromSys = fromSys;
		m_fromExt = fromExt;
	}

	public Properties getPropertiesFromDeprecatedNames() {
		return m_fromDep;
	}

	public Properties getPropertiesFromSystemProperties() {
		return m_fromSys;
	}

	public Properties getPropertiesFromExternalNames() {
		return m_fromExt;
	}

}

