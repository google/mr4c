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
#include "mbtiles/mbtiles_api.h"

namespace MR4C {

class TestTileKey : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestTileKey);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqualZoom);
	CPPUNIT_TEST(testNotEqualX);
	CPPUNIT_TEST(testNotEqualY);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST(testToDataKey);
	CPPUNIT_TEST_SUITE_END();

	private:

		TileKey* m_key1;
		TileKey* m_key1a;
		TileKey* m_key2;
		TileKey* m_key3;
		TileKey* m_key4;

	public:

		void setUp() {
			m_key1 = new TileKey(5, 10, 20);
			m_key1a = new TileKey(5, 10, 20);
			m_key2 = new TileKey(6, 10, 20);
			m_key3 = new TileKey(5, 30, 20);
			m_key4 = new TileKey(5, 10, 40);
		}

		void tearDown() {
			delete m_key1;
			delete m_key1a;
			delete m_key2;
		}

		void testEqual() {
			CPPUNIT_ASSERT_EQUAL(*m_key1,*m_key1a);
		}
		
		void testNotEqualZoom() {
			CPPUNIT_ASSERT(*m_key1!=*m_key2);
			CPPUNIT_ASSERT(!(*m_key1==*m_key2));
		}
		
		void testNotEqualX() {
			CPPUNIT_ASSERT(*m_key1!=*m_key3);
			CPPUNIT_ASSERT(!(*m_key1==*m_key3));
		}
		
		void testNotEqualY() {
			CPPUNIT_ASSERT(*m_key1!=*m_key4);
			CPPUNIT_ASSERT(!(*m_key1==*m_key4));
		}
		
		void testAssignment() {
			TileKey key = *m_key1;
			CPPUNIT_ASSERT_EQUAL(*m_key1, key);
		}

		void testCopy() {
			TileKey key(*m_key1);
			CPPUNIT_ASSERT_EQUAL(*m_key1, key);
		}

		void testToDataKey() {
			DataKey dataKey = m_key1->toDataKey();
			TileKey key(dataKey);
			CPPUNIT_ASSERT_EQUAL(*m_key1, key);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestTileKey, "TestTileKey");

}
