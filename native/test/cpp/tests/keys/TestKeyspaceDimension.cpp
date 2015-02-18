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

class TestKeyspaceDimension : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestKeyspaceDimension);
	CPPUNIT_TEST(dummy);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqual);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST(testCount);
	CPPUNIT_TEST(testGetElements);
	CPPUNIT_TEST_EXCEPTION(testConstructorFail, std::invalid_argument);
	CPPUNIT_TEST_SUITE_END();

	private:

		DataKeyDimension m_dim1;
		DataKeyDimension m_dim2;

		DataKeyElement m_ele1;
		DataKeyElement m_ele2;
		DataKeyElement m_ele3;
		DataKeyElement m_ele4;

		std::vector<DataKeyElement> m_elements;

		KeyspaceDimension* m_ksd1a;
		KeyspaceDimension* m_ksd1b;
		KeyspaceDimension* m_ksd2;

	public:

		void setUp() {

			m_dim1 = DataKeyDimension("dim1");
			m_dim2 = DataKeyDimension("dim2");

			m_ele1 = DataKeyElement("ele1", m_dim1);
			m_ele2 = DataKeyElement("ele2", m_dim1);
			m_ele3 = DataKeyElement("ele3", m_dim1);
			m_ele4 = DataKeyElement("ele4", m_dim2);

			m_elements.push_back(m_ele1);
			m_elements.push_back(m_ele2);
			m_elements.push_back(m_ele3);
			
			m_ksd1a = buildKeyspaceDimension1();
			m_ksd1b = buildKeyspaceDimension1();
			m_ksd2 = buildKeyspaceDimension2();
		}

		void dummy() {}

		void tearDown() {
			delete m_ksd1a;
			delete m_ksd1b;
			delete m_ksd2;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_ksd1a==*m_ksd1b);
		}
		
		void testNotEqual() {
			CPPUNIT_ASSERT(*m_ksd1a!=*m_ksd2);
			CPPUNIT_ASSERT(!(*m_ksd1a==*m_ksd2));
		}
		
		void testAssignment() {
			KeyspaceDimension ksd = *m_ksd1a;
			CPPUNIT_ASSERT(ksd==*m_ksd1a);
		}

		void testCopy() {
			KeyspaceDimension ksd(*m_ksd1a);
			CPPUNIT_ASSERT(ksd==*m_ksd1a);
		}

		void testCount() {
			CPPUNIT_ASSERT_EQUAL(3, (int) m_ksd1a->getElementCount());
		}

		void testGetElements() {
			CPPUNIT_ASSERT(m_ksd1a->getElement(0)==m_ele1);
			CPPUNIT_ASSERT(m_ksd1a->getElement(1)==m_ele2);
			CPPUNIT_ASSERT(m_ksd1a->getElement(2)==m_ele3);
			CPPUNIT_ASSERT(m_ksd1a->getElements()==m_elements);
		}

		void testConstructorFail() {
			std::vector<DataKeyElement> elements;
			elements.push_back(m_ele1);	
			elements.push_back(m_ele2);
			elements.push_back(m_ele4);
			KeyspaceDimension ksd(m_dim1, elements);
		}
	

	private:

		 KeyspaceDimension* buildKeyspaceDimension1() {
			std::vector<DataKeyElement> elements;
			elements.push_back(m_ele1);	
			elements.push_back(m_ele2);
			elements.push_back(m_ele3);
			return new KeyspaceDimension(m_dim1, elements);
		}	

		 KeyspaceDimension* buildKeyspaceDimension2() {
			std::vector<DataKeyElement> elements;
			elements.push_back(m_ele1);	
			elements.push_back(m_ele2);
			return new KeyspaceDimension(m_dim1, elements);
		}	

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestKeyspaceDimension, "TestKeyspaceDimension");

}
