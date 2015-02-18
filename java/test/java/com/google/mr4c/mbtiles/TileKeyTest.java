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

import org.junit.*;
import static org.junit.Assert.*;

public class TileKeyTest {

	private int m_zoom = 8;
	private int m_x = 25;
	private int m_y = 45;
	private TileKey m_key;

	@Before public void setup() throws Exception {
		m_key = buildKey();
	}

	@Test public void testEquals() {
		assertEquals(m_key, buildKey());
	}

	@Test public void testNotEqualZoom() {
		TileKey key = new TileKey(13, m_x, m_y);
		assertFalse(m_key.equals(key));
	}

	@Test public void testNotEqualX() {
		TileKey key = new TileKey(m_zoom, 333, m_y);
		assertFalse(m_key.equals(key));
	}

	@Test public void testNotEqualY() {
		TileKey key = new TileKey(m_zoom, m_x, 666);
		assertFalse(m_key.equals(key));
	}

	private TileKey buildKey() {
		return new TileKey(m_zoom, m_x, m_y);
	}
}




