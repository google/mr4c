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
import com.google.mr4c.config.execution.MapConfig;
import com.google.mr4c.content.ContentFactories;
import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;
import com.google.mr4c.serialize.ConfigSerializer;
import com.google.mr4c.serialize.SerializerFactories;

import java.io.Reader;
import java.net.URI;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

public class TransformedDatasetSourceTest {

	private URI m_srcConfigURI;
	private URI m_mapConfigURI;
	private URI m_inputDir;
	private URI m_outputDir;
	private ConfigSerializer m_configSerializer;
	private FilesDatasetSourceConfig m_srcConfig;
	private MapConfig m_mapConfig;
	private FileSource m_inputFileSrc;
	private FileSource m_outputFileSrc;
	private DatasetSource m_wrappedInputSrc;
	private DatasetSource m_wrappedOutputSrc;
	private DatasetSource m_inputSrc;
	private DatasetSource m_outputSrc;


	@Before public void setUp() throws Exception {
		buildURIs();
		buildSerializer();
		loadConfigs();
		buildSources();
	}

	private void buildURIs() throws Exception {
		m_srcConfigURI = new URI("input/data/dataset/test1/source.json");
		m_mapConfigURI = new URI("input/data/dataset/test2/map.json");
		m_inputDir = new URI("input/data/dataset/test1/input_data");
		m_outputDir = new URI("output/data/dataset/test2");
	}

	private void buildSerializer() {
		m_configSerializer = SerializerFactories.getSerializerFactory("application/json").createConfigSerializer();
	}

	private void loadConfigs() throws Exception {
		m_srcConfig = FilesDatasetSourceConfig.load(new ConfigDescriptor(m_srcConfigURI));
		Reader reader = ContentFactories.readContentAsReader(m_mapConfigURI);
		try {
			m_mapConfig = m_configSerializer.deserializeMapConfig(reader);
		} finally {
			reader.close();
		}
	}

	private void buildSources() {
		m_inputFileSrc = FileSources.getFileSource(m_inputDir);
		m_outputFileSrc = FileSources.getFileSource(m_outputDir);
		m_wrappedInputSrc = new FilesDatasetSource(m_srcConfig, m_inputFileSrc);
		m_wrappedOutputSrc = new FilesDatasetSource(m_srcConfig, m_outputFileSrc);
		m_inputSrc = new TransformedDatasetSource(m_wrappedInputSrc, m_mapConfig);
		m_outputSrc = new TransformedDatasetSource(m_wrappedOutputSrc, m_mapConfig);
	}

	@Test public void testLoad() throws Exception {
		Dataset dataset = m_inputSrc.readDataset();
		assertEquals("Check # of file keys", 40, dataset.getAllFileKeys().size());
		DataKey key = buildKey("1","2455874.21556848", "MULTISPECTRAL");
		DataFile file = dataset.getFile(key);
		byte[] bytes = file.getBytes();
		assertEquals("Check size of a file", 518371, bytes.length);
	}

	private DataKey buildKey(String sensor, String frame, String type) {
		DataKeyDimension sensorDim = new DataKeyDimension("SENSOR");
		DataKeyDimension frameDim = new DataKeyDimension("FRAME");
		DataKeyDimension typeDim = new DataKeyDimension("IMAGE_TYPE");
		return DataKeyFactory.newKey( 
			new DataKeyElement(sensor, sensorDim),
			new DataKeyElement(frame, frameDim),
			new DataKeyElement(type, typeDim)
		);
	}

	@Test public void testRoundTrip() throws Exception {
		SourceTestUtils.testSource(m_inputSrc, m_outputSrc);
	}

}
