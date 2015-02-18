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

public class PatternKeyFileMapperTest {

	private String m_pattern = "image_f${frame}_s${sensor}.jpg";
	private String m_extraPattern = "image_f${frame}_s${sensor}_${id}.jpg";
	private String m_goodName = "image_f66_s1.jpg";
	private String m_badName = "images_f66_s1.jpg";
	private String m_extraGoodName = "image_f66_s1_123.jpg";
	private DataKeyDimension m_frameDim = new DataKeyDimension("frame");
	private DataKeyDimension m_sensorDim = new DataKeyDimension("sensor");
	private DataKeyDimension m_otherDim = new DataKeyDimension("other");
	private DataKeyElement m_frameElement;
	private DataKeyElement m_sensorElement;
	private DataKeyElement m_otherElement;
	private DataKey m_goodKey;
	private DataKey m_badKey;
	private DataKeyFileMapper m_mapper;
	private DataKeyFileMapper m_extraMapper;


	@Before public void setUp() {
		buildMapper();
		buildElements();
		buildGoodKey();
		buildBadKey();
	}

	private void buildMapper() {
		Set<DataKeyDimension> dims = 
			new HashSet<DataKeyDimension>( 
				Arrays.asList(
					m_frameDim,
					m_sensorDim
				)
			);
		m_mapper = new PatternKeyFileMapper( m_pattern, dims);
		m_extraMapper = new PatternKeyFileMapper( m_extraPattern, dims);
	}

	private void buildElements() {
		m_frameElement = new DataKeyElement("66", m_frameDim);
		m_sensorElement = new DataKeyElement("1", m_sensorDim);
		m_otherElement = new DataKeyElement("xx", m_otherDim);
	}

	private void buildGoodKey() {
		m_goodKey = DataKeyFactory.newKey ( 
			m_frameElement,
			m_sensorElement
		);
	}

	private void buildBadKey() {
		m_badKey = DataKeyFactory.newKey ( 
			m_frameElement,
			m_otherElement
		);
	}


	@Test public void testParse() {
		DataKey key = m_mapper.getKey(m_goodName);
		assertEquals(m_goodKey, key);
	}

	@Test public void testParseExtra() {
		DataKey key = m_extraMapper.getKey(m_extraGoodName);
		assertEquals(m_goodKey, key);
	}

	@Test public void testFormat() {
		String name = m_mapper.getFileName(m_goodKey);
		assertEquals(m_goodName, name);
	}

	@Test(expected=IllegalStateException.class)
	public void testFormatExtraFail() {
		m_extraMapper.getFileName(m_goodKey);
	}

	@Test public void testMatchNameTrue() {
		assertTrue(m_mapper.canMapName(m_goodName));
	}

	@Test public void testMatchNameFalse() {
		assertFalse(m_mapper.canMapName(m_badName));
	}

	@Test public void testMatchKeyTrue() {
		assertTrue(m_mapper.canMapKey(m_goodKey));
	}

	@Test public void testMatchKeyFalse() {
		assertFalse(m_mapper.canMapKey(m_badKey));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testCreateFail() {
		new PatternKeyFileMapper( m_pattern,
			new HashSet<DataKeyDimension>( 
				Arrays.asList(
					m_frameDim,
					m_otherDim
				)
			)
		);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParseFail() {
		m_mapper.getKey(m_badName);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testFormatFail() {
		m_mapper.getFileName(m_badKey);
	}
	
}
