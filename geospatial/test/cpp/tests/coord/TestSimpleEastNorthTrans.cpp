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

class TestSimpleEastNorthTrans : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestSimpleEastNorthTrans);
	CPPUNIT_TEST(testFromLatLon);
	CPPUNIT_TEST(testRoundTripConversion);
	CPPUNIT_TEST_SUITE_END();

	private:

		SimpleEastNorthTrans* m_trans;

	public:

		void setUp() {
			// using miles here
			m_trans = new SimpleEastNorthTrans(4000);
		}

		void tearDown() {
			delete m_trans;
		}

		void testFromLatLon() {
			double eps = 1;
			LatLonCoord latlon = LatLonCoord::fromDegrees(60, 135);
			EastNorthCoord expected(9425, 4189);
			EastNorthCoord actual = m_trans->toEastNorth(latlon);
			CPPUNIT_ASSERT_DOUBLES_EQUAL(expected.getEast(), actual.getEast(), eps);
			CPPUNIT_ASSERT_DOUBLES_EQUAL(expected.getNorth(), actual.getNorth(), eps);
		}

		void testRoundTripConversion() {
			EastNorthCoord en(1500, 2000);
			LatLonCoord ll = m_trans->toLatLon(en);
			EastNorthCoord en2 = m_trans->toEastNorth(ll);
			CPPUNIT_ASSERT_EQUAL(en, en2);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestSimpleEastNorthTrans, "TestSimpleEastNorthTrans");

}
