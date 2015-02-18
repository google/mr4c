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

import com.google.mr4c.algorithm.AlgorithmData;
import com.google.mr4c.algorithm.AlgorithmDataTestUtils;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.dataset.DatasetTestUtils;
import com.google.mr4c.nativec.jna.JnaExternalFactory;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.serialize.SerializerFactory;

import java.io.File;

import org.junit.*;
import static org.junit.Assert.*;

public class ExternalAlgorithmDataSerializerTest {

	private AlgorithmData m_algoData;
	private ExternalFactory m_extFactory;
	private ExternalAlgorithmDataSerializer m_serializer;

	@Before public void setup() throws Exception {
		m_algoData = AlgorithmDataTestUtils.buildAlgorithmData1();
		SerializerFactory serFactory = SerializerFactories.getSerializerFactory("application/json");
		m_extFactory = new JnaExternalFactory(serFactory);
		m_serializer = new ExternalAlgorithmDataSerializer(serFactory, m_extFactory);
	}

	@Test public void testAlgoData() throws Exception {
		ExternalAlgorithmData extData = m_extFactory.newAlgorithmData();
		m_serializer.serializeInputData(m_algoData, extData);
		m_serializer.serializeOutputData(m_algoData, extData);
		AlgorithmData algoData2 = new AlgorithmData();
		m_serializer.deserializeInputData(algoData2, extData);
		m_serializer.deserializeOutputData(algoData2, extData);
		assertEquals(m_algoData, algoData2);
	}

}

