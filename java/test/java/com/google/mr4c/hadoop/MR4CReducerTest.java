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
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.sources.DatasetSource.WriteMode;
import com.google.mr4c.testing.TestDataManager;
import com.google.mr4c.util.CollectionUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import org.junit.*;
import static org.junit.Assert.*;

public class MR4CReducerTest {

	private List<String> m_outputs = Arrays.asList("output1", "output2");
	private List<List<DataKey>> m_keySlices = new ArrayList<List<DataKey>>();
	private DatasetSerializer m_serializer = SerializerFactories.getSerializerFactory("application/json").createDatasetSerializer();
	private TestDataManager m_mgr = new TestDataManager();

	@Before public void setUp() {

		m_mgr.addDimension("dim1", Arrays.asList("d1v1", "d1v2", "d1v3"));
		m_mgr.addDimension("dim2", Arrays.asList("d2v1", "d2v2", "d2v3"));
		m_mgr.readyToTest();

		m_keySlices = CollectionUtils.partition(new ArrayList<DataKey>(m_mgr.getKeys()), 4);
	} 

	@Test public void testReduce() throws Exception {
		MR4CReducer reducer = new MR4CReducer(m_mgr.getExecutionSource());
		TestOutputCollector collector = new TestOutputCollector();

		for ( String name : m_outputs ) {
			Text key = new Text(name);
			Collection<Text> values = buildValues(name);
			reducer.reduce(key, values.iterator(), collector, Reporter.NULL);
		}
		
		for ( String  name : m_outputs ) {
			Dataset expected = TestDataManager.buildDataset(name, m_mgr.getKeys());
			expected.release();
			Dataset actual = collector.m_outputs.get(name);
			assertEquals(expected, actual);
		}
	}

	private Collection<Text> buildValues(String name) throws IOException {
		List<Text> values = new ArrayList<Text>();
		for ( List<DataKey> keySlice : m_keySlices ) {
			Set<DataKey> keys = new HashSet<DataKey>(keySlice);
			Dataset dataset = TestDataManager.buildDataset(name,keys);
			StringWriter sw = new StringWriter();
			m_serializer.serializeDataset(dataset,sw);
			values.add(new Text(sw.toString()));
		}
		return values;
	}
			

	class TestOutputCollector implements OutputCollector<Text,Text> {
		private Map<String,Dataset> m_outputs = new HashMap<String,Dataset>();

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

