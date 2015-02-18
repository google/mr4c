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
#include <set>
#include <stdexcept>

namespace MR4C {

class TestDataKey : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestDataKey);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqual);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST(testCount);
	CPPUNIT_TEST(testGetElement);
	CPPUNIT_TEST_EXCEPTION(testGetElementFail, std::invalid_argument);
	CPPUNIT_TEST_EXCEPTION(testConstructorFail, std::invalid_argument);
	CPPUNIT_TEST(testToName);
	CPPUNIT_TEST_SUITE_END();

	private:

		DataKeyDimension m_dim1;
		DataKeyDimension m_dim2;
		DataKeyDimension m_dim3;

		DataKeyElement m_ele1;
		DataKeyElement m_ele2;
		DataKeyElement m_ele2b;
		DataKeyElement m_ele3;

		DataKey* m_key1;
		DataKey* m_key1a;
		DataKey* m_key2;

	public:

		void setUp() {
			m_dim1 = DataKeyDimension("dim1");
			m_dim2 = DataKeyDimension("dim2");
			m_dim3 = DataKeyDimension("dim3");
			m_ele1 = DataKeyElement("ele1", m_dim1);
			m_ele2 = DataKeyElement("ele2", m_dim2);
			m_ele2b = DataKeyElement("ele2b", m_dim2);
			m_ele3 = DataKeyElement("ele1", m_dim3);
			m_key1 = buildKey1();
			m_key1a = buildKey1();
			m_key2 = buildKey2();
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
			DataKey key = *m_key1;
			CPPUNIT_ASSERT(key==*m_key1);
		}

		void testCopy() {
			DataKey key(*m_key1);
			CPPUNIT_ASSERT(key==*m_key1);
		}

		void testCount() {
			CPPUNIT_ASSERT_EQUAL(2, (int) m_key1->getElementCount());
		}

		void testHasElement() {
			CPPUNIT_ASSERT(m_key1->hasElement(m_ele1));
		}

		void testGetElement() {
			CPPUNIT_ASSERT(m_key1->getElement(m_dim1)==m_ele1);
		}

		void testGetElementFail() {
			m_key1->getElement(m_dim3);
		}

		void testConstructorFail() {
			std::set<DataKeyElement> elements;
			elements.insert(m_ele1);	
			elements.insert(m_ele2);
			elements.insert(m_ele2b);
			DataKey key(elements);
		}

		void testToName() {
			std::string expected = "ele1__ele2__ele1";
			CPPUNIT_ASSERT_EQUAL(expected, m_key2->toName("__"));
		}

	private:

		 DataKey* buildKey1() {
			std::set<DataKeyElement> elements;
			elements.insert(m_ele1);	
			elements.insert(m_ele2);
			return new DataKey(elements);
		}	

		DataKey* buildKey2() {
			std::set<DataKeyElement> elements;
			elements.insert(m_ele1);	
			elements.insert(m_ele2);
			elements.insert(m_ele3);
			return new DataKey(elements);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestDataKey, "TestDataKey");

}
