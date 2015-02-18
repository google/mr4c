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
#include "util/util_api.h"
#include "serialize/serialize_api.h"
#include "serialize/json/json_api.h"
#include <iostream>

namespace MR4C {

class TestJsonPropertiesSerializer : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestJsonPropertiesSerializer);
	CPPUNIT_TEST(testProperties);
	CPPUNIT_TEST_SUITE_END();

	private:

		Properties m_props;
		PropertiesSerializer* m_serializer;

	public:

		void setUp() {
			buildProperties();
			m_serializer = new JsonPropertiesSerializer();
		}

		void tearDown() {
			delete m_serializer;
		}

		void testProperties() {
			std::string json = m_serializer->serializeProperties(m_props);
			Properties props2 = m_serializer->deserializeProperties(json);
			CPPUNIT_ASSERT(m_props==props2);
		}

		void buildProperties() {
			m_props.setProperty("prop1", "val1");
			m_props.setProperty("prop2", "val2");
			m_props.setProperty("prop3", "val3");
		}
		
};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestJsonPropertiesSerializer, "TestJsonPropertiesSerializer");

}


