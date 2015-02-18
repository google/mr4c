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

package com.google.mr4c.serialize.json;

import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.dataset.DatasetTestUtils;
import com.google.mr4c.keys.Keyspace;
import com.google.mr4c.serialize.bean.keys.KeyspaceBean;
import com.google.mr4c.serialize.bean.KeyspaceBeanSerializer;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.*;
import static org.junit.Assert.*;

public class JsonKeyspaceBeanSerializerTest {

	private Keyspace m_keyspace;
	private KeyspaceBeanSerializer m_serializer;

	@Before public void setup() throws Exception {
		Dataset dataset = DatasetTestUtils.buildDataset1();
		m_keyspace = new Keyspace();
		m_keyspace.addKeys(dataset.getAllFileKeys());
		m_keyspace.addKeys(dataset.getAllMetadataKeys());
		m_serializer = new JsonKeyspaceBeanSerializer();
	}

	@Test public void testKeyspace() throws Exception {
		KeyspaceBean bean = KeyspaceBean.instance(m_keyspace);
		StringWriter writer = new StringWriter();
		m_serializer.serializeKeyspaceBean(bean, writer);
		String json = writer.toString();
		StringReader reader = new StringReader(json);
		KeyspaceBean bean2 = m_serializer.deserializeKeyspaceBean(reader);
		Keyspace keyspace2 = bean2.toKeyspace();
		assertEquals(m_keyspace, keyspace2);
	}

}
