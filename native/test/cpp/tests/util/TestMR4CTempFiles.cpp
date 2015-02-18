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

#include <string>
#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>
#include "util/util_api.h"

namespace MR4C {

class TestMR4CTempFiles : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestMR4CTempFiles);
	CPPUNIT_TEST(testCreateTempDirectory);
	CPPUNIT_TEST(testDeleteAllocatedDirectories);
	CPPUNIT_TEST_SUITE_END();

	public:

		void setUp() {
		}

		void tearDown() {
		}

		void testCreateTempDirectory() {
			std::string dir = MR4CTempFiles::instance().createTempDirectory("output");
			CPPUNIT_ASSERT(IOUtil::directoryExists(dir));
		}

		void testDeleteAllocatedDirectories() {
		    std::string dir = MR4CTempFiles::instance().createTempDirectory("output");
		    CPPUNIT_ASSERT(IOUtil::directoryExists(dir));
		    MR4CTempFiles::instance().deleteAllocatedDirectories();
		    CPPUNIT_ASSERT(!IOUtil::directoryExists(dir));
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestMR4CTempFiles, "TestMR4CTempFiles");

}


