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
#include <fstream>
#include <string>
#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>
#include "dataset/dataset_api.h"
#include "util/util_api.h"

namespace MR4C {

class TestLocalDataFileSource : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestLocalDataFileSource);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqual);
	CPPUNIT_TEST(testGetBytes);
	CPPUNIT_TEST(testGetSize);
	CPPUNIT_TEST(testReadAndSkip);
	CPPUNIT_TEST(testIsReleased);
	CPPUNIT_TEST(testGetBytesAfterRelease);
	CPPUNIT_TEST(testGetSizeAfterRelease);
	CPPUNIT_TEST(testIsReleasedAfterRelease);
	CPPUNIT_TEST_SUITE_END();

	private:

		char* m_byte1;
		char* m_byte2;
		std::string m_path1;
		std::string m_path2;
		LocalDataFileSource* m_src1;
		LocalDataFileSource* m_src1a;
		LocalDataFileSource* m_src2;
		

	public:

		void setUp() {
			m_byte1 = new char[6] {6, 66, 33, 22, 75, 88};
			m_byte2 = new char[6] {6, 66, 33, 22, 75, 0};
			m_path1 = "output/src1";
			m_path2 = "output/src2";
			writeFile(m_path1, m_byte1, 6);
			writeFile(m_path2, m_byte2, 6);
			m_src1 = new LocalDataFileSource(m_path1);
			m_src1a = new LocalDataFileSource(m_path1);
			m_src2 = new LocalDataFileSource(m_path2);
		}

		void writeFile(const std::string& path, char* bytes, size_t size) {
			std::ofstream file(path.c_str(), std::ios::out | std::ios::binary | std::ios::trunc );
			file.write(bytes, size);
			file.close();
		}

		void tearDown() {
			delete m_src1;
			delete m_src1a;
			delete m_src2;
			delete m_byte1;
			delete m_byte2;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_src1==*m_src1a);
		}
		
		void testNotEqual() {
			CPPUNIT_ASSERT(*m_src1!=*m_src2);
			CPPUNIT_ASSERT(!(*m_src1==*m_src2));
		}
	
		void testGetBytes() {
			CPPUNIT_ASSERT(compareArray(m_src1->getFileBytes(),m_byte1, 6));
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
			m_src1->getFileBytes();
			m_src1->release();
			CPPUNIT_ASSERT(compareArray(m_src1->getFileBytes(),m_byte1, 6));
		}

		void testGetSizeAfterRelease() {
			m_src1->getFileSize();
			m_src1->release();
			CPPUNIT_ASSERT(m_src1->getFileSize()==6);
		}

		void testIsReleasedAfterRelease() {
			m_src1->release();
			CPPUNIT_ASSERT(m_src1->isReleased());
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestLocalDataFileSource, "TestLocalDataFileSource");

}


