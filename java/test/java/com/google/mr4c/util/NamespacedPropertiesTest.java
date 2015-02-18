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

package com.google.mr4c.util;

import java.util.HashMap;
import java.util.Properties;

import org.junit.*;
import static org.junit.Assert.*;

public class NamespacedPropertiesTest {

	private static final String PREFIX="test.prefix.";
	private Properties m_props;
	private Properties m_prefixProps;
	private HashMap<String,String> m_propSrc;
	private NamespacedProperties m_nsProps;

	@Before public void setup() throws Exception {
		m_props = new Properties();
		m_prefixProps = new Properties();
		m_propSrc = new HashMap<String,String>();
		m_nsProps = new NamespacedProperties(PREFIX);
		addProperty("name1", "val1");
		addProperty("name2", "val2");
		addProperty("name3", "val3");
		m_propSrc.put("aName", "aValue");
		m_propSrc.put("otherName", "otherValue");
	}

	private void addProperty(String name, String val) {
		String prefixName = PREFIX+name;
		m_props.setProperty(name,val);
		m_prefixProps.setProperty(prefixName, val);
		m_propSrc.put(prefixName, val);
	}


	@Test public void testIsPropertyTrue() {
		String name = "test.prefix.name";
		assertTrue(m_nsProps.isNamespacedProperty(name));
	}

	@Test public void testIsPropertyFalse() {
		String name = "name";
		assertFalse(m_nsProps.isNamespacedProperty(name));
	}

	@Test public void testAddPrefix() {
		String name = "name";
		String nsName = m_nsProps.addPrefix(name);
		assertEquals("test.prefix.name", nsName);
	}

	@Test public void testStripPrefix() {
		String nsName = "test.prefix.name";
		String name = m_nsProps.stripPrefix(nsName);
		assertEquals("name", name);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testStripPrefixFail() {
		m_nsProps.stripPrefix("whatever.test.prefix.whatever");
	}

	@Test public void testSetNoPrefix() {
		m_nsProps.setProperties(m_props, false);
		assertEquals(m_props, m_nsProps.getProperties(false));
		assertEquals(m_prefixProps, m_nsProps.getProperties(true));
	}

	@Test public void testSetWithPrefix() {
		m_nsProps.setProperties(m_prefixProps, true);
		assertEquals(m_props, m_nsProps.getProperties(false));
		assertEquals(m_prefixProps, m_nsProps.getProperties(true));
	}

	@Test public void testGetProperty() {
		m_nsProps.setProperties(m_prefixProps, true);
		assertEquals("val1", m_nsProps.getProperty("name1", false));
		assertEquals("val1", m_nsProps.getProperty("test.prefix.name1", true));
	}

	@Test public void testGetPropertyWithDefault() {
		m_nsProps.setProperties(m_prefixProps, true);
		assertEquals("default", m_nsProps.getProperty("name4", "default", false));
		assertEquals("default", m_nsProps.getProperty("test.prefix.name4", "default", true));
	}

	@Test public void testExtractFromSource() {
		Properties props = m_nsProps.extractProperties(m_propSrc.entrySet());
		assertEquals(m_prefixProps, props);
	}

	@Test public void testSetFromSource() {
		m_nsProps.setProperties(m_propSrc.entrySet());
		assertEquals(m_props, m_nsProps.getProperties(false));
	}

	@Test public void testAddPrefixToProperties() {
		assertEquals(m_prefixProps, m_nsProps.addPrefix(m_props));
	}

	@Test public void testStripPrefixFromProperties() {
		assertEquals(m_props, m_nsProps.stripPrefix(m_prefixProps));
	}

	@Test public void testClearProperty() {
		m_nsProps.setProperty("this", "that", false);
		m_nsProps.clearProperty("this", false);
		assertNull(m_nsProps.getProperty("this", false));
	}

	@Test public void testClear() {
		m_nsProps.setProperties(m_props, false);
		m_nsProps.clear();
		assertTrue(m_nsProps.getProperties(true).isEmpty());
	}

	@Test public void testClearProperties() {
		m_nsProps.setProperties(m_props, false);
		Properties expected = new Properties();
		expected.setProperty("something", "else");
		m_prefixProps.setProperty("something", "else");
		m_nsProps.clear(m_prefixProps);
		assertEquals(expected, m_prefixProps);
	}

	@After public void teardown() throws Exception {
		m_nsProps.clear();
	}

}

