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

import com.google.mr4c.algorithm.AlgorithmSchema;
import com.google.mr4c.algorithm.AlgorithmDataTestUtils;
import com.google.mr4c.nativec.jna.JnaExternalFactory;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.serialize.SerializerFactory;

import java.io.File;

import org.junit.*;
import static org.junit.Assert.*;

public class ExternalAlgorithmSerializerTest {

	private AlgorithmSchema m_algoSchema;
	private ExternalAlgorithmSerializer m_serializer;

	@Before public void setup() throws Exception {
		m_algoSchema = AlgorithmDataTestUtils.buildAlgorithmSchema();
		SerializerFactory serFactory = SerializerFactories.getSerializerFactory("application/json");
		ExternalFactory extFactory= new JnaExternalFactory(serFactory);
		m_serializer = new ExternalAlgorithmSerializer(
			serFactory,
			extFactory
		);
	}

	@Test public void testAlgorithm() throws Exception {
		ExternalAlgorithm extAlgorithm = m_serializer.serializeAlgorithm("test", m_algoSchema);
		AlgorithmSchema algoSchema2 = m_serializer.deserializeAlgorithm(extAlgorithm);
		assertEquals(m_algoSchema, algoSchema2);
	}

}

