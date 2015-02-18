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

#include <thread>
#include <stdexcept>
#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>
#include "dataset/DatasetTestUtil.h"
#include "dataset/dataset_api.h"
#include "util/util_api.h"
#include "multithread/SimultaneousThreadRunner.h"

namespace MR4C {

class AddManyFilesRunner : public SimultaneousThreadRunner {

    friend class TestDatasetMultithread;

private:
    Dataset* m_dataset;
    DatasetTestUtil m_util;
    int m_filesPerThread;

public:
    AddManyFilesRunner(int numThreads, int filesPerThread)
        : SimultaneousThreadRunner(numThreads), m_filesPerThread(filesPerThread)
    { m_dataset = new Dataset(); }

    virtual ~AddManyFilesRunner() { delete m_dataset; }

protected:
    void run() {
        std::stringstream ss;
        for ( int i = 0; i < m_filesPerThread; ++i ) {
            ss.clear();
            ss << std::this_thread::get_id() << i;
            DataKey key = DataKey(DataKeyElement(ss.str(), DataKeyDimension("d")));
            MetadataMap* map = new MetadataMap();
            m_dataset->addDataFile(key, m_util.buildDataFile1());
            m_dataset->addMetadata(key, map);
        }
    }
};

class TestDatasetMultithread : public CPPUNIT_NS::TestFixture {

    CPPUNIT_TEST_SUITE(TestDatasetMultithread);
    CPPUNIT_TEST(testAddFilesAndMetadataMultithread);
    CPPUNIT_TEST_SUITE_END();

    public:

        void testAddFilesAndMetadataMultithread() {

            AddManyFilesRunner* runner = new AddManyFilesRunner(10, 1000);

            runner->start();
            runner->join();

            CPPUNIT_ASSERT(runner->m_dataset->getAllFileKeys().size() == 10 * 1000);
            CPPUNIT_ASSERT(runner->m_dataset->getAllMetadataKeys().size() == 10 * 1000);

            delete runner;

        }

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestDatasetMultithread, "TestDatasetMultithread");

}


