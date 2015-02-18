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

package com.google.mr4c.keys;

import java.util.HashSet;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class DataKeyUtilsTest {

	private DataKeyDimension m_dim1;
	private DataKeyDimension m_dim2;
	private DataKeyDimension m_dim3;
	private DataKeyDimension m_dim4;

	private DataKeyElement m_ele1a;
	private DataKeyElement m_ele1b;
	private DataKeyElement m_ele2a;
	private DataKeyElement m_ele2b;
	private DataKeyElement m_ele3a;
	private DataKeyElement m_ele3b;
	private DataKeyElement m_ele4a;
	private DataKeyElement m_ele4b;


	@Before public void setUp() {
		buildDimensions();
		buildElements();
	}

	@Test public void testNarrowsEqual() {
		DataKey key = DataKeyFactory.newKey(m_ele1a, m_ele2a, m_ele3a);
		assertTrue(DataKeyUtils.narrows(key,key));
	}
		
	@Test public void testNarrowsTrue() {
		DataKey src = DataKeyFactory.newKey(m_ele1a, m_ele2a);
		DataKey target = DataKeyFactory.newKey(m_ele1a, m_ele2a, m_ele3a);
		assertTrue(DataKeyUtils.narrows(src,target));
		assertFalse(DataKeyUtils.narrows(target,src));
	}
		
	@Test public void testNarrowsOverlap() {
		DataKey src = DataKeyFactory.newKey(m_ele1a, m_ele2a, m_ele3b);
		DataKey target = DataKeyFactory.newKey(m_ele1a, m_ele2a, m_ele3a);
		assertFalse(DataKeyUtils.narrows(src,target));
		assertFalse(DataKeyUtils.narrows(target,src));
	}

	@Test public void testFilterElements() {
		BasicElementFilter filter = new BasicElementFilter(m_dim1);
		Set<DataKeyElement> all = new HashSet<DataKeyElement>();
		all.add(m_ele1a);
		all.add(m_ele1b);
		Set<DataKeyElement> expected = new HashSet<DataKeyElement>();
		expected.add(m_ele1a);
		filter.addElement(m_ele1a);
		Set<DataKeyElement> result = DataKeyUtils.filter(filter,all);
		assertEquals(expected, result);
	}

	@Test public void testFilterKeys() {
		DataKey key1 = DataKeyFactory.newKey(m_ele1a, m_ele2a);
		DataKey key2 = DataKeyFactory.newKey(m_ele1a, m_ele2a, m_ele3b);
		DataKey key3 = DataKeyFactory.newKey(m_ele2b);
		BasicDataKeyFilter filter = new BasicDataKeyFilter();
		Set<DataKey> all = new HashSet<DataKey>();
		all.add(key1);
		all.add(key2);
		all.add(key3);
		Set<DataKey> expected = new HashSet<DataKey>();
		expected.add(key1);
		expected.add(key2);
		filter.addKey(key1);
		filter.addKey(key2);
		Set<DataKey> result = DataKeyUtils.filter(filter,all);
		assertEquals(expected, result);
	}
		
				


	private void buildDimensions() {
		m_dim1 = new DataKeyDimension("dim1");
		m_dim2 = new DataKeyDimension("dim2");
		m_dim3 = new DataKeyDimension("dim3");
		m_dim4 = new DataKeyDimension("dim4");
	}

	private void buildElements() {
		m_ele1a = new DataKeyElement("ele1a", m_dim1);
		m_ele1b = new DataKeyElement("ele1b", m_dim1);
		m_ele2a = new DataKeyElement("ele2a", m_dim2);
		m_ele2b = new DataKeyElement("ele2b", m_dim2);
		m_ele3a = new DataKeyElement("ele3a", m_dim3);
		m_ele3b = new DataKeyElement("ele3b", m_dim3);
		m_ele4a = new DataKeyElement("ele4a", m_dim4);
		m_ele4b = new DataKeyElement("ele4b", m_dim4);
	}

}
