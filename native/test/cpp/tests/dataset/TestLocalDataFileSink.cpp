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
#include <string>
#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>
#include "dataset/dataset_api.h"
#include "util/util_api.h"

namespace MR4C {

class TestLocalDataFileSink : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestLocalDataFileSink);
	CPPUNIT_TEST(testWrite);
	CPPUNIT_TEST(testClose);
	CPPUNIT_TEST_EXCEPTION(testWriteAfterClose, std::logic_error);
	CPPUNIT_TEST_SUITE_END();

	private:

		char* m_bytes;
		std::string m_path;
		LocalDataFileSink* m_sink;

	public:

		void setUp() {
			char bytes[6] = {6, 66, 33, 99, 43, 12};
			m_bytes = copyArray<char>(bytes, 6);
			m_path = "output/sink";
			m_sink = new LocalDataFileSink(m_path);
		}

		void tearDown() {
			delete m_sink;
			delete m_bytes;
		}

		void testWrite() {
			m_sink->write(m_bytes, 6);
			m_sink->close();
			LocalDataFileSource* src = new LocalDataFileSource(m_path);
			CPPUNIT_ASSERT_EQUAL(6, (int) src->getFileSize());
			CPPUNIT_ASSERT(compareArray(src->getFileBytes(),m_bytes, 6));
			delete src;
		}

		void testClose() {
			m_sink->write(m_bytes, 6);
			m_sink->close();
			CPPUNIT_ASSERT(m_sink->isClosed());
		}

		void testWriteAfterClose() {
			m_sink->write(m_bytes, 6);
			m_sink->close();
			m_sink->write(m_bytes, 6);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestLocalDataFileSink, "TestLocalDataFileSink");

}


