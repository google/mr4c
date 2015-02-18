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
#include <stdexcept>
#include "coord/coord_api.h"

namespace MR4C {

class TestBoundingBox : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestBoundingBox);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqualNW);
	CPPUNIT_TEST(testNotEqualSE);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST(testCreateFromEastNorth);
	CPPUNIT_TEST(testCreateFromLatLon);
	CPPUNIT_TEST(testCreateFromNormMerc);
	CPPUNIT_TEST_EXCEPTION(testCreateInvalidOrientationX, std::invalid_argument);
	CPPUNIT_TEST_EXCEPTION(testCreateInvalidOrientationY, std::invalid_argument);
	CPPUNIT_TEST_EXCEPTION(testCreateZeroWidth, std::invalid_argument);
	CPPUNIT_TEST_EXCEPTION(testCreateZeroHeight, std::invalid_argument);
	CPPUNIT_TEST(testIntersecting);
	CPPUNIT_TEST(testIntersect);
	CPPUNIT_TEST_SUITE_END();

	private:

		EastNorthCoord m_nwEN;
		EastNorthCoord m_seEN;
		LatLonCoord m_nwLL;
		LatLonCoord m_seLL;
		NormMercCoord m_nwNM;
		NormMercCoord m_seNM;
		std::shared_ptr<EastNorthTrans> m_trans;
		double m_dx;
		double m_dy;
		BoundingBox* m_box1;
		BoundingBox* m_box1a;
		BoundingBox* m_box2;
		BoundingBox* m_box3;
		BoundingBox* m_box4;
		BoundingBox m_boxA;
		BoundingBox m_boxB;
		BoundingBox m_boxC;
		BoundingBox m_boxAB;

	public:

		void setUp() {
			// using miles here
			m_nwEN = EastNorthCoord(1000, 2000);
			m_seEN = EastNorthCoord(3000, 500);
			m_nwLL = LatLonCoord::fromRadians(.5, .25);
			m_seLL = LatLonCoord::fromRadians(.125, .75);
			m_nwNM = NormMercCoord(m_nwLL);
			m_seNM = NormMercCoord(m_seLL);
			m_trans = std::shared_ptr<EastNorthTrans>(new SimpleEastNorthTrans(4000));
			m_dx = m_seNM.getX() - m_nwNM.getX();
			m_dy = m_seNM.getY() - m_nwNM.getY();
			m_box1 = new BoundingBox(m_nwEN, m_seEN, m_trans);
			m_box1a = new BoundingBox(m_nwEN, m_seEN, m_trans);
			m_box2 = new BoundingBox(EastNorthCoord(1100, 2000), m_seEN, m_trans);
			m_box3 = new BoundingBox(m_nwEN, EastNorthCoord(3000, 400), m_trans);
			buildIntersectingBoxes();
		}

		void tearDown() {
			delete m_box1;
			delete m_box1a;
			delete m_box2;
			delete m_box3;
		}

		void testEqual() {
			CPPUNIT_ASSERT_EQUAL(*m_box1,*m_box1a);
		}
		
		void testNotEqualNW() {
			CPPUNIT_ASSERT(*m_box1!=*m_box2);
			CPPUNIT_ASSERT(!(*m_box1==*m_box2));
		}
		
		void testNotEqualSE() {
			CPPUNIT_ASSERT(*m_box1!=*m_box3);
			CPPUNIT_ASSERT(!(*m_box1==*m_box3));
		}
		
		void testAssignment() {
			BoundingBox box = *m_box1;
			CPPUNIT_ASSERT_EQUAL(*m_box1, box);
		}

		void testCopy() {
			BoundingBox box(*m_box1);
			CPPUNIT_ASSERT_EQUAL(*m_box1, box);
		}

		void testCreateFromEastNorth() {
			BoundingBox box(m_nwEN, m_seEN, m_trans);
			validateBox(box);
		}

		void testCreateFromLatLon() {
			BoundingBox box(m_nwLL, m_seLL, m_trans);
			validateBox(box);
		}

		void testCreateFromNormMerc() {
			BoundingBox box(m_nwNM, m_seNM, m_trans);
			validateBox(box);
		}

		void testCreateInvalidOrientationX() {
			EastNorthCoord nw(1000, 3000);
			EastNorthCoord se(500, 1500);
			BoundingBox box(nw, se, m_trans);
		}

		void testCreateInvalidOrientationY() {
			EastNorthCoord nw(1000, 3000);
			EastNorthCoord se(2000, 3500);
			BoundingBox box(nw, se, m_trans);
		}

		void testCreateZeroWidth() {
			EastNorthCoord nw(1000, 3000);
			EastNorthCoord se(1000, 1500);
			BoundingBox box(nw, se, m_trans);
		}

		void testCreateZeroHeight() {
			EastNorthCoord nw(1000, 3000);
			EastNorthCoord se(2000, 3000);
			BoundingBox box(nw, se, m_trans);
		}

		void testIntersecting() {
			CPPUNIT_ASSERT(BoundingBox::intersecting(m_boxA, m_boxB));
			CPPUNIT_ASSERT(!BoundingBox::intersecting(m_boxA, m_boxC));
		}

		void testIntersect() {
			BoundingBox box = BoundingBox::intersect(m_boxA, m_boxB);	
			CPPUNIT_ASSERT_EQUAL(m_boxAB, box);
		}

	private :

		void validateBox(const BoundingBox& box) {
			CPPUNIT_ASSERT_EQUAL(*m_box1, box);
			double eps = 1e-6;
			CPPUNIT_ASSERT_DOUBLES_EQUAL(2000, box.dE(), eps);
			CPPUNIT_ASSERT_DOUBLES_EQUAL(1500, box.dN(), eps);
			CPPUNIT_ASSERT_DOUBLES_EQUAL(m_dx, box.dx(), eps);
			CPPUNIT_ASSERT_DOUBLES_EQUAL(m_dy, box.dy(), eps);
		}
			

		void buildIntersectingBoxes() {
			m_boxA = BoundingBox(
				NormMercCoord(0.4, 0.6),
				NormMercCoord(0.5, 0.8),
				m_trans
			);
			m_boxB = BoundingBox(
				NormMercCoord(0.45, 0.5),
				NormMercCoord(0.6, 0.65),
				m_trans
			);
			m_boxAB = BoundingBox(
				NormMercCoord(0.45, 0.6),
				NormMercCoord(0.5, 0.65),
				m_trans
			);
			m_boxC = BoundingBox(
				NormMercCoord(0.45, 0.9),
				NormMercCoord(0.6, 0.95),
				m_trans
			);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestBoundingBox, "TestBoundingBox");

}
