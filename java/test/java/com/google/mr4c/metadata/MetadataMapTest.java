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
import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import static org.junit.Assert.*;

public class MetadataMapTest {


	@Test public void testEqual() {
		MetadataMap map1 = buildMap1();
		MetadataMap map2 = buildMap1();
		assertEquals("should be equal", map1,map2);
	}

	@Test public void testKeyNotEqual() {
		MetadataMap map1 = buildMap1();
		MetadataMap map2 = buildMap2();
		assertFalse("should not be equal", map1.equals(map2));
	}

	@Test public void testValueNotEqual() {
		MetadataMap map1 = buildMap1();
		MetadataMap map2 = buildMap3();
		assertFalse("should not be equal", map1.equals(map2));
	}

	private MetadataMap buildMap1() {
		Map map = new HashMap();
		map.put("f1", new MetadataField(new Integer(234), PrimitiveType.INTEGER));
		map.put("f2", new MetadataArray(Arrays.asList(33,44,55), PrimitiveType.INTEGER));
		map.put("f3", new MetadataField(new Float(88.77), PrimitiveType.FLOAT));
		return new MetadataMap(map);
	}

	private MetadataMap buildMap2() {
		// key "f3" becomes key "f4"
		Map map = new HashMap();
		map.put("f1", new MetadataField(new Integer(234), PrimitiveType.INTEGER));
		map.put("f2", new MetadataArray(Arrays.asList(33,44,55), PrimitiveType.INTEGER));
		map.put("f4", new MetadataField(new Float(88.77), PrimitiveType.FLOAT));
		return new MetadataMap(map);
	}

	private MetadataMap buildMap3() {
		// value for "f1" is different
		Map map = new HashMap();
		map.put("f1", new MetadataField(new Integer(2345), PrimitiveType.INTEGER));
		map.put("f2", new MetadataArray(Arrays.asList(33,44,55), PrimitiveType.INTEGER));
		map.put("f3", new MetadataField(new Float(88.77), PrimitiveType.FLOAT));
		return new MetadataMap(map);
	}

}
