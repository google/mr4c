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

import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import org.junit.*;
import static org.junit.Assert.*;

public class CategoryBuilderTest {

	private CategoryInfo m_category;
	private CategorySystemData m_categorySysData;
	private CategoryBuilder m_builder;
	private Properties m_cmdProps;
	private Properties m_sysProps;
	private Properties m_depProps;
	private Properties m_extProps;
	private String m_cmdFiles;
	private String m_sysFiles;
	private Properties m_expectedProps;


	@Before public void setup() throws Exception {
		buildExpectedProps();
		buildCmdLineProps();
		buildSystemProps();
		buildExternalProps();
		buildDeprecatedProps();
		buildCmdLineFiles();
		buildSysPropFiles();
		buildCategoryInfo();
		buildCategorySystemData();
		m_builder = new CategoryBuilder(m_category);
		setSystemProperties();
	}

	@After public void teardown() throws Exception {
		clearSystemProperties();
	}

	@Test public void testBuilder() throws Exception {
		m_builder.buildStandardCategory(m_cmdFiles, m_cmdProps, m_categorySysData);
		assertEquals(m_expectedProps, m_builder.getProperties());
	}

	// Test Data here and on disk follows the following plan:
	//
	//	Files specified by:
	//		args: file1, file2
	//		sys props: file3, file4
	//		env: file5, file6
	//
	//	Properties specified by:
	//		external prop: name0
	//		cmd line args: name0, name1
	//		sys props: name1, name2
	//		file 1: name2, name3
	//		file 2: name3, name4
	//		file 3: name4, name5
	//		file 4: name5, name6
	//		file 5: name6, name7
	//		file 6: name7, name8
	//		deprecated props: name8, name9

	private void buildCmdLineProps() {
		m_cmdProps = new Properties();
		m_cmdProps.setProperty("name0", "wrong");
		m_cmdProps.setProperty("name1", "val1");
	}

	private void buildExternalProps() {
		m_extProps = new Properties();
		m_extProps.setProperty("name0", "val0");
	}

	private void buildSystemProps() {
		m_sysProps = new Properties();
		m_sysProps.setProperty("name1", "wrong");
		m_sysProps.setProperty("name2", "val2");
	}

	private void buildDeprecatedProps() {
		m_depProps = new Properties();
		m_depProps.setProperty("name8", "wrong");
		m_depProps.setProperty("name9", "val9");
	}

	private void buildCmdLineFiles() {
		m_cmdFiles = StringUtils.join(
			Arrays.asList(
				"input/conftest/file1.properties",
				"input/conftest/file2.properties"
			),
			","
		);
	}

	private void buildSysPropFiles() {
		m_sysFiles = StringUtils.join(
			Arrays.asList(
				"input/conftest/file3.properties",
				"input/conftest/file4.properties"
			),
			","
		);
	}

	private void buildExpectedProps() {
		m_expectedProps = new Properties();
		for ( int i=0; i<=9; i++ ) {
			m_expectedProps.setProperty("name"+i, "val"+i);
		}
	}

	private void setSystemProperties() {
		System.clearProperty(m_category.getCategoryProperty());
		System.setProperty(m_category.getCategoryProperty(), m_sysFiles);
	}

	private void clearSystemProperties() {
		System.clearProperty(m_category.getCategoryProperty());
	}

	private void buildCategoryInfo() {
		m_category = new TestCategoryInfo( "cat1", "MR4C_UNIT_TEST");
	}

	private void buildCategorySystemData() {
		m_categorySysData = new TestCategorySystemData(
			m_depProps,
			m_sysProps,
			m_extProps
		);
	}

}

