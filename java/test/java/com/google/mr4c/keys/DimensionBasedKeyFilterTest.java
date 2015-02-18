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

public class DimensionBasedKeyFilterTest {

	private DataKeyDimension m_dim1;
	private DataKeyDimension m_dim2;
	private DataKeyDimension m_dim3;

	private DataKeyElement m_ele1a;
	private DataKeyElement m_ele1b;
	private DataKeyElement m_ele1c;
	private DataKeyElement m_ele2a;
	private DataKeyElement m_ele2b;
	private DataKeyElement m_ele2c;
	private DataKeyElement m_ele3a;
	private DataKeyElement m_ele3b;
	private DataKeyElement m_ele3c;

	private DataKey m_keyFound;
	private DataKey m_keyNotFound;
	private DataKey m_keyMissingDim;
	private DataKey m_keyExtraDim;

	private BasicElementFilter m_eleFilter1;
	private BasicElementFilter m_eleFilter2;

	private DimensionBasedKeyFilter m_filterExtraMissing;
	private DimensionBasedKeyFilter m_filterExtraNotMissing;
	private DimensionBasedKeyFilter m_filterNotExtraMissing;
	private DimensionBasedKeyFilter m_filterNotExtraNotMissing;

	@Before public void setUp() {
		buildDimensions();
		buildElements();
		buildKeys();
		buildElementFilters();
		buildKeyFilters();
	}

	@Test public void testExtraMissing_KeyFound() {
		assertTrue(m_filterExtraMissing.filter(m_keyFound));
	}

	@Test public void testExtraMissing_KeyNotFound() {
		assertTrue(!m_filterExtraMissing.filter(m_keyNotFound));
	}

	@Test public void testExtraMissing_KeyMissingDim() {
		assertTrue(m_filterExtraMissing.filter(m_keyMissingDim));
	}

	@Test public void testExtraMissing_KeyExtraDim() {
		assertTrue(m_filterExtraMissing.filter(m_keyExtraDim));
	}


	@Test public void testExtraNotMissing_KeyFound() {
		assertTrue(m_filterExtraNotMissing.filter(m_keyFound));
	}

	@Test public void testExtraNotMissing_KeyNotFound() {
		assertTrue(!m_filterExtraNotMissing.filter(m_keyNotFound));
	}

	@Test public void testExtraNotMissing_KeyMissingDim() {
		assertTrue(!m_filterExtraNotMissing.filter(m_keyMissingDim));
	}

	@Test public void testExtraNotMissing_KeyExtraDim() {
		assertTrue(m_filterExtraNotMissing.filter(m_keyExtraDim));
	}


	@Test public void testNotExtraMissing_KeyFound() {
		assertTrue(m_filterNotExtraMissing.filter(m_keyFound));
	}

	@Test public void testNotExtraMissing_KeyNotFound() {
		assertTrue(!m_filterNotExtraMissing.filter(m_keyNotFound));
	}

	@Test public void testNotExtraMissing_KeyMissingDim() {
		assertTrue(m_filterNotExtraMissing.filter(m_keyMissingDim));
	}

	@Test public void testNotExtraMissing_KeyExtraDim() {
		assertTrue(!m_filterNotExtraMissing.filter(m_keyExtraDim));
	}


	@Test public void testNotExtraNotMissing_KeyFound() {
		assertTrue(m_filterNotExtraNotMissing.filter(m_keyFound));
	}

	@Test public void testNotExtraNotMissing_KeyNotFound() {
		assertTrue(!m_filterNotExtraNotMissing.filter(m_keyNotFound));
	}

	@Test public void testNotExtraNotMissing_KeyMissingDim() {
		assertTrue(!m_filterNotExtraNotMissing.filter(m_keyMissingDim));
	}

	@Test public void testNotExtraNotMissing_KeyExtraDim() {
		assertTrue(!m_filterNotExtraNotMissing.filter(m_keyExtraDim));
	}


	@Test(expected=IllegalArgumentException.class)
	public void testAddDimensionTwice() {
		m_filterExtraMissing.addFilter(m_eleFilter1);
	}

	private void buildDimensions() {
		m_dim1 = new DataKeyDimension("dim1");
		m_dim2 = new DataKeyDimension("dim2");
		m_dim3 = new DataKeyDimension("dim3");
	}

	private void buildElements() {
		m_ele1a = new DataKeyElement("val1a", m_dim1);
		m_ele1b = new DataKeyElement("val1b", m_dim1);
		m_ele1c = new DataKeyElement("val1c", m_dim1);
		m_ele2a = new DataKeyElement("val2a", m_dim2);
		m_ele2b = new DataKeyElement("val2b", m_dim2);
		m_ele2c = new DataKeyElement("val2c", m_dim2);
		m_ele3a = new DataKeyElement("val3a", m_dim3);
		m_ele3b = new DataKeyElement("val3b", m_dim3);
		m_ele3c = new DataKeyElement("val3c", m_dim3);
	}

	private void buildKeys() {
		m_keyFound = DataKeyFactory.newKey(m_ele1a, m_ele2b);
		m_keyNotFound = DataKeyFactory.newKey(m_ele1a, m_ele2c);
		m_keyMissingDim = DataKeyFactory.newKey(m_ele1a);
		m_keyExtraDim = DataKeyFactory.newKey(m_ele1a, m_ele2b, m_ele3b);
	}

	void buildElementFilters() {
		m_eleFilter1 = new BasicElementFilter(m_dim1);
		m_eleFilter1.addElements(m_ele1a, m_ele1b);
		m_eleFilter2 = new BasicElementFilter(m_dim2);
		m_eleFilter2.addElements(m_ele2a, m_ele2b);
	} 

	private void buildKeyFilters() {

		m_filterExtraMissing = new DimensionBasedKeyFilter(true, true);
		m_filterExtraMissing.addFilter(m_eleFilter1);
		m_filterExtraMissing.addFilter(m_eleFilter2);

		m_filterExtraNotMissing = new DimensionBasedKeyFilter(true, false);
		m_filterExtraNotMissing.addFilter(m_eleFilter1);
		m_filterExtraNotMissing.addFilter(m_eleFilter2);

		m_filterNotExtraMissing = new DimensionBasedKeyFilter(false, true);
		m_filterNotExtraMissing.addFilter(m_eleFilter1);
		m_filterNotExtraMissing.addFilter(m_eleFilter2);

		m_filterNotExtraNotMissing = new DimensionBasedKeyFilter(false, false);
		m_filterNotExtraNotMissing.addFilter(m_eleFilter1);
		m_filterNotExtraNotMissing.addFilter(m_eleFilter2);
	}
}
