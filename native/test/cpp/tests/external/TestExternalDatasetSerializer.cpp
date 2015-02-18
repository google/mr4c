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
#include "dataset/DatasetTestUtil.h"
#include "dataset/dataset_api.h"
#include "external/external_api.h"
#include "serialize/serialize_api.h"
#include <iostream>

namespace MR4C {

class TestExternalDatasetSerializer : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestExternalDatasetSerializer);
	CPPUNIT_TEST(testDataset);
	CPPUNIT_TEST_SUITE_END();

	private:

		DatasetTestUtil m_util;
		Dataset* m_dataset;
		ExternalDatasetSerializer* m_serializer;

	public:

		void setUp() {
			m_dataset = m_util.buildDataset1();
			SerializerFactory* factory = SerializerRegistry::instance().getSerializerFactory("application/json");
			m_serializer = new ExternalDatasetSerializer(*factory);
		}

		void tearDown() {
			delete m_dataset;
			delete m_serializer;
		}

		void testDataset() {
			ExternalDataset* extDataset = new ExternalDataset();
			extDataset->init("test");
			m_serializer->serializeDataset(extDataset, *m_dataset);
			Dataset* dataset2 = m_serializer->deserializeDataset(*extDataset, false);
			CPPUNIT_ASSERT(*m_dataset==*dataset2);
			delete dataset2;
			delete extDataset;
		}
		
};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestExternalDatasetSerializer, "TestExternalDatasetSerializer");

}


