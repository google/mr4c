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
#include "dataset/dataset_api.h"
#include "gdal/gdal_api.h"
#include "util/util_api.h"

namespace MR4C {

class TestGDALUtils : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestGDALUtils);
	CPPUNIT_TEST(testCopyGDALDataset);
	CPPUNIT_TEST(testCopyGDALImage);
	CPPUNIT_TEST(testGenerateCoordinateTransform);
	CPPUNIT_TEST_SUITE_END();

	private:


	public:

		// test to confirm we can do create by copy of formats that don't support plain create
		void testCopyGDALDataset() {
			DataFile file1("input/thumb.png", "image/png");
			GDALMemoryFile gdalFile1("copytest1", file1);
			GDALDataset* gdal1 = gdalFile1.getGDALDataset();

			GDALMemoryFile gdalFile2("copytest2");
			GDALDataset* gdal2 = copyGDALDataset(gdalFile2.getPath(), gdal1, "JPEG"); 
			gdalFile2.setGDALDataset(gdal2);

			// confirm its the same size

			CPPUNIT_ASSERT_EQUAL(gdal1->GetRasterXSize(), gdal2->GetRasterXSize());
			CPPUNIT_ASSERT_EQUAL(gdal1->GetRasterYSize(), gdal2->GetRasterYSize());
			gdalFile1.close();
			gdalFile2.close();

		}

		void testCopyGDALImage() {
			DataFile file1("input/thumb.png", "image/png");
			GDALMemoryFile gdalFile1("copytest1", file1);
			GDALDataset* gdal1 = gdalFile1.getGDALDataset();

			int width = gdal1->GetRasterXSize();
			int height = gdal1->GetRasterYSize();

			GDALMemoryFile gdalFile2("copytest2");
			GDALDataset* gdal2 = newGDALDataset(gdalFile2.getPath(), "GTiff", width, height, 3, GDT_Byte); // need a format that supports create
			gdalFile2.setGDALDataset(gdal2);

			// bound doesn't really matter for this
			std::shared_ptr<EastNorthTrans> trans(new SimpleEastNorthTrans(4000));
			BoundingBox bound(
				NormMercCoord(0,0),
				NormMercCoord(1,1),
				trans
			);

			// can use the same ImageBox for both
			ImageBox imgBox(width, height, bound);

			copyGDALImage(gdal1, imgBox, gdal2, imgBox);

			char* bytes1 = extractRasterData(gdal1);
			char* bytes2 = extractRasterData(gdal2);

			CPPUNIT_ASSERT(compareArray(bytes1, bytes2, height * width * 3));
			delete bytes1;
			delete bytes2;
			gdalFile1.close();
			gdalFile2.close();

		}

		void testGenerateCoordinateTransform() {
			OGRSpatialReference sref;
			sref.SetWellKnownGeogCS("WGS84");
			sref.SetMercator(0.0, 0.0, 1.0, 0.0, 0.0);
			OGRCoordinateTransformation* trans = generateCoordinateTransform(&sref);
			double x = 3.0e6; // 27 deg
			double y = 2.0e6; // 18 deg
			trans->Transform(1, &x, &y);
			double eps = .1; // just want to know its in the neighborhood
			CPPUNIT_ASSERT_DOUBLES_EQUAL(27.0, x, eps);
			CPPUNIT_ASSERT_DOUBLES_EQUAL(17.8, y, eps);
			delete trans;
		}

	private:

		char* extractRasterData(GDALDataset* gdal) {
			int width = gdal->GetRasterXSize();
			int height = gdal->GetRasterYSize();
			char* bytes = new char[width*height*3];
			gdal->RasterIO(
				GF_Read,
				0,
				0,
				width,
				height,
				bytes,
				width,
				height,
				GDT_Byte,
				3,
				NULL,
				0,
				0,
				0
			);
			return bytes;
		}
		
};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestGDALUtils, "TestGDALUtils");

}
