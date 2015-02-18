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

#include <log4cxx/logger.h>

namespace MR4C {

class TestMR4CLogging : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestMR4CLogging);
	CPPUNIT_TEST(testExtractLogFiles);
	CPPUNIT_TEST_SUITE_END();

	public:

		void setUp() {
		}

		void tearDown() {
		}

		void testExtractLogFiles() {
			// make sure the loggers are being used
			log4cxx::LoggerPtr logger = MR4CLogging::getLogger("whatever");
			LOG4CXX_INFO(logger, "testing 123");
			log4cxx::LoggerPtr algoLogger = MR4CLogging::getAlgorithmLogger("whatever");
			LOG4CXX_INFO(algoLogger, "testing 123");

			std::set<std::string> files = MR4CLogging::extractLogFiles();
			std::set<std::string> expected;
			expected.insert("./logs/mr4c-algo.log");
			expected.insert("./logs/mr4c-native.log");
			CPPUNIT_ASSERT(expected==files);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestMR4CLogging, "TestMR4CLogging");

}


