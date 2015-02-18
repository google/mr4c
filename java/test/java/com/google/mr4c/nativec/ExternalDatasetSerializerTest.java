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

package com.google.mr4c.nativec;

import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.dataset.DatasetTestUtils;
import com.google.mr4c.nativec.jna.JnaExternalFactory;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.serialize.SerializerFactory;

import java.io.File;

import org.junit.*;
import static org.junit.Assert.*;

public class ExternalDatasetSerializerTest {

	private Dataset m_dataset;
	private ExternalDatasetSerializer m_serializer;

	@Before public void setup() throws Exception {
		m_dataset = DatasetTestUtils.buildDataset1();
		SerializerFactory serFactory = SerializerFactories.getSerializerFactory("application/json");
		ExternalFactory extFactory= new JnaExternalFactory(serFactory);
		m_serializer = new ExternalDatasetSerializer(
			serFactory.createDatasetSerializer(),
			extFactory
		);
	}

	@Test public void testDataset() throws Exception {
		ExternalDataset extDataset = m_serializer.serializeDataset("test", m_dataset);
		Dataset dataset2 = m_serializer.deserializeDataset(extDataset);
		assertEquals(m_dataset, dataset2);
	}

}

