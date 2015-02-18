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

package com.google.mr4c.metadata;

import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;

import java.util.HashSet;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class MetadataKeyExtractorTest {

	private DataKey m_key1;
	private DataKey m_key2;
	private MetadataMap m_map;

	@Before public void setUp() {
		buildKey1();
		buildKey2();

		MetadataList list = new MetadataList();
		list.getList().add( new MetadataKey(m_key1) );

		m_map = new MetadataMap();
		m_map.getMap().put("key", new MetadataKey(m_key2) );
		m_map.getMap().put("list", list);

	}

	@Test public void testExtractor() {
		Set<DataKey> expected = new HashSet<DataKey>();
		expected.add(m_key1);
		expected.add(m_key2);
		Set<DataKey> result = MetadataKeyExtractor.findKeys(m_map);
		assertEquals(expected,result);
	}

	private void buildKey1() {
		DataKeyDimension dim = new DataKeyDimension("dim1");
		DataKeyElement ele = new DataKeyElement("val1", dim);
		m_key1 = DataKeyFactory.newKey(ele);
	}

	private void buildKey2() {
		DataKeyDimension dim = new DataKeyDimension("dim2");
		DataKeyElement ele = new DataKeyElement("val2", dim);
		m_key2 = DataKeyFactory.newKey(ele);
	}

}
