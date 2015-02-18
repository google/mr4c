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
#include "keys/keys_api.h"
#include "keys/KeyspaceTestUtil.h"

namespace MR4C {

class TestKeyspace : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestKeyspace);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqual);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST_SUITE_END();

	private:

		KeyspaceTestUtil m_util;
		Keyspace* m_keyspace1a;
		Keyspace* m_keyspace1b;
		Keyspace* m_keyspace2;

	public:

		void setUp() {

			m_keyspace1a = m_util.buildKeyspace1();
			m_keyspace1b = m_util.buildKeyspace1();
			m_keyspace2 = m_util.buildKeyspace2();
		}

		void dummy() {}

		void tearDown() {
			delete m_keyspace1a;
			delete m_keyspace1b;
			delete m_keyspace2;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_keyspace1a==*m_keyspace1b);
		}
		
		void testNotEqual() {
			CPPUNIT_ASSERT(*m_keyspace1a!=*m_keyspace2);
			CPPUNIT_ASSERT(!(*m_keyspace1a==*m_keyspace2));
		}
		
		void testAssignment() {
			Keyspace keyspace = *m_keyspace1a;
			CPPUNIT_ASSERT(keyspace==*m_keyspace1a);
		}

		void testCopy() {
			Keyspace keyspace(*m_keyspace1a);
			CPPUNIT_ASSERT(keyspace==*m_keyspace1a);
		}


};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestKeyspace, "TestKeyspace");

}
