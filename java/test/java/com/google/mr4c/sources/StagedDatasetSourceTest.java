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

package com.google.mr4c.sources;

import com.google.mr4c.config.ConfigDescriptor;
import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;

import java.net.URI;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

public class StagedDatasetSourceTest {

	private URI m_configURI;
	private URI m_inputDir;
	private URI m_actualDir;
	private URI m_stageDir;
	private FileSource m_inputFileSrc;
	private FileSource m_actualFileSrc;
	private FileSource m_stageFileSrc;
	private FilesDatasetSourceConfig m_config;
	private DatasetSource m_inputSrc;
	private DatasetSource m_outputSrc;
	private DatasetSource m_actualSrc;
	private DatasetSource m_stageSrc;


	@Before public void setUp() throws Exception {
		m_configURI = new URI("input/data/dataset/test1/source.json");
		m_inputDir = new URI("input/data/dataset/test1/input_data");
		m_actualDir = new URI("output/data/sources/staged/actual");
		m_stageDir = new URI("output/data/sources/staged/stage");
		m_inputFileSrc = FileSources.getFileSource(m_inputDir);
		m_actualFileSrc = FileSources.getFileSource(m_actualDir);
		m_stageFileSrc = FileSources.getFileSource(m_stageDir);
		m_config = FilesDatasetSourceConfig.load(new ConfigDescriptor(m_configURI));
		m_inputSrc = new FilesDatasetSource(m_config, m_inputFileSrc);
		m_actualSrc = new FilesDatasetSource(m_config, m_actualFileSrc);
		m_stageSrc = new FilesDatasetSource(m_config, m_stageFileSrc);
		m_outputSrc = new StagedDatasetSource(m_actualSrc, m_stageSrc);
		m_actualFileSrc.ensureExists();
		m_actualFileSrc.clear();
		m_stageFileSrc.ensureExists();
		m_stageFileSrc.clear();
	}

	@Test public void testRoundTrip() throws Exception {
		Dataset dataset = m_inputSrc.readDataset();
		m_outputSrc.writeDataset(dataset);
		SourceTestUtils.compareSources(m_inputSrc, m_stageSrc);
		m_outputSrc.copyToFinal();
		SourceTestUtils.compareSources(m_inputSrc, m_actualSrc);
		SourceTestUtils.compareSources(m_inputSrc, m_outputSrc);
	}

}
