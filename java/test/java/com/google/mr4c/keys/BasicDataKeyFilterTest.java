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

public class BasicDataKeyFilterTest {

	private DataKeyDimension m_dima;
	private DataKeyDimension m_dimb;

	private DataKey m_key1;
	private DataKey m_key2;
	private DataKey m_key3;

	private BasicDataKeyFilter m_filter;


	@Before public void setUp() {
		buildDimensions();
		buildKeys();
		buildFilter();
	} 

	@Test public void testFound() {
		assertTrue(m_filter.filter(m_key1));
	}

	@Test public void testNotFound() {
		assertTrue(!m_filter.filter(m_key3));
	}

	private void buildDimensions() {
		m_dima = new DataKeyDimension("dima");
		m_dimb = new DataKeyDimension("dimb");
	}

	private void buildKeys() {

		m_key1 = DataKeyFactory.newKey( new DataKeyElement("val1", m_dima) );
	
		m_key2 = DataKeyFactory.newKey(
			new DataKeyElement("val1", m_dima), 
			new DataKeyElement("val3", m_dimb) 
		);

		m_key3 = DataKeyFactory.newKey(
			new DataKeyElement("val1", m_dima), 
			new DataKeyElement("val4", m_dimb) 
		);

	}

	private void buildFilter() {
		m_filter = new BasicDataKeyFilter();
		m_filter.addKeys(m_key1, m_key2);
	}

}
