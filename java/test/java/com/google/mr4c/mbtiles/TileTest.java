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

public class TileTest {

	private TileKey m_key;
	private byte[] m_data;
	private Tile m_tile;

	@Before public void setup() throws Exception {
		buildKey();
		buildData();
		m_tile = buildTile();
	}

	@Test public void testEquals() {
		assertEquals(m_tile, buildTile());
	}

	@Test public void testNotEqualKey() {
		Tile tile = new Tile(
			new TileKey(8,16, 24),
			m_data
		);
		assertFalse(m_tile.equals(tile));
	}

	@Test public void testNotEqualData() {
		Tile tile = new Tile(
			m_key,
			new byte[] { 55, 66}
		);
		assertFalse(m_tile.equals(tile));
	}

	private void buildKey() {
		m_key = new TileKey(10, 20, 30);
	}

	private void buildData() {
		m_data = new byte[] { 5, 6, 7, 8};
	}

	private Tile buildTile() {
		return new Tile(m_key, m_data);
	}
}




