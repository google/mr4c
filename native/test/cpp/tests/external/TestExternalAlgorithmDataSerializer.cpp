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
#include "external/external_api.h"
#include "serialize/serialize_api.h"
#include <iostream>

namespace MR4C {

class TestExternalAlgorithmDataSerializer : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestExternalAlgorithmDataSerializer);
	CPPUNIT_TEST(testAlgoData);
	CPPUNIT_TEST_SUITE_END();

	private:

		AlgorithmDataTestUtil m_util;
		AlgorithmData* m_algoData;
		ExternalAlgorithmDataSerializer* m_serializer;

	public:

		void setUp() {
			buildAlgoData();
			buildSerializer();
		}

		void tearDown() {
			delete m_algoData;
			delete m_serializer;
		}

		void testAlgoData() {
			ExternalAlgorithmData* extData = new ExternalAlgorithmData();
			m_serializer->serializeInputData(*m_algoData, *extData);
			m_serializer->serializeOutputData(*m_algoData, *extData);

			AlgorithmData* algoData2 = new AlgorithmData();

			m_serializer->deserializeInputData(*algoData2, *extData);
			m_serializer->deserializeOutputData(*algoData2, *extData);
			
			CPPUNIT_ASSERT(*m_algoData==*algoData2);
			delete algoData2;
			delete extData;
		}
	
	private:

		 void buildAlgoData() {
			m_algoData = m_util.buildAlgorithmData1();
		}

		void buildSerializer() {
			SerializerFactory* factory = SerializerRegistry::instance().getSerializerFactory("application/json");
			m_serializer = new ExternalAlgorithmDataSerializer(*factory);
		}
};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestExternalAlgorithmDataSerializer, "TestExternalAlgorithmDataSerializer");

}


