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

import java.net.URI;

import org.junit.*;
import static org.junit.Assert.*;

public class ConfigDescriptorTest {

	private ConfigDescriptor m_descName1a;
	private ConfigDescriptor m_descName1b;
	private ConfigDescriptor m_descName2;
	private ConfigDescriptor m_descFile1a;
	private ConfigDescriptor m_descFile1b;
	private ConfigDescriptor m_descFile2;
	private ConfigDescriptor m_descInline1a;
	private ConfigDescriptor m_descInline1b;
	private ConfigDescriptor m_descInline2;
	private ConfigDescriptor m_descInvalid;
	private Document m_doc1;
	private Document m_doc2;

	@Before public void setup() throws Exception {
		m_descName1a = new ConfigDescriptor("name1");
		m_descName1b = new ConfigDescriptor("name1");
		m_descName2 = new ConfigDescriptor("name2");
		m_descFile1a = new ConfigDescriptor(new URI("file:///data1"));
		m_descFile1b = new ConfigDescriptor(new URI("file:///data1"));
		m_descFile2 = new ConfigDescriptor(new URI("file:///data2"));
		m_doc1 = new Document("{ \"key1\" : \"val1\" }");
		m_doc2 = new Document("{ \"key2\" : \"val2\" }");
		m_descInline1a = new ConfigDescriptor(m_doc1);
		m_descInline1b = new ConfigDescriptor(m_doc1);
		m_descInline2 = new ConfigDescriptor(m_doc2);
		m_descInvalid = ConfigDescriptor.createInvalid("name", new URI("file:///data"), m_doc1);
	}

	@Test public void testEquals() {
		assertEquals(m_descName1a, m_descName1b);
		assertEquals(m_descFile1a, m_descFile1b);
	}

	@Test public void testNotEqual() {
		assertFalse(m_descName1a.equals(m_descName2));
		assertFalse(m_descFile1a.equals(m_descFile2));
		assertFalse(m_descName1a.equals(m_descFile2));
	}

	@Test public void testValid() {
		assertTrue(m_descName1a.isValid());
		assertTrue(m_descFile1a.isValid());
		assertFalse(m_descInvalid.isValid());
	}

}
