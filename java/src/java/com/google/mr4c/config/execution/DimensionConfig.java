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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class DimensionConfig {

	private String name;
	private String mapTo;
	private List<ValueConfig> values = new ArrayList<ValueConfig>();

	public DimensionConfig(String name, String mapTo) {
		this(name);
		this.mapTo = mapTo;
	}

	public DimensionConfig(String name) {
		this.name = name;
	}
		
	private DimensionConfig() {} // for gson

	public String getName() {
		return this.name;
	}

	public String getMapTo() {
		return this.mapTo;
	}

	public void addValue(ValueConfig valConf) {
		this.values.add(valConf);
	}

	public List<ValueConfig> getValues() {
		return this.values;
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		DimensionConfig config = (DimensionConfig) obj;
		if ( !name.equals(config.name) ) return false;
		if ( !StringUtils.equals(mapTo, config.mapTo) ) return false;
		if ( !ObjectUtils.equals(values, config.values) ) return false;
		return true; 
	}

	public int hashCode() {
		// this isn't a key, don't bother getting involved
		return name.hashCode();
	}

}

