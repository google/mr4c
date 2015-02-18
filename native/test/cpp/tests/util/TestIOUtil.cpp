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
#include <cstdlib>
#include <sys/stat.h>

#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>
#include "util/util_api.h"

namespace MR4C {

class TestIOUtil : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestIOUtil);
	CPPUNIT_TEST(testDirectoryExists);
	CPPUNIT_TEST(testPrintfToString);
	CPPUNIT_TEST_SUITE_END();

	public:

		void setUp() {
		}

		void tearDown() {
		}

		void testDirectoryExists() {
			CPPUNIT_ASSERT(IOUtil::directoryExists("./output"));
			CPPUNIT_ASSERT(!IOUtil::directoryExists("./blahblahblah"));
		}

		void testPrintfToString() {
			std::string expected("this test is going to happen 5 times");
			std::string result = IOUtil::printfToString("%s is going to happen %d times", "this test", 5);
			CPPUNIT_ASSERT_EQUAL(expected, result);
		}
		// NOTE: would test failure, but can't break it :-(

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestIOUtil, "TestIOUtil");

}



