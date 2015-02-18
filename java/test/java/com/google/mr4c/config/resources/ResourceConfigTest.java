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
import com.google.mr4c.config.category.Category;
import com.google.mr4c.config.category.CategoryConfig;
import com.google.mr4c.config.category.HadoopConfig;

import org.apache.hadoop.conf.Configuration;

import org.junit.*;
import static org.junit.Assert.*;

public class ResourceConfigTest {

	private ResourceConfig m_config;

	@Before public void setup() throws Exception {
		m_config = buildConfig();
	}

	@Test public void testEquals() {
		assertEquals(m_config, buildConfig());
	}

	@Test public void testNotEqualRequests() {
		ResourceConfig config = buildConfig();
		config.addRequest(new ResourceRequest(new TestResourceInfo("whatever", Category.CORE), 55, 77));
		assertFalse(m_config.equals(config));
	}

	@Test public void testMR4CConfigUpdate() {
		MR4CConfig conf = new MR4CConfig(false);
		conf.initStandardCategories();
		m_config.applyTo(conf);
		ResourceConfig config = new ResourceConfig();
		config.addStandardResources(conf);
		assertEquals(m_config, config);
	}

	@Test public void testFullLifecycle() {
		MR4CConfig bbConf = buildMR4CConfig();
		Configuration jobConf = buildJobConf();
		ResourceConfig config = new ResourceConfig();
		config.addStandardResources(bbConf);
		config.addStandardLimits(jobConf);
		config.addLimit(new ResourceLimit(Resource.MEMORY, 3072, LimitSource.CLUSTER));
		config.addLimit(new ResourceLimit(Resource.CORES, 90, LimitSource.CLUSTER)); // should be ignored
		config.resolveRequests();
		config.applyTo(jobConf);
		assertEquals("4", jobConf.get(Resource.CORES.getHadoopName()));
		assertEquals("3072", jobConf.get(Resource.MEMORY.getHadoopName()));
	}

	private ResourceConfig buildConfig() {
		ResourceConfig config = new ResourceConfig();
		config.addRequest(new ResourceRequest(Resource.CORES, 1, 5));
		config.addRequest(new ResourceRequest(Resource.MEMORY, 1, 5));
		return config;
	}

	private MR4CConfig buildMR4CConfig() {
		MR4CConfig bbConf = new MR4CConfig(false);
		bbConf.initStandardCategories();
		CategoryConfig hadoopConf = bbConf.getCategory(Category.HADOOP);
		hadoopConf.setProperty(HadoopConfig.PROP_MIN_CORES, "2");
		hadoopConf.setProperty(HadoopConfig.PROP_MAX_CORES, "6");
		hadoopConf.setProperty(HadoopConfig.PROP_MIN_MEMORY, "2048");
		hadoopConf.setProperty(HadoopConfig.PROP_MAX_MEMORY, "8192");
		return bbConf;		
	}

	private Configuration buildJobConf() {
		Configuration conf = new Configuration(false);
		conf.set(Resource.CORES.getMaxHadoopName(), "4");
		return conf;
	}

}



