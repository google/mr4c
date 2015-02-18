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

#include <stdexcept>

#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>
#include "coord/coord_api.h"

namespace MR4C {

class TestImageBox : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestImageBox);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqualWidth);
	CPPUNIT_TEST(testNotEqualHeight);
	CPPUNIT_TEST(testNotEqualBound);
	CPPUNIT_TEST(testNotEqualX);
	CPPUNIT_TEST(testNotEqualY);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST(testXYCalcs);
	CPPUNIT_TEST(testWindow);
	CPPUNIT_TEST_EXCEPTION(testCreateZeroWidth, std::invalid_argument);
	CPPUNIT_TEST_EXCEPTION(testCreateZeroHeight, std::invalid_argument);
	CPPUNIT_TEST_EXCEPTION(testCreateNegativeX1, std::invalid_argument);
	CPPUNIT_TEST_EXCEPTION(testCreateNegativeY1, std::invalid_argument);

	CPPUNIT_TEST_SUITE_END();

	private:

		NormMercCoord m_nw;
		NormMercCoord m_se;
		std::shared_ptr<EastNorthTrans> m_trans;
		BoundingBox m_bound;
		static const int m_width = 200;
		static const int m_height = 100;
		static const int m_x = 50;
		static const int m_y = 75;
		ImageBox* m_box1;
		ImageBox* m_box1a;
		ImageBox* m_box2;
		ImageBox* m_box3;
		ImageBox* m_box4;
		ImageBox* m_box5;
		ImageBox* m_box6;

	public:

		void setUp() {
			m_nw = NormMercCoord(0.2, 0.4);
			m_se = NormMercCoord(1.0, 0.9);
			m_trans = std::shared_ptr<EastNorthTrans>(new SimpleEastNorthTrans(4000));
			m_bound = BoundingBox(m_nw, m_se, m_trans);
			m_box1 = new ImageBox(m_width, m_height, m_bound, m_x, m_y);
			m_box1a = new ImageBox(m_width, m_height, m_bound, m_x, m_y);
			m_box2 = new ImageBox(1000, m_height, m_bound, m_x, m_y);
			m_box3 = new ImageBox(m_width, 1000, m_bound, m_x, m_y);
			m_box4 = new ImageBox(m_width, m_height, BoundingBox(NormMercCoord(0.0, 0.0), m_se, m_trans), m_x, m_y);
			m_box5 = new ImageBox(m_width, m_height, m_bound, 400, m_y);
			m_box6 = new ImageBox(m_width, m_height, m_bound, m_x, 400);
		}

		void tearDown() {
			delete m_box1;
			delete m_box1a;
			delete m_box2;
			delete m_box3;
			delete m_box4;
			delete m_box5;
			delete m_box6;
		}

		void testEqual() {
			CPPUNIT_ASSERT_EQUAL(*m_box1,*m_box1a);
		}
		
		void testNotEqualWidth() {
			CPPUNIT_ASSERT(*m_box1!=*m_box2);
			CPPUNIT_ASSERT(!(*m_box1==*m_box2));
		}
		
		void testNotEqualHeight() {
			CPPUNIT_ASSERT(*m_box1!=*m_box3);
			CPPUNIT_ASSERT(!(*m_box1==*m_box3));
		}
		
		void testNotEqualBound() {
			CPPUNIT_ASSERT(*m_box1!=*m_box4);
			CPPUNIT_ASSERT(!(*m_box1==*m_box4));
		}
		
		void testNotEqualX() {
			CPPUNIT_ASSERT(*m_box1!=*m_box5);
			CPPUNIT_ASSERT(!(*m_box1==*m_box5));
		}
		
		void testNotEqualY() {
			CPPUNIT_ASSERT(*m_box1!=*m_box6);
			CPPUNIT_ASSERT(!(*m_box1==*m_box6));
		}
		
		void testAssignment() {
			ImageBox box = *m_box1;
			CPPUNIT_ASSERT_EQUAL(*m_box1, box);
		}

		void testCopy() {
			ImageBox box(*m_box1);
			CPPUNIT_ASSERT_EQUAL(*m_box1, box);
		}

		void testXYCalcs() {
			CPPUNIT_ASSERT_EQUAL(m_box1->getX2(), 250);
			CPPUNIT_ASSERT_EQUAL(m_box1->getY2(), 175);
		}

		void testWindow() {

			EastNorthCoord nw1(1000, 4000);
			EastNorthCoord se1(1500, 2000);
			BoundingBox bound1(nw1, se1, m_trans);

			EastNorthCoord nw2(1200, 3500);
			EastNorthCoord se2(1300, 3000);
			BoundingBox bound2(nw2, se2, m_trans);

			ImageBox img1(25, 100, bound1);
			ImageBox expected(5, 25, bound2, 10, 25);
			ImageBox actual = img1.window(bound2);
			CPPUNIT_ASSERT_EQUAL(expected, actual);
		}

		void testCreateZeroWidth() {
			ImageBox(0, 10, m_bound);
		}

		void testCreateZeroHeight() {
			ImageBox(10, 0, m_bound);
		}

		void testCreateNegativeX1() {
			ImageBox(20, 10, m_bound, -5, 0);
		}

		void testCreateNegativeY1() {
			ImageBox(20, 10, m_bound, 0, -3);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestImageBox, "TestImageBox");

}
