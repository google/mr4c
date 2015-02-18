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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.junit.*;
import static org.junit.Assert.*;

public class CustomFormatTest {

	private String m_normalPattern = "text${name1}some.text${name2}more_text";
	private String m_normalInput = "textval1some.textval2more_text";
	private String m_normalInputMismatch = "textval1some.textval2moree_text";
	private List<String> m_normalNameList;
	private CustomFormat m_normalFormat;
	private Map<String,String> m_normalValues;
	private Map<String,String> m_normalWrongNameValues;

	private String m_dupPattern = "text${name1}some.text${name2}more_text${name1}";
	private String m_dupInput = "textval1some.textval2more_textval1";
	private String m_dupInputMismatch = "textval1some.textval2more_textanything";
	private List<String> m_dupNameList;
	private CustomFormat m_dupFormat;
	private Map<String,String> m_dupValues;

	private String m_customPattern = "text${name}${number}";
	private String m_customInput = "textval99";
	private List<String> m_customNameList;
	private CustomFormat m_customFormat;
	private Map<String,String> m_customValues;
	private Map<String,String> m_customRegex;


	@Before public void setUp() {
		setUpNormal();
		setUpDup();
		setUpCustom();
	}

	private void setUpNormal() {
		m_normalFormat = CustomFormat.createInstance(m_normalPattern);
		m_normalNameList = Arrays.asList("name1", "name2");
		m_normalValues = new HashMap<String,String>();
		m_normalValues.put("name1", "val1");
		m_normalValues.put("name2", "val2");
		m_normalWrongNameValues = new HashMap<String,String>(m_normalValues);
		m_normalWrongNameValues.put("wrongName", "wrongValue");
	}

	private void setUpDup() {
		m_dupFormat = CustomFormat.createInstance(m_dupPattern);
		m_dupNameList = Arrays.asList("name1", "name2", "name1");
		m_dupValues = new HashMap<String,String>();
		m_dupValues.put("name1", "val1");
		m_dupValues.put("name2", "val2");
	}

	private void setUpCustom() {
		m_customRegex = new HashMap<String,String>();
		m_customRegex.put("name", "[a-z]+");
		m_customRegex.put("number", "\\d+");
		m_customFormat = CustomFormat.createInstance(m_customPattern, m_customRegex);
		m_customNameList = Arrays.asList("name", "number");
		m_customValues = new HashMap<String,String>();
		m_customValues.put("name", "val");
		m_customValues.put("number", "99");
	}

	@Test public void testNormalNameList() {
		assertEquals(m_normalNameList, m_normalFormat.getNameList());
	}

	@Test public void testNormalFormat() {
		String result = m_normalFormat.format(m_normalValues);
		assertEquals(m_normalInput, result);
	}
		
	@Test public void testNormalParse() {
		Map<String,String> result = m_normalFormat.parse(m_normalInput);
		assertEquals(m_normalValues, result);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNormalFormatWrongName() {
		m_normalFormat.format(m_normalWrongNameValues);
	}
		
	@Test(expected=IllegalArgumentException.class)
	public void testNormalParseMismatch() {
		m_normalFormat.parse(m_normalInputMismatch);
	}

	@Test public void testDupNameList() {
		assertEquals(m_dupNameList, m_dupFormat.getNameList());
	}

	@Test public void testDupFormat() {
		String result = m_dupFormat.format(m_dupValues);
		assertEquals(m_dupInput, result);
	}
		
	@Test public void testDupParse() {
		Map<String,String> result = m_dupFormat.parse(m_dupInput);
		assertEquals(m_dupValues, result);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testDupParseValueMismatch() {
		m_dupFormat.parse(m_dupInputMismatch);
	}

	@Test public void testCustomNameList() {
		assertEquals(m_customNameList, m_customFormat.getNameList());
	}

	@Test public void testCustomFormat() {
		String result = m_customFormat.format(m_customValues);
		assertEquals(m_customInput, result);
	}
		
	@Test public void testCustomParse() {
		Map<String,String> result = m_customFormat.parse(m_customInput);
		assertEquals(m_customValues, result);
	}

}

