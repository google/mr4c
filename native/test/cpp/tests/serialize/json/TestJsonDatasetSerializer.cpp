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
#include "dataset/dataset_api.h"
#include "dataset/DatasetTestUtil.h"
#include "serialize/serialize_api.h"
#include "serialize/json/json_api.h"
#include "util/util_api.h"
#include <iostream>

namespace MR4C {

class TestJsonDatasetSerializer : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestJsonDatasetSerializer);
	CPPUNIT_TEST(testDataset);
	CPPUNIT_TEST(testMetadata);
	CPPUNIT_TEST_SUITE_END();

	private:

		DatasetTestUtil m_util;
		Dataset* m_dataset;
		std::map<DataKey,MetadataMap*> m_meta;
		DatasetSerializer* m_serializer;

	public:

		void setUp() {
			m_dataset = m_util.buildDataset1();
			m_dataset->release();
			m_meta = m_util.buildMetadata();
			m_serializer = new JsonDatasetSerializer();
		}

		void tearDown() {
			delete m_dataset;
			delete m_serializer;
			Dataset::freeMetadata(m_meta);
		}

		void testDataset() {
			std::string json = m_serializer->serializeDataset(*m_dataset);
			Dataset* dataset2 = m_serializer->deserializeDataset(json);
			CPPUNIT_ASSERT(*m_dataset==*dataset2);
			delete dataset2;
		}
		
		void testMetadata() {

			std::string json = m_serializer->serializeMetadata(m_meta);
			std::map<DataKey,MetadataMap*> meta2 = m_serializer->deserializeMetadata(json);

			std::string json2 = m_serializer->serializeMetadata(meta2);
			CPPUNIT_ASSERT(compareMapsOfPointers(m_meta,meta2));
			Dataset::freeMetadata(meta2);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestJsonDatasetSerializer, "TestJsonDatasetSerializer");

}


