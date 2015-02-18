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
#include "gdal/gdal_api.h"

namespace MR4C {

class TestGDALCoordTrans : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestGDALCoordTrans);
	CPPUNIT_TEST(testRoundTripConversion);
	CPPUNIT_TEST_SUITE_END();

	private:

		GDALCoordTrans* m_trans;

	public:

		void setUp() {
			// using miles here
			OGRSpatialReference* sref = new OGRSpatialReference();
			sref->SetWellKnownGeogCS("WGS84");
			sref->SetMercator(0.0, 0.0, 1.0, 0.0, 0.0);
			std::shared_ptr<OGRCoordinateTransformation> trans(generateCoordinateTransform(sref));
			m_trans = new GDALCoordTrans(trans);
		}

		void tearDown() {
			delete m_trans;
		}

		void testRoundTripConversion() {
			LatLonCoord ll1 = LatLonCoord::fromDegrees(33.0, 51.0);
			EastNorthCoord en = m_trans->toEastNorth(ll1);
			LatLonCoord ll2 = m_trans->toLatLon(en);
			CPPUNIT_ASSERT_EQUAL(ll1, ll2);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestGDALCoordTrans, "TestGDALCoordTrans");

}
