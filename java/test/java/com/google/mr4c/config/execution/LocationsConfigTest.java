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

package com.google.mr4c.config.execution;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.*;
import static org.junit.Assert.*;

public class LocationsConfigTest {

	private LocationsConfig m_locList1a;
	private LocationsConfig m_locList1b;
	private LocationsConfig m_locList2;
	private LocationsConfig m_locMap1a;
	private LocationsConfig m_locMap1b;
	private LocationsConfig m_locMap2;

	@Before public void setup() throws Exception {
		m_locList1a = new LocationsConfig(buildList(1));
		m_locList1b = new LocationsConfig(buildList(1));
		m_locList2 = new LocationsConfig(buildList(2));
		m_locMap1a = new LocationsConfig(buildMap(1));
		m_locMap1b = new LocationsConfig(buildMap(1));
		m_locMap2 = new LocationsConfig(buildMap(2));
	}

	@Test public void testEquals() {
		assertEquals(m_locList1a, m_locList1b);
		assertEquals(m_locMap1a, m_locMap1b);
	}

	@Test public void testNotEqual() {
		assertFalse(m_locList1a.equals(m_locList2));
		assertFalse(m_locMap1a.equals(m_locMap2));
		assertFalse(m_locList2.equals(m_locMap2));
	}

	private Map<String,URI> buildMap(int index) {
		Map<String,URI> map = new HashMap<String,URI>();
		map.put("key"+index, URI.create("file:///whatever/"+index));
		return map;
	}

	private List<URI> buildList(int index) {
		return Arrays.asList(
			URI.create("file:///whatever/"+index)
		);
	}
		
}

