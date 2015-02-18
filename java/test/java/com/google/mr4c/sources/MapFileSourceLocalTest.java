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

import com.google.mr4c.hadoop.HadoopTestUtils;

import java.io.File;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;

import org.junit.*;
import static org.junit.Assert.*;

public class MapFileSourceLocalTest {

	private ArchiveSource m_src;
	private ArchiveSourceTester m_tester;
	private Path m_dir = new Path("output/data/sources/map");

	@Before public void setUp() throws Exception {
		FileSystem fs = FileSystem.getLocal(new Configuration());
		m_src = new MapFileSource(fs, new Path(fs.getWorkingDirectory(),m_dir));
		m_tester = new ArchiveSourceTester();
	}

	@Test public void testFileList() throws Exception {
		m_tester.testFileList(m_src);
	}

	@Test public void testMetadataFileList() throws Exception {
		m_tester.testMetadataFileList(m_src);
	}

	@Test public void testGetFileBytes() throws Exception {
		m_tester.testGetFileBytes(m_src);
	}

	@Test public void testGetFileSize() throws Exception {
		m_tester.testGetFileSize(m_src);
	}

	@Test public void testFileExists() throws Exception {
		m_tester.testFileExists(m_src);
	}

	@Test public void testGetSourceOnlyIfExists() throws Exception {
		m_tester.testGetSourceOnlyIfExists(m_src);
	}

	@Test public void testGetMetadataBytes() throws Exception {
		m_tester.testGetMetadataBytes(m_src);
	}

	@Test public void testGetFileByInputStream() throws Exception {
		m_tester.testGetFileByInputStream(m_src);
	}

	@Test public void testGetMetadataFileByInputStream() throws Exception {
		m_tester.testGetMetadataFileByInputStream(m_src);
	}

	@Test public void testAddFileByInputStream() throws Exception {
		m_tester.testAddFileByInputStream(m_src);
	}

	@Test public void testAddMetadataFileByInputStream() throws Exception {
		m_tester.testAddMetadataFileByInputStream(m_src);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testAddFileByOutputStream() throws Exception {
		m_tester.testAddFileByOutputStream(m_src);
	}

	@Test public void testAddMetadataFileByOutputStream() throws Exception {
		m_tester.testAddMetadataFileByOutputStream(m_src);
	}

	@Test public void testClearAndExists() throws Exception {
		m_tester.testClearAndExists(m_src);
	}

	@After public void tearDown() throws Exception {
		if ( m_src!=null ) {
			m_src.close();
		}
	}
}
