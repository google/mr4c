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
#include "serialize/serialize_api.h"
#include "serialize/json/json_api.h"
#include <iostream>

namespace MR4C {

class TestJsonKeyspaceSerializer : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestJsonKeyspaceSerializer);
	CPPUNIT_TEST(testKeyspace);
	CPPUNIT_TEST_SUITE_END();

	private:

		KeyspaceTestUtil m_util;
		Keyspace* m_keyspace;
		KeyspaceSerializer* m_serializer;

	public:

		void setUp() {
			m_keyspace = m_util.buildKeyspace1();
			m_serializer = new JsonKeyspaceSerializer();
		}

		void tearDown() {
			delete m_keyspace;
			delete m_serializer;
		}

		void testKeyspace() {
			std::string json = m_serializer->serializeKeyspace(*m_keyspace);
			Keyspace keyspace2 = m_serializer->deserializeKeyspace(json);
			CPPUNIT_ASSERT(*m_keyspace==keyspace2);
		}
		
};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestJsonKeyspaceSerializer, "TestJsonKeyspaceSerializer");

}


