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

class TestNormMercCoord : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestNormMercCoord);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqualX);
	CPPUNIT_TEST(testNotEqualY);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST(testFromLatLon);
	CPPUNIT_TEST(testRoundTripConversion);
	CPPUNIT_TEST_SUITE_END();

	private:

		NormMercCoord* m_coord1;
		NormMercCoord* m_coord1a;
		NormMercCoord* m_coord2;
		NormMercCoord* m_coord3;

	public:

		void setUp() {
			m_coord1 = new NormMercCoord(0.4, 0.6);
			m_coord1a = new NormMercCoord(0.4, 0.6);
			m_coord2 = new NormMercCoord(0.5, 0.6);
			m_coord3 = new NormMercCoord(0.4, 0.7);
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
		
		void testNotEqualX() {
			CPPUNIT_ASSERT(*m_coord1!=*m_coord2);
			CPPUNIT_ASSERT(!(*m_coord1==*m_coord2));
		}
		
		void testNotEqualY() {
			CPPUNIT_ASSERT(*m_coord1!=*m_coord3);
			CPPUNIT_ASSERT(!(*m_coord1==*m_coord3));
		}
		
		void testAssignment() {
			NormMercCoord coord = *m_coord1;
			CPPUNIT_ASSERT_EQUAL(*m_coord1, coord);
		}

		void testCopy() {
			NormMercCoord coord(*m_coord1);
			CPPUNIT_ASSERT_EQUAL(*m_coord1, coord);
		}

		void testFromLatLon() {
			double eps = 1e-10;
			double eps2 = 1e-3;
			LatLonCoord latlon = LatLonCoord::fromDegrees(60, 135);
			NormMercCoord expected(.875, .2904);
			NormMercCoord actual(latlon);
			CPPUNIT_ASSERT_DOUBLES_EQUAL(expected.getX(), actual.getX(), eps);
			CPPUNIT_ASSERT_DOUBLES_EQUAL(expected.getY(), actual.getY(), eps2);
		}

		void testRoundTripConversion() {
			LatLonCoord ll = m_coord1->toLatLon();
			NormMercCoord nm(ll);
			CPPUNIT_ASSERT_EQUAL(*m_coord1, nm);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestNormMercCoord, "TestNormMercCoord");

}
