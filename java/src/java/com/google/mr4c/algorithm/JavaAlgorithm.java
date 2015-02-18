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

public abstract class JavaAlgorithm extends AlgorithmBase {

	private Set<String> m_requiredInputDatasets;
	private Set<String> m_optionalInputDatasets;
	private Set<String> m_excludedInputDatasets;
	private Set<String> m_outputDatasets;
	private Set<DataKeyDimension> m_expectedDimensions;

	public JavaAlgorithm() {
		m_requiredInputDatasets = Collections.synchronizedSet( new HashSet<String>() );
		m_optionalInputDatasets = Collections.synchronizedSet( new HashSet<String>() );
		m_excludedInputDatasets = Collections.synchronizedSet( new HashSet<String>() );
		m_outputDatasets = Collections.synchronizedSet( new HashSet<String>() );
		m_expectedDimensions = Collections.synchronizedSet( new HashSet<DataKeyDimension>() );
	}


	public void init() {
	}

	public void cleanup() {}

	public Collection<File> getRequiredFiles() {
		// Don't need to return anything here, since all our dependencies are
		// part of MR4C itself, and the actual algorithm jar is handled by
		// MR4CConfig and MR4CGenericOptions
		return Collections.emptyList();
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

	public Collection<File> getGeneratedLogFiles() {
		// Don't need to return anything here because MR4C is already aware
		// of all of the Log4j logs that are generated
		return Collections.<File>emptySet();
	}


	public void addOutputDataset(String name) {
		m_outputDatasets.add(name);
	}

	public void addInputDataset(String name) {
		addInputDataset(name, false, false);
	}

	public void addInputDataset(String name, boolean optional) {
		addInputDataset(name, optional, false);
	}

	public void addExpectedDimension(DataKeyDimension dimension) {
		m_expectedDimensions.add(dimension);
	}

	public void addInputDataset(String name, boolean optional, boolean excludeFromKeyspace) {
		if (optional) {
			m_optionalInputDatasets.add(name);
		} else {
			m_requiredInputDatasets.add(name);
		}
		if (excludeFromKeyspace) {
			m_excludedInputDatasets.add(name);
		}
	}

}
