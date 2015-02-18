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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class MBTilesTestUtil {

	public static Map<String,String> generateTestMetadata(int count) {
		Map<String,String> meta = new HashMap<String,String>();
		for ( int i=1; i<=count; i++ ) {
			meta.put("prop"+i, "val"+i);
		}
		return meta;
	}

	public static List<Tile> generateTestTiles(int count) {
		List<Tile> tiles = new ArrayList<Tile>();
		for ( int i=1; i<=count; i++ ) {
			tiles.add(buildTile(i));
		}
		return tiles;
	}

	private static Tile buildTile(int index) {
		int zoom = index;
		int x = index * zoom;
		int y = index * x;
		return buildTile(zoom, x, y);
	}

	private static Tile buildTile(int zoom, int x, int y) {
		TileKey key = new TileKey(zoom, x, y);
		byte[] data = new byte[] {
			(byte) zoom,
			(byte) x,
			(byte) y
		};
		return new Tile(key, data);
	}
	
	public static void checkTiles(MBTilesFile mbtiles, List<Tile> expectedTiles) throws Exception {
		Set<TileKey> keys = new HashSet<TileKey>();
		for ( Tile tile : expectedTiles ) {
			keys.add(tile.getKey());
			assertEquals(tile, mbtiles.findTile(tile.getKey()));
		}
		assertEquals(keys, mbtiles.getAllTileKeys());
	}

	public static void cleanup(Iterable<MBTilesFile> files ) throws IOException {
		for ( MBTilesFile file : files ) {
			file.close();
		}
	}

}
