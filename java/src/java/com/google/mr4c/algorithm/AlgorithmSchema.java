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

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.mr4c.keys.DataKeyDimension;

import org.apache.commons.lang3.ObjectUtils;

public class AlgorithmSchema {

	private Set<String> m_inputDatasets;
	private Set<String> m_requiredInputDatasets;
	private Set<String> m_optionalInputDatasets;
	private Set<String> m_excludedInputDatasets;
	private Set<String> m_outputDatasets;
	private Set<DataKeyDimension> m_expectedDimensions;

	public AlgorithmSchema() {
		m_inputDatasets = Collections.synchronizedSet( new HashSet<String>() );
		m_requiredInputDatasets = Collections.synchronizedSet( new HashSet<String>() );
		m_optionalInputDatasets = Collections.synchronizedSet( new HashSet<String>() );
		m_excludedInputDatasets = Collections.synchronizedSet( new HashSet<String>() );
		m_outputDatasets = Collections.synchronizedSet( new HashSet<String>() );
		m_expectedDimensions = Collections.synchronizedSet( new HashSet<DataKeyDimension>() );
	}

	public Collection<String> getInputDatasets() {
		return m_inputDatasets;
	}

	public Collection<String> getRequiredInputDatasets() {
		return m_requiredInputDatasets;
	}

	public Collection<String> getOptionalInputDatasets() {
		return m_optionalInputDatasets;
	}

	public Collection<String> getExcludedInputDatasets() {
		return m_excludedInputDatasets;
	}

	public Collection<String> getOutputDatasets() {
		return m_outputDatasets;
	}

	public Collection<DataKeyDimension> getExpectedDimensions() {
		return m_expectedDimensions;
	}

	public void addInputDataset(String name) {
		addInputDataset(name, false, false);
	}

	public void addInputDataset(String name, boolean optional) {
		addInputDataset(name, optional, false);
	}

	public void addInputDataset(String name, boolean optional, boolean excludeFromKeyspace) {
		m_inputDatasets.add(name);
		if (optional) {
			m_optionalInputDatasets.add(name);
		} else {
			m_requiredInputDatasets.add(name);
		}
		if (excludeFromKeyspace) {
			m_excludedInputDatasets.add(name);
		}
	}

	public void addOutputDataset(String name) {
		m_outputDatasets.add(name);
	}

	public void addExpectedDimension(DataKeyDimension dimension) {
		m_expectedDimensions.add(dimension);
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( obj==null ) return false;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		AlgorithmSchema schema = (AlgorithmSchema) obj;
		if ( !ObjectUtils.equals(m_inputDatasets, schema.m_inputDatasets) ) return false;
		if ( !ObjectUtils.equals(m_requiredInputDatasets, schema.m_requiredInputDatasets) ) return false;
		if ( !ObjectUtils.equals(m_optionalInputDatasets, schema.m_optionalInputDatasets) ) return false;
		if ( !ObjectUtils.equals(m_excludedInputDatasets, schema.m_excludedInputDatasets) ) return false;
		if ( !ObjectUtils.equals(m_outputDatasets, schema.m_outputDatasets) ) return false;
		if ( !ObjectUtils.equals(m_expectedDimensions, schema.m_expectedDimensions) ) return false;
		return true; 
	}

}
