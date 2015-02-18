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

package com.google.mr4c.util;

import org.junit.*;
import static org.junit.Assert.*;

public class PathUtilsTest {

	@Test public void testPrependMissingPathElements() {
		doPrependTest("", "", "", ":");
		doPrependTest("e1", "e1", "", ":");
		doPrependTest("e1", "", "e1", ":");
		doPrependTest("e1:e2", "e1:e2", "e1:e2", ":");
		doPrependTest("e3:e4:e1:e2", "e1:e2", "e3:e4", ":");
		doPrependTest("e3:e1:e2", "e1:e2", "e2:e3", ":");
		doPrependTest("e1:e2:e3", "e1:e2:e3", "e2", ":");
	}

	private void doPrependTest(String expected, String path, String otherPath, String sep) {
		assertEquals( expected, PathUtils.prependMissingPathElements(path, otherPath, sep));
	}

}

