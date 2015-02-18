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

package com.google.mr4c.dataset;

import org.junit.*;
import static org.junit.Assert.*;

public class DataFileTest {

	private DataFile m_file1a;
	private DataFile m_file1b;
	private DataFile m_file2;

	@Before public void setup() throws Exception {
		m_file1a = DatasetTestUtils.buildDataFile1();
		m_file1b = DatasetTestUtils.buildDataFile1();
		m_file2 = DatasetTestUtils.buildDataFile2();
	}
	@Test public void testEquals() {
		assertEquals(m_file1a, m_file1b);
	}

	@Test public void testNotEqual() {
		assertFalse(m_file1a.equals(m_file2));
	}

}
