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

package com.google.mr4c.hadoop;

import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.sources.DatasetSource.WriteMode;
import com.google.mr4c.testing.TestDataManager;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import org.junit.*;
import static org.junit.Assert.*;

public class MR4CMapperTest {


	private List<String> m_inputs = Arrays.asList("input1", "input2", "input3");
	private List<String> m_outputs = Arrays.asList("output1", "output2");
	private TestDataManager m_mgr = new TestDataManager();

	@Before public void setUp() {

		m_mgr.addDimension("dim1", Arrays.asList("d1v1", "d1v2", "d1v3"));
		m_mgr.addDimension("dim2", Arrays.asList("d2v1", "d2v2", "d2v3"));
		for( String input : m_inputs ) {
			m_mgr.addInputDataset(input);
		}
		for( String output : m_outputs ) {
			m_mgr.addOutputDataset(output);
		}
		m_mgr.readyToTest();
	} 

	@Test public void testMap() throws Exception {
		MR4CMapper mapper = new MR4CMapper(m_mgr.getExecutionSource());
		TestOutputCollector collector = new TestOutputCollector();

		Text key = new Text("1");
		DataKeyList value = new DataKeyList(m_mgr.getKeys());

		mapper.map(key, value, collector, Reporter.NULL);
		
		for ( String  name : m_outputs ) {
			m_mgr.assertWriteCalled(name); // no direct dataset write calls expected, just the per-file writes
			m_mgr.assertFileContentCorrect(name);

			assertFalse(m_mgr.copyToFinalCalled(name));

			Dataset expected = TestDataManager.buildDataset(name, m_mgr.getKeys());
			expected.release();
			Dataset actual = collector.m_outputs.get(name);
			assertEquals(expected, actual);
		}
	}


	class TestOutputCollector implements OutputCollector<Text,Text> {
		private Map<String,Dataset> m_outputs = new HashMap<String,Dataset>();
		private DatasetSerializer m_serializer = SerializerFactories.getSerializerFactory("application/json").createDatasetSerializer();

		public void collect(Text key, Text value) throws IOException {
			//key --> name
			String name = key.toString();
			if ( m_outputs.containsKey(name) ) {
				throw new IllegalStateException(String.format("Already added dataset [%s]", name));
			}
			Reader reader = new StringReader(value.toString());
			Dataset dataset = m_serializer.deserializeDataset(reader);
			m_outputs.put(name,dataset);
		}
	}
		

}

