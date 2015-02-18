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

package com.google.mr4c.sources;

import com.google.mr4c.sources.DiffSource.DiffOutput;

import java.util.HashMap;
import java.util.Map;

public class CustomDiffSource implements DiffSource {

	private DatasetSource m_expected;
	private DatasetSource m_actual;
	private Map<DiffOutput,DatasetSource> m_outputs = new HashMap<DiffOutput,DatasetSource>();

	public DatasetSource getExpectedDatasetSource() {
		return m_expected;
	}

	public void setExpectedDatasetSource(DatasetSource expected) {
		m_expected = expected;
	}

	public DatasetSource getActualDatasetSource() {
		return m_actual;
	}

	public void setActualDatasetSource(DatasetSource actual) {
		m_actual = actual;
	}

	public DatasetSource getOutputDatasetSource(DiffOutput output) {
		if ( !m_outputs.containsKey(output) ) {
			throw new IllegalArgumentException(String.format("No output dataset for %s", output));
		}
		return m_outputs.get(output);
	}

	public void addOutputSource(DiffOutput output, DatasetSource src) {
		m_outputs.put(output,src);
	}
	

}

