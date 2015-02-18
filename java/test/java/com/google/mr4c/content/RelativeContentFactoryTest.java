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

package com.google.mr4c.content;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;

public class RelativeContentFactoryTest {

	@Test public void testRelativizeURI() {
		File file = new File("/home/mitchell/ground/Ground_Software/AMPlus/ProductionPlatform/Container/trunk/input/small/fakesr/test1");
		File ancestor = new File("/home/mitchell/ground/Ground_Software/AMPlus/ProductionPlatform/Container/trunk");
		String relPath = RelativeContentFactory.toRelativeFilePath(file, ancestor);
		URI relUri = RelativeContentFactory.toRelativeFileURI(file, ancestor);
		assertEquals("input/small/fakesr/test1", relPath);
		assertEquals("rel:input/small/fakesr/test1", relUri.toString());
	}

	@Test(expected=IllegalStateException.class) public void testNotRelated() throws Exception {
		File file = new File("/home/mitchell/ground/Ground_Software/AMPlus/ProductionPlatform/Container/trunk/output/small/fakesr/test1");
		File ancestor = new File("/home/mitchell/ground/Ground_Software/AMPlus/ProductionPlatform/Container/trunk/input");
		URI rel = RelativeContentFactory.toRelativeFileURI(file, ancestor);
	}

	@Test public void testToFile() throws Exception {
		File parent = new File("/home/mitchell/ground/Ground_Software/AMPlus/ProductionPlatform/Container/trunk");
		URI rel = new URI("rel:input/small/fakesr/test1");
		File file = RelativeContentFactory.toFile(rel, parent);
		File expected = new File("/home/mitchell/ground/Ground_Software/AMPlus/ProductionPlatform/Container/trunk/input/small/fakesr/test1");
		assertEquals(expected.getPath(), file.getPath());
	}

}


