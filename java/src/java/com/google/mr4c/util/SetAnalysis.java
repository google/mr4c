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

package com.google.mr4c.util;

import java.util.HashSet;
import java.util.Set;

public class SetAnalysis<T> {

	private Set<T> m_set1;
	private Set<T> m_set2;
	private Set<T> m_only1;
	private Set<T> m_only2;
	private Set<T> m_both;


	public Set<T> getSet1() {
		return m_set1;
	}

	public Set<T> getSet2() {
		return m_set2;
	}

	public Set<T> getOnlyInSet1() {
		return m_only1;
	}

	public Set<T> getOnlyInSet2() {
		return m_only2;
	}

	public Set<T> getInBothSets() {
		return m_both;
	}

	public static <T> SetAnalysis<T> analyze(Set<T> set1, Set<T> set2) {
		SetAnalysis<T> analysis = new SetAnalysis<T>(set1, set2);
		analysis.analyze();
		return analysis;
	}

	private SetAnalysis(Set<T> set1, Set<T> set2) {
		m_set1 = set1;
		m_set2 = set2;
	}

	private void analyze() {
		m_only1 = new HashSet<T>();
		m_only1.addAll(m_set1);
		m_only1.addAll(m_set2);
		m_only1.removeAll(m_set2);

		m_only2 = new HashSet<T>();
		m_only2.addAll(m_set1);
		m_only2.addAll(m_set2);
		m_only2.removeAll(m_set1);

		m_both = new HashSet<T>();
		m_both.addAll(m_set1);
		m_both.retainAll(m_set2);
	}

}
