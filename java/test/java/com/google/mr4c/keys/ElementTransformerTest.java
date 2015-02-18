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

public class ElementTransformerTest {

	private DataKeyDimension m_dima;
	private DataKeyDimension m_dimb;

	private DataKeyElement m_ele1a;
	private DataKeyElement m_ele1b;
	private DataKeyElement m_ele11a;
	private DataKeyElement m_ele11b;
	private DataKeyElement m_ele2a;
	private DataKeyElement m_ele2b;
	private DataKeyElement m_ele22a;
	private DataKeyElement m_ele22b;
	private DataKeyElement m_ele3a;
	private DataKeyElement m_ele3b;

	private ElementTransformer m_dimOnly;
	private ElementTransformer m_valsOnly;
	private ElementTransformer m_combined;


	@Before public void setUp() {
		buildDimensions();
		buildElements();
		buildDimOnlyTransform();
		buildValsOnlyTransform();
		buildCombinedTransform();
	} 

	@Test public void testDimOnly() {
		DataKeyElement element = m_dimOnly.transformElement(m_ele1a);
		assertEquals(m_ele1b, element);
	}

	@Test public void testValsOnlyFound() {
		DataKeyElement element = m_valsOnly.transformElement(m_ele1a);
		assertEquals(m_ele11a, element);
	}

	@Test public void testValsOnlyNotFound() {
		DataKeyElement element = m_valsOnly.transformElement(m_ele3a);
		assertEquals(m_ele3a, element);
	}

	@Test public void testCombinedFound() {
		DataKeyElement element = m_combined.transformElement(m_ele1a);
		assertEquals(m_ele11b, element);
	}

	@Test public void testCombinedNotFound() {
		DataKeyElement element = m_combined.transformElement(m_ele3a);
		assertEquals(m_ele3b, element);
	}


	@Test(expected=IllegalStateException.class)
	public void testWrongDimension() {
		m_combined.transformElement(m_ele1b);
	}

	private void buildDimensions() {
		m_dima = new DataKeyDimension("dima");
		m_dimb = new DataKeyDimension("dimb");
	}

	private void buildElements() {
		m_ele1a = new DataKeyElement("val1", m_dima);
		m_ele11a = new DataKeyElement("val11", m_dima);
		m_ele1b = new DataKeyElement("val1", m_dimb);
		m_ele11b = new DataKeyElement("val11", m_dimb);
		m_ele2a = new DataKeyElement("val2", m_dima);
		m_ele22a = new DataKeyElement("val22", m_dima);
		m_ele2b = new DataKeyElement("val2", m_dimb);
		m_ele22b = new DataKeyElement("val22", m_dimb);
		m_ele3a = new DataKeyElement("val3", m_dima);
		m_ele3b = new DataKeyElement("val3", m_dimb);
	}

	private void buildDimOnlyTransform() {
		m_dimOnly = new ElementTransformer(m_dima, m_dimb);
	}

	private void buildValsOnlyTransform() {
		m_valsOnly = new ElementTransformer(m_dima);
		m_valsOnly.addValueTransform("val1", "val11");
		m_valsOnly.addValueTransform("val2", "val22");
	}

	private void buildCombinedTransform() {
		m_combined = new ElementTransformer(m_dima, m_dimb);
		m_combined.addValueTransform("val1", "val11");
		m_combined.addValueTransform("val2", "val22");
	}


}
