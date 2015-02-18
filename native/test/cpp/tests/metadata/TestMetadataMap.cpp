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

#include <stdexcept>
#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>
#include "metadata/metadata_api.h"

namespace MR4C {

class TestMetadataMap : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestMetadataMap);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqual);
	CPPUNIT_TEST(testElements);
	CPPUNIT_TEST(testCast);
	CPPUNIT_TEST_EXCEPTION(testCastWrong, std::runtime_error);
	CPPUNIT_TEST_SUITE_END();

	private:
		MetadataField m_field;
		MetadataArray m_array;
		MetadataKey m_key;
		MetadataMap* m_map1;
		MetadataMap* m_map1a;
		MetadataMap* m_map2;

	public:

		void setUp() {
			m_field = buildField();
			m_array = buildArray();
			m_key = buildKey();
			m_map1 = buildMap1();
			m_map1a = buildMap1();
			m_map2 = buildMap2();
		}

		void tearDown() {
			delete m_map1;
			delete m_map1a;
			delete m_map2;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_map1==*m_map1a);
		}
		
		void testNotEqual() {
			CPPUNIT_ASSERT(*m_map1!=*m_map2);
			CPPUNIT_ASSERT(!(*m_map1==*m_map2));
		}
		
		void testElements() {
			CPPUNIT_ASSERT(m_field==*m_map1->getElement("key1"));
			CPPUNIT_ASSERT(m_array==*m_map1->getElement("key2"));
			CPPUNIT_ASSERT(m_key==*m_map1->getElement("key3"));
		}

		void testCast() {
			MetadataElement* element = m_map1;
			MetadataMap* map = MetadataMap::castToMap(element);
		}
			
		void testCastWrong() {
			MetadataElement* element = &m_field;
			MetadataMap* map = MetadataMap::castToMap(element);
		}


	private:

		MetadataField buildField() {
			return MetadataField::createInteger(666);
		}

		MetadataArray buildArray() {
			char bytes[5] = { 1,4,9,16,25 };
			return MetadataArray::createByte(bytes,5);
		}

		
		MetadataKey buildKey() {
			DataKeyDimension dim = DataKeyDimension("dim1");
			DataKeyElement ele = DataKeyElement("val1", dim);
			DataKey key(ele);
			return MetadataKey(key);
		}

		MetadataMap* buildMap1() {
			MetadataMap* map = new MetadataMap();
			map->putElement("key1",m_field);
			map->putElement("key2",m_array);
			map->putElement("key3",m_key);
			return map;
		}

		MetadataMap* buildMap2() {
			MetadataMap* map = new MetadataMap();
			map->putElement("key1",m_array);
			map->putElement("key2",m_key);
			map->putElement("key3",m_field);
			return map;
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestMetadataMap, "TestMetadataMap");

}

