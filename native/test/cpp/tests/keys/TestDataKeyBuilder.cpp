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

namespace MR4C {

class TestDataKeyBuilder : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestDataKeyBuilder);
	CPPUNIT_TEST(testAddElement);
	CPPUNIT_TEST(testAddElements);
	CPPUNIT_TEST(testAddAllElements);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST_SUITE_END();

	private:
		DataKeyDimension m_dim1;
		DataKeyDimension m_dim2;
		DataKeyDimension m_dim3;
		DataKeyElement m_ele1;
		DataKeyElement m_ele2;
		DataKeyElement m_ele3;

	public:

		void setUp() {
			m_dim1 = DataKeyDimension("dim1");
			m_dim2 = DataKeyDimension("dim2");
			m_dim3 = DataKeyDimension("dim3");
			m_ele1 = DataKeyElement("ele1", m_dim1);
			m_ele2 = DataKeyElement("ele2", m_dim2);
			m_ele3 = DataKeyElement("ele3", m_dim3);
		}



		void tearDown() {
		}

		void testAddElement() {
			DataKeyBuilder builder;
			std::set<DataKeyElement> elements;
			builder.addElement(m_ele1);
			elements.insert(m_ele1);
			DataKey key = builder.toKey();
			checkElements(elements,key);
		}

		void testAddElements() {
			DataKeyBuilder builder;
			std::set<DataKeyElement> elements;
			elements.insert(m_ele1);
			elements.insert(m_ele2);
			elements.insert(m_ele3);
			builder.addElements(elements);
			DataKey key = builder.toKey();
			checkElements(elements,key);
		}

		void testAddAllElements() {
			DataKeyBuilder builder;
			std::set<DataKeyElement> elements;
			elements.insert(m_ele1);
			elements.insert(m_ele2);
			elements.insert(m_ele3);
			DataKey key(elements);
			builder.addAllElements(key);
			DataKey key2 = builder.toKey();
			checkElements(elements,key2);
		}

		void testAssignment() {
			DataKeyBuilder builder1;
			builder1.addElement(m_ele1);
			builder1.addElement(m_ele2);
			DataKeyBuilder builder2 = builder1;
			checkBuilder(builder1, builder2);
		}

		void testCopy() {
			DataKeyBuilder builder1;
			builder1.addElement(m_ele1);
			builder1.addElement(m_ele2);
			DataKeyBuilder builder2 = DataKeyBuilder(builder1);
			checkBuilder(builder1, builder2);
		}


	private: 

		// check all elements there, and no more!
		void checkElements(std::set<DataKeyElement> elements,DataKey key) {
			std::set<DataKeyElement>::iterator iter = elements.begin();
			while ( iter!=elements.end() ) {
				CPPUNIT_ASSERT(key.hasElement(*iter));
				iter++;
			}
			CPPUNIT_ASSERT_EQUAL(elements.size(), key.getElementCount());
		}

		void checkBuilder(DataKeyBuilder& builder1, DataKeyBuilder& builder2) {
			DataKey key1 = builder1.toKey();
			DataKey key2 = builder2.toKey();
			CPPUNIT_ASSERT(key1==key2);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestDataKeyBuilder, "TestDataKeyBuilder");

}
