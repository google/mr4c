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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

public class MR4CLoggingTest {

	@Test public void testExtractLogFiles() {
		// make sure logging initializes
		Logger logger = MR4CLogging.instance().getLogger(MR4CLoggingTest.class);
		logger.info("something");
		Set<File> expected = new HashSet<File>();
		expected.add(new File("./logs/mr4c-java.log"));
		expected.add(new File("./logs/mr4c-java-algo.log"));
		Set<File> actual = MR4CLogging.instance().extractLogFiles();
		assertEquals(expected,actual);
	}


}

