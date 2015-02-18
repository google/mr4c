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
import com.google.mr4c.serialize.AlgorithmSerializer;
import com.google.mr4c.serialize.SerializerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class ExternalAlgorithmSerializer {

	private AlgorithmSerializer m_serializer;
	private ExternalFactory m_factory;

	public ExternalAlgorithmSerializer(
		SerializerFactory serializerFactory,
		ExternalFactory factory
	) {
		m_serializer = serializerFactory.createAlgorithmSerializer();
		m_factory = factory;
	}

	public ExternalAlgorithm serializeAlgorithm(String name, AlgorithmSchema algo) throws IOException {
		ExternalAlgorithm extAlgorithm = m_factory.newAlgorithm(name);
		StringWriter writer = new StringWriter();
		m_serializer.serializeAlgorithmSchema(algo,writer);
		extAlgorithm.setSerializedAlgorithm(writer.toString());
		return extAlgorithm;
	}

	public AlgorithmSchema deserializeAlgorithm(ExternalAlgorithm extAlgorithm) throws IOException {
		AlgorithmSchema algo = new AlgorithmSchema();
		String serializedAlgo = extAlgorithm.getSerializedAlgorithm();
		StringReader reader = new StringReader(serializedAlgo);
		return m_serializer.deserializeAlgorithmSchema(reader);
	}

}
