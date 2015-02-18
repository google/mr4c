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
#include "dataset/dataset_api.h"
#include "mbtiles/mbtiles_api.h"

namespace MR4C {

class TestTileExtractor : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestTileExtractor);
	CPPUNIT_TEST(testExtractJPEGTile);
	CPPUNIT_TEST(testExtractPNGTile);
	CPPUNIT_TEST_SUITE_END();


	public:

		void testExtractJPEGTile() {
			testExtractTile("JPEG", "image/jpg", "tile.jpg");
		}

		void testExtractPNGTile() {
			testExtractTile("PNG", "image/png", "tile.png");
		}

		void testExtractTile(
			const std::string& format,
			const std::string& type,
			const std::string& outputName
		) {
			DataFile mosaicFile("input/thumb.png", "image/png");
			GDALMemoryFile mosaicGDALFile("mosaic", mosaicFile);
			GDALDataset* mosaicGDAL = mosaicGDALFile.getGDALDataset();
			NormMercCoord nw(0.4, 0.3);
			NormMercCoord se(0.41, 0.32);
			std::shared_ptr<EastNorthTrans> trans(new SimpleEastNorthTrans(4000));
			BoundingBox bound(nw, se, trans);
			ImageBox imgBox(
				mosaicGDAL->GetRasterXSize(),
				mosaicGDAL->GetRasterYSize(),
				bound
			);
			TileExtractor extractor(mosaicGDALFile, imgBox);
			TileKey tile(7, 51, 88);
			GDALFile* tileGDALFile = extractor.extractTile(tile, "tile", format);

			tileGDALFile->close();
			mosaicGDALFile.close();
			DataFile* tileFile = tileGDALFile->toDataFile(type);
			LocalDataFileSink tileSink("output/" + outputName);
			Dataset::copySourceToSink(tileFile->getFileSource(), &tileSink);
			delete tileGDALFile;
			delete tileFile;
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestTileExtractor, "TestTileExtractor");

}
