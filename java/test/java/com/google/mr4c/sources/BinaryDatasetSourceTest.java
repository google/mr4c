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

import java.io.File;
import java.net.URI;

import org.junit.*;
import static org.junit.Assert.*;

public class BinaryDatasetSourceTest {

	private DatasetSource m_inputSrc;
	private DatasetSource m_outputSrc;


	@Before public void setUp() throws Exception {
		String inputDir= "input/data/dataset/test1/input_data";
		String outputDir= "output/data/dataset/binary";
		String fileName = "ss01_c1_2455874.21556848_MS_1.5bps.jpc";
		File inputFile = new File(inputDir, fileName);
		File outputFile = new File(outputDir, fileName);
		m_inputSrc = new BinaryDatasetSource(inputFile.toURI());
		m_outputSrc = new BinaryDatasetSource(outputFile.toURI());
	}

	@Test public void testRoundTrip() throws Exception {
		SourceTestUtils.testSource(m_inputSrc, m_outputSrc);
	}

}

