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

import com.google.common.collect.Lists;

import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.dataset.DatasetTestUtils;
import com.google.mr4c.keys.BasicDataKeyFilter;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class AlgorithmDataTest {

	private AlgorithmData m_algoData1a;
	private AlgorithmData m_algoData1b;
	private AlgorithmData m_algoData2;

	@Before public void setup() throws Exception {
		m_algoData1a = AlgorithmDataTestUtils.buildAlgorithmData1();
		m_algoData1b = AlgorithmDataTestUtils.buildAlgorithmData1();
		m_algoData2 = AlgorithmDataTestUtils.buildAlgorithmData2();
	}

	@Test public void testEquals() {
		assertEquals(m_algoData1a, m_algoData1b);
	}

	@Test public void testNotEqual() {
		assertFalse(m_algoData1a.equals(m_algoData2));
	}

	@Test public void testSlice() {
		Collection<DataKeyFilter> filters = makeFilters(m_algoData1a.getAllInputKeys(),3);
		for ( DataKeyFilter filter : filters) {
			testSlice(filter);
		}
	}

	private void testSlice(DataKeyFilter filter) {
		AlgorithmData slice1 = AlgorithmDataTestUtils.buildAlgorithmData1Slice(filter);
		AlgorithmData slice2 = m_algoData1a.slice(filter);
		assertEquals(slice1, slice2);
	}

	private Collection<DataKeyFilter> makeFilters(Collection<DataKey> keys, int num) {
		Collection<DataKeyFilter> filters = new ArrayList<DataKeyFilter>();
		List<List<DataKey>> parts = Lists.partition(new ArrayList<DataKey>(keys), num);
		for ( List<DataKey> part : parts ) {
			BasicDataKeyFilter filter = new BasicDataKeyFilter();
			filter.addKeys(part);
			filters.add(filter);
		}
		return filters;
	}
		

}
