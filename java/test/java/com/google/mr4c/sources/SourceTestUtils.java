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

import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.sources.DatasetSource.WriteMode;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class SourceTestUtils {

	public static void populateSource(FileSource src) throws IOException {
		writeFile(src, "file1", new byte[] { 23, 45, 66} );
		writeFile(src, "file2", new byte[] { -55, 0, 127} );
		writeFile(src, "file3", new byte[] { 12, -33, 69} );
	}

	public static void populateSource(ArchiveSource src) throws IOException {
		src.startWrite();
		writeArchiveFile(src, "file1", new byte[] { 23, 45, 66} );
		writeArchiveFile(src, "file2", new byte[] { -55, 0, 127} );
		writeArchiveFile(src, "file3", new byte[] { 12, -33, 69} );
		writeArchiveMetadata(src, "meta1", new byte[] { -6, -13} );
		writeArchiveMetadata(src, "meta2", new byte[] { -9, 88} );
	}

	/**
	  * Returns a source containing a bunch of image files that can be used to test copy operations
	*/
	public static FileSource getTestInputSource() {
		File dir = new File("input/data/dataset/test1/input_data");
		return new DiskFileSource(dir);
	}

	public static void writeFile(FileSource src, String name, byte[] data) throws IOException {
		DataFileSink sink = src.getFileSink(name);
		sink.writeFile(data);
	}

	public static void writeArchiveFile(ArchiveSource src, String name, byte[] data) throws IOException {
		DataFileSink sink = src.getFileSink(name);
		sink.writeFile(data);
	}

	public static void writeArchiveMetadata(ArchiveSource src, String name, byte[] data) throws IOException {
		DataFileSink sink = src.getMetadataFileSink(name);
		sink.writeFile(data);
	}

	public static void testSource(DatasetSource src1, DatasetSource src2) throws IOException {
		SourceUtils.copySource(src1, src2);
		compareSources(src1, src2);
	}

	public static void compareSources(DatasetSource src1, DatasetSource src2) throws IOException {
		Dataset dataset1 = src1.readDataset();
		Dataset dataset2 = src2.readDataset();
		assertEquals(dataset1,dataset2);
	}

	public static void testCopySource(FileSource src1, FileSource src2, boolean streamOutput) throws IOException {
		SourceUtils.copySource(src1, src2, streamOutput);
		compareSources(src1, src2);
	}

	public static void testCopySource(ArchiveSource src1, ArchiveSource src2, boolean streamOutput) throws IOException {
		SourceUtils.copySource(src1, src2, streamOutput);
		compareSources(src1, src2);
	}

	public static void testCopySource(ArchiveSource src1, FileSource src2, boolean streamOutput) throws IOException {
		SourceUtils.copySource(src1, src2, streamOutput);
		compareSources(src1, src2);
	}

	public static void compareSources(FileSource expectedSrc, FileSource actualSrc) throws IOException {

		List<String> expectedFiles = new ArrayList<String>(expectedSrc.getAllFileNames());
		List<String> actualFiles = new ArrayList<String>(actualSrc.getAllFileNames());
		Collections.sort(expectedFiles);
		Collections.sort(actualFiles);

		assertEquals("checking same set of file names", actualFiles, expectedFiles);

		for ( String fileName : expectedFiles ) {
			DataFileSource expectedFile = expectedSrc.getFileSource(fileName);
			DataFileSource actualFile = actualSrc.getFileSource(fileName);
			compareFiles(expectedFile, actualFile);
		}
	}

	public static void compareSources(ArchiveSource expectedSrc, ArchiveSource actualSrc) throws IOException {

		List<String> expectedFiles = new ArrayList<String>(expectedSrc.getAllFileNames());
		List<String> actualFiles = new ArrayList<String>(actualSrc.getAllFileNames());
		Collections.sort(expectedFiles);
		Collections.sort(actualFiles);

		assertEquals("checking same set of file names", actualFiles, expectedFiles);

		for ( String fileName : expectedFiles ) {
			DataFileSource expectedFile = expectedSrc.getFileSource(fileName);
			DataFileSource actualFile = actualSrc.getFileSource(fileName);
			compareFiles(expectedFile, actualFile);
		}

		List<String> expectedMetaFiles = new ArrayList<String>(expectedSrc.getAllMetadataFileNames());
		List<String> actualMetaFiles = new ArrayList<String>(actualSrc.getAllMetadataFileNames());
		Collections.sort(expectedMetaFiles);
		Collections.sort(actualMetaFiles);

		assertEquals("checking same set of metadata file names", actualMetaFiles, expectedMetaFiles);

		for ( String fileName : expectedMetaFiles ) {
			DataFileSource expectedFile = expectedSrc.getMetadataFileSource(fileName);
			DataFileSource actualFile = actualSrc.getMetadataFileSource(fileName);
			compareFiles(expectedFile, actualFile);
		}

	}

	public static void compareSources(ArchiveSource expectedSrc, FileSource actualSrc) throws IOException {

		List<String> expectedFiles = new ArrayList<String>(expectedSrc.getAllFileNames());
		List<String> actualFiles = new ArrayList<String>(actualSrc.getAllFileNames());
		List<String> expectedMetaFiles = new ArrayList<String>(expectedSrc.getAllMetadataFileNames());
		List<String> allExpectedFiles = new ArrayList<String>(expectedFiles);
		allExpectedFiles.addAll(expectedMetaFiles);

		Collections.sort(allExpectedFiles);
		Collections.sort(actualFiles);

		assertEquals("checking same set of file names", allExpectedFiles, actualFiles);

		for ( String fileName : expectedFiles ) {
			DataFileSource expectedFile = expectedSrc.getFileSource(fileName);
			DataFileSource actualFile = actualSrc.getFileSource(fileName);
			compareFiles(expectedFile, actualFile);
		}

		for ( String fileName : expectedMetaFiles ) {
			DataFileSource expectedFile = expectedSrc.getMetadataFileSource(fileName);
			DataFileSource actualFile = actualSrc.getFileSource(fileName);
			compareFiles(expectedFile, actualFile);
		}

	}

	public static void compareFiles(DataFileSource expectedFile, DataFileSource actualFile) throws IOException {

		String msg = String.format("binary content doesn't match: expected file is %s; actual file is %s", expectedFile.getDescription(), actualFile.getDescription());

		InputStream expectedInput = null;
		InputStream actualInput = null;

		try {
			expectedInput = expectedFile.getFileInputStream();
			actualInput = actualFile.getFileInputStream();
			assertTrue(msg, IOUtils.contentEquals(expectedInput, actualInput));
		} finally {
			IOUtils.closeQuietly(actualInput);
			IOUtils.closeQuietly(expectedInput);
		}
			
	}



}
