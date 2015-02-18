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

package com.google.mr4c.keys;

import org.junit.*;
import static org.junit.Assert.*;

public class DataKeyDimensionTest {

	@Test public void testEquals() {
		DataKeyDimension dim1 = new DataKeyDimension("dimName");
		DataKeyDimension dim2 = new DataKeyDimension("dimName");
		assertEquals(dim1,dim2);
	}

	@Test public void testNotEqual() {
		DataKeyDimension dim1 = new DataKeyDimension("dimName");
		DataKeyDimension dim2 = new DataKeyDimension("dimName2");
		assertFalse(dim1.equals(dim2));
	}

	@Test public void testCompare() {
		DataKeyDimension dim1a = new DataKeyDimension("dim1");
		DataKeyDimension dim1b = new DataKeyDimension("dim1");
		DataKeyDimension dim2 = new DataKeyDimension("dim2");
		assertEquals(0, dim1a.compareTo(dim1b));
		assertEquals(-1, dim1a.compareTo(dim2));
		assertEquals(1, dim2.compareTo(dim1a));
	}

}
