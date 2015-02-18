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

package com.google.mr4c.config.category;

import com.google.mr4c.util.NamespacedProperties;

import java.util.Arrays;
import java.util.Properties;

import org.junit.*;
import static org.junit.Assert.*;

public class CategoryConfigTest {

	private CategoryInfo m_category;
	private CategorySystemData m_categorySysData;
	private CategoryBuilder m_builder;
	private Properties m_normProps;
	private Properties m_nsSysProps;
	private Properties m_sysProps;
	private Properties m_depProps;
	private Properties m_extProps;
	private Properties m_expectedNoSysProps;
	private Properties m_expectedWithSysProps;
	private TestConfig m_config;
	private NamespacedProperties m_nsProps;

	@Before public void setup() throws Exception {
		buildExpectedNoSysProps();
		buildExpectedWithSysProps();
		buildNamespacedSystemProps();
		buildNormalProps();
		buildSystemProps();
		buildExternalProps();
		buildDeprecatedProps();
		buildCategoryInfo();
	}

	@After public void teardown() {
		clearSystemProperties();
	}

	@Test public void testPropertiesFromDeprecatedNamesNoSysProps() {
		initTest(false);
		assertEquals(new Properties(), m_config.getPropertiesFromDeprecatedNames());
	}

	@Test public void testPropertiesFromDeprecatedNamesWithSysProps() {
		initTest(true);
		assertEquals(m_depProps, m_config.getPropertiesFromDeprecatedNames());
	}

	@Test public void testPropertiesFromSystemPropertiesNoSysProps() {
		initTest(false);
		assertEquals(new Properties(), m_config.getPropertiesFromSystemProperties());
	}

	@Test public void testPropertiesFromSystemPropertiesWithSysProps() {
		initTest(true);
		assertEquals(m_nsSysProps, m_config.getPropertiesFromSystemProperties());
	}

	@Test public void testPropertiesFromExternalNamesNoSysProps() {
		initTest(false);
		assertEquals(new Properties(), m_config.getPropertiesFromExternalNames());
	}

	@Test public void testPropertiesFromExternalNamesWithSysProps() {
		initTest(true);
		assertEquals(m_extProps, m_config.getPropertiesFromExternalNames());
	}

	@Test public void testPropertiesNoSysProps() throws Exception {
		initTest(false);
		assertEquals(m_expectedNoSysProps, m_config.getProperties(false));
	}
		
	@Test public void testPropertiesWithSysProps() throws Exception {
		initTest(true);
		assertEquals(m_expectedWithSysProps, m_config.getProperties(false));
	}

	@Test public void testNullPropertyNoSysProps() throws Exception {
		initTest(false);
		doPropertyTest("yoyo", null, "some_new_value");
	}

	@Test public void testNullPropertyWithSysProps() throws Exception {
		initTest(true);
		doPropertyTest("yoyo", null, "some_new_value");
	}

	@Test public void testPropertyNoSysProps() throws Exception {
		initTest(false);
		doPropertyTest("name3", "norm3", "some_new_value");
	}

	@Test public void testPropertyWithSysProps() throws Exception {
		initTest(true);
		doPropertyTest("name3", "norm3", "some_new_value");
	}

	@Test public void testDeprecatedPropertyNoSysProps() throws Exception {
		initTest(false);
		doPropertyTest("name5", null, "some_new_value");
	}

	@Test public void testDeprecatedPropertyWithSysProps() throws Exception {
		initTest(true);
		doPropertyTest("name5", "dep5", "some_new_value");
	}

	@Test public void testSystemPropertyNoSysProps() throws Exception {
		initTest(false);
		doPropertyTest("name4", null, "some_new_value");
	}

	@Test public void testSystemPropertyWithSysProps() throws Exception {
		initTest(true);
		doPropertyTest("name4", "sys4", "some_new_value");
	}

	@Test public void testNormConfExternalPropertyNoSysProps() throws Exception {
		initTest(false);
		doPropertyTest("name1", "norm1", "some_new_value");
		assertNull(System.getProperty("ext_name1"));
	}

	@Test public void testNormConfExternalPropertyWithSysProps() throws Exception {
		initTest(true);
		doPropertyTest("name1", "norm1", "some_new_value");
		assertEquals("some_new_value", System.getProperty("ext_name1"));
	}

	@Test public void testExtConfExternalPropertyNoSysProps() throws Exception {
		initTest(false);
		doPropertyTest("name2", null, "some_new_value");
		assertEquals("ext2", System.getProperty("ext_name2"));
	}

	@Test public void testExtConfExternalPropertyWithSysProps() throws Exception {
		initTest(true);
		doPropertyTest("name2", "ext2", "some_new_value");
		assertEquals("some_new_value", System.getProperty("ext_name2"));
	}

	private void initTest(boolean includeSysProps) {
		m_config = new TestConfig(m_category);
		m_config.init(includeSysProps);
		m_config.setProperties(m_normProps);
		setSystemProperties();
	}

	private void doPropertyTest(String name, String initValue, String newValue) {

		String val1 = m_config.getProperty(name);
		if ( initValue==null ) {
			assertNull(val1);
		} else {
			assertEquals(initValue, val1);
		}
		m_config.setProperty(name, newValue);
		String val2 = m_config.getProperty(name);
		assertEquals(newValue, val2);
	}

	private void buildNormalProps() {
		m_normProps = new Properties();
		m_normProps.setProperty("name1", "norm1");
		m_normProps.setProperty("name3", "norm3");
	}

	private void buildExternalProps() {
		m_extProps = new Properties();
		m_extProps.setProperty("name2", "ext2");
	}

	private void buildNamespacedSystemProps() {
		m_nsSysProps = new Properties();
		m_nsSysProps.setProperty("name3", "sys3");
		m_nsSysProps.setProperty("name4", "sys4");
	}

	private void buildDeprecatedProps() {
		m_depProps = new Properties();
		m_depProps.setProperty("name4", "dep4");
		m_depProps.setProperty("name5", "dep5");
	}

	private void buildSystemProps() {
		m_sysProps = new Properties();
		m_sysProps.setProperty("ext_name2", "ext2");
		m_sysProps.setProperty("dep_name4", "dep4");
		m_sysProps.setProperty("dep_name5", "dep5");
	}

	private void buildExpectedNoSysProps() {
		m_expectedNoSysProps = new Properties();
		m_expectedNoSysProps.setProperty("name1", "norm1");
		m_expectedNoSysProps.setProperty("name3", "norm3");
	}

	private void buildExpectedWithSysProps() {
		m_expectedWithSysProps = new Properties();
		m_expectedWithSysProps.setProperty("name1", "norm1");
		m_expectedWithSysProps.setProperty("name2", "ext2");
		m_expectedWithSysProps.setProperty("name3", "norm3");
		m_expectedWithSysProps.setProperty("name4", "sys4");
		m_expectedWithSysProps.setProperty("name5", "dep5");
	}

	private void setSystemProperties() {
		m_nsProps = new NamespacedProperties(m_category.getPropertiesPrefix());
		clearSystemProperties();
		m_nsProps.setProperties(m_nsSysProps, false);
		System.getProperties().putAll(m_sysProps);
	}

	private void clearSystemProperties() {
		m_nsProps.clear();
		System.clearProperty("ext_name1");
		System.clearProperty("ext_name2");
		System.clearProperty("dep_name4");
		System.clearProperty("dep_name5");
	}
		
	private void buildCategoryInfo() {
		m_category = new TestCategoryInfo( "cat1", "MR4C_UNIT_TEST");
	}

	private static class TestConfig extends CategoryConfig {
		TestConfig(CategoryInfo category) {
			super(category);
		}
		protected void customInit() {
			addDeprecatedProperty("name4", "dep_name4");
			addDeprecatedProperty("name5", "dep_name5");
			addExternalProperty("name1", "ext_name1");
			addExternalProperty("name2", "ext_name2");
		}
	}
	

}

