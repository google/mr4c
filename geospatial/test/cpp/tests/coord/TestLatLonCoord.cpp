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

#include <cmath>

#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>
#include "coord/coord_api.h"

namespace MR4C {

class TestLatLonCoord : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestLatLonCoord);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqualLat);
	CPPUNIT_TEST(testNotEqualLon);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST(testDegreesToRadians);
	CPPUNIT_TEST(testRoundTripConversion);
	CPPUNIT_TEST_SUITE_END();

	private:

		LatLonCoord* m_coord1;
		LatLonCoord* m_coord1a;
		LatLonCoord* m_coord2;
		LatLonCoord* m_coord3;

	public:

		void setUp() {
			m_coord1 = LatLonCoord::newFromDegrees(15, 25);
			m_coord1a = LatLonCoord::newFromDegrees(15, 25);
			m_coord2 = LatLonCoord::newFromDegrees(80, 25);
			m_coord3 = LatLonCoord::newFromDegrees(15, 44);
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
		
		void testNotEqualLat() {
			CPPUNIT_ASSERT(*m_coord1!=*m_coord2);
			CPPUNIT_ASSERT(!(*m_coord1==*m_coord2));
		}
		
		void testNotEqualLon() {
			CPPUNIT_ASSERT(*m_coord1!=*m_coord3);
			CPPUNIT_ASSERT(!(*m_coord1==*m_coord3));
		}
		
		void testAssignment() {
			LatLonCoord coord = *m_coord1;
			CPPUNIT_ASSERT_EQUAL(*m_coord1, coord);
		}

		void testCopy() {
			LatLonCoord coord(*m_coord1);
			CPPUNIT_ASSERT_EQUAL(*m_coord1, coord);
		}

		void testDegreesToRadians() {
			LatLonCoord coordDeg = LatLonCoord::fromDegrees(30,90);
			LatLonCoord coordRad = LatLonCoord::fromRadians(M_PI/6.0,M_PI/2.0);
			CPPUNIT_ASSERT_EQUAL(coordDeg, coordRad);
		}
		
		void testRoundTripConversion() {
			LatLonCoord deg1 = LatLonCoord::fromDegrees(12, 22);
			LatLonCoord rad1 = LatLonCoord::fromRadians(
				deg1.getLatRadians(),
				deg1.getLonRadians()
			);
			LatLonCoord deg2 = LatLonCoord::fromDegrees(
				rad1.getLatDegrees(),
				rad1.getLonDegrees()
			);
			CPPUNIT_ASSERT_EQUAL(deg1, deg2);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestLatLonCoord, "TestLatLonCoord");

}
