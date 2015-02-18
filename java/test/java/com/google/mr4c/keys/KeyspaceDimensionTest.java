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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class KeyspaceDimensionTest {

	private DataKeyDimension m_dim1;
	private DataKeyDimension m_dim2;

	private DataKeyElement m_ele1;
	private DataKeyElement m_ele2;
	private DataKeyElement m_ele3;

	private List<DataKeyElement> m_eles;

	private KeyspaceDimension m_ksd1a;
	private KeyspaceDimension m_ksd1b;
	private KeyspaceDimension m_ksd2;

	@Before public void setUp() {
		m_dim1 = new DataKeyDimension("dim1");
		m_dim2 = new DataKeyDimension("dim2");

		m_ele1 = new DataKeyElement("ele1", m_dim1);
		m_ele2 = new DataKeyElement("ele2", m_dim1);
		m_ele3 = new DataKeyElement("ele3", m_dim1);

		m_eles = Arrays.asList(m_ele1, m_ele2, m_ele3);

		m_ksd1a = buildKeyspaceDimension1(); 
		m_ksd1b = buildKeyspaceDimension1(); 
		m_ksd2 = buildKeyspaceDimension2(); 
	} 

	@Test
	public void testElements() {
		assertEquals(m_eles, m_ksd1a.getElements());
	}

	@Test
	public void testEquals() {
		assertEquals(m_ksd1a, m_ksd1b);
	}

	@Test
	public void testNotEqual() {
		assertFalse(m_ksd1a.equals(m_ksd2));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testAddElementFail() {
		DataKeyElement element = new DataKeyElement("val", m_dim2);
		m_ksd1a.addElement(element);
	}

	private KeyspaceDimension buildKeyspaceDimension1() {
		KeyspaceDimension ksd = new KeyspaceDimension(m_dim1);
		Collection<DataKeyElement> elements = Arrays.asList(m_ele1, m_ele2, m_ele3);
		ksd.addElements(elements);
		return ksd;
	}

	private KeyspaceDimension buildKeyspaceDimension2() {
		KeyspaceDimension ksd = new KeyspaceDimension(m_dim1);
		Collection<DataKeyElement> elements = Arrays.asList(m_ele1, m_ele2);
		ksd.addElements(elements);
		return ksd;
	}

}
