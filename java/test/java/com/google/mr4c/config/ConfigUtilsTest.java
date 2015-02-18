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

package com.google.mr4c.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class ConfigUtilsTest {

	private String m_template = "abc ${var1} 123\n${var2} do re mi";
	private Properties m_partProps;
	private Properties m_allProps;
	private String m_allResult = "abc def 123\n456 do re mi";
	private String m_partResult = "abc def 123\n${var2} do re mi";

	@Before public void setup() throws Exception {
		m_partProps = new Properties();
		m_partProps.setProperty("var1", "def");
		m_allProps = new Properties();
		m_allProps.setProperty("var1", "def");
		m_allProps.setProperty("var2", "456");
	}

	@Test public void testApplyPropertiesHaveAllCheckAll() {
		String result = ConfigUtils.applyProperties(m_template, m_allProps, true);
		assertEquals(m_allResult, result);
	}

	@Test(expected=IllegalStateException.class)
	public void testApplyPropertiesHavePartCheckAll() {
		String result = ConfigUtils.applyProperties(m_template, m_partProps, true);
		assertEquals(m_partResult, result);
	}

	@Test public void testApplyPropertiesHaveAllDontCheckAll() {
		String result = ConfigUtils.applyProperties(m_template, m_allProps, false);
		assertEquals(m_allResult, result);
	}

	@Test public void testApplyPropertiesHavePartDontCheckAll() {
		String result = ConfigUtils.applyProperties(m_template, m_partProps, false);
		assertEquals(m_partResult, result);
	}

	@Test public void testResolveProperties() {
		Properties input = new Properties();
		input.setProperty("var1", "do ${var2} twice");
		input.setProperty("var2", "something");
		Properties expected = new Properties();
		expected.setProperty("var1", "do something twice");
		expected.setProperty("var2", "something");
		assertEquals(expected, ConfigUtils.resolveProperties(input, true));
	}

	@Test public void testExtractVariables() {
		Set<String> expected = new HashSet<String>(Arrays.asList("var1", "var2"));
		Set<String> actual = ConfigUtils.extractVariables(m_template);
		assertEquals(expected, actual);
	}

	@Test public void testContainsRuntimeVariables() {
		assertTrue(ConfigUtils.containsVariables(m_template));
		assertFalse(ConfigUtils.containsVariables(m_allResult));
	}

}

