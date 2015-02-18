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

public class DataKeyElementTest {

	private DataKeyDimension m_dim1;
	private DataKeyDimension m_dim3;
	private DataKeyElement m_ele1a;
	private DataKeyElement m_ele1b;
	private DataKeyElement m_ele2; // different id
	private DataKeyElement m_ele3; // different dim

	@Before public void setUp() {
		m_dim1 = buildDimension1(); 
		m_dim3 = buildDimension3(); 
		m_ele1a = buildElement1(); 
		m_ele1b = buildElement1(); 
		m_ele2 = buildElement2();
		m_ele3 = buildElement3();
	} 

	@Test public void testEquals() {
		assertEquals(m_ele1a, m_ele1b);
	}

	@Test public void testNotEqualId() {
		assertFalse(m_ele1a.equals(m_ele2));
	}

	@Test public void testNotEqualDim() {
		assertFalse(m_ele1a.equals(m_ele3));
	}

	@Test public void testCompare() {
		assertEquals(0, m_ele1a.compareTo(m_ele1b));
		assertEquals(-1, m_ele1a.compareTo(m_ele2));
		assertEquals(1, m_ele2.compareTo(m_ele1a));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testCompareFail() {
		m_ele1a.compareTo(m_ele3);
	}

	private DataKeyDimension buildDimension1() {
		return new DataKeyDimension("dim1");
	}

	private DataKeyDimension buildDimension3() {
		return new DataKeyDimension("dim3");
	}

	private DataKeyElement buildElement1() {
		return new DataKeyElement("ele1", m_dim1);
	}

	private DataKeyElement buildElement2() {
		return new DataKeyElement("ele2", m_dim1);
	}

	private DataKeyElement buildElement3() {
		return new DataKeyElement("ele1", m_dim3);
	}


}
