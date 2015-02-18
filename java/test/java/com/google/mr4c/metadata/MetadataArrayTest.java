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

package com.google.mr4c.metadata;

import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

public class MetadataArrayTest {


	@Test public void testEqual() {
		MetadataArray array1 = new MetadataArray(Arrays.asList("one", "two", "three"), PrimitiveType.STRING);
		MetadataArray array2 = new MetadataArray(Arrays.asList("one", "two", "three"), PrimitiveType.STRING);
		assertEquals("should be equal", array1,array2);
	}

	@Test public void testNotEqual() {
		MetadataArray array1 = new MetadataArray(Arrays.asList("one", "two", "three"), PrimitiveType.STRING);
		MetadataArray array2 = new MetadataArray(Arrays.asList("four", "two", "three"), PrimitiveType.STRING);
		assertFalse("should not be equal", array1.equals(array2));
	}

}
