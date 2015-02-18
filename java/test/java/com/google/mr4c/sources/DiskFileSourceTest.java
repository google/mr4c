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

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.*;
import static org.junit.Assert.*;

public class DiskFileSourceTest {

	private FileSource m_flatSrc;
	private FileSource m_recurseSrc;
	private FileSourceTester m_flatTester;
	private FileSourceTester m_recurseTester;
	private File m_flatDir = new File("output/data/sources/disk/flat");
	private File m_recurseDir = new File("output/data/sources/disk/recurse");
	private File m_badDir = new File("output/data/sources/disk/flat/disk");

	@Before public void setUp() throws Exception {
		m_flatSrc = new DiskFileSource(m_flatDir);
		m_flatSrc.ensureExists();
		m_flatSrc.clear();
		m_flatTester = new FileSourceTester();
		m_recurseSrc = new DiskFileSource(m_recurseDir, false);
		m_recurseSrc.ensureExists();
		m_recurseSrc.clear();
		m_recurseTester = new FileSourceTester(false);
	}

	@Test public void testFileList() throws Exception {
		m_flatTester.testFileList(m_flatSrc);
		m_recurseTester.testFileList(m_recurseSrc);
	}

	@Test(expected=FileNotFoundException.class)
	public void testFileListNoDir() throws Exception {
		FileSource src = new DiskFileSource(m_badDir);
		src.getAllFileNames();
	}

	@Test public void testGetBytes() throws Exception {
		m_flatTester.testGetBytes(m_flatSrc);
		m_recurseTester.testGetBytes(m_recurseSrc);
	}

	@Test public void testGetFileSize() throws Exception {
		m_flatTester.testGetFileSize(m_flatSrc);
		m_recurseTester.testGetFileSize(m_recurseSrc);
	}

	@Test public void testFileExists() throws Exception {
		m_flatTester.testFileExists(m_flatSrc);
		m_recurseTester.testFileExists(m_recurseSrc);
	}

	@Test public void testGetSourceOnlyIfExists() throws Exception {
		m_flatTester.testGetSourceOnlyIfExists(m_flatSrc);
		m_recurseTester.testGetSourceOnlyIfExists(m_recurseSrc);
	}

	@Test public void testGetByInputStream() throws Exception {
		m_flatTester.testGetByInputStream(m_flatSrc);
		m_recurseTester.testGetByInputStream(m_recurseSrc);
	}

	@Test public void testAddByInputStream() throws Exception {
		m_flatTester.testAddByInputStream(m_flatSrc);
		m_recurseTester.testAddByInputStream(m_recurseSrc);
	}

	@Test public void testAddByOutputStream() throws Exception {
		m_flatTester.testAddByOutputStream(m_flatSrc);
		m_recurseTester.testAddByOutputStream(m_recurseSrc);
	}

	@Test public void testClear() throws Exception {
		m_flatTester.testClear(m_flatSrc);
		m_recurseTester.testClear(m_recurseSrc);
	}

	@Test public void testCopy() throws Exception {
		FileSource copyFrom = SourceTestUtils.getTestInputSource();
		SourceTestUtils.testCopySource(copyFrom, m_flatSrc, true);
		SourceTestUtils.testCopySource(copyFrom, m_recurseSrc, true);
	}
		
}
