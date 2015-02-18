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

package com.google.mr4c.serialize.bean.keys;

import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;

import java.util.HashSet;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class DataKeyBeanTest {

	private DataKey m_simpleKey;
	private DataKey m_compoundKey;

	@Before public void setUp() {
		buildSimpleKey();
		buildCompoundKey();
	}

	private void buildSimpleKey() {
		m_simpleKey = DataKeyFactory.newKey(
			new DataKeyElement("some_value",
				new DataKeyDimension("some_dimension")
			)
		);
	} 

	private void buildCompoundKey() {

		DataKeyDimension dim1 = new DataKeyDimension("dim1");
		DataKeyDimension dim2 = new DataKeyDimension("dim2");
		DataKeyElement ele1 = new DataKeyElement("val1", dim1);
		DataKeyElement ele2 = new DataKeyElement("val2", dim2);
		m_compoundKey = DataKeyFactory.newKey(ele1, ele2);
	}

	@Test public void testSimpleKey() {
		testKey(m_simpleKey);
	}

	@Test public void testCompoundKey() {
		testKey(m_compoundKey);
	}

	private void testKey(DataKey key) {
		DataKeyBean bean = DataKeyBean.instance(key);
		DataKey key2 = bean.toKey();
		assertEquals(key, key2);
	}

}
