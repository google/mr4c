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

class TestDataKeyElement : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestDataKeyElement);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqualId);
	CPPUNIT_TEST(testNotEqualDim);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST_SUITE_END();

	private:
		DataKeyDimension* m_dim1;
		DataKeyDimension* m_dim3;
		DataKeyElement* m_ele1;
		DataKeyElement* m_ele1a;
		DataKeyElement* m_ele2;
		DataKeyElement* m_ele3;

	public:

		void setUp() {
			m_dim1 = buildDimension1();
			m_dim3 = buildDimension3();
			m_ele1 = buildElement1();
			m_ele1a = buildElement1();
			m_ele2 = buildElement2();
			m_ele3 = buildElement3();
		}



		void tearDown() {
			delete m_dim1;
			delete m_dim3;
			delete m_ele1;
			delete m_ele1a;
			delete m_ele2;
			delete m_ele3;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_ele1==*m_ele1a);
		}
		
		void testNotEqualId() {
			CPPUNIT_ASSERT(*m_ele1!=*m_ele2);
			CPPUNIT_ASSERT(!(*m_ele1==*m_ele2));
		}
		
		void testNotEqualDim() {
			CPPUNIT_ASSERT(*m_ele1!=*m_ele3);
			CPPUNIT_ASSERT(!(*m_ele1==*m_ele3));
		}
		
		void testAssignment() {
			DataKeyElement ele = *m_ele1;
			CPPUNIT_ASSERT(ele==*m_ele1);
		}

		void testCopy() {
			DataKeyElement ele(*m_ele1);
			CPPUNIT_ASSERT(ele==*m_ele1);
		}


	private: 

		DataKeyDimension* buildDimension1() {
			return new DataKeyDimension("dim1");
		}

		DataKeyDimension* buildDimension3() {
			return new DataKeyDimension("dim3");
		}

		DataKeyElement* buildElement1() {
			return new DataKeyElement("ele1", *m_dim1);
		}

		DataKeyElement* buildElement2() {
			return new DataKeyElement("ele2", *m_dim1);
		}

		DataKeyElement* buildElement3() {
			return new DataKeyElement("ele1", *m_dim3);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestDataKeyElement, "TestDataKeyElement");

}
