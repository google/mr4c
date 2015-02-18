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

package com.google.mr4c.sources;

import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class CompositeKeyFileMapperTest {

	private String m_pattern1 = "image_${dim1}_${dim2}.jpg";
	private String m_pattern2 = "video_${dim1}_${dim2}.mpg";
	private String m_pattern3 = "whatever_${dim1}.bin";

	private String m_file1 = "image_val1_val2.jpg";
	private String m_file2 = "video_val1_val2.mpg";
	private String m_file3 = "whatever_val1.bin";
	private String m_file4 = "something_else";

	private DataKeyDimension m_dim1 = new DataKeyDimension("dim1");
	private DataKeyDimension m_dim2 = new DataKeyDimension("dim2");
	private DataKeyElement m_ele1;
	private DataKeyElement m_ele2;

	private DataKey m_key1;
	private DataKey m_key3;
	private DataKey m_key4;

	private CompositeKeyFileMapper m_mapper;


	@Before public void setUp() {
		buildMapper();
		buildElements();
		buildKeys();
	}

	private void buildMapper() {
		m_mapper = new CompositeKeyFileMapper();
		m_mapper.addMapper(buildMapper(m_pattern1, m_dim1, m_dim2));
		m_mapper.addMapper(buildMapper(m_pattern2, m_dim1, m_dim2));
		m_mapper.addMapper(buildMapper(m_pattern3, m_dim1));
	}

	private DataKeyFileMapper buildMapper(String pattern, DataKeyDimension ... dims) {
		return new PatternKeyFileMapper( pattern, new HashSet<DataKeyDimension>(Arrays.asList(dims)) );
	}

	private void buildElements() {
		m_ele1 = new DataKeyElement("val1", m_dim1);
		m_ele2 = new DataKeyElement("val2", m_dim2);
	}

	private void buildKeys() {
		m_key1 = DataKeyFactory.newKey(m_ele1, m_ele2);
		m_key3 = DataKeyFactory.newKey(m_ele1);
		m_key4 = DataKeyFactory.newKey();
	}

	@Test public void testParse() {
		testParse(m_file1, m_key1);
		testParse(m_file2, m_key1);
		testParse(m_file3, m_key3);
	}

	private void testParse(String name, DataKey expected) {
		DataKey key = m_mapper.getKey(name);
		assertEquals(expected, key);
	}
		
	@Test(expected=IllegalArgumentException.class)
	public void testParseFail() {
		m_mapper.getKey(m_file4);
	}

	@Test public void testFormat() {
		testFormat(m_key1, m_file1);
		testFormat(m_key3, m_file3);
	}

	private void testFormat(DataKey key, String expected) {
		String name = m_mapper.getFileName(key);
		assertEquals(expected, name);
	}
		
	@Test(expected=IllegalArgumentException.class)
	public void testFormatFail() {
		m_mapper.getFileName(m_key4);
	}

	@Test public void testMatchNameTrue() {
		assertTrue(m_mapper.canMapName(m_file1));
		assertTrue(m_mapper.canMapName(m_file2));
		assertTrue(m_mapper.canMapName(m_file3));
	}

	@Test public void testMatchNameFalse() {
		assertFalse(m_mapper.canMapName(m_file4));
	}

	@Test public void testMatchKeyTrue() {
		assertTrue(m_mapper.canMapKey(m_key1));
		assertTrue(m_mapper.canMapKey(m_key3));
	}

	@Test public void testMatchKeyFalse() {
		assertFalse(m_mapper.canMapKey(m_key4));
	}

}

