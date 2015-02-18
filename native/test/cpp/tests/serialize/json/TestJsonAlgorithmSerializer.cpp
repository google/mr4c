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
#include "keys/keys_api.h"
#include "serialize/serialize_api.h"
#include "serialize/json/json_api.h"
#include <iostream>

namespace MR4C {

class TestJsonAlgorithmSerializer : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestJsonAlgorithmSerializer);
	CPPUNIT_TEST(testAlgorithm);
	CPPUNIT_TEST_SUITE_END();

	private:

		Algorithm* m_algorithm;
		AlgorithmSerializer* m_serializer;

		class TestAlgorithm : public Algorithm {
			void executeAlgorithm(AlgorithmData& data, AlgorithmContext& context) {}
		};


	public:

		void setUp() {
			buildAlgorithm();
			m_serializer = new JsonAlgorithmSerializer();
		}

		void tearDown() {
			delete m_algorithm;
			delete m_serializer;
		}

		void testAlgorithm() {
			std::string json = m_serializer->serializeAlgorithm(*m_algorithm);
			Algorithm* algorithm2 = new TestAlgorithm();
			m_serializer->deserializeAlgorithm(json, *algorithm2);
			CPPUNIT_ASSERT(m_algorithm->getInputDatasets()==algorithm2->getInputDatasets());
			CPPUNIT_ASSERT(m_algorithm->getRequiredInputDatasets()==algorithm2->getRequiredInputDatasets());
			CPPUNIT_ASSERT(m_algorithm->getOptionalInputDatasets()==algorithm2->getOptionalInputDatasets());
			CPPUNIT_ASSERT(m_algorithm->getExcludedInputDatasets()==algorithm2->getExcludedInputDatasets());
			CPPUNIT_ASSERT(m_algorithm->getOutputDatasets()==algorithm2->getOutputDatasets());
			CPPUNIT_ASSERT(m_algorithm->getExpectedDimensions()==algorithm2->getExpectedDimensions());
			delete algorithm2;
		}

	private:

		void buildAlgorithm() {
			m_algorithm = new TestAlgorithm();

			m_algorithm->addInputDataset("input1");
			m_algorithm->addInputDataset("input2");
			m_algorithm->addInputDataset("input3", true);
			m_algorithm->addInputDataset("input4", false, true);
			m_algorithm->addInputDataset("input5", true, true);
			m_algorithm->addOutputDataset("output1");
			m_algorithm->addOutputDataset("output2");
			m_algorithm->addExpectedDimension(DataKeyDimension("dim1"));
			m_algorithm->addExpectedDimension(DataKeyDimension("dim2"));
		}
};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestJsonAlgorithmSerializer, "TestJsonAlgorithmSerializer");

}


