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

import org.junit.*;
import static org.junit.Assert.*;

public class DataKeyComparatorTest {

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

	private DataKeyComparator m_comparator = new DataKeyComparator();

	@Before public void setUp() {
		buildDimensions();
		buildElements();

	}

	@Test public void testEqual() {
		DataKey key = DataKeyFactory.newKey(m_ele1a, m_ele2a, m_ele3a);
		assertEquals(0, m_comparator.compare(key,key));
	}
		
	@Test public void testSameDimensions() {
		DataKey keya = DataKeyFactory.newKey(m_ele1a, m_ele2a, m_ele3a);
		DataKey keyb = DataKeyFactory.newKey(m_ele1b, m_ele2b, m_ele3b);
		assertEquals(-1, m_comparator.compare(keya,keyb));
	}
		
	@Test public void testSameDimensionsPlusExtra() {
		DataKey keya = DataKeyFactory.newKey(m_ele1a, m_ele2a);
		DataKey keyb = DataKeyFactory.newKey(m_ele1b, m_ele2b, m_ele3b, m_ele4b);
		assertEquals(-1, m_comparator.compare(keya,keyb));
	}
		 
	@Test public void testDifferentDimensions() {
		DataKey keya = DataKeyFactory.newKey(m_ele1a, m_ele3a);
		DataKey keyb = DataKeyFactory.newKey(m_ele1a, m_ele2b, m_ele3b, m_ele4b);
		assertEquals(1, m_comparator.compare(keya,keyb));
	}
		 
	@Test public void testEmpty() {
		DataKey keya = DataKeyFactory.newKey();
		DataKey keyb = DataKeyFactory.newKey();
		assertEquals(0, m_comparator.compare(keya,keyb));
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
