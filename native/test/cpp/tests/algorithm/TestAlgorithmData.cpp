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
#include "dataset/dataset_api.h"
#include "dataset/DatasetTestUtil.h"

namespace MR4C {

class TestAlgorithmData : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestAlgorithmData);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqual);
	CPPUNIT_TEST(testInput);
	CPPUNIT_TEST(testOutput);
	CPPUNIT_TEST(testGenerateKeyspace);
	CPPUNIT_TEST_SUITE_END();

	private:

		AlgorithmDataTestUtil m_util;
		DatasetTestUtil m_datasetUtil;
		AlgorithmData* m_algoData1;
		AlgorithmData* m_algoData1a;
		AlgorithmData* m_algoData2;
		Keyspace m_keyspace;

	public:

		void setUp() {
			m_algoData1 = m_util.buildAlgorithmData1();
			m_algoData1a = m_util.buildAlgorithmData1();
			m_algoData2 = m_util.buildAlgorithmData2();
			buildKeyspace();
		}

		void tearDown() {
			delete m_algoData1;
			delete m_algoData1a;
			delete m_algoData2;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_algoData1==*m_algoData1a);
		}
		
		void testNotEqual() {
			CPPUNIT_ASSERT(*m_algoData1!=*m_algoData2);
			CPPUNIT_ASSERT(!(*m_algoData1==*m_algoData2));
		}

		void testInput() {
			Dataset* input1 = m_datasetUtil.buildDataset1();
			Dataset* input2 = m_datasetUtil.buildDataset2();
			Dataset* input1a = m_algoData1->getInputDataset("input1");
			Dataset* input2a = m_algoData1->getInputDataset("input2");
			CPPUNIT_ASSERT(*input1==*input1a);
			CPPUNIT_ASSERT(*input2==*input2a);
		}
	
		void testOutput() {
			Dataset* output = m_datasetUtil.buildDataset2();
			Dataset* outputa = m_algoData1->getOutputDataset("output");
			CPPUNIT_ASSERT(*output==*outputa);
		}

		void testGenerateKeyspace() {
			m_algoData1->generateKeyspaceFromInputDatasets();
			CPPUNIT_ASSERT(m_algoData1->getKeyspace()==m_keyspace);
		}

	private:

		void buildKeyspace() {
			// replicate the keyspace expected in algo data #1
			KeyspaceBuilder builder;
			DataKeyDimension dim1("dim1");
			DataKeyDimension dim2("dim2");
			builder.addKey(DataKeyElement("val1", dim1));
			builder.addKey(DataKeyElement("val11", dim1));
			builder.addKey(DataKeyElement("val2", dim2));
			builder.addKey(DataKeyElement("val22", dim2));
			m_keyspace = builder.toKeyspace();
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestAlgorithmData, "TestAlgorithmData");

}


