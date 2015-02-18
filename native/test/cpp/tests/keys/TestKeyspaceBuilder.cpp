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

class TestKeyspaceBuilder : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestKeyspaceBuilder);
	CPPUNIT_TEST(testBuilder);
	CPPUNIT_TEST_SUITE_END();

	private:
		DataKeyDimension m_dim1;
		DataKeyDimension m_dim2;
		DataKeyDimension m_dim3;
		DataKeyElement m_ele1a;
		DataKeyElement m_ele1b;
		DataKeyElement m_ele1c;
		DataKeyElement m_ele2a;
		DataKeyElement m_ele2b;
		DataKeyElement m_ele3;
		DataKey m_key1;
		DataKey m_key2;
		DataKey m_key3;
		DataKey m_key4;
		KeyspaceTestUtil m_util;

	public:

		void setUp() {
			buildDimensions();
			buildElements();
			buildKeys();
		}

		void tearDown() {
		}

		void testBuilder() {
			KeyspaceBuilder builder;
			std::set<DataKey> keys;
			keys.insert(m_key1);
			keys.insert(m_key2);
			keys.insert(m_key3);
			keys.insert(m_key4);
			builder.addKeys(keys);
			Keyspace keyspace = builder.toKeyspace();
			Keyspace* keyspace1 = m_util.buildKeyspace1();
			CPPUNIT_ASSERT(keyspace==*keyspace1);
		}

	private: 

		void buildDimensions() {
			m_dim1 = DataKeyDimension("dim1");
			m_dim2 = DataKeyDimension("dim2");
			m_dim3 = DataKeyDimension("dim3");
		}

		void buildElements() {
			m_ele1a = DataKeyElement("ele1a", m_dim1);
			m_ele1b = DataKeyElement("ele1b", m_dim1);
			m_ele1c = DataKeyElement("ele1c", m_dim1);
			m_ele2a = DataKeyElement("ele2a", m_dim2);
			m_ele2b = DataKeyElement("ele2b", m_dim2);
			m_ele3 = DataKeyElement("ele3", m_dim3);
		}

		void buildKeys( ){
			buildKey1();
			buildKey2();
			buildKey3();
			buildKey4();
		}

		void buildKey1() {
			DataKeyBuilder builder;
			builder.addElement(m_ele1a);
			builder.addElement(m_ele2b);
			m_key1 = builder.toKey();
		}

		void buildKey2() {
			DataKeyBuilder builder;
			builder.addElement(m_ele1b);
			builder.addElement(m_ele2a);
			builder.addElement(m_ele3);
			m_key2 = builder.toKey();
		}

		void buildKey3() {
			m_key3 = DataKey(m_ele1c);
		}

		void buildKey4() {
			DataKeyBuilder builder;
			builder.addElement(m_ele1c);
			builder.addElement(m_ele2a);
			builder.addElement(m_ele3);
			m_key4 = builder.toKey();
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestKeyspaceBuilder, "TestKeyspaceBuilder");

}
