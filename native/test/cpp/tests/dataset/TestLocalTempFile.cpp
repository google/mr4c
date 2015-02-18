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
#include <string>
#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>
#include "dataset/dataset_api.h"
#include "util/util_api.h"

namespace MR4C {

class TestLocalTempFile : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestLocalTempFile);
	CPPUNIT_TEST(testRoundTrip);
	CPPUNIT_TEST_SUITE_END();

	public:


		void testRoundTrip() {
			char* bytes = new char[6] {6, 66, 33, 99, 43, 12};

			std::string type = "image/jpg";
			std::string dir = "output";
			std::string name = "testlocal";

			DataFile file1(bytes, 6, type, DataFile::Allocation::NEW);
			LocalTempFile temp1(dir, name);
			temp1.copyFrom(file1);

			LocalTempFile temp2(dir, name);
			DataFile* file2 = temp2.toDataFile(type);

			CPPUNIT_ASSERT(file1==*file2);

			delete file2;
		}
			
};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestLocalTempFile, "TestLocalTempFile");

}


