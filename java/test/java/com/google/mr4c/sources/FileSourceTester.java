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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.io.IOUtils;

import org.junit.*;
import static org.junit.Assert.*;

/**
  * Runs a FileSource through its basic operations.  Clients should prepare a 
  * file source that points to some scratch space that can be overwritten.
  * Then call the public testXXX() methods one at a time.  Cleaning up the
  * source after each test is a good idea, but not strictly required.
*/
public class FileSourceTester {

	private byte[] m_data1 = new byte[] { 23, 45, 66, 67, 70};
	private byte[] m_data2 = new byte[] { -55, 0, 127};
	private byte[] m_data3 = new byte[] { 12, -33, 69}; 
	private String[] m_flatFiles = new String[] { "file1", "file2", "file3" };
	private String[] m_recurseFiles = new String[] {
		 "dir1/file1",
		 "dir1/dir2/file2",
		 "dir1/dir2/file3"
	};
	private String m_file1;
	private String m_file2;
	private String m_file3;
	private List<String> m_files;

	public FileSourceTester() {
		this(true);
	}

	public FileSourceTester(boolean flat) {
		String[] testFiles = flat ? m_flatFiles : m_recurseFiles;
		m_file1 = testFiles[0];
		m_file2 = testFiles[1];
		m_file3 = testFiles[2];
		m_files = Arrays.asList(testFiles);
		Collections.sort(m_files);
	}

	// other tests depend on this one - will use getFileBytes to check what was added by various means
	public void testGetBytes(FileSource src) throws IOException {
		addAllTestData(src);
		checkAdded(src, m_file1, m_data1);
		checkAdded(src, m_file2, m_data2);
		checkAdded(src, m_file3, m_data3);
	}

	public void testGetFileSize(FileSource src) throws IOException {
		addAllTestData(src);
		checkSize(src, m_file1, m_data1.length);
		checkSize(src, m_file2, m_data2.length);
		checkSize(src, m_file3, m_data3.length);
	}

	public void testFileExists(FileSource src) throws IOException {
		addAllTestData(src);
		assertTrue(src.fileExists(m_file1));
		assertTrue(src.fileExists(m_file2));
		assertTrue(src.fileExists(m_file3));
		assertFalse(src.fileExists("some_other_file"));
	}

	public void testGetSourceOnlyIfExists(FileSource src) throws IOException {
		addAllTestData(src);
		assertNotNull(src.getFileSourceOnlyIfExists(m_file1));
		assertNull(src.getFileSourceOnlyIfExists("some_other_file"));
	}

	public void testFileList(FileSource src) throws IOException {
		// get extra files that we aren't going to add anyway
		List<String> start = src.getAllFileNames();
		start.removeAll(m_files);
		addAllTestData(src);
		List<String> files = new ArrayList<String>(src.getAllFileNames());
		files.removeAll(start);
		Collections.sort(files);
		assertEquals(m_files,files);
	}

	public void testGetByInputStream(FileSource src) throws IOException {
		writeFile(src, m_file1, m_data1);
		DataFileSource fileSrc = src.getFileSource(m_file1);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		InputStream input = fileSrc.getFileInputStream();
		try {
			IOUtils.copy(input,output);
		} finally {
			input.close();
		}
		assertByteArrayEquals(m_data1, output.toByteArray());
	}
		
	public void testAddByInputStream(FileSource src) throws IOException {
		InputStream input = new ByteArrayInputStream(m_data1);
		DataFileSink fileSink = src.getFileSink(m_file1);
		fileSink.writeFile(input);
		checkAdded(src, m_file1, m_data1);
	}

	public void testAddByOutputStream(FileSource src) throws IOException {
		DataFileSink fileSink = src.getFileSink(m_file1);
		InputStream input = new ByteArrayInputStream(m_data1);
		OutputStream output = fileSink.getFileOutputStream();
		try {
			IOUtils.copy(input,output);
		} finally {
			output.close();
		}
		checkAdded(src, m_file1, m_data1);
	}

	public void testClear(FileSource src) throws IOException {
		addAllTestData(src);
		src.clear();
		assertTrue("Check no files after clear()", src.getAllFileNames().isEmpty());
	}

	private void addAllTestData(FileSource src) throws IOException {
		writeFile(src, m_file1, m_data1);
		writeFile(src, m_file2, m_data2);
		writeFile(src, m_file3, m_data3);
		src.close();
	}

	private void writeFile(FileSource src, String name, byte[] data) throws IOException {
		DataFileSink sink = src.getFileSink(name);
		sink.writeFile(data);
	}

	private byte[] readFile(FileSource src, String name) throws IOException {
		DataFileSource fileSrc = src.getFileSource(name);
		return fileSrc.getFileBytes();
	}

	private void checkAdded(FileSource src, String name, byte[] expected) throws IOException {
		byte[] actual = readFile(src, name);
		assertByteArrayEquals(expected, actual);
	}

	private void checkSize(FileSource src, String name, int expected) throws IOException {
		DataFileSource fileSrc = src.getFileSource(name);
		assertEquals(expected, fileSrc.getFileSize());
	}

	private void assertByteArrayEquals(byte[] expected, byte[] actual) {
		List<Byte> expectedList = Arrays.asList(ArrayUtils.toObject(expected));
		List<Byte> actualList = Arrays.asList(ArrayUtils.toObject(actual));
		assertEquals(expectedList, actualList);
	}
	
}
