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

using std::shared_ptr;

namespace MR4C {

class TestDataFile : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestDataFile);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqual);
	CPPUNIT_TEST(testGetBytesNormal);
	CPPUNIT_TEST(testGetSizeNormal);
	CPPUNIT_TEST(testHasContentNormal);
	CPPUNIT_TEST(testHasSourceNormal);
	CPPUNIT_TEST_EXCEPTION(testGetBytesNoSource, std::logic_error);
	CPPUNIT_TEST_EXCEPTION(testGetSizeNoSource, std::logic_error);
	CPPUNIT_TEST(testHasContentNoSource);
	CPPUNIT_TEST(testHasSourceNoSource);
	CPPUNIT_TEST_EXCEPTION(testGetBytesAfterRelease, std::logic_error);
	CPPUNIT_TEST_EXCEPTION(testGetSizeAfterRelease, std::logic_error);
	CPPUNIT_TEST(testHasContentAfterRelease);
	CPPUNIT_TEST(testHasSourceAfterRelease);
	CPPUNIT_TEST(testPointerSharing);
	CPPUNIT_TEST_SUITE_END();

	private:

		DatasetTestUtil m_util;
		DataFile* m_file1;
		DataFile* m_file1a;
		DataFile* m_file2;
		DataFile* m_noSourceFile;
		DataFile* m_releasedFile;

	public:

		void setUp() {
			m_file1 = m_util.buildDataFile1();
			m_file1a = m_util.buildDataFile1();
			m_file2 = m_util.buildDataFile2();
			m_noSourceFile = new DataFile("image/jpg");
			m_releasedFile = m_util.buildDataFile1();
			m_releasedFile->release();
		}



		void tearDown() {
			delete m_file1;
			delete m_file1a;
			delete m_file2;
			delete m_noSourceFile;
			delete m_releasedFile;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_file1==*m_file1a);
		}
		
		void testNotEqual() {
			CPPUNIT_ASSERT(*m_file1!=*m_file2);
			CPPUNIT_ASSERT(!(*m_file1==*m_file2));
		}

		void testGetBytesNormal() {
			CPPUNIT_ASSERT(m_file1->getBytes()!=NULL);
		}

		void testGetSizeNormal() {
			CPPUNIT_ASSERT(m_file1->getSize()==4);
		}

		void testHasContentNormal() {
			CPPUNIT_ASSERT(m_file1->hasContent());
		}

		void testHasSourceNormal() {
			CPPUNIT_ASSERT(m_file1->hasFileSource());
		}

		void testGetBytesNoSource() {
			m_noSourceFile->getBytes();
		}

		void testGetSizeNoSource() {
			m_noSourceFile->getSize();
		}

		void testHasContentNoSource() {
			CPPUNIT_ASSERT(!m_noSourceFile->hasContent());
		}

		void testHasSourceNoSource() {
			CPPUNIT_ASSERT(!m_noSourceFile->hasFileSource());
		}

		void testGetBytesAfterRelease() {
			m_releasedFile->getBytes();
		}

		void testGetSizeAfterRelease() {
			m_releasedFile->getSize();
		}

		void testHasContentAfterRelease() {
			CPPUNIT_ASSERT(!m_releasedFile->hasContent());
		}

		void testHasSourceAfterRelease() {
			CPPUNIT_ASSERT(m_releasedFile->hasFileSource());
		}

		void testPointerSharing() {
			DataFile* file1 = m_util.buildDataFile1();
			shared_ptr<DataFileSource> src = file1->getFileSource();
			CPPUNIT_ASSERT(src.use_count()==2);
			DataFile* file2 = new DataFile(src, "image/jpg");
			CPPUNIT_ASSERT(src.use_count()==3);
			delete file1;
			CPPUNIT_ASSERT(src.use_count()==2);
			CPPUNIT_ASSERT(file2->hasContent());
			file2->getBytes();
			delete file2;
			CPPUNIT_ASSERT(src.use_count()==1);
		}
			

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestDataFile, "TestDataFile");

}


