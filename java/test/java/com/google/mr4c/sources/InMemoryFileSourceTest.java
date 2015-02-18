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

import org.junit.*;
import static org.junit.Assert.*;

public class InMemoryFileSourceTest {

	private FileSource m_src;
	private FileSourceTester m_tester;

	@Before public void setUp() throws Exception {
		m_src = new InMemoryFileSource();
		m_tester = new FileSourceTester();
	}

	@Test public void testFileList() throws Exception {
		m_tester.testFileList(m_src);
	}

	@Test public void testGetBytes() throws Exception {
		m_tester.testGetBytes(m_src);
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

	@Test public void testGetByInputStream() throws Exception {
		m_tester.testGetByInputStream(m_src);
	}

	@Test public void testAddByInputStream() throws Exception {
		m_tester.testAddByInputStream(m_src);
	}

	@Test public void testAddByOutputStream() throws Exception {
		m_tester.testAddByOutputStream(m_src);
	}

	@Test public void testClear() throws Exception {
		m_tester.testClear(m_src);
	}

}
