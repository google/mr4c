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
import com.google.mr4c.metadata.MetadataField;
import com.google.mr4c.metadata.MetadataList;
import com.google.mr4c.metadata.MetadataMap;
import com.google.mr4c.metadata.PrimitiveType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import static org.junit.Assert.*;

public class MetadataMapBeanTest {

	private MetadataList m_list;
	private MetadataMap m_subMap;
	private MetadataMap m_map;

	@Before public void setUp() {
		buildList();
		buildSubMap();
		buildMap();
	}

	private void buildList() {
		m_list = new MetadataList(
			Arrays.asList(
				new MetadataField(new Integer(234), PrimitiveType.INTEGER),
				new MetadataField(new Double(88.77), PrimitiveType.DOUBLE),
				new MetadataField("yo yo yo", PrimitiveType.STRING),
				new MetadataArray(Arrays.asList(33,44,55), PrimitiveType.INTEGER)
			)
		);
	}

	private void buildSubMap() {
		Map map = new HashMap();
		map.put("f1", new MetadataArray(Arrays.asList(666, 7, 13, 19), PrimitiveType.INTEGER));
		map.put("f2", new MetadataField("something", PrimitiveType.STRING));
		m_subMap = new MetadataMap(map);
	}

	private void buildMap() {
		Map map = new HashMap();
		map.put("f1", new MetadataField(new Integer(234), PrimitiveType.INTEGER));
		map.put("f2", new MetadataArray(Arrays.asList(33,44,55), PrimitiveType.INTEGER));
		map.put("f3", m_list);
		map.put("f4", new MetadataField(new Double(88.77), PrimitiveType.DOUBLE));
		map.put("f5", m_subMap );
		m_map = new MetadataMap(map);
	}


	@Test public void testMap() {
		MetadataMapBean bean = MetadataMapBean.instance(m_map);
		MetadataMap map2 = bean.toMetadataElement();
		assertEquals(m_map, map2);
	}


}

