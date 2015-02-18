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

package com.google.mr4c.serialize.bean.metadata;

import com.google.mr4c.metadata.MetadataField;
import com.google.mr4c.metadata.PrimitiveType;

import java.util.HashSet;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class MetadataFieldBeanTest {

	private MetadataField m_booleanField;
	private MetadataField m_byteField;
	private MetadataField m_intField;
	private MetadataField m_doubleField;
	private MetadataField m_stringField;

	@Before public void setUp() {
		m_booleanField = new MetadataField(Boolean.TRUE, PrimitiveType.BOOLEAN);
		byte b = 33;
		m_byteField = new MetadataField(b, PrimitiveType.BYTE);
		m_intField = new MetadataField(12345, PrimitiveType.INTEGER);
		m_doubleField = new MetadataField(12345.6789, PrimitiveType.DOUBLE);
		m_stringField = new MetadataField("Some string", PrimitiveType.STRING);
	}

	@Test public void testBoolean() {
		testField(m_booleanField);
	}

	@Test public void testByte() {
		testField(m_byteField);
	}

	@Test public void testInteger() {
		testField(m_intField);
	}

	@Test public void testDouble() {
		testField(m_doubleField);
	}

	@Test public void testString() {
		testField(m_stringField);
	}

	private void testField(MetadataField field) {
		MetadataFieldBean bean = MetadataFieldBean.instance(field);
		MetadataField field2 = bean.toMetadataElement();
		assertEquals(field, field2);
	}


}

