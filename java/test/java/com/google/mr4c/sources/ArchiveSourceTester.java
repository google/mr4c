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
  * Runs an ArchiveSource through its basic operations.  Clients should expect
  * existing contents of the source to be wiped out by the test methods.
  * Call the public testXXX() methods one at a time.  Cleaning up the
  * source after each test is a good idea, but not strictly required.
*/
public class ArchiveSourceTester {

	private byte[] m_data1 = new byte[] { 23, 45, 66, 67, 70};
	private byte[] m_data2 = new byte[] { -55, 0, 127};
	private byte[] m_data3 = new byte[] { 12, -33, 69};
	private byte[] m_metadata1 = new byte[] { 11, 22, 33, -77 };
	private byte[] m_metadata2 = new byte[] { 123, 124, 125 };
	private String m_file1 = "file1";
	private String m_file2 = "file2";
	private String m_file3 = "file3";
	private String m_metafile1 = "meta1";
	private String m_metafile2 = "meta2";
	private List<String> m_files;
	private List<String> m_metafiles;

	public ArchiveSourceTester() {
		m_files = Arrays.asList(m_file1, m_file2, m_file3);
		m_metafiles = Arrays.asList(m_metafile1, m_metafile2);
	}

	/**
	  * tests reading files by FileDataSource.getFileBytes()
	*/
	public void testGetFileBytes(ArchiveSource src) throws IOException {
		src.clear();
		addAllTestData(src);
		checkFilesAdded(src);
	}

	public void testGetFileSize(ArchiveSource src) throws IOException {
		src.clear();
		addAllTestData(src);
		checkSize(src, m_file1, m_data1.length);
		checkSize(src, m_file2, m_data2.length);
		checkSize(src, m_file3, m_data3.length);
	}

	public void testFileExists(ArchiveSource src) throws IOException {
		src.clear();
		addAllTestData(src);
		assertTrue(src.fileExists(m_file1));
		assertTrue(src.fileExists(m_file2));
		assertTrue(src.fileExists(m_file3));
		assertFalse(src.fileExists("some_other_file"));
	}

	public void testGetSourceOnlyIfExists(ArchiveSource src) throws IOException {
		src.clear();
		addAllTestData(src);
		assertNotNull(src.getFileSourceOnlyIfExists(m_file1));
		assertNull(src.getFileSourceOnlyIfExists("some_other_file"));
	}

	/**
	  * tests reading metadata files by FileDataSource.getFileBytes()
	*/
	public void testGetMetadataBytes(ArchiveSource src) throws IOException {
		src.clear();
		addAllTestData(src);
		checkMetaAdded(src);
	}

	/**
	  * tests ArchiveSource.getAllFileNames() method
	*/
	public void testFileList(ArchiveSource src) throws IOException {
		src.clear();
		addAllTestData(src);
		List<String> files = new ArrayList<String>(src.getAllFileNames());
		Collections.sort(files);
		assertEquals(m_files,files);
	}

	/**
	  * tests ArchiveSource.getAllMetadataFileNames() method
	*/
	public void testMetadataFileList(ArchiveSource src) throws IOException {
		src.clear();
		addAllTestData(src);
		List<String> files = new ArrayList<String>(src.getAllMetadataFileNames());
		Collections.sort(files);
		assertEquals(m_metafiles,files);
	}

	/**
	  * tests reading files by FileDataSource.getFileInputStream()
	*/
	public void testGetFileByInputStream(ArchiveSource src) throws IOException {
		src.clear();
		addAllTestData(src);
		testGetFileByInputStream(src, m_file1, m_data1);
		testGetFileByInputStream(src, m_file2, m_data2);
		testGetFileByInputStream(src, m_file3, m_data3);
	}

	private void testGetFileByInputStream(ArchiveSource src, String name, byte[] data) throws IOException {
		testGetByInputStream(src.getFileSource(name), data);
	}

	/**
	  * tests reading metadata files by FileDataSource.getFileInputStream()
	*/
	public void testGetMetadataFileByInputStream(ArchiveSource src) throws IOException {
		src.clear();
		addAllTestData(src);
		testGetMetadataFileByInputStream(src, m_metafile1, m_metadata1);
		testGetMetadataFileByInputStream(src, m_metafile2, m_metadata2);
	}

	private void testGetMetadataFileByInputStream(ArchiveSource src, String name, byte[] data) throws IOException {
		testGetByInputStream(src.getMetadataFileSource(name), data);
	}

	private void testGetByInputStream(DataFileSource fileSrc, byte[] data) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		InputStream input = fileSrc.getFileInputStream();
		try {
			IOUtils.copy(input,output);
		} finally {
			input.close();
		}
		assertByteArrayEquals(data, output.toByteArray());
	}
		
	/**
	  * tests adding metadata files by FileDataSink.writeFile(InputStream)
	*/
	public void testAddFileByInputStream(ArchiveSource src) throws IOException {
		src.clear();
		try {
			src.startWrite();
			addFileByInputStream(src, m_file1, m_data1);
			addFileByInputStream(src, m_file2, m_data2);
			addFileByInputStream(src, m_file3, m_data3);
		} finally {
			src.finishWrite();
		}
		checkFilesAdded(src);
	}

	/**
	  * tests adding metadata files by FileDataSink.writeFile(InputStream)
	*/
	public void testAddMetadataFileByInputStream(ArchiveSource src) throws IOException {
		src.clear();
		try {
			src.startWrite();
			addMetadataFileByInputStream(src, m_metafile1, m_metadata1);
			addMetadataFileByInputStream(src, m_metafile2, m_metadata2);
		} finally {
			src.finishWrite();
		}
		checkMetaAdded(src);
	}

	private void addFileByInputStream(ArchiveSource src, String name, byte[] data) throws IOException {
		DataFileSink fileSink = src.getFileSink(name);
		addByInputStream(fileSink, data);
	}

	private void addMetadataFileByInputStream(ArchiveSource src, String name, byte[] data) throws IOException {
		DataFileSink fileSink = src.getMetadataFileSink(name);
		addByInputStream(fileSink, data);
	}

	private void addByInputStream(DataFileSink fileSink, byte[] data) throws IOException {
		
		InputStream input = new ByteArrayInputStream(data);
		fileSink.writeFile(input);
	}

	/**
	  * tests adding files by FileDataSink.getFileOutputStream()
	*/
	public void testAddFileByOutputStream(ArchiveSource src) throws IOException {
		src.clear();
		try {
			src.startWrite();
			addFileByOutputStream(src, m_file1, m_data1);
			addFileByOutputStream(src, m_file2, m_data2);
			addFileByOutputStream(src, m_file3, m_data3);
		} finally {
			src.finishWrite();
		}
		checkFilesAdded(src);
	}

	/**
	  * tests adding metadata files by FileDataSink.getFileOutputStream()
	*/
	public void testAddMetadataFileByOutputStream(ArchiveSource src) throws IOException {
		src.clear();
		try {
			src.startWrite();
			addMetadataFileByOutputStream(src, m_metafile1, m_metadata1);
			addMetadataFileByOutputStream(src, m_metafile2, m_metadata2);
		} finally {
			src.finishWrite();
		}
		checkMetaAdded(src);
	}

	private void addFileByOutputStream(ArchiveSource src, String name, byte[] data) throws IOException {
		DataFileSink fileSink = src.getFileSink(name);
		addByOutputStream(fileSink, data);
	}

	private void addMetadataFileByOutputStream(ArchiveSource src, String name, byte[] data) throws IOException {
		DataFileSink fileSink = src.getMetadataFileSink(name);
		addByOutputStream(fileSink, data);
	}

	private void addByOutputStream(DataFileSink fileSink, byte[] data) throws IOException {
		InputStream input = new ByteArrayInputStream(data);
		OutputStream output = fileSink.getFileOutputStream();
		try {
			IOUtils.copy(input,output);
		} finally {
			output.close();
		}
	}


	/**
	  * Tests clear() and exists() methods in a cycle
	*/
	public void testClearAndExists(ArchiveSource src) throws IOException {
		src.clear();
		assertFalse("Check doesn't exist after clear", src.exists());
		addAllTestData(src);
		assertTrue("Check exists after adding data", src.exists());
		src.clear();
		assertFalse("Check doesn't exist after clear", src.exists());
	}
		
	private void addAllTestData(ArchiveSource src) throws IOException {
		try {
			src.startWrite();
			writeFile(src, m_file1, m_data1);
			writeFile(src, m_file2, m_data2);
			writeFile(src, m_file3, m_data3);
			writeMeta(src, m_metafile1, m_metadata1);
			writeMeta(src, m_metafile2, m_metadata2);
		} finally {
			src.finishWrite();
		}
	}

	private void writeFile(ArchiveSource src, String name, byte[] data) throws IOException {
		DataFileSink sink = src.getFileSink(name);
		sink.writeFile(data);
	}

	private void writeMeta(ArchiveSource src, String name, byte[] data) throws IOException {
		DataFileSink sink = src.getMetadataFileSink(name);
		sink.writeFile(data);
	}

	private byte[] readFile(ArchiveSource src, String name) throws IOException {
		DataFileSource fileSrc = src.getFileSource(name);
		return fileSrc.getFileBytes();
	}

	private byte[] readMeta(ArchiveSource src, String name) throws IOException {
		DataFileSource fileSrc = src.getMetadataFileSource(name);
		return fileSrc.getFileBytes();
	}

	private void checkFilesAdded(ArchiveSource src) throws IOException {
		checkFileAdded(src, m_file1, m_data1);
		checkFileAdded(src, m_file2, m_data2);
		checkFileAdded(src, m_file3, m_data3);
	}
		
	private void checkFileAdded(ArchiveSource src, String name, byte[] expected) throws IOException {
		byte[] actual = readFile(src, name);
		assertByteArrayEquals(expected, actual);
	}

	private void checkMetaAdded(ArchiveSource src) throws IOException {
		checkMetaAdded(src, m_metafile1, m_metadata1);
		checkMetaAdded(src, m_metafile2, m_metadata2);
	}

	private void checkMetaAdded(ArchiveSource src, String name, byte[] expected) throws IOException {
		byte[] actual = readMeta(src, name);
		assertByteArrayEquals(expected, actual);
	}

	private void checkSize(ArchiveSource src, String name, int expected) throws IOException {
		DataFileSource fileSrc = src.getFileSource(name);
		assertEquals(expected, fileSrc.getFileSize());
	}

	private void assertByteArrayEquals(byte[] expected, byte[] actual) {
		List<Byte> expectedList = Arrays.asList(ArrayUtils.toObject(expected));
		List<Byte> actualList = Arrays.asList(ArrayUtils.toObject(actual));
		assertEquals(expectedList, actualList);
	}
	
}
