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
#include "metadata/metadata_api.h"

namespace MR4C {

class TestMetadataKey : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestMetadataKey);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqual);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST(testCast);
	CPPUNIT_TEST_SUITE_END();

	private:
		MetadataKey* m_key1;
		MetadataKey* m_key1a;
		MetadataKey* m_key2;

	public:

		void setUp() {
			m_key1 = new MetadataKey(buildKey1());
			m_key1a = new MetadataKey(buildKey1());
			m_key2 = new MetadataKey(buildKey2());
		}

		void tearDown() {
			delete m_key1;
			delete m_key1a;
			delete m_key2;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_key1==*m_key1a);
		}
		
		void testNotEqual() {
			CPPUNIT_ASSERT(*m_key1!=*m_key2);
			CPPUNIT_ASSERT(!(*m_key1==*m_key2));
		}
		
		void testAssignment() {
			MetadataKey key = *m_key1;
			CPPUNIT_ASSERT(key==*m_key1);
		}

		void testCopy() {
			MetadataKey key(*m_key1);
			CPPUNIT_ASSERT(key==*m_key1);
		}

		void testCast() {
			MetadataElement& element = *m_key1;
			MetadataKey& key = MetadataKey::castToKey(element);
		}

	private:

		DataKey buildKey1() {
			DataKeyDimension dim = DataKeyDimension("dim1");
			DataKeyElement ele = DataKeyElement("val1", dim);
			return DataKey(ele);
		}

		DataKey buildKey2() {
			DataKeyDimension dim = DataKeyDimension("dim2");
			DataKeyElement ele = DataKeyElement("val2", dim);
			return DataKey(ele);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestMetadataKey, "TestMetadataKey");

}

