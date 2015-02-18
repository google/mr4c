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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.*;
import static org.junit.Assert.*;

public class MBTilesFileTest {

	private List<Tile> m_tiles;
	private Map<String,String> m_meta;
	private File m_file = new File("output/mbtiles/test.db");
	private MBTilesFile m_memoryFile;
	private List<MBTilesFile> m_cleanup = new ArrayList<MBTilesFile>();

	@Before public void setup() throws Exception {
		buildTestData();
		m_memoryFile = MBTilesFile.createInMemory();
		m_cleanup.add(m_memoryFile);
		deleteTestFile();
	}

	@After public void teardown() throws Exception {
		MBTilesTestUtil.cleanup(m_cleanup);
	}

	@Test public void testMetadata() throws Exception {
		addMetadata(m_memoryFile);
		checkMetadata(m_memoryFile);
	}

	@Test public void testAddTiles() throws Exception {
		addTiles(m_memoryFile);
		checkTiles(m_memoryFile);
	}

	@Test public void testGetFormat() throws Exception {
		assertNull(m_memoryFile.getFormat(false));
		assertEquals(TileFormat.PNG, m_memoryFile.getFormat(true));
		m_memoryFile.setFormat(TileFormat.JPG);
		assertEquals(TileFormat.JPG, m_memoryFile.getFormat(true));
		assertEquals(TileFormat.JPG, m_memoryFile.getFormat(false));
	}
		
	@Test public void testFindTileNotFound() throws Exception {
		TileKey key = new TileKey(0,0,0);
		assertNull(m_memoryFile.findTile(key));
	}

	@Test(expected=IllegalStateException.class)
	public void testReadOnly() throws Exception {
		MBTilesFile mb1 = MBTilesFile.create(m_file, MBTilesFile.FileMode.REPLACE);
		m_cleanup.add(mb1);
		addTiles(mb1);
		addMetadata(mb1);
		checkTiles(mb1);
		checkMetadata(mb1);
		mb1.close();
		MBTilesFile mb2 = MBTilesFile.create(m_file, MBTilesFile.FileMode.READ_ONLY);
		m_cleanup.add(mb2);
		checkTiles(mb2);
		checkMetadata(mb2);
		mb2.addMetadata("whatever", "yo");
	}
		
	@Test(expected=FileNotFoundException.class)
	public void testReadOnlyNoFile() throws Exception {
		File notFound = new File("input/abcdefghijk");
		MBTilesFile.create(notFound, MBTilesFile.FileMode.READ_ONLY);
	}

	@Test public void testUpdate() throws Exception {
		MBTilesFile mb1 = MBTilesFile.create(m_file, MBTilesFile.FileMode.REPLACE);
		m_cleanup.add(mb1);
		addTiles(mb1);
		addMetadata(mb1);
		checkTiles(mb1);
		checkMetadata(mb1);
		mb1.close();
		MBTilesFile mb2 = MBTilesFile.create(m_file, MBTilesFile.FileMode.UPDATE);
		m_cleanup.add(mb2);
		checkTiles(mb2);
		checkMetadata(mb2);
		mb2.close();
	}
		
	@Test public void testReplace() throws Exception {
		MBTilesFile mb1 = MBTilesFile.create(m_file, MBTilesFile.FileMode.REPLACE);
		addTiles(mb1);
		addMetadata(mb1);
		checkTiles(mb1);
		checkMetadata(mb1);
		mb1.close();
		MBTilesFile mb2 = MBTilesFile.create(m_file, MBTilesFile.FileMode.REPLACE);
		assertEquals(Collections.emptySet(), mb2.getAllTileKeys());
		assertEquals(Collections.emptyMap(), mb2.getMetadataMap());
		mb2.close();
	}
		
	private void addMetadata(MBTilesFile mbtiles) throws Exception {
		mbtiles.addMetadata(m_meta);
	}

	private void checkMetadata(MBTilesFile mbtiles) throws Exception {
		assertEquals(m_meta, mbtiles.getMetadataMap());
	}

	private void addTiles(MBTilesFile mbtiles) throws Exception {
		mbtiles.addTiles(m_tiles);
	}

	private void checkTiles(MBTilesFile mbtiles) throws Exception {
		MBTilesTestUtil.checkTiles(mbtiles, m_tiles);
	}

	private void buildTestData() {
		m_meta = MBTilesTestUtil.generateTestMetadata(3);
		m_tiles = MBTilesTestUtil.generateTestTiles(3);
	}

	private void deleteTestFile() throws Exception {
		if ( m_file.exists() ) {
			if ( !m_file.delete() ) {
				throw new Exception("Can't delete test file");
			}
		}
	}

	
}

