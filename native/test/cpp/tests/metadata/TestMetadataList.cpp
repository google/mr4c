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
#include "metadata/metadata_api.h"

namespace MR4C {

class TestMetadataList : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestMetadataList);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqual);
	CPPUNIT_TEST(testElements);
	CPPUNIT_TEST(testCast);
	CPPUNIT_TEST_SUITE_END();

	private:
		MetadataField m_field;
		MetadataArray m_array;
		MetadataKey m_key;
		MetadataList* m_list1;
		MetadataList* m_list1a;
		MetadataList* m_list2;

	public:

		void setUp() {
			m_field = buildField();
			m_array = buildArray();
			m_key = buildKey();
			m_list1 = buildList1();
			m_list1a = buildList1();
			m_list2 = buildList2();
		}

		void tearDown() {
			delete m_list1;
			delete m_list1a;
			delete m_list2;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_list1==*m_list1a);
		}
		
		void testNotEqual() {
			CPPUNIT_ASSERT(*m_list1!=*m_list2);
			CPPUNIT_ASSERT(!(*m_list1==*m_list2));
		}
		
		void testElements() {
			CPPUNIT_ASSERT(m_field==*m_list1->getElement(0));
			CPPUNIT_ASSERT(m_array==*m_list1->getElement(1));
			CPPUNIT_ASSERT(m_key==*m_list1->getElement(2));
		}

		void testCast() {
			const MetadataElement* element = m_list1;
			const MetadataList* list = MetadataList::castToList(element);
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

		MetadataList* buildList1() {
			MetadataList* list = new MetadataList();
			list->addElement(m_field);
			list->addElement(m_array);
			list->addElement(m_key);
			return list;
		}

		MetadataList* buildList2() {
			MetadataList* list = new MetadataList();
			list->addElement(m_array);
			list->addElement(m_key);
			list->addElement(m_field);
			return list;
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestMetadataList, "TestMetadataList");

}

