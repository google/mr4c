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

public class MetafilesDatasetSourceTest {

	private FileSource m_inputFileSrc;
	private FileSource m_outputFileSrc;
	private DatasetSource m_inputSrc;
	private DatasetSource m_outputSrc;


	@Before public void setUp() throws Exception {
		m_inputFileSrc = new InMemoryFileSource();
		SourceTestUtils.writeFile(m_inputFileSrc, "file1", "Content of file1".getBytes());
		SourceTestUtils.writeFile(m_inputFileSrc, "file2", "Content of file2".getBytes());
		SourceTestUtils.writeFile(m_inputFileSrc, "file3", "Content of file3".getBytes());
		m_outputFileSrc = new InMemoryFileSource();
		m_inputSrc = new MetafilesDatasetSource(m_inputFileSrc);
		m_outputSrc = new MetafilesDatasetSource(m_outputFileSrc);
	}

	@Test public void testRoundTrip() throws Exception {
		SourceTestUtils.testSource(m_inputSrc, m_outputSrc);
	}

}
