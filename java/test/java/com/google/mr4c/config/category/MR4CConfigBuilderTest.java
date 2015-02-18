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

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.*;
import static org.junit.Assert.*;

public class MR4CConfigBuilderTest {

	private Properties m_cat1Props;
	private Properties m_cat2Props;
	private List<String> m_args;
	private List<String> m_expectedRemainingArgs;
	private MR4CConfig m_config;
	private CategoryConfig m_cat1Conf;
	private CategoryConfig m_cat2Conf;
	private MR4CConfigBuilder m_builder;

	@Before public void setup() throws Exception {
		buildCat1Props();
		buildCat2Props();
		buildCommandLine();
		buildRemainingArgs();
		buildConfig();
		m_builder = new MR4CConfigBuilder(m_config, m_args);
	}

	@Test public void testBuild() throws Exception {
		m_builder.build();
		assertEquals(m_cat1Props, m_cat1Conf.getProperties(false));
		assertEquals(m_cat2Props, m_cat2Conf.getProperties(false));
		assertEquals(m_expectedRemainingArgs, m_builder.getRemainingArguments());
	}

	private void buildConfig() {
		m_config = new MR4CConfig(false);
		m_cat1Conf = initCategory("cat1", true);
		m_cat2Conf = initCategory("cat2", false);
		m_config.addCategory(m_cat1Conf);
		m_config.addCategory(m_cat2Conf);
	}

	private CategoryConfig initCategory(String name, boolean isDefault) {
		CategoryInfo category = new TestCategoryInfo(name, null, isDefault);
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

	private void buildCommandLine() {
		m_args = Arrays.asList(
			"-CAT2propB3=valB3",
			"propA2=valA2",
			"-CAT2propB2=valB2",
			"something",
			"-CAT2propB1=valB1",
			"else",
			"-CAT1propA1=valA1"
		);
	}

	private void buildRemainingArgs() {
		m_expectedRemainingArgs = Arrays.asList(
			"something",
			"else"
		);
	}

}

