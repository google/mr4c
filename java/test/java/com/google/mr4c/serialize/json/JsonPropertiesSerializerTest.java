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

import com.google.mr4c.serialize.PropertiesSerializer;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import org.junit.*;
import static org.junit.Assert.*;

public class JsonPropertiesSerializerTest {

	private Properties m_props;
	private PropertiesSerializer m_serializer;

	@Before public void setup() throws Exception {
		m_props = new Properties();
		m_props.setProperty("prop1", "val1");
		m_props.setProperty("prop2", "val2");
		m_props.setProperty("prop3", "val3");
		m_serializer = new JsonPropertiesSerializer();
	}

	@Test public void testProperties() throws Exception {
		StringWriter writer = new StringWriter();
		m_serializer.serializeProperties(m_props, writer);
		String json = writer.toString();
		StringReader reader = new StringReader(json);
		Properties props2 = m_serializer.deserializeProperties(reader);
		assertEquals(m_props, props2);
	}

}
