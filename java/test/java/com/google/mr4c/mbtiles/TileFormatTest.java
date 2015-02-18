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

package com.google.mr4c.mbtiles;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import static org.junit.Assert.*;

public class TileFormatTest {

	@Test public void testGetByName() {
		assertEquals(TileFormat.JPG, TileFormat.getByName("jpg"));
		assertEquals(TileFormat.PNG, TileFormat.getByName("png"));
	}

	@Test(expected=IllegalStateException.class)
	public void testGetByNameNotFound() {
		TileFormat.getByName("blah");
	}

	@Test public void testExtractFromMetadata() {
		Map<String,String> meta = new HashMap<String,String>();
		assertNull(TileFormat.extractFromMetadata(meta));
		meta.put("format", "jpg");
		assertEquals(TileFormat.JPG, TileFormat.extractFromMetadata(meta));
	}
}




