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

class TestDataKeyDimension : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestDataKeyDimension);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqual);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST_SUITE_END();

	private:
		DataKeyDimension* m_dim1;
		DataKeyDimension* m_dim1a;
		DataKeyDimension* m_dim2;

	public:

		void setUp() {
			m_dim1 = new DataKeyDimension("dim1");
			m_dim1a = new DataKeyDimension("dim1");
			m_dim2 = new DataKeyDimension("dim2");
		}

		void tearDown() {
			delete m_dim1;
			delete m_dim1a;
			delete m_dim2;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_dim1==*m_dim1a);
		}
		
		void testNotEqual() {
			CPPUNIT_ASSERT(*m_dim1!=*m_dim2);
			CPPUNIT_ASSERT(!(*m_dim1==*m_dim2));
		}
		
		void testAssignment() {
			DataKeyDimension dim = *m_dim1;
			CPPUNIT_ASSERT(dim==*m_dim1);
		}

		void testCopy() {
			DataKeyDimension dim(*m_dim1);
			CPPUNIT_ASSERT(dim==*m_dim1);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestDataKeyDimension, "TestDataKeyDimension");

}
