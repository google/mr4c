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

import com.google.mr4c.config.category.MR4CConfig;

import org.apache.hadoop.conf.Configuration;

import org.junit.*;
import static org.junit.Assert.*;

public class ResourceRequestTest {

	private Resource m_resource = Resource.CORES;
	private int m_min = 5;
	private int m_max = 10;
	private ResourceRequest m_request;

	@Before public void setup() throws Exception {
		m_request = buildRequest();
	}

	@Test public void testEquals() {
		assertEquals(m_request, buildRequest());
	}

	@Test public void testNotEqualResource() {
		ResourceRequest request = new ResourceRequest(Resource.MEMORY, m_min, m_max);
		assertFalse(m_request.equals(request));
	}

	@Test public void testNotEqualMin() {
		ResourceRequest request = new ResourceRequest(m_resource, 7, m_max);
		assertFalse(m_request.equals(request));
	}

	@Test public void testNotEqualMax() {
		ResourceRequest request = new ResourceRequest(m_resource, m_min, 25);
		assertFalse(m_request.equals(request));
	}

	@Test(expected=IllegalStateException.class)
	public void testMinMaxNull() {
		ResourceRequest request = new ResourceRequest(m_resource, null, null);
	}

	@Test(expected=IllegalStateException.class)
	public void testMinGreaterThanMax() {
		ResourceRequest request = new ResourceRequest(m_resource, 15, 10);
	}

	@Test public void testAddLimit() {
		ResourceLimit confLimit = new ResourceLimit(m_resource, 10, LimitSource.CONFIG);
		ResourceLimit clusterLimit = new ResourceLimit(m_resource, 12, LimitSource.CLUSTER);
		ResourceLimit algoLimit = new ResourceLimit(m_resource, 15, LimitSource.ALGORITHM);
		m_request.addLimit(clusterLimit);
		assertEquals(clusterLimit, m_request.getLimit());
		m_request.addLimit(confLimit);
		assertEquals(confLimit, m_request.getLimit());
		m_request.addLimit(algoLimit);
		assertEquals(confLimit, m_request.getLimit());
	}

	@Test public void testResolve() {
		doResolveTest(5, null, null, 5, false);
		doResolveTest(null, 10, null, 10, false);
		doResolveTest(5, 10, null, 10, false);
		doResolveTest(5, null, 15, 5, false);
		doResolveTest(null, 10, 15, 10, false);
		doResolveTest(5, 10, 15, 10, false);
		doResolveTest(null, 20, 15, 15, true);
		doResolveTest(5, 20, 15, 15, true);
	}

	@Test(expected=IllegalStateException.class)
	public void testResolveMinGreaterThanLimit() {
		doResolveTest(15, 20, 10, 10, true);
	}

	private void doResolveTest(
		Integer min,
		Integer max,
		Integer limit,
		int expectedActual,
		boolean expectedLimited
	) {
		ResourceRequest request = new ResourceRequest(m_resource, min, max);
		if ( limit!=null ) {
			ResourceLimit resLimit = new ResourceLimit(m_resource, limit, LimitSource.CONFIG);
			request.addLimit(resLimit);
		}
		request.resolve();
		assertEquals((Integer)expectedActual, (Integer)request.getActual());
		assertEquals(expectedLimited, request.isLimited());
	}

	@Test public void testMR4CConfigUpdate() {
		MR4CConfig conf = new MR4CConfig(false);
		conf.initStandardCategories();
		m_request.applyTo(conf);
		ResourceRequest request = ResourceRequest.extractFrom(m_resource, conf);
		assertEquals(m_request, request);

	}

	@Test public void testMR4CConfigUpdateNoRequest() {
		MR4CConfig conf = new MR4CConfig(false);
		conf.initStandardCategories();
		assertNull(ResourceRequest.extractFrom(m_resource, conf));
	}


	private ResourceRequest buildRequest() {
		return new ResourceRequest(m_resource, m_min, m_max);
	}
}



