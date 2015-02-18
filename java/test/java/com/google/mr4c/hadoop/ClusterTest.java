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

package com.google.mr4c.hadoop;

import org.apache.hadoop.mapred.JobConf;

import org.junit.*;
import static org.junit.Assert.*;

public class ClusterTest {

	private Cluster m_cluster1;
	private Cluster m_cluster1a;
	private Cluster m_cluster2;

	@Before public void setup() throws Exception {
		m_cluster1 = buildCluster1();
		m_cluster1a = buildCluster1();
		m_cluster2 = buildCluster2();
	}

	@Test public void testEqual() throws Exception {
		assertEquals(m_cluster1, m_cluster1a);
	}

	@Test public void testNotEqual() throws Exception {
		assertFalse(m_cluster1.equals(m_cluster2));
	}
	
	@Test public void testRoundTrip() throws Exception {
		JobConf conf = new JobConf();
		conf.clear();
		m_cluster1.applyToConfig(conf);
		Cluster cluster = Cluster.extractFromConfig(conf);
		assertEquals(m_cluster1, cluster);
	}

	private Cluster buildCluster1() {
		return new Cluster("tracker1", "fs1");
	}

	private Cluster buildCluster2() {
		return new Cluster("tracker2", "fs2");
	}


}
