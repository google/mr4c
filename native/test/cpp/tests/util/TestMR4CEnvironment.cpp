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

#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>
#include "util/util_api.h"

namespace MR4C {

class TestMR4CEnvironment : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestMR4CEnvironment);
	CPPUNIT_TEST(testGetVariableName);
	CPPUNIT_TEST(testGetEnv);
	CPPUNIT_TEST_SUITE_END();

	public:

		void setUp() {
		}

		void tearDown() {
		}

		void testGetVariableName() {
			CPPUNIT_ASSERT(MR4CEnvironment::getVariableName(MR4CEnvironment::RUNTIME, "prop1")=="mr4c.runtime.prop1");
			CPPUNIT_ASSERT(MR4CEnvironment::getVariableName(MR4CEnvironment::JAVA, "prop2")=="mr4c.java.prop2");
			CPPUNIT_ASSERT(MR4CEnvironment::getVariableName(MR4CEnvironment::CUSTOM, "prop3")=="mr4c.custom.prop3");
		}

		void testGetEnv() {
			Properties props;
			props.setProperty("prop1", "val1");
			props.setProperty("prop2", "val2");
			MR4CEnvironment::instance().addPropertySet(MR4CEnvironment::JAVA, props);
			checkGetEnv("mr4c.java.prop1","val1");
			checkGetEnv("mr4c.java.prop2","val2");

			Properties props2;
			props2.setProperty("prop2", "some_other_val");
			props2.setProperty("prop3", "val3");
			MR4CEnvironment::instance().addPropertySet(MR4CEnvironment::JAVA, props2);
			checkGetEnv("mr4c.java.prop1","val1");
			checkGetEnv("mr4c.java.prop2","some_other_val");
			checkGetEnv("mr4c.java.prop3","val3");
		}

		void checkGetEnv(const std::string& name, const std::string& expectedValue) {
			const char* valPtr = getenv(name.c_str());
			CPPUNIT_ASSERT(valPtr!=NULL);
			std::string value = valPtr;
			CPPUNIT_ASSERT(expectedValue==value);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestMR4CEnvironment, "TestMR4CEnvironment");

}


