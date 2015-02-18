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

package com.google.mr4c.message;

import org.junit.*;
import static org.junit.Assert.*;

public class MessageTest {

	private Message m_msg1a;
	private Message m_msg1b;
	private Message m_msg2; // different topic
	private Message m_msg3; // different content
	private Message m_msg4; // different content type

	@Before public void setUp() {
		m_msg1a = buildMessage1(); 
		m_msg1b = buildMessage1(); 
		m_msg2 = buildMessage2();
		m_msg3 = buildMessage3();
		m_msg4 = buildMessage4();
	} 

	@Test public void testEquals() {
		assertEquals(m_msg1a, m_msg1b);
	}

	@Test public void testNotEqualTopic() {
		assertFalse(m_msg1a.equals(m_msg2));
	}

	@Test public void testNotEqualContent() {
		assertFalse(m_msg1a.equals(m_msg3));
	}

	@Test public void testNotEqualContentType() {
		assertFalse(m_msg1a.equals(m_msg4));
	}

	private Message buildMessage1() {
		return new Message("topic1", "content1", "type1");
	}

	private Message buildMessage2() {
		return new Message("topic2", "content1", "type1");
	}

	private Message buildMessage3() {
		return new Message("topic1", "content2", "type1");
	}

	private Message buildMessage4() {
		return new Message("topic1", "content1", "type2");
	}


}

