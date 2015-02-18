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

package com.google.mr4c.serialize.json;

import com.google.mr4c.config.algorithm.AlgorithmConfig;
import com.google.mr4c.config.algorithm.AlgoConfigTestUtils;
import com.google.mr4c.config.diff.DiffConfig;
import com.google.mr4c.config.diff.DiffConfigTestUtils;
import com.google.mr4c.config.execution.DatasetConfig;
import com.google.mr4c.config.execution.DirectoryConfig;
import com.google.mr4c.config.execution.ExecutionConfig;
import com.google.mr4c.config.execution.LocationsConfig;
import com.google.mr4c.config.execution.MapConfig;
import com.google.mr4c.config.execution.ConfigTestUtils;
import com.google.mr4c.config.site.SiteConfig;
import com.google.mr4c.config.site.SiteConfigTestUtils;
import com.google.mr4c.config.test.AlgoTestConfig;
import com.google.mr4c.config.test.AlgoTestConfigTestUtils;
import com.google.mr4c.serialize.ConfigSerializer;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.*;
import static org.junit.Assert.*;

public class JsonConfigSerializerTest {

	private AlgorithmConfig m_algoConfig;
	private DatasetConfig m_datasetConfig1;
	private DatasetConfig m_datasetConfig3;
	private ExecutionConfig m_exeConfig;
	private MapConfig m_mapConfig;
	private DirectoryConfig m_dirConfig1;
	private DirectoryConfig m_dirConfig2;
	private SiteConfig m_siteConfig;
	private DiffConfig m_diffConfig;
	private AlgoTestConfig m_algoTestConfig;
	private LocationsConfig m_locationsConfig1;
	private LocationsConfig m_locationsConfig2;
	private ConfigSerializer m_serializer;

	@Before public void setup() throws Exception {
		m_algoConfig = AlgoConfigTestUtils.buildAlgorithmConfig1();
		m_datasetConfig1 = ConfigTestUtils.buildDatasetConfig1();
		m_datasetConfig3 = ConfigTestUtils.buildDatasetConfig3();
		m_exeConfig = ConfigTestUtils.buildExecutionConfig1();
		m_mapConfig = ConfigTestUtils.buildMapConfig1();
		m_dirConfig1 = ConfigTestUtils.buildDirectoryConfig1();
		m_dirConfig2 = ConfigTestUtils.buildDirectoryConfig2();
		m_siteConfig = SiteConfigTestUtils.buildSiteConfig1();
		m_diffConfig = DiffConfigTestUtils.buildDiffConfig1();
		m_algoTestConfig = AlgoTestConfigTestUtils.buildAlgoTestConfig1();
		m_locationsConfig1 = ConfigTestUtils.buildLocationsConfig1();
		m_locationsConfig2 = ConfigTestUtils.buildLocationsConfig2();
		m_serializer = new JsonConfigSerializer();
	}

	@Test public void testAlgorithm() throws Exception {
		StringWriter writer = new StringWriter();
		m_serializer.serializeAlgorithmConfig(m_algoConfig, writer);
		String json = writer.toString();
		StringReader reader = new StringReader(json);
		AlgorithmConfig config2 = m_serializer.deserializeAlgorithmConfig(reader);
		assertEquals(m_algoConfig, config2);
	}

	@Test public void testDataset() throws Exception {
		testDataset(m_datasetConfig1);
		testDataset(m_datasetConfig3);
	}

	private void testDataset(DatasetConfig config) throws Exception {
		StringWriter writer = new StringWriter();
		m_serializer.serializeDatasetConfig(config, writer);
		String json = writer.toString();
		StringReader reader = new StringReader(json);
		DatasetConfig config2 = m_serializer.deserializeDatasetConfig(reader);
		assertEquals(config, config2);
	}

	@Test public void testExecution() throws Exception {
		StringWriter writer = new StringWriter();
		m_serializer.serializeExecutionConfig(m_exeConfig, writer);
		String json = writer.toString();
		StringReader reader = new StringReader(json);
		ExecutionConfig config2 = m_serializer.deserializeExecutionConfig(reader);
		assertEquals(m_exeConfig, config2);
	}

	@Test public void testMap() throws Exception {
		StringWriter writer = new StringWriter();
		m_serializer.serializeMapConfig(m_mapConfig, writer);
		String json = writer.toString();
		StringReader reader = new StringReader(json);
		MapConfig config2 = m_serializer.deserializeMapConfig(reader);
		assertEquals(m_mapConfig, config2);
	}

	@Test public void testDirectory() throws Exception {
		testDirectory(m_dirConfig1);
		testDirectory(m_dirConfig2);
	}

	private void testDirectory(DirectoryConfig config) throws Exception {
		StringWriter writer = new StringWriter();
		m_serializer.serializeDirectoryConfig(config, writer);
		String json = writer.toString();
		StringReader reader = new StringReader(json);
		DirectoryConfig config2 = m_serializer.deserializeDirectoryConfig(reader);
		assertEquals(config, config2);
	}

	@Test public void testSite() throws Exception {
		StringWriter writer = new StringWriter();
		m_serializer.serializeSiteConfig(m_siteConfig, writer);
		String json = writer.toString();
		StringReader reader = new StringReader(json);
		SiteConfig config2 = m_serializer.deserializeSiteConfig(reader);
		assertEquals(m_siteConfig, config2);
	}

	@Test public void testDiff() throws Exception {
		StringWriter writer = new StringWriter();
		m_serializer.serializeDiffConfig(m_diffConfig, writer);
		String json = writer.toString();
		StringReader reader = new StringReader(json);
		DiffConfig config2 = m_serializer.deserializeDiffConfig(reader);
		assertEquals(m_diffConfig, config2);
	}

	@Test public void testAlgoTest() throws Exception {
		StringWriter writer = new StringWriter();
		m_serializer.serializeAlgoTestConfig(m_algoTestConfig, writer);
		String json = writer.toString();
		StringReader reader = new StringReader(json);
		AlgoTestConfig config2 = m_serializer.deserializeAlgoTestConfig(reader);
		assertEquals(m_algoTestConfig, config2);
	}

	@Test public void testLocations() throws Exception {
		testLocations(m_locationsConfig1);
		testLocations(m_locationsConfig2);
	}

	private void testLocations(LocationsConfig config) throws Exception {
		StringWriter writer = new StringWriter();
		m_serializer.serializeLocationsConfig(config, writer);
		String json = writer.toString();
		StringReader reader = new StringReader(json);
		LocationsConfig config2 = m_serializer.deserializeLocationsConfig(reader);
		assertEquals(config, config2);
	}

}
