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
import com.google.mr4c.serialize.bean.dataset.DatasetBean;
import com.google.mr4c.serialize.bean.DatasetBeanSerializer;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.*;
import static org.junit.Assert.*;

public class JsonDatasetBeanSerializerTest {

	private Dataset m_dataset;
	private DatasetBeanSerializer m_serializer;

	@Before public void setup() throws Exception {
		m_dataset = DatasetTestUtils.buildDataset1();
		m_dataset.release();
		m_serializer = new JsonDatasetBeanSerializer();
	}

	@Test public void testDataset() throws Exception {
		DatasetBean bean = DatasetBean.instance(m_dataset);
		StringWriter writer = new StringWriter();
		m_serializer.serializeDatasetBean(bean, writer);
		String json = writer.toString();
		StringReader reader = new StringReader(json);
		DatasetBean bean2 = m_serializer.deserializeDatasetBean(reader);
		Dataset dataset2 = bean2.toDataset();
		assertEquals(m_dataset, dataset2);
	}

}
