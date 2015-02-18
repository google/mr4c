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

import com.google.mr4c.metadata.MetadataArray;
import com.google.mr4c.metadata.PrimitiveType;

import org.apache.commons.lang3.ArrayUtils;

import org.junit.*;
import static org.junit.Assert.*;

public class MetadataArrayBeanTest {

	private MetadataArray m_booleanArray;
	private MetadataArray m_byteArray;
	private MetadataArray m_intArray;
	private MetadataArray m_doubleArray;
	private MetadataArray m_stringArray;

	@Before public void setUp() {
		buildBooleanArray();
		buildByteArray();
		buildIntegerArray();
		buildDoubleArray();
		buildStringArray();
	}

	private void buildBooleanArray() {
		m_booleanArray = new MetadataArray(
			new Boolean[] {true, false, true, true},
			PrimitiveType.BOOLEAN
		);
	}

	private void buildByteArray() {
		byte[] bytes = new byte[] {55, 66, -125, 0};
		Byte[] byteObjs = ArrayUtils.toObject(bytes);
		m_byteArray = new MetadataArray(
			byteObjs,
			PrimitiveType.BYTE
		);
	}

	private void buildIntegerArray() {
		m_intArray = new MetadataArray(
			new Integer[] {12, 67, 999},
			PrimitiveType.INTEGER
		);
	}

	private void buildDoubleArray() {
		m_doubleArray = new MetadataArray(
			new Double[] {96.55, -666.66, 12.0, -0.0005},
			PrimitiveType.DOUBLE
		);
	}

	private void buildStringArray() {
		m_stringArray = new MetadataArray(
			new String[] {"one", "two", "three"},
			PrimitiveType.STRING
		);
	}

	@Test public void testBoolean() {
		testArray(m_booleanArray);
	}

	@Test public void testByte() {
		testArray(m_byteArray);
	}

	@Test public void testInteger() {
		testArray(m_intArray);
	}

	@Test public void testDouble() {
		testArray(m_doubleArray);
	}

	@Test public void testString() {
		testArray(m_stringArray);
	}

	private void testArray(MetadataArray array) {
		MetadataArrayBean bean = MetadataArrayBean.instance(array);
		MetadataArray array2 = bean.toMetadataElement();
		assertEquals(array, array2);
	}


}

