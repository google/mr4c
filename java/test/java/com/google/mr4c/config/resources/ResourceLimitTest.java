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

package com.google.mr4c.config.resources;

import org.apache.hadoop.conf.Configuration;

import org.junit.*;
import static org.junit.Assert.*;

public class ResourceLimitTest {

	private Resource m_resource = Resource.CORES;
	private int m_value = 8;
	private LimitSource m_source = LimitSource.CLUSTER;
	private ResourceLimit m_limit;

	@Before public void setup() throws Exception {
		m_limit = buildLimit();
	}

	@Test public void testEquals() {
		assertEquals(m_limit, buildLimit());
	}

	@Test public void testNotEqualResource() {
		ResourceLimit limit = new ResourceLimit(Resource.MEMORY, m_value, m_source);
		assertFalse(m_limit.equals(limit));
	}

	@Test public void testNotEqualValue() {
		ResourceLimit limit = new ResourceLimit(m_resource, 64, m_source);
		assertFalse(m_limit.equals(limit));
	}

	@Test public void testNotEqualSource() {
		ResourceLimit limit = new ResourceLimit(m_resource, m_value, LimitSource.CONFIG);
		assertFalse(m_limit.equals(limit));
	}

	@Test public void testConfigurationUpdate() {
		Configuration conf = new Configuration(false);
		m_limit.applyTo(conf);
		ResourceLimit expected = new ResourceLimit(m_resource, m_value, LimitSource.CONFIG);
		ResourceLimit limit = ResourceLimit.extractFrom(m_resource, conf);
		assertEquals(expected, limit);
	}

	@Test public void testConfigurationUpdateNoLimit() {
		Configuration conf = new Configuration(false);
		assertNull(ResourceLimit.extractFrom(m_resource, conf));
	}

	private ResourceLimit buildLimit() {
		return new ResourceLimit(m_resource, m_value, m_source);
	}
}



