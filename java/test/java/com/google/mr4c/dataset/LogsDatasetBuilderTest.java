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

package com.google.mr4c.dataset;

import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.Category;
import com.google.mr4c.config.category.CategoryConfig;
import com.google.mr4c.config.category.CustomConfig;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.BasicDataKeyFilter;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;
import com.google.mr4c.keys.DataKeyFilter;
import com.google.mr4c.metadata.MetadataField;
import com.google.mr4c.metadata.MetadataMap;
import com.google.mr4c.metadata.PrimitiveType;
import com.google.mr4c.sources.DataFileSource;
import com.google.mr4c.sources.URIDataFileSource;
import com.google.mr4c.util.NamespacedProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class LogsDatasetBuilderTest {

	private MR4CConfig m_bbConf;
	private CategoryConfig m_custConf;
	private Dataset m_noTaskDataset;
	private Dataset m_withTaskDataset;
	private String m_root = "input/data/dataset/logsbuilder/";
	private String m_task = "taskid1";
	private String m_job = "jobid1";
	private String m_host = "somehost";
	private List<File> m_files = new ArrayList<File>();

	@Before public void setup() throws Exception {
		buildConfig();
		buildFiles();
		buildDatasets();
	}

	@Test public void testNoTask() {
		doTest(m_noTaskDataset);
	}

	@Test public void testWithTask() {
		addTaskProperties();
		doTest(m_withTaskDataset);
	}

	private void doTest(Dataset expected) {
		LogsDatasetBuilder builder = new LogsDatasetBuilder(m_bbConf);
		builder.init();
		for ( File file : m_files ) {
			builder.addFile(file);
		}
		assertEquals(expected, builder.getDataset());
	}

	private void buildConfig() {
		m_bbConf = new MR4CConfig(false);
		m_bbConf.initStandardCategories();
		m_custConf = m_bbConf.getCategory(Category.CUSTOM);
	}

	private void addTaskProperties() {
		m_custConf.setProperty(CustomConfig.PROP_JOBID, m_job);
		m_custConf.setProperty(CustomConfig.PROP_TASKID, m_task);
		m_custConf.setProperty(CustomConfig.PROP_HOST, m_host);
	}

	private void buildFiles() {
		addFile("log1.log");
		addFile("log2.log");
		addFile("somepath/log3.log");
	}

	private void addFile(String path) {
		File file = new File(m_root, path);
		m_files.add(file);
	}

	private void buildDatasets() {
		buildNoTaskDataset();
		buildWithTaskDataset();
	}

	private void buildNoTaskDataset() {
		m_noTaskDataset = new Dataset();
		for ( File file : m_files ) {
			DataKey key = buildFileKey(file.getName());
			DataFile dataFile = buildDataFile(file);
			m_noTaskDataset.addFile(key, dataFile);
			
		}
	}

	private void buildWithTaskDataset() {
		m_withTaskDataset = new Dataset();
		for ( File file : m_files ) {
			DataKey key = buildFileAndTaskKey(file.getName());
			DataFile dataFile = buildDataFile(file);
			m_withTaskDataset.addFile(key, dataFile);
		}
		MetadataMap metaMap = new MetadataMap();
		MetadataField hostField = new MetadataField(m_host, PrimitiveType.STRING);
		MetadataField jobField = new MetadataField(m_job, PrimitiveType.STRING);
		metaMap.getMap().put("host", hostField);
		metaMap.getMap().put("job", jobField);
		m_withTaskDataset.addMetadata(buildTaskKey(), metaMap);
	}

	private DataKey buildTaskKey() {
		return DataKeyFactory.newKey(new DataKeyElement(m_task, LogsDatasetBuilder.TASKID));
	}
	private DataKey buildFileKey(String name) {
		return DataKeyFactory.newKey(new DataKeyElement(name, LogsDatasetBuilder.NAME));
	}

	private DataKey buildFileAndTaskKey(String name) {
		return DataKeyFactory.newKey(
			new DataKeyElement(name, LogsDatasetBuilder.NAME),
			new DataKeyElement(m_task, LogsDatasetBuilder.TASKID)
		);
	}

	private DataFile buildDataFile(File file) {
		DataFileSource src = new URIDataFileSource(file.toURI(), file.getName());
		return new DataFile(src, "text/plain");
	}

}
