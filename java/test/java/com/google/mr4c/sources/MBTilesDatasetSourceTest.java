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

package com.google.mr4c.sources;

import com.google.mr4c.hadoop.HadoopTestUtils;
import com.google.mr4c.mbtiles.MBTilesFile;
import com.google.mr4c.mbtiles.MBTilesTestUtil;
import com.google.mr4c.mbtiles.Tile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;

import org.junit.*;
import static org.junit.Assert.*;

public class MBTilesDatasetSourceTest {

	private List<Tile> m_tiles;
	private Map<String,String> m_meta;
	private List<MBTilesFile> m_cleanup = new ArrayList<MBTilesFile>();

	@Before public void setUp() throws Exception {
		buildTestData();
	}

	@After public void teardown() throws Exception {
		MBTilesTestUtil.cleanup(m_cleanup);
	}

	@Test public void testRoundTrip() throws Exception {

		// files to use
		File dir = new File("output/mbtilessource"); 
		File file1 = new File(dir, "test1.db"); 
		File file2 = new File(dir, "test2.db"); 

		// save a file on disk
		saveInputFile(file1);

		// test copying as dataset source
		MBTilesDatasetSource src1 = new MBTilesDatasetSource(file1.toURI());
		MBTilesDatasetSource src2 = new MBTilesDatasetSource(file2.toURI());
		SourceTestUtils.testSource(src1, src2);

		// make sure its really the same mbtiles on disk
		MBTilesFile mbtiles2 = MBTilesFile.create(file2, MBTilesFile.FileMode.READ_ONLY);
		m_cleanup.add(mbtiles2);
		MBTilesTestUtil.checkTiles(mbtiles2, m_tiles);
		assertEquals(m_meta, mbtiles2.getMetadataMap());
		mbtiles2.close();
		
	}

	@Test public void testStaging() throws Exception {
		// save a file on disk
		File file1 = new File("output/mbtilessource/test_stage1.db"); 
		saveInputFile(file1);

		// specify location in HDFS
		FileSystem fs = HadoopTestUtils.getTestDFS();
		Path root = new Path(fs.getUri());
		Path file2 = new Path(root, "/test/sources/MBTilesDatasetSourceTest/test_stage2.db");

		// standard source test
		MBTilesDatasetSource src1 = new MBTilesDatasetSource(file1.toURI());
		MBTilesDatasetSource src2 = new MBTilesDatasetSource(file2.toUri());
		SourceTestUtils.testSource(src1, src2);
	}

	private void saveInputFile(File file) throws Exception {
		MBTilesFile mbtiles = MBTilesFile.create(file, MBTilesFile.FileMode.REPLACE);
		m_cleanup.add(mbtiles);
		mbtiles.addTiles(m_tiles);
		mbtiles.addMetadata(m_meta);
		mbtiles.close();
	}

	private void buildTestData() {
		m_meta = MBTilesTestUtil.generateTestMetadata(3);
		m_tiles = MBTilesTestUtil.generateTestTiles(3);
	}

}
