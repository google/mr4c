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

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

public class FilesDatasetSourceTest {

	private URI m_configNoSelfURI;
	private URI m_configSelfURI;
	private URI m_inputDir;
	private URI m_inputSelfDir;
	private URI m_outputDir;
	private FileSource m_inputFileSrc;
	private FileSource m_inputSelfFileSrc;
	private FileSource m_outputFileSrc;
	private FilesDatasetSourceConfig m_configSelf;
	private FilesDatasetSourceConfig m_configNoSelf;
	private DatasetSource m_inputSrc;
	private DatasetSource m_outputSrc;


	@Before public void setUp() throws Exception {
		m_configNoSelfURI = new URI("input/data/dataset/test1/source.json");
		m_configSelfURI = new URI("input/data/dataset/test1/source_self.json");
		m_inputDir = new URI("input/data/dataset/test1/input_data");
		m_inputSelfDir = new URI("input/data/dataset/test1/input_data_self");
		m_outputDir = new URI("output/data/dataset/test1");
		m_inputFileSrc = FileSources.getFileSource(m_inputDir);
		m_inputSelfFileSrc = FileSources.getFileSource(m_inputSelfDir);
		m_outputFileSrc = FileSources.getFileSource(m_outputDir);
		m_configNoSelf = FilesDatasetSourceConfig.load(new ConfigDescriptor(m_configNoSelfURI));
		m_configSelf = FilesDatasetSourceConfig.load(new ConfigDescriptor(m_configSelfURI));
		m_inputSrc = new FilesDatasetSource(m_configNoSelf, m_inputFileSrc);
		m_outputSrc = new FilesDatasetSource(m_configNoSelf, m_outputFileSrc);
	}

	@Test public void testLoad() throws Exception {
		Dataset dataset = m_inputSrc.readDataset();
		assertEquals("Check # of file keys", 40, dataset.getAllFileKeys().size());
		DataKey key = buildKey("1","2455874.21556848", "MS");
		DataFile file = dataset.getFile(key);
		byte[] bytes = file.getBytes();
		assertEquals("Check size of a file", 518371, bytes.length);
	}

	@Test public void testFindFile() throws Exception {
		DataKey key = buildKey("1","2455874.21556848", "MS");
		DataFile file = m_inputSrc.findDataFile(key);
		byte[] bytes = file.getBytes();
		assertEquals("Check size of a file", 518371, bytes.length);
	}

	@Test public void testFindFileNotFound() throws Exception {
		DataKey key = buildKey("1","blah_blah", "MS");
		assertNull(m_inputSrc.findDataFile(key));
	}

	private DataKey buildKey(String sensor, String frame, String type) {
		DataKeyDimension sensorDim = new DataKeyDimension("sensor");
		DataKeyDimension frameDim = new DataKeyDimension("frame");
		DataKeyDimension typeDim = new DataKeyDimension("type");
		return DataKeyFactory.newKey( 
			new DataKeyElement(sensor, sensorDim),
			new DataKeyElement(frame, frameDim),
			new DataKeyElement(type, typeDim)
		);
	}

	@Test public void testRoundTrip() throws Exception {
		doTest(m_inputFileSrc, m_configNoSelf, m_configNoSelf, 1); 
		doTest(m_inputFileSrc, m_configNoSelf, m_configSelf, 2);
		doTestFail(m_inputFileSrc, m_configNoSelf, null, 3);
		doTest(m_inputFileSrc, m_configSelf, m_configNoSelf, 4);
		doTest(m_inputFileSrc, m_configSelf, m_configSelf, 5);
		doTestFail(m_inputFileSrc, null, null, 6);
		doTestFail(m_inputFileSrc, null, m_configNoSelf, 7);
		doTestFail(m_inputFileSrc, null, m_configSelf, 8);
		doTestFail(m_inputFileSrc, null, null, 9);
		doTest(m_inputSelfFileSrc, m_configNoSelf, m_configNoSelf, 10);
		doTest(m_inputSelfFileSrc, m_configNoSelf, m_configSelf, 11);
		doTestFail(m_inputSelfFileSrc, m_configNoSelf, null, 12);
		doTest(m_inputSelfFileSrc, m_configSelf, m_configNoSelf, 13);
		doTest(m_inputSelfFileSrc, m_configSelf, m_configSelf, 14);
		doTestFail(m_inputSelfFileSrc, null, null, 15);
		doTest(m_inputSelfFileSrc, null, m_configNoSelf, 16);
		doTest(m_inputSelfFileSrc, null, m_configSelf, 17);
		doTestFail(m_inputSelfFileSrc, null, null, 18);

	}

	private void doTestFail(
		FileSource inputFileSrc,
		FilesDatasetSourceConfig inputConfig,
		FilesDatasetSourceConfig outputConfig,
		int testNumber
	) throws IOException {
		try {
			doTest(
				inputFileSrc,
				inputConfig,
				outputConfig,
				testNumber
			);
		} catch (IllegalStateException ise) {
			return; // its what we wanted
		}
		fail("Didn't get IllegalStateException");
	}

	private void doTest(
		FileSource inputFileSrc,
		FilesDatasetSourceConfig inputConfig,
		FilesDatasetSourceConfig outputConfig,
		int testNumber
	) throws IOException {
		URI outputDir = URI.create("output/data/dataset/test1/case"+testNumber);
		FileSource outputFileSrc = FileSources.getFileSource(outputDir);
		outputFileSrc.clear();
		DatasetSource inputSrc = new FilesDatasetSource(inputConfig, inputFileSrc);
		DatasetSource outputSrc = new FilesDatasetSource(outputConfig, outputFileSrc);
		SourceTestUtils.testSource(inputSrc, outputSrc);
	}

}
