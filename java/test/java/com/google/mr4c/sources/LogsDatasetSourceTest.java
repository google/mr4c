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

public class LogsDatasetSourceTest {

	private URI m_noTaskInputDir;
	private URI m_noTaskOutputDir;
	private URI m_withTaskInputDir;
	private URI m_withTaskOutputDir;

	@Before public void setUp() throws Exception {
		m_noTaskInputDir = new URI("input/data/dataset/logsrc/notask");
		m_noTaskOutputDir = new URI("output/data/dataset/logsrc/notask");
		m_withTaskInputDir = new URI("input/data/dataset/logsrc/withtask");
		m_withTaskOutputDir = new URI("output/data/dataset/logsrc/withtask");
	}

	@Test public void testRoundTripNoTask() throws Exception {
		doTest( m_noTaskInputDir, m_noTaskOutputDir);
	}

	@Test public void testRoundTripWithTask() throws Exception {
		doTest( m_withTaskInputDir, m_withTaskOutputDir);
	}

	private void doTest(
		URI inputDir,
		URI outputDir
	) throws IOException {
		FileSource inputFileSrc = FileSources.getFileSource(inputDir, false);
		FileSource outputFileSrc = FileSources.getFileSource(outputDir, false);
		outputFileSrc.clear();
		DatasetSource inputSrc = LogsDatasetSource.create(inputFileSrc);
		DatasetSource outputSrc = LogsDatasetSource.create(outputFileSrc);
		SourceTestUtils.testSource(inputSrc, outputSrc);
	}

}
