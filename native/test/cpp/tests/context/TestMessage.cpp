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


#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>
#include "context/context_api.h"

namespace MR4C {

class TestMessage : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestMessage);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqualTopic);
	CPPUNIT_TEST(testNotEqualContent);
	CPPUNIT_TEST(testNotEqualContentType);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST_SUITE_END();

	private:
		Message* m_msg1;
		Message* m_msg1a;
		Message* m_msg2;
		Message* m_msg3;
		Message* m_msg4;

	public:

		void setUp() {
			m_msg1 = buildMessage1();
			m_msg1a = buildMessage1();
			m_msg2 = buildMessage2();
			m_msg3 = buildMessage3();
			m_msg4 = buildMessage4();
		}



		void tearDown() {
			delete m_msg1;
			delete m_msg1a;
			delete m_msg2;
			delete m_msg3;
			delete m_msg4;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_msg1==*m_msg1a);
		}
		
		void testNotEqualTopic() {
			CPPUNIT_ASSERT(*m_msg1!=*m_msg2);
			CPPUNIT_ASSERT(!(*m_msg1==*m_msg2));
		}
		
		void testNotEqualContent() {
			CPPUNIT_ASSERT(*m_msg1!=*m_msg3);
			CPPUNIT_ASSERT(!(*m_msg1==*m_msg3));
		}
		
		void testNotEqualContentType() {
			CPPUNIT_ASSERT(*m_msg1!=*m_msg4);
			CPPUNIT_ASSERT(!(*m_msg1==*m_msg4));
		}
		
		void testAssignment() {
			Message msg = *m_msg1;
			CPPUNIT_ASSERT(msg==*m_msg1);
		}

		void testCopy() {
			Message msg(*m_msg1);
			CPPUNIT_ASSERT(msg==*m_msg1);
		}


	private: 

		Message* buildMessage1() {
			return new Message("topic1", "content1", "type1");
		}

		Message* buildMessage2() {
			return new Message("topic2", "content1", "type1");
		}

		Message* buildMessage3() {
			return new Message("topic1", "content2", "type1");
		}

		Message* buildMessage4() {
			return new Message("topic1", "content1", "type2");
		}


};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestMessage, "TestMessage");

}

