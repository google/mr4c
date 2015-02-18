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
#include "coord/coord_api.h"

namespace MR4C {

class TestEastNorthCoord : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestEastNorthCoord);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqualEast);
	CPPUNIT_TEST(testNotEqualNorth);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST_SUITE_END();

	private:

		EastNorthCoord* m_coord1;
		EastNorthCoord* m_coord1a;
		EastNorthCoord* m_coord2;
		EastNorthCoord* m_coord3;

	public:

		void setUp() {
			// using miles here
			m_coord1 = new EastNorthCoord(1500, 2000);
			m_coord1a = new EastNorthCoord(1500, 2000);
			m_coord2 = new EastNorthCoord(2500, 2000);
			m_coord3 = new EastNorthCoord(1500, 3000);
		}

		void tearDown() {
			delete m_coord1;
			delete m_coord1a;
			delete m_coord2;
			delete m_coord3;
		}

		void testEqual() {
			CPPUNIT_ASSERT_EQUAL(*m_coord1,*m_coord1a);
		}
		
		void testNotEqualEast() {
			CPPUNIT_ASSERT(*m_coord1!=*m_coord2);
			CPPUNIT_ASSERT(!(*m_coord1==*m_coord2));
		}
		
		void testNotEqualNorth() {
			CPPUNIT_ASSERT(*m_coord1!=*m_coord3);
			CPPUNIT_ASSERT(!(*m_coord1==*m_coord3));
		}
		
		void testAssignment() {
			EastNorthCoord coord = *m_coord1;
			CPPUNIT_ASSERT_EQUAL(*m_coord1, coord);
		}

		void testCopy() {
			EastNorthCoord coord(*m_coord1);
			CPPUNIT_ASSERT_EQUAL(*m_coord1, coord);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestEastNorthCoord, "TestEastNorthCoord");

}
