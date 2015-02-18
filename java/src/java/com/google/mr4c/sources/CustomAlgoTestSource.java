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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CustomAlgoTestSource implements AlgoTestSource {

	private ExecutionSource m_exeSrc;
	private Map<String,DiffSource> m_outputs = new HashMap<String,DiffSource>();

	public CustomAlgoTestSource(ExecutionSource exeSrc) {
		m_exeSrc = exeSrc;
	}

	public ExecutionSource getExecutionSource() {
		return m_exeSrc;
	}

	public DiffSource getOutputDiffSource(String name) {
		if ( !m_outputs.containsKey(name) ) {
			throw new IllegalArgumentException(String.format("No output dataset named [%s]", name));
		}
		return m_outputs.get(name);
	}

	public Set<String> getOutputDiffNames() {
		return m_outputs.keySet();
	}

	public void addOutputSource(String name, DiffSource src) {
		m_outputs.put(name,src);
	}
	
}

