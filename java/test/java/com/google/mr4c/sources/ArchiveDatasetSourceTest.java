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
import com.google.mr4c.content.ContentFactories;
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

public class ArchiveDatasetSourceTest {

	private URI m_configNoSelfURI;
	private URI m_configSelfURI;
	private URI m_inputDir;
	private URI m_outputDir;
	private FileSource m_inputFileSrc;
	private FilesDatasetSourceConfig m_configSelf;
	private FilesDatasetSourceConfig m_configNoSelf;
	private DatasetSource m_inputSrc;


	@Before public void setUp() throws Exception {
		m_configNoSelfURI = new URI("input/data/dataset/test1/source.json");
		m_configSelfURI = new URI("input/data/dataset/test1/source_self.json");
		m_inputDir = new URI("input/data/dataset/test1/input_data");
		m_outputDir = new URI("output/data/dataset/archive");
		m_inputFileSrc = FileSources.getFileSource(m_inputDir);
		m_configNoSelf = FilesDatasetSourceConfig.load(new ConfigDescriptor(m_configNoSelfURI));
		m_configSelf = FilesDatasetSourceConfig.load(new ConfigDescriptor(m_configSelfURI));
		m_inputSrc = new FilesDatasetSource(m_configNoSelf, m_inputFileSrc);
	}

	@Test public void testRoundTrip() throws Exception {
		doTest(m_configNoSelf, 1); 
		doTest(m_configSelf,  2);
		doTestFail(null, 3);
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

	private void doTestFail(
		FilesDatasetSourceConfig outputConfig,
		int testNumber
	) throws IOException {
		try {
			doTest(
				outputConfig,
				testNumber
			);
		} catch (IllegalStateException ise) {
			return; // its what we wanted
		}
		fail("Didn't get IllegalStateException");
	}

	private void doTest(
		FilesDatasetSourceConfig outputConfig,
		int testNumber
	) throws IOException {
		URI outputDir = URI.create("output/data/dataset/archive/case"+testNumber);
		ArchiveSource outputFileSrc = new MapFileSource(ContentFactories.toPath(outputDir));
		outputFileSrc.clear();
		DatasetSource outputSrc = new ArchiveDatasetSource(outputConfig, outputFileSrc);
		SourceTestUtils.testSource(m_inputSrc, outputSrc);
	}

}
