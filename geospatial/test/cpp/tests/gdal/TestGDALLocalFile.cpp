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
#include "gdal/gdal_api.h"

namespace MR4C {

class TestGDALLocalFile : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestGDALLocalFile);
	CPPUNIT_TEST(testLocalFile);
	CPPUNIT_TEST_SUITE_END();

	public:

		void testLocalFile() {
			DataFile file1("input/thumb.png", "image/png");
			std::string dir = "output";
			GDALLocalFile gdalFile1(dir, "localtest1", file1);

			GDALLocalFile* gdalFile2 = copyGDALFileToLocal(dir, "localtest2", gdalFile1, "PNG");

			gdalFile1.close();
			gdalFile2->close();

			DataFile* file2 = gdalFile2->toDataFile("image/png");

			CPPUNIT_ASSERT(file1==*file2);

			delete gdalFile2;
			delete file2;
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestGDALLocalFile, "TestGDALLocalFile");

}
