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
#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>
#include "dataset/DatasetTestUtil.h"
#include "dataset/dataset_api.h"
#include "util/util_api.h"

namespace MR4C {

class TestDatasetContext : public DatasetContext {

	friend class TestDataset;

	private:

		DataKey m_key;
		DataFile* m_file;

		TestDatasetContext(const DataKey& key, DataFile* file) {
			m_key = key;
			m_file = file;
		}
		
	public:

		DataFile* findDataFile(const DataKey& key) const {
			return key==m_key ?
				m_file :
				NULL;
		}

};

class TestDataset : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestDataset);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqual);
	CPPUNIT_TEST_EXCEPTION(testFailDuplicateFileKey, std::invalid_argument);
	CPPUNIT_TEST_EXCEPTION(testFailDuplicateMetadataKey, std::invalid_argument);
	CPPUNIT_TEST(testGetFileNoContext);
	CPPUNIT_TEST(testGetFileWithContext);
	CPPUNIT_TEST(testGenerateKeyspaces);
	CPPUNIT_TEST(testCopySourceToSink);
	CPPUNIT_TEST(testFileAsMetadata);
	CPPUNIT_TEST_SUITE_END();

	private:

		DatasetTestUtil m_util;
		Dataset* m_dataset1;
		Dataset* m_dataset1a;
		Dataset* m_dataset2;
		Keyspace m_keyspace;

	public:

		void setUp() {
			m_dataset1 = m_util.buildDataset1();
			m_dataset1a = m_util.buildDataset1();
			m_dataset2 = m_util.buildDataset2();
			buildKeyspace();
		}

		void tearDown() {
			delete m_dataset1;
			delete m_dataset1a;
			delete m_dataset2;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_dataset1==*m_dataset1a);
		}
		
		void testNotEqual() {
			CPPUNIT_ASSERT(*m_dataset1!=*m_dataset2);
			CPPUNIT_ASSERT(!(*m_dataset1==*m_dataset2));
		}

		void testFailDuplicateFileKey() {
			Dataset dataset;
			DataFile* file1 = m_util.buildDataFile1();
			DataFile* file2 = m_util.buildDataFile2();
			DataKey key = quickKey("val", "dim");
			dataset.addDataFile(key,file1);
			dataset.addDataFile(key,file2);
		}
		
		void testFailDuplicateMetadataKey() {
			Dataset dataset;
			MetadataMap* map1 = new MetadataMap();
			MetadataMap* map2 = new MetadataMap();
			DataKey key = quickKey("val", "dim");
			dataset.addMetadata(key,map1);
			dataset.addMetadata(key,map2);
		}

		void testGetFileNoContext() {
			Dataset dataset;
			DataFile* file1 = m_util.buildDataFile1();
			DataKey key1 = quickKey("val1", "dim");
			DataKey key2 = quickKey("val2", "dim");
			dataset.addDataFile(key1,file1);
			CPPUNIT_ASSERT(dataset.getDataFile(key1)==file1);
			CPPUNIT_ASSERT(!dataset.hasDataFile(key2));
		}
	
		void testGetFileWithContext() {
			Dataset dataset;
			DataFile* file1 = m_util.buildDataFile1();
			DataFile* file2 = m_util.buildDataFile2();
			DataKey key1 = quickKey("val1", "dim");
			DataKey key2 = quickKey("val2", "dim");
			DataKey key3 = quickKey("val3", "dim");
			dataset.addDataFile(key1,file1);
			DatasetContext* context = new TestDatasetContext(key2, file2);
			dataset.setContext(context);
			CPPUNIT_ASSERT(dataset.getDataFile(key1)==file1);
			CPPUNIT_ASSERT(dataset.getDataFile(key2)==file2);
			CPPUNIT_ASSERT(!dataset.hasDataFile(key3));
		}
	
		void testGenerateKeyspaces() {
			m_dataset1->generateKeyspaces();
			CPPUNIT_ASSERT(m_dataset1->getKeyspace()==m_keyspace);
		}

		void testCopySourceToSink() {
			size_t size = 123456;
			char* data = new char[size];
			for ( size_t i=0; i<size; i++ ) {
				data[i] = (char)i;
			}
			std::string path = "output/testsrcsinkcopy";

			DataFileSource* src = new SimpleDataFileSource(data, size);
			std::shared_ptr<DataFileSource> srcPtr(src);
			LocalDataFileSink sink(path);
			Dataset::copySourceToSink(srcPtr, &sink);
			LocalDataFileSource src2(path);
			
			CPPUNIT_ASSERT(size==src2.getFileSize());
			CPPUNIT_ASSERT(compareArray(data, src2.getFileBytes(), size));

		}

		void testFileAsMetadata() {
			DataKey key = quickKey("val666", "dim");
			std::map<DataKey,MetadataMap*> meta = m_util.buildMetadata();
			// need copy because dataset will free original
			std::map<DataKey,MetadataMap*> metaCopy = m_util.buildMetadata();
			m_dataset1->addDataFileAsMetadata(key, meta);
			std::map<DataKey,MetadataMap*> meta2 = m_dataset1->getDataFileAsMetadata(key);
			CPPUNIT_ASSERT(compareMapsOfPointers(metaCopy,meta2));
			Dataset::freeMetadata(metaCopy);
			Dataset::freeMetadata(meta2);
		}

	private:

		void buildKeyspace() {
			// replicate the keyspace expected in dataset #1
			KeyspaceBuilder builder;
			DataKeyDimension dim1("dim1");
			DataKeyDimension dim2("dim2");
			builder.addKey(DataKeyElement("val1", dim1));
			builder.addKey(DataKeyElement("val11", dim1));
			builder.addKey(DataKeyElement("val2", dim2));
			builder.addKey(DataKeyElement("val22", dim2));
			m_keyspace = builder.toKeyspace();
		}

		DataKey quickKey(const std::string& val, const std::string& dim) {
			return DataKey(
				DataKeyElement(val,
					 DataKeyDimension(dim)
				)
			);
		}
	
};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestDataset, "TestDataset");

}


