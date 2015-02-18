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
import java.util.List;
import java.util.Properties;

import org.junit.*;
import static org.junit.Assert.*;

public class CategoryParserTest {


	private List<String> m_args;
	private CategoryParser m_parser;

	private static class ExpectedResult {
		Properties props;
		String fileList;
		List<String> remainingArgs;
	}

	private ExpectedResult m_normalResult;
	private ExpectedResult m_defaultResult;


	@Before public void setup() throws Exception {
		buildArgs();
		buildNormalResult();
		buildDefaultResult();
		m_parser = new CategoryParser("PRE");
	}

	@Test public void testNormalParsing() throws Exception {
		m_args = m_parser.parseNormal(m_args);
		checkResult(m_normalResult);
	}

	@Test public void testDefaultParsing() throws Exception {
		m_args = m_parser.parseNormal(m_args);
		m_args = m_parser.parseDefault(m_args);
		checkResult(m_defaultResult);
	}

	private void checkResult(ExpectedResult expected) {
		assertEquals(expected.props, m_parser.getProperties());
		assertEquals(expected.fileList, m_parser.getFileList());
		assertEquals(expected.remainingArgs, m_args);
	}

	private void buildArgs() {
		m_args = Arrays.asList(
			"PREname1=val1",
			"name2=val2", 
			"yo",
			"PRE=file1,file2",
			"PREname3=val3"	
		);
	}

	private void buildNormalResult() {
		m_normalResult = new ExpectedResult();
		addNormalStep(m_normalResult);
		m_normalResult.remainingArgs= Arrays.asList("name2=val2", "yo");
	}
		
	private void buildDefaultResult() {
		m_defaultResult = new ExpectedResult();
		addNormalStep(m_defaultResult);
		m_defaultResult.props.setProperty("name2", "val2");
		m_defaultResult.remainingArgs= Arrays.asList("yo");
	}

	private void addNormalStep(ExpectedResult result) {
		Properties props = new Properties();
		props.setProperty("name1", "val1");
		props.setProperty("name3", "val3");
		result.props = props;
		result.fileList="file1,file2";
	}
		
}

