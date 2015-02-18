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

public class BasicElementFilterTest {

	private BasicElementFilter m_filter;
	private DataKeyDimension m_dim1;
	private DataKeyDimension m_dim2;
	private DataKeyElement m_ele1a;
	private DataKeyElement m_ele1b;
	private DataKeyElement m_ele1c;
	private DataKeyElement m_ele2; 

	@Before public void setUp() {
		m_dim1 = new DataKeyDimension("dim1");
		m_dim2 = new DataKeyDimension("dim2");
		m_ele1a = new DataKeyElement("val1a", m_dim1);
		m_ele1b = new DataKeyElement("val1b", m_dim1);
		m_ele1c = new DataKeyElement("val1c", m_dim1);
		m_ele2 = new DataKeyElement("val2", m_dim2);
		m_filter = new BasicElementFilter(m_dim1);
		m_filter.addElements(m_ele1a, m_ele1b);
	} 

	@Test public void testFound() {
		assertTrue(m_filter.filter(m_ele1a));
	}

	@Test public void testNotFound() {
		assertTrue(!m_filter.filter(m_ele1c));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testAddWrongDim() {
		m_filter.addElement(m_ele2);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testFilterWrongDim() {
		m_filter.filter(m_ele2);
	}

}
