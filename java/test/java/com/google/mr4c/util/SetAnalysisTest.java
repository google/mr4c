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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class SetAnalysisTest {

	@Test public void testAnalyze() {
		Set<Integer> set1 = toSet(1,2,3);
		Set<Integer> set2 = toSet(2,3,4,5);
		Set<Integer> same = toSet(2,3);
		Set<Integer> only1 = toSet(1);
		Set<Integer> only2 = toSet(4,5);
		SetAnalysis<Integer> analysis = SetAnalysis.analyze(set1,set2);
		assertEquals(same,analysis.getInBothSets());
		assertEquals(only1,analysis.getOnlyInSet1());
		assertEquals(only2,analysis.getOnlyInSet2());
	}

	private Set<Integer> toSet(Integer... vals) {
		return new HashSet<Integer>(Arrays.asList(vals));
	}

}

