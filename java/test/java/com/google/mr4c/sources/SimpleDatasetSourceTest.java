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

import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyFactory;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.SerializerFactories;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

public class SimpleDatasetSourceTest {

	private URI m_input;
	private URI m_badInput;
	private URI m_output;
	private DatasetSource m_inputSrc;
	private DatasetSource m_badInputSrc;
	private DatasetSource m_outputSrc;
	private DatasetSerializer m_serializer;
	private DatasetSerializer m_badSerializer;

	@Before public void setUp() throws Exception {
		m_serializer = SerializerFactories.getSerializerFactory("application/json").createDatasetSerializer();
		m_input = new URI("input/data/dataset/simple/simple.json");
		m_badInput = new URI("input/data/dataset/simple/not_simple.json");
		m_output = new URI("output/data/dataset/simple/dataset.json");
		m_inputSrc = new SimpleDatasetSource(m_input, m_serializer);
		m_badInputSrc = new SimpleDatasetSource(m_badInput, m_serializer);
		m_outputSrc = new SimpleDatasetSource(m_output, m_serializer);
	}

	@Test public void testRoundTrip() throws Exception {
		SourceTestUtils.testSource(m_inputSrc, m_outputSrc);
	}

	@Test(expected=IllegalStateException.class)
	public void testReadNotSimple() throws Exception {
		m_badInputSrc.readDataset();
	}

	@Test(expected=IllegalStateException.class)
	public void testWriteNotSimple() throws Exception {
		Dataset dataset = new Dataset();
		DataFile file = new DataFile("text/plain");
		DataKey key = DataKeyFactory.newKey();
		dataset.addFile(key,file);
		m_outputSrc.writeDataset(dataset);
	}
		

}
