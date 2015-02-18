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

class TestMBTilesUtils : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestMBTilesUtils);
	CPPUNIT_TEST(testZoomToTileCount);
	CPPUNIT_TEST(testTileCountToZoom);
	CPPUNIT_TEST(testZoomToTileSize);
	CPPUNIT_TEST(testTileSizeToZoom);
	CPPUNIT_TEST(testFlipTileY);
	CPPUNIT_TEST(testTileIndex);
	CPPUNIT_TEST(testToBoundingBox);
	CPPUNIT_TEST(testToBoundingBoxSet);
	CPPUNIT_TEST(testFindTilesInBoundingBox);
	CPPUNIT_TEST_SUITE_END();

	private:

		std::shared_ptr<EastNorthTrans> m_trans;


	public:

		void setUp() {
			m_trans = std::shared_ptr<EastNorthTrans>(new SimpleEastNorthTrans(4000));
		}

		void tearDown() {
		}

		void testZoomToTileCount() {
			CPPUNIT_ASSERT_EQUAL(32, zoomToTileCount(5));
			CPPUNIT_ASSERT_EQUAL(1, zoomToTileCount(0));
			CPPUNIT_ASSERT_EQUAL(1024, zoomToTileCount(10));
		}
		
		void testTileCountToZoom() {
			CPPUNIT_ASSERT_EQUAL(5, tileCountToZoom(30));
			CPPUNIT_ASSERT_EQUAL(8, tileCountToZoom(200));
		}

		void testZoomToTileSize() {
			CPPUNIT_ASSERT_EQUAL(.125, zoomToTileSize(3));
			CPPUNIT_ASSERT_EQUAL(1.0, zoomToTileSize(0));
		}
		
		void testTileSizeToZoom() {
			CPPUNIT_ASSERT_EQUAL(3, tileSizeToZoom(.125));
			CPPUNIT_ASSERT_EQUAL(4, tileSizeToZoom(.1));
			CPPUNIT_ASSERT_EQUAL(6, tileSizeToZoom(.02));
		}

		void testFlipTileY() {
			int zoom = 10;
			int y1 = 250;
			int flipY = flipTileY(zoom, y1);
			int y2 = flipTileY(zoom, flipY);
			CPPUNIT_ASSERT_EQUAL(y1, y2);
		}

		void testTileIndex() {
			CPPUNIT_ASSERT_EQUAL(1, tileIndex(.3, 2));
			CPPUNIT_ASSERT_EQUAL(30, tileIndex(.95, 5));
		}
		
		void testToBoundingBox() {
			TileKey tile(3, 4, 1);
			NormMercCoord nw(.5, .75);
			NormMercCoord se(.625, .875);
			
			BoundingBox expected(nw, se, m_trans);
			BoundingBox actual = toBoundingBox(tile, m_trans);
			CPPUNIT_ASSERT_EQUAL(expected, actual);
		}

		void testToBoundingBoxSet() {
			std::set<TileKey> tiles;
			tiles.insert(TileKey(3, 2, 1));
			tiles.insert(TileKey(3, 5, 2));
			NormMercCoord nw(.25, .625);
			NormMercCoord se(.75, .875);
			BoundingBox expected(nw, se, m_trans);
			BoundingBox actual = toBoundingBox(tiles, m_trans);
			CPPUNIT_ASSERT_EQUAL(expected, actual);
		}

		void testFindTilesInBoundingBox() {
			NormMercCoord nw(.4, .7);
			NormMercCoord se(.6, .8);
			BoundingBox bound(nw, se, m_trans);
			std::set<TileKey> expected;
			expected.insert(TileKey(4, 6, 3));
			expected.insert(TileKey(4, 7, 3));
			expected.insert(TileKey(4, 8, 3));
			expected.insert(TileKey(4, 9, 3));
			expected.insert(TileKey(4, 6, 4));
			expected.insert(TileKey(4, 7, 4));
			expected.insert(TileKey(4, 8, 4));
			expected.insert(TileKey(4, 9, 4));
			std::set<TileKey> actual = findTilesInBoundingBox(bound, 4);
			CPPUNIT_ASSERT(expected==actual);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestMBTilesUtils, "TestMBTilesUtils");

}
