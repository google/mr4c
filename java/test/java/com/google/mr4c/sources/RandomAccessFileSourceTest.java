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

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

public class RandomAccessFileSourceTest {

	private byte[] m_data = new byte[] { 33, 44, 55, 66};

	@Test public void testLocal() throws Exception {
		File file = new File("output/rafsource/test1.bin");
		URI uri = file.toURI();
		URIDataFileSink sink = new URIDataFileSink(uri);
		sink.writeFile(m_data);
		URIDataFileSource src = new URIDataFileSource(uri, "test1.bin", file);
		doTest(src, false);
	}

	@Test public void testStaged() throws Exception {
		BytesDataFileSource src = new BytesDataFileSource(m_data);
		doTest(src, true);
	}

	private void doTest(DataFileSource src, boolean staged) throws Exception {
		RandomAccessFileSource rafSrc = new RandomAccessFileSource(src);
		RandomAccessFile raf = rafSrc.getRandomAccess();
		assertEquals("staged flag", staged, rafSrc.isStaged());
		byte[] data = new byte[(int)raf.length()];
		raf.read(data);
		rafSrc.close();
		assertTrue("data", Arrays.equals(m_data, data));
	}

}
