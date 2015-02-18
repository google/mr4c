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

package com.google.mr4c.dataset;

import com.google.mr4c.keys.BasicDataKeyFilter;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;
import com.google.mr4c.keys.DataKeyFilter;
import com.google.mr4c.metadata.MetadataKey;
import com.google.mr4c.metadata.MetadataMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class DatasetTest {

	private Dataset m_dataset1a;
	private Dataset m_dataset1b;
	private Dataset m_dataset2;
	private Collection<Dataset> m_slices;

	@Before public void setup() throws Exception {
		m_dataset1a = DatasetTestUtils.buildDataset1();
		m_dataset1b = DatasetTestUtils.buildDataset1();
		m_dataset2 = DatasetTestUtils.buildDataset2();
		m_slices = DatasetTestUtils.buildDataset1Slices();
	}

	@Test public void testEquals() {
		assertEquals(m_dataset1a, m_dataset1b);
	}

	@Test public void testNotEqual() {
		assertFalse(m_dataset1a.equals(m_dataset2));
	}

	@Test public void testSlice() {
		for ( Dataset slice : m_slices ) {
			testSlice(slice);
		}
	}

	private void testSlice(Dataset slice) {
		Set<DataKey> keys = new HashSet<DataKey>();
		keys.addAll(slice.getAllFileKeys());
		keys.addAll(slice.getAllMetadataKeys());
		BasicDataKeyFilter filter = new BasicDataKeyFilter();
		filter.addKeys(keys);
		Dataset slice2 = m_dataset1a.slice(filter);
		assertEquals(slice, slice2);
	}

	@Test public void testCombine() {
		Dataset combined = Dataset.combineSlices(m_slices);
		assertEquals(m_dataset1a, combined);
	}

	@Test public void testDependentKeys() {
		DataKeyDimension indepDim = new DataKeyDimension("indep");
		DataKeyDimension depDim = new DataKeyDimension("dep");
		DataKey indepKey = DataKeyFactory.newKey(new DataKeyElement("ele1", indepDim));
		DataKey depKey = DataKeyFactory.newKey(new DataKeyElement("ele2", depDim));
		MetadataMap map = new MetadataMap();
		map.getMap().put("key1", new MetadataKey(indepKey));
		map.getMap().put("key2", new MetadataKey(depKey));
		m_dataset1a.addMetadata(indepKey, map);
		Set<DataKey> expected = new HashSet<DataKey>();
		expected.add(depKey);
		Set<DataKey> result = m_dataset1a.getDependentKeys(depDim);
		assertEquals(expected,result);
	}

}
