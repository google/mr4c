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
#include "multithread/SimultaneousThreadRunner.h"

using std::shared_ptr;

namespace MR4C {

/**
 * Mock File Sink that does nothing but count the number of bytes that were
 * "written" to it, without actually persisting the writes. The implementation
 * of "write(...)" is written in an intentionally non-atomic fashion to expose
 * lack of synchronization or issues with it.
 */
class ByteCountingDataFileSink : public DataFileSink {

    friend class TestDataFileMultithread;

private:
    unsigned long m_bytesWritten;

public:
    ByteCountingDataFileSink() {
        m_bytesWritten = 0;
    }

public: // Interface methods
    void write(char* buf, size_t num) {
        // If this is called from many threads without synchronization,
        // expected =/= actual bytes written.
        unsigned long bw = m_bytesWritten;
        bw += num;
        m_bytesWritten = bw;
    };
    void close() {};
    bool isClosed() const { return false; };
};

class ConflictCheckingDataFileSource : public DataFileSource {

    friend class TestDataFileMultithread;

private:
    mutable bool m_doingSomething;
    mutable bool m_didHaveConflict;

    void noop() const {
        // Do nothing
    }

    inline void check(const std::string& method) const {
        if (m_doingSomething) {
            m_didHaveConflict = true;
            throw std::runtime_error("Thread-safety conflict in DataFile when calling DataFileSource::" + method);
        }
        m_doingSomething = true;
        noop(); // Allow for thread conflict if another method is executed here
        m_doingSomething = false;
    }

public:
    ConflictCheckingDataFileSource() {
        m_doingSomething = false;
        m_didHaveConflict = false;
    }

public: // Interface methods
    char* getFileBytes() const {
        check("getFileBytes");
        return NULL;
    }

    size_t getFileSize() const {
        check("getFileSize()");
        return 0;
    }

    size_t read(char* buf, size_t num) {
        check("read()");
        return 0;
    }

    size_t skip(size_t num) {
        check("skip()");
        return 0;
    }

    void release() {
        check("release()");
    };

    bool isReleased() const { return false; };
};

class DataFileWriteFromThreadsRunner : public SimultaneousThreadRunner {

    friend class TestDataFileMultithread;

private:
    DataFile * m_dataFile;
    ByteCountingDataFileSink * m_dataFileSink;
    unsigned long m_bytesPerThread;

public:
    DataFileWriteFromThreadsRunner( int numThreads, unsigned long bytesPerThread)
    : SimultaneousThreadRunner(numThreads), m_bytesPerThread(bytesPerThread) {
        m_dataFile = new DataFile("img/png");
        m_dataFileSink = new ByteCountingDataFileSink();
        m_dataFile->setFileSink( m_dataFileSink );
    }

    virtual ~DataFileWriteFromThreadsRunner() {
        delete m_dataFile;
    }

protected:
    void run() {
        for ( int i = 0; i < m_bytesPerThread; ++i ) {
            m_dataFile->write(NULL, 1);
        }
    }

};

class DataFileActionsThreadRunner : public SimultaneousThreadRunner {

    friend class TestDataFileMultithread;

private:
    DataFile * m_dataFile;
    std::shared_ptr<ConflictCheckingDataFileSource> m_dataFileSource;
    int m_callsPerThread;

public:
    DataFileActionsThreadRunner(int numThreads, int callsPerThread)
            : SimultaneousThreadRunner(numThreads), m_callsPerThread(callsPerThread) {
        m_dataFile = new DataFile("img/png");
        m_dataFileSource = std::make_shared<ConflictCheckingDataFileSource>();
        std::shared_ptr<DataFileSource> dfs = std::dynamic_pointer_cast<DataFileSource>(m_dataFileSource);
        m_dataFile->setFileSource(dfs);
    }

    virtual~DataFileActionsThreadRunner() {
        delete m_dataFile;
    }

protected:
    void run() {
        char buf[1];
        for ( int i = 0; i < m_callsPerThread; ++i ) {
            m_dataFile->read(buf, 0);
            m_dataFile->skip(0);
            m_dataFile->getBytes();
            m_dataFile->getSize();
            m_dataFile->release();
        }
    }
};

class TestDataFileMultithread : public CPPUNIT_NS::TestFixture {

    CPPUNIT_TEST_SUITE(TestDataFileMultithread);
    CPPUNIT_TEST(testWriteMultithread);
    CPPUNIT_TEST(testActionsMultithread);
    CPPUNIT_TEST_SUITE_END();

    private:

        DatasetTestUtil m_util;
        DataFile* m_file1;
        DataFile* m_file1a;
        DataFile* m_file2;
        DataFile* m_noSourceFile;
        DataFile* m_releasedFile;

    public:

        /**
         * Threading tests are nondeterministic, and thus have the possibility to pass
         * even though the code is not properly synchronized. These take a few seconds
         * to try to make it more likely to catch an error.
         */
        void testWriteMultithread() {
            DataFileWriteFromThreadsRunner runner(10, 1000000);
            runner.start();
            runner.join();

            CPPUNIT_ASSERT(runner.m_dataFileSink->m_bytesWritten == 10 * 1000000);
        }

        /**
         * Threading tests are nondeterministic, and thus have the possibility to pass
         * even though the code is not properly synchronized. These take a few seconds
         * to try to make it more likely to catch an error.
         */
        void testActionsMultithread() {
            DataFileActionsThreadRunner runner(10, 1000000);
            runner.start();
            runner.join();

            CPPUNIT_ASSERT(runner.m_dataFileSource->m_didHaveConflict == false);
        }

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestDataFileMultithread, "TestDataFileMultithread");

}


