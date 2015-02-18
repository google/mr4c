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

import com.google.mr4c.util.CollectionUtils;

import java.util.Properties;

import org.junit.*;
import static org.junit.Assert.*;

public class MR4CConfigTest {

	private Properties m_importProps;
	private Properties m_expectedExportProps;
	private Properties m_cat1Props;
	private Properties m_cat2Props;
	private MR4CConfig m_config;
	private CategoryConfig m_cat1Conf;
	private CategoryConfig m_cat2Conf;

	@Before public void setup() throws Exception {
		buildCat1Props();
		buildCat2Props();
		buildImportProps();
		buildExportProps();
		buildConfig();
	}

	@Test public void testImportExport() {
		m_config.importProperties(CollectionUtils.toMap(m_importProps).entrySet());
		assertEquals(m_cat1Props, m_cat1Conf.getProperties(false));
		assertEquals(m_cat2Props, m_cat2Conf.getProperties(false));
		Properties exportProps = m_config.getProperties();
		assertEquals(m_expectedExportProps, exportProps);
	}

	@Test public void testClone() {
		MR4CConfig newConfig = MR4CConfig.clone(m_config);
		assertEquals(m_config.getProperties(), newConfig.getProperties());
	}

	private void buildConfig() {
		m_config = new MR4CConfig(false);
		m_cat1Conf = initCategory("cat1");
		m_cat2Conf = initCategory("cat2");
		m_config.addCategory(m_cat1Conf);
		m_config.addCategory(m_cat2Conf);
	}

	private CategoryConfig initCategory(String name) {
		CategoryInfo category = new TestCategoryInfo(name, null);
		CategoryConfig catConf = new CategoryConfig(category);
		catConf.init(false);
		return catConf;
	}

	private void buildCat1Props() {
		m_cat1Props = new Properties();
		m_cat1Props.setProperty("propA1", "valA1");
		m_cat1Props.setProperty("propA2", "valA2");
	}

	private void buildCat2Props() {
		m_cat2Props = new Properties();
		m_cat2Props.setProperty("propB1", "valB1");
		m_cat2Props.setProperty("propB2", "valB2");
		m_cat2Props.setProperty("propB3", "valB3");
	}

	private void buildImportProps() {
		m_importProps = new Properties();
		populateCategoryProps(m_importProps);
		m_importProps.setProperty("dude", "whatever");
		m_importProps.setProperty("something", "else");
	}

	private void buildExportProps() {
		m_expectedExportProps = new Properties();
		populateCategoryProps(m_expectedExportProps);
	}

	private void populateCategoryProps(Properties props) {
		props.setProperty("mr4ctest.cat1.propA1", "valA1");
		props.setProperty("mr4ctest.cat1.propA2", "valA2");
		props.setProperty("mr4ctest.cat2.propB1", "valB1");
		props.setProperty("mr4ctest.cat2.propB2", "valB2");
		props.setProperty("mr4ctest.cat2.propB3", "valB3");
	}
	
}

