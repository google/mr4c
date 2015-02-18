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

import com.google.mr4c.sources.DatasetSource.SourceType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class SourceUtilsTest {

	private FileSource m_fileSrc;
	private FileSource m_fileDest;
	private ArchiveSource m_archSrc;
	private ArchiveSource m_archDest;

	@Before public void setUp() throws Exception {
		m_fileSrc = new InMemoryFileSource();
		m_fileDest = new InMemoryFileSource();
		m_archSrc = new InMemoryArchiveSource();
		m_archDest = new InMemoryArchiveSource();
		SourceTestUtils.populateSource(m_fileSrc);
		SourceTestUtils.populateSource(m_archSrc);
	}

	@Test public void testCopyFileWithStream() throws Exception {
		SourceUtils.copyFile(m_fileSrc, "file1", m_fileDest, "file1_dest", true);
		SourceTestUtils.compareFiles(m_fileSrc.getFileSource("file1"), m_fileDest.getFileSource("file1_dest") );
	}

	@Test public void testCopyFileWithoutStream() throws Exception {
		SourceUtils.copyFile(m_fileSrc, "file1", m_fileDest, "file1_dest", false);
		SourceTestUtils.compareFiles(m_fileSrc.getFileSource("file1"), m_fileDest.getFileSource("file1_dest") );
	}

	@Test public void testCopyFileSourceWithStream() throws Exception {
		SourceUtils.copySource(m_fileSrc, m_fileDest, true);
		SourceTestUtils.compareSources(m_fileSrc, m_fileDest);
	}

	@Test public void testCopyFileSourceWithoutStream() throws Exception {
		SourceUtils.copySource(m_fileSrc, m_fileDest, false);
		SourceTestUtils.compareSources(m_fileSrc, m_fileDest);
	}

	@Test public void testCopyArchiveSourceWithStream() throws Exception {
		SourceUtils.copySource(m_archSrc, m_archDest, true);
		SourceTestUtils.compareSources(m_archSrc, m_archDest);
	}

	@Test public void testCopyArchiveSourceWithoutStream() throws Exception {
		SourceUtils.copySource(m_archSrc, m_archDest, false);
		SourceTestUtils.compareSources(m_archSrc, m_archDest);
	}

	@Test public void testCopyArchiveToFileSourceWithStream() throws Exception {
		SourceUtils.copySource(m_archSrc, m_fileDest, true);
		SourceTestUtils.compareSources(m_archSrc, m_fileDest);
	}

	@Test public void testCopyArchiveToFileSourceWithoutStream() throws Exception {
		SourceUtils.copySource(m_archSrc, m_fileDest, false);
		SourceTestUtils.compareSources(m_archSrc, m_fileDest);
	}
 
	@Test public void testSortSourceNamesByType() {
		Map<String,DatasetSource> srcMap = new HashMap<String,DatasetSource>();
		srcMap.put("out1", new NullDatasetSource());
		srcMap.put("out2", new NullDatasetSource());
		srcMap.put("log1", new NullDatasetSource(SourceType.LOGS));
		srcMap.put("log2", new NullDatasetSource(SourceType.LOGS));

		Map<SourceType,Set<String>> expected = new HashMap<SourceType,Set<String>>();

		expected.put(SourceType.DATA, new HashSet<String>(Arrays.asList("out1", "out2")));
		expected.put(SourceType.LOGS, new HashSet<String>(Arrays.asList("log1", "log2")));
		Map<SourceType,Set<String>> actual = SourceUtils.sortSourceNamesByType(srcMap);
		assertEquals(expected, actual);
	}
}
