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

package com.google.mr4c.config.diff;

import com.google.mr4c.config.execution.DatasetConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Properties;

import org.apache.commons.lang3.ObjectUtils;

public class DiffConfig {

	private DatasetConfig expected;
	private DatasetConfig actual;
	private DatasetConfig diff;
	private String diffParam;

	public void setExpectedDataset(DatasetConfig dataset) {
		this.expected = dataset;
	}

	public DatasetConfig getExpectedDataset() {
		return this.expected;
	}

	public void setActualDataset(DatasetConfig dataset) {
		this.actual = dataset;
	}

	public DatasetConfig getActualDataset() {
		return this.actual;
	}

	public void setDiffDataset(DatasetConfig dataset) {
		this.diff = dataset;
	}

	public DatasetConfig getDiffDataset() {
		return this.diff;
	}

	public void setDiffParam(String diffParam) {
		this.diffParam = diffParam;
	}

	public String getDiffParam() {
		return this.diffParam;
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		DiffConfig config = (DiffConfig) obj;
		if ( !ObjectUtils.equals(actual, config.actual) ) return false;
		if ( !ObjectUtils.equals(expected, config.expected) ) return false;
		if ( !ObjectUtils.equals(diff, config.diff) ) return false;
		if ( !ObjectUtils.equals(diffParam, config.diffParam) ) return false;
		return true; 
	}

}
