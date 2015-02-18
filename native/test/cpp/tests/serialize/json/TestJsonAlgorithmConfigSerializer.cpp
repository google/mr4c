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
#include "algorithm/algorithm_api.h"
#include "algorithm/AlgorithmDataTestUtil.h"
#include "serialize/serialize_api.h"
#include "serialize/json/json_api.h"
#include <iostream>

namespace MR4C {

class TestJsonAlgorithmConfigSerializer : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestJsonAlgorithmConfigSerializer);
	CPPUNIT_TEST(testAlgorithmConfig);
	CPPUNIT_TEST_SUITE_END();

	private:

		AlgorithmDataTestUtil m_util;
		AlgorithmConfig* m_config;
		AlgorithmConfigSerializer* m_serializer;

	public:

		void setUp() {
			m_config = m_util.buildAlgorithmConfig1();
			m_serializer = new JsonAlgorithmConfigSerializer();
		}

		void tearDown() {
			delete m_config;
			delete m_serializer;
		}

		void testAlgorithmConfig() {

			std::string json = m_serializer->serializeAlgorithmConfig(*m_config);

			AlgorithmConfig config2 = m_serializer->deserializeAlgorithmConfig(json);

			std::string json2 = m_serializer->serializeAlgorithmConfig(config2
);

			CPPUNIT_ASSERT(*m_config==config2);
		}
		
};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestJsonAlgorithmConfigSerializer, "TestJsonAlgorithmConfigSerializer");

}


