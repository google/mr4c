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
#include "external/external_api.h"
#include "serialize/serialize_api.h"

namespace MR4C {

class TestExternalEntry : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestExternalEntry);
	CPPUNIT_TEST(testCloneDataset); 
	CPPUNIT_TEST(testCloneAlgorithmData); 
	CPPUNIT_TEST_SUITE_END();


	private:

		DatasetTestUtil m_datasetUtil;
		AlgorithmDataTestUtil m_algoDataUtil;
		Dataset* m_dataset;
		ExternalDatasetSerializer* m_datasetSerializer;
		ExternalAlgorithmDataSerializer* m_algoDataSerializer;
		ExternalEntry* m_entry;

	public:

		void setUp() {
			m_dataset = m_datasetUtil.buildDataset1();
			buildSerializers();
			m_entry = new ExternalEntry();
		}

		void tearDown() {
			delete m_dataset;
			delete m_entry;
			delete m_algoDataSerializer;
			delete m_datasetSerializer;
		}

		void testCloneDataset() {
			ExternalDataset* extDataset = new ExternalDataset();
			extDataset->init("test");
			m_datasetSerializer->serializeDataset(extDataset, *m_dataset);
			ExternalDataset* extDataset2 = m_entry->cloneDataset(extDataset);

			Dataset* dataset2 = m_datasetSerializer->deserializeDataset(*extDataset2, false);
			CPPUNIT_ASSERT(*m_dataset==*dataset2);
			delete extDataset;
			delete extDataset2;
			delete dataset2;
		}
	
		void testCloneAlgorithmData() {
			AlgorithmData* srcData = m_algoDataUtil.buildAlgorithmData1();
			ExternalAlgorithmData* extSrcData = new ExternalAlgorithmData();
			m_algoDataSerializer->serializeInputData(*srcData, *extSrcData);
			m_algoDataSerializer->serializeOutputData(*srcData, *extSrcData);
			


			ExternalAlgorithmData* extResultData = m_entry->cloneAlgorithmData(extSrcData);

			AlgorithmData* resultData = new AlgorithmData();
			m_algoDataSerializer->deserializeInputData(*resultData, *extResultData);
			m_algoDataSerializer->deserializeOutputData(*resultData, *extResultData);


			CPPUNIT_ASSERT(*srcData==*resultData);

			delete srcData;
			delete resultData;
			delete extSrcData;
			delete extResultData;
	
	}

	private:

		void buildSerializers() {
			SerializerFactory* factory = SerializerRegistry::instance().getSerializerFactory("application/json");
			m_datasetSerializer = new ExternalDatasetSerializer(*factory);
			m_algoDataSerializer = new ExternalAlgorithmDataSerializer(*factory);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestExternalEntry, "TestExternalEntry");

}


