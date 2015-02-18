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

import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;
import com.google.mr4c.metadata.MetadataKey;

import java.util.HashSet;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class MetadataKeyBeanTest {

	private MetadataKey m_key;

	@Before public void setUp() {
		buildKey();
	}

	private void buildKey() {
		DataKeyDimension dim1 = new DataKeyDimension("dim1");
		DataKeyDimension dim2 = new DataKeyDimension("dim2");
		DataKeyElement ele1 = new DataKeyElement("val1", dim1);
		DataKeyElement ele2 = new DataKeyElement("val2", dim2);
		Set<DataKeyElement> eles = new HashSet<DataKeyElement>();
		eles.add(ele1);
		eles.add(ele2);
		DataKey key = DataKeyFactory.newKey(eles);
		m_key = new MetadataKey(key);
	}

	@Test public void testKey() {
		MetadataKeyBean bean = MetadataKeyBean.instance(m_key);
		MetadataKey key2 = bean.toMetadataElement();
		assertEquals(m_key, key2);
	}


}

