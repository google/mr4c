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

class TestMBTilesDataset : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestMBTilesDataset);
	CPPUNIT_TEST(testGetAllTileKeys);
	CPPUNIT_TEST(testGetZoomLevels);
	CPPUNIT_TEST(testGetXValues);
	CPPUNIT_TEST(testGetYValues);
	CPPUNIT_TEST(testAddMetadata);
	CPPUNIT_TEST_SUITE_END();

	private:

		std::set<TileKey> m_keys;
		std::set<int> m_zooms;
		std::set<int> m_x3;
		std::set<int> m_x4;
		std::set<int> m_y3;
		std::set<int> m_y4;
		Dataset* m_dataset;
		MBTilesDataset* m_mbtiles;

	public:

		void setUp() {
			m_dataset = new Dataset();
			m_zooms.insert(3);
			m_zooms.insert(4);
			addLevel3();
			addLevel4();
			m_mbtiles = new MBTilesDataset(m_dataset);
		}

		void tearDown() {
			delete m_mbtiles;
			delete m_dataset;
		}

		void testGetAllTileKeys() {
			std::set<TileKey> keys = m_mbtiles->getAllTileKeys();
			CPPUNIT_ASSERT(m_keys==keys);
		}

		void testGetZoomLevels() {
			std::set<int> zooms = m_mbtiles->getZoomLevels();
			CPPUNIT_ASSERT(m_zooms==zooms);
		}

		void testGetXValues() {
			std::set<int> x3 = m_mbtiles->getXValues(3);
			CPPUNIT_ASSERT(m_x3==x3);
			std::set<int> x4 = m_mbtiles->getXValues(4);
			CPPUNIT_ASSERT(m_x4==x4);
			std::set<int> x5 = m_mbtiles->getXValues(5);
			CPPUNIT_ASSERT(std::set<int>()==x5);
		}

		void testGetYValues() {
			std::set<int> y3 = m_mbtiles->getYValues(3);
			CPPUNIT_ASSERT(m_y3==y3);
			std::set<int> y4 = m_mbtiles->getYValues(4);
			CPPUNIT_ASSERT(m_y4==y4);
			std::set<int> y5 = m_mbtiles->getYValues(5);
			CPPUNIT_ASSERT(std::set<int>()==y5);
		}

		void testAddMetadata() {
			std::map<std::string,std::string> expected;
			expected["key1"]="val1";
			expected["key2"]="val2";
			expected["key3"]="val3";
			m_mbtiles->addMetadata("key1", "val1");
			m_mbtiles->addMetadata("key2", "val2");
			m_mbtiles->addMetadata("key3", "val3");
			std::map<std::string,std::string> actual = m_mbtiles->getMetadata();
			CPPUNIT_ASSERT(expected==actual);
		}

	private:

		void addLevel3() {
			for ( int x=4; x<=5; x++ ) {
				for ( int y=3; y<=6; y++ ) {
					m_x3.insert(x);
					m_y3.insert(y);
					addTile(3, x, y);
				}
			}
		}

		void addLevel4() {
			for ( int x=7; x<=10; x++ ) {
				for ( int y=6; y<=11; y++ ) {
					m_x4.insert(x);
					m_y4.insert(y);
					addTile(4, x, y);
				}
			}
		}

		void addTile(int zoom, int x, int y) {
			TileKey tileKey(zoom, x, y);
			DataKey dataKey = tileKey.toDataKey();
			DataFile* file = buildDataFile();
			m_keys.insert(tileKey);
			m_dataset->addDataFile(dataKey, file);
		}

		DataFile* buildDataFile() {
			char* data = new char[4] {5, 6, 7, 8};
			return new DataFile(data, 4, "image/png");
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestMBTilesDataset, "TestMBTilesDataset");

}
