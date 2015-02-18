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

import org.junit.*;
import static org.junit.Assert.*;

public class MetadataFieldTest {


	@Test public void testEqual() {
		MetadataField field1 = new MetadataField(new Integer(55), PrimitiveType.INTEGER);
		MetadataField field2 = new MetadataField(new Integer(55), PrimitiveType.INTEGER);
		assertEquals("should be equal", field1,field2);
	}

	@Test public void testNotEqual() {
		MetadataField field1 = new MetadataField(new Integer(55), PrimitiveType.INTEGER);
		MetadataField field2 = new MetadataField(new Integer(65), PrimitiveType.INTEGER);
		assertFalse("should not be equal", field1.equals(field2));
	}

}
