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
#include "error/error_api.h"

namespace MR4C {

class TestJsonErrorSerializer : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestJsonErrorSerializer);
	CPPUNIT_TEST(testRoundTrip);
	CPPUNIT_TEST_SUITE_END();

	private:

	public:

		void testRoundTrip() {
			JsonErrorSerializer ser;
			Error error(
				"This is a summary",
				"This is some detail\nWith multiple lines",
				"PIXEL_LOOM",
				Error::Severity::WARN
			);
			std::string json = ser.serializeError(error);
			Error error2 = ser.deserializeError(json);
			CPPUNIT_ASSERT(error==error2);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestJsonErrorSerializer, "TestJsonErrorSerializer");

}


