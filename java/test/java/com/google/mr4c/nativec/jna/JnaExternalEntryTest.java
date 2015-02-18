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

package com.google.mr4c.nativec.jna;

import com.google.mr4c.algorithm.AlgorithmContext;
import com.google.mr4c.algorithm.AlgorithmData;
import com.google.mr4c.algorithm.AlgorithmDataTestUtils;
import com.google.mr4c.algorithm.LogLevel;
import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.dataset.DatasetContext;
import com.google.mr4c.dataset.DatasetTestUtils;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;
import com.google.mr4c.message.Message;
import com.google.mr4c.nativec.ExternalAlgorithmData;
import com.google.mr4c.nativec.ExternalContext;
import com.google.mr4c.nativec.ExternalDataFile;
import com.google.mr4c.nativec.ExternalDataset;
import com.google.mr4c.nativec.ExternalEntry;
import com.google.mr4c.nativec.ExternalFactory;
import com.google.mr4c.nativec.ExternalDatasetSerializer;
import com.google.mr4c.nativec.ExternalAlgorithmDataSerializer;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.serialize.SerializerFactory;
import com.google.mr4c.sources.BytesDataFileSink;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.junit.*;
import static org.junit.Assert.*;

public class JnaExternalEntryTest {

	private Dataset m_dataset;
	private DatasetSerializer m_keySerializer;
	private ExternalAlgorithmDataSerializer m_algoDataSerializer;
	private ExternalDatasetSerializer m_datasetSerializer;
	private ExternalFactory m_extFactory;
	private ExternalEntry m_entry;

	@Before public void setup() throws Exception {
		m_dataset = DatasetTestUtils.buildDataset1();
		SerializerFactory serFact = SerializerFactories.getSerializerFactory("application/json");
		m_extFactory = new JnaExternalFactory(serFact);
		m_entry = m_extFactory.newEntry();
		m_keySerializer = serFact.createDatasetSerializer();
		m_datasetSerializer = new ExternalDatasetSerializer(
			serFact.createDatasetSerializer(),
			m_extFactory
		);
		m_algoDataSerializer = new ExternalAlgorithmDataSerializer(
			serFact,
			m_extFactory
		);

	}

	@Test public void testCloneDataset() throws Exception {
		ExternalDataset extDataset1 = m_datasetSerializer.serializeDataset("test", m_dataset);
		ExternalDataset extDataset2 = m_entry.cloneDataset(extDataset1);
		
		Dataset dataset2 = m_datasetSerializer.deserializeDataset(extDataset2);
		
		assertEquals(m_dataset, dataset2);
	}

	@Test public void testCloneAlgorithmData() throws Exception {
		AlgorithmData srcData = AlgorithmDataTestUtils.buildAlgorithmData1();

		ExternalAlgorithmData extSrcData = m_extFactory.newAlgorithmData();
 		m_algoDataSerializer.serializeInputData(srcData, extSrcData);
 		m_algoDataSerializer.serializeOutputData(srcData, extSrcData);
		ExternalAlgorithmData extResultData = m_entry.cloneAlgorithmData(extSrcData);
		AlgorithmData resultData = new AlgorithmData();
 		m_algoDataSerializer.deserializeInputData(resultData, extResultData);
 		m_algoDataSerializer.deserializeOutputData(resultData, extResultData);
		assertEquals(srcData, resultData);
	}

	@Test public void testLogging() throws Exception {
		testLogging(LogLevel.INFO, "some info message");
		testLogging(LogLevel.ERROR, "some error message");
		testLogging(LogLevel.DEBUG, "some debug message");
		testLogging(LogLevel.WARN, "some warning message");
	}
	
	@Test public void testProgress() throws Exception {
		testProgress(55.5f, "some progress message");
	}

	@Test public void testFailure() throws Exception {
		testFailure("some failure message");
	}

	@Test public void testSendMessage() throws Exception {
		testSendMessage(
			new Message(
				"some_topic",
				"blah blah blah",
				"text/special"
			)
		);
	}

	@Test public void testAddFileNoStreaming() throws Exception {
		DataKey key = quickKey("val1", "dim1");
		byte[] bytes = new byte[] { 55, 66, 77, 88 };
		testAddFile(key, bytes, false);
	}

	@Test public void testAddFileWithStreaming() throws Exception {
		DataKey key = quickKey("val1", "dim1");
		byte[] bytes = new byte[] { 55, 66, 77, 88 };
		testAddFile(key, bytes, true);
	}

	@Test public void testGetLogFiles() throws Exception {
		List<String> expected = new ArrayList<String>(Arrays.asList("./logs/mr4c-algo.log", "./logs/mr4c-native.log"));
		Collections.sort(expected);
		List<String> actual = new ArrayList<String>(m_entry.getLogFiles());
		Collections.sort(actual);
		assertEquals(expected, actual);
	}

	@Test public void testFindFileFound() throws Exception {
		testFindFile(true);
	}

	@Test public void testFindFileNotFound() throws Exception {
		testFindFile(false);
	}

	private void testFindFile(boolean find) throws Exception {
		final DataKey key = quickKey("val1", "dim1");
		final DataFile file1 = new DataFile(new byte[] {45, 56, 67}, "image/jpeg");
		Dataset dataset = new Dataset();
		
		TestDatasetContext context = new TestDatasetContext();
		dataset.setContext(context);
		if ( find ) {
			context.m_key = key;
			context.m_file = file1;
		}
		ExternalDataset extDataset = m_datasetSerializer.serializeDataset("test", dataset);
		String serKey = m_keySerializer.serializeDataKey(key);
		ExternalDataFile extFile = m_entry.testFindFile(extDataset, serKey);
		if ( find ) {
			assertNotNull(extFile);
			DataFile file2 = m_datasetSerializer.deserializeDataFile(extFile);
			assertEquals(file1, file2);
		} else {
			assertNull(extFile);
		}
	}

	@Test public void testReadFileAsRandomAccess() throws Exception {
		final DataKey key = quickKey("val1", "dim1");
		final DataFile file1 = quickFile(200, "image/jpeg");
		Dataset dataset = new Dataset();
		dataset.addFile(key, file1);
		
		ExternalDataset extDataset = m_datasetSerializer.serializeDataset("test", dataset);
		String serKey = m_keySerializer.serializeDataKey(key);
		ExternalDataFile extFile = m_entry.testReadFileAsRandomAccess(extDataset, serKey);
		DataFile file2 = m_datasetSerializer.deserializeDataFile(extFile);
		assertEquals(file1, file2);
	}

	@Test public void testWriteFileAsRandomAccess() throws Exception {
		final DataKey key = quickKey("val1", "dim1");
		final DataFile file = quickFile(200, "image/jpeg");
		Dataset dataset = new Dataset();
		TestDatasetContext context = new TestDatasetContext();
		context.m_output = true;
		dataset.setContext(context);
		ExternalDataFile extFile = m_datasetSerializer.serializeDataFile(key, file);
		ExternalDataset extDataset = m_datasetSerializer.serializeDataset("test", dataset);
		m_entry.testWriteFileAsRandomAccess(extDataset, extFile);
		assertEquals(key, context.m_key);
		assertTrue(file.equalsIgnoreContent(context.m_file));
		assertTrue(Arrays.equals(file.getBytes(), context.m_sink.getWrittenBytes()));
	}

	private DataKey quickKey(String val, String dim) {
		return DataKeyFactory.newKey(
			new DataKeyElement(
				val,
				new DataKeyDimension(dim)
			)
		);
	}

	private DataFile quickFile(int size, String type) {
		byte[] data = new byte[200];
		for ( int i=0; i<200; i++ ) {
			data[i] = (byte)i;
		}
		return new DataFile(data, type);
	}

	@Test public void testLoadLib() throws Exception {
		com.sun.jna.NativeLibrary.getInstance("mr4c");
	}

	private void testLogging(LogLevel level, String msg) {
		TestContext context = new TestContext();
		ExternalContext extContext = m_extFactory.newContext(context);
		m_entry.testLogging(extContext, level, msg);
		assertEquals(level, context.m_logLevel);
		assertEquals(msg, context.m_logMsg);
	}

	private void testProgress(float percentDone, String msg) {
		TestContext context = new TestContext();
		ExternalContext extContext = m_extFactory.newContext(context);
		m_entry.testProgressReporting(extContext, percentDone, msg);
		assertEquals(percentDone, context.m_percentDone, 0.0f);
		assertEquals(msg, context.m_progMsg);
	}

	private void testFailure(String msg) {
		TestContext context = new TestContext();
		ExternalContext extContext = m_extFactory.newContext(context);
		m_entry.testFailureReporting(extContext, msg);
		assertTrue(msg, context.isFailed());
		assertEquals(msg, context.m_failMsg);
	}

	private void testSendMessage(Message msg) {
		TestContext context = new TestContext();
		ExternalContext extContext = m_extFactory.newContext(context);
		m_entry.testSendMessage(extContext, msg);
		assertEquals(msg, context.m_msg);
	}

	private void testAddFile(DataKey key, byte[] bytes, boolean stream) throws Exception {
		DataFile file = new DataFile(bytes, "image/jpeg");
		Dataset dataset = new Dataset();
		TestDatasetContext context = new TestDatasetContext();
		context.m_output = true;
		dataset.setContext(context);
		ExternalDataFile extFile = m_datasetSerializer.serializeDataFile(key, file);
		ExternalDataset extDataset = m_datasetSerializer.serializeDataset("test", dataset);
		m_entry.testAddFile(extDataset, extFile, stream);
		assertEquals(key, context.m_key);
		assertTrue(file.equalsIgnoreContent(context.m_file));
		assertTrue(Arrays.equals(file.getBytes(), context.m_sink.getWrittenBytes()));
	}

	@Test public void testGetFileNameFound() throws Exception {
		testGetFileName(true);
	}

	@Test public void testGetFileNameNotFound() throws Exception {
		testGetFileName(false);
	}

	private void testGetFileName(boolean find) throws Exception {
		DataKey key = quickKey("val1", "dim1");
		String fileName = "file name";
		Dataset dataset = new Dataset();
		TestDatasetContext context = new TestDatasetContext();
		dataset.setContext(context);
		if ( find ) {
			context.m_key = key;
			context.m_fileName = fileName;
		}
		ExternalDataset extDataset = m_datasetSerializer.serializeDataset("test", dataset);
		String serKey = m_keySerializer.serializeDataKey(key);
		String result = m_entry.testGetFileName(extDataset, serKey);
		if ( find ) {
			assertEquals(fileName, result);
		} else {
			assertTrue("Empty or null string", StringUtils.isEmpty(result));
		}
	}

	@Test public void testIsQueryOnlyTrue() throws Exception {
		testIsQueryOnly(true);
	}

	@Test public void testIsQueryOnlyFalse() throws Exception {
		testIsQueryOnly(false);
	}

	private void testIsQueryOnly(boolean queryOnly) throws Exception {
		Dataset dataset = new Dataset();
		TestDatasetContext context = new TestDatasetContext();
		dataset.setContext(context);
		context.m_queryOnly = queryOnly;
		ExternalDataset extDataset = m_datasetSerializer.serializeDataset("test", dataset);
		boolean result = m_entry.testIsQueryOnly(extDataset);
		assertEquals(queryOnly, result);
	}

	private static class TestContext implements AlgorithmContext {

		LogLevel m_logLevel;
		String m_logMsg;
		float m_percentDone;
		String m_progMsg;
		boolean m_failed;
		String m_failMsg;
		private Message m_msg;

		public void log(LogLevel level, String msg) {
			m_logLevel = level;
			m_logMsg = msg;
		}

		public void progress(float percentDone, String msg) {
			m_percentDone = percentDone;
			m_progMsg = msg;
		}

		public void failure(String msg) {
			m_failed = true;
			m_failMsg = msg;
		}

		public boolean isFailed() {
			return m_failed;
		}

		public String getEnvironmentDescription() {
			return "testing";
		}

		public void sendMessage(Message msg) {
			m_msg = msg;
		}
	}

	private static class TestDatasetContext implements DatasetContext {

		private boolean m_output=false;
		private boolean m_queryOnly=false;
		private DataKey m_key;
		private DataFile m_file;
		private String m_fileName;
		private BytesDataFileSink m_sink;

		public DataFile findFile(DataKey key) {
			return key.equals(m_key) ? m_file : null;
		}

		public boolean isOutput() {
			return m_output;
		}

		public boolean isQueryOnly() {
			return m_queryOnly;
		}

		public void addFile(DataKey key, DataFile file) throws IOException {
			m_key = key;
			m_file = file;
			m_sink = new BytesDataFileSink();
			if ( file.hasContent() ) {
				// capture it now
				m_sink.writeFile(file.getBytes());
			} else {
				// native side will write after return
				file.setFileSink(m_sink);
			}
		}

		public String getFileName(DataKey key) {
			return key.equals(m_key) ? m_fileName : null;
		}

	}

}

