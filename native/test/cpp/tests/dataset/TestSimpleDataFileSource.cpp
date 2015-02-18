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

#include <cstdlib>
#include <stdexcept>
#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>
#include "dataset/dataset_api.h"
#include "util/util_api.h"

namespace MR4C {

class TestSimpleDataFileSource : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestSimpleDataFileSource);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqual);
	CPPUNIT_TEST(testGetBytes);
	CPPUNIT_TEST(testGetSize);
	CPPUNIT_TEST(testReadAndSkip);
	CPPUNIT_TEST(testIsReleased);
	CPPUNIT_TEST_EXCEPTION(testGetBytesAfterRelease, std::logic_error);
	CPPUNIT_TEST_EXCEPTION(testGetSizeAfterRelease, std::logic_error);
	CPPUNIT_TEST_EXCEPTION(testReadAfterRelease, std::logic_error);
	CPPUNIT_TEST_EXCEPTION(testSkipAfterRelease, std::logic_error);
	CPPUNIT_TEST(testIsReleasedAfterRelease);
	CPPUNIT_TEST(testMalloc);
	CPPUNIT_TEST_SUITE_END();

	private:

		char* m_byte1;
		char* m_byte1a;
		char* m_byte2;
		SimpleDataFileSource* m_src1;
		SimpleDataFileSource* m_src1a;
		SimpleDataFileSource* m_src2;

	public:

		void setUp() {
			char byte1[6] = {6, 66, 33, 43, 89, 56};
			char byte2[6] = {6, 66, 33, 43, 89, 0};
			m_byte1 = copyArray<char>(byte1, 6);
			m_byte1a = copyArray<char>(byte1, 6);
			m_byte2 = copyArray<char>(byte2, 6);
			m_src1 = new SimpleDataFileSource(m_byte1, 6);
			m_src1a = new SimpleDataFileSource(m_byte1a, 6);
			m_src2 = new SimpleDataFileSource(m_byte2, 6);
		}

		void tearDown() {
			delete m_src1;
			delete m_src1a;
			delete m_src2;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_src1==*m_src1a);
		}
		
		void testNotEqual() {
			CPPUNIT_ASSERT(*m_src1!=*m_src2);
			CPPUNIT_ASSERT(!(*m_src1==*m_src2));
		}
	
		void testGetBytes() {
			CPPUNIT_ASSERT(m_src1->getFileBytes()==m_byte1);
		}

		void testGetSize() {
			CPPUNIT_ASSERT(m_src1->getFileSize()==6);
		}

		void testReadAndSkip() {
			char* buf = new char[100];
			char* expected = new char[4] {
				m_byte1[0],
				m_byte1[1],
				m_byte1[4],
				m_byte1[5]
			};
			CPPUNIT_ASSERT(m_src1->read(buf,2)==2);
			CPPUNIT_ASSERT(m_src1->skip(2)==2);
			CPPUNIT_ASSERT(m_src1->read(buf+2,2)==2);
			CPPUNIT_ASSERT(m_src1->skip(2)==0);
			CPPUNIT_ASSERT(m_src1->read(buf+4,2)==0);
			CPPUNIT_ASSERT(compareArray(expected, buf, 4));
			delete[] buf;
			delete[] expected;
		}

		void testIsReleased() {
			CPPUNIT_ASSERT(!m_src1->isReleased());
		}

		void testGetBytesAfterRelease() {
			m_src1->release();
			m_src1->getFileBytes();
		}

		void testGetSizeAfterRelease() {
			m_src1->release();
			m_src1->getFileSize();
		}

		void testReadAfterRelease() {
			char* buf = new char[2];
			m_src1->release();
			m_src1->read(buf, 2);
		}

		void testSkipAfterRelease() {
			m_src1->release();
			m_src1->skip(2);
		}

		void testIsReleasedAfterRelease() {
			m_src1->release();
			CPPUNIT_ASSERT(m_src1->isReleased());
		}

		void testMalloc() {
			// just checking malloc releases cleanly
			char* bytes = (char*) malloc(3);
			bytes[0] = 45;
			bytes[1] = 67;
			bytes[2] = 89;
			DataFileSource* src = new SimpleDataFileSource(bytes, 3);
			delete src;
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestSimpleDataFileSource, "TestSimpleDataFileSource");

}


