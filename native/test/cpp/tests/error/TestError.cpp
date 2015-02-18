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
#include "error/error_api.h"

namespace MR4C {

class TestError : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestError);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqualSummary);
	CPPUNIT_TEST(testNotEqualDetail);
	CPPUNIT_TEST(testNotEqualSource);
	CPPUNIT_TEST(testNotEqualSeverity);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST_SUITE_END();

	private:
		Error* m_error1;
		Error* m_error1a;
		Error* m_error2;
		Error* m_error3;
		Error* m_error4;
		Error* m_error5;

	public:

		void setUp() {
			m_error1 = buildError1();
			m_error1a = buildError1();
			m_error2 = buildError2();
			m_error3 = buildError3();
			m_error4 = buildError4();
			m_error5 = buildError5();
		}



		void tearDown() {
			delete m_error1;
			delete m_error1a;
			delete m_error2;
			delete m_error3;
			delete m_error4;
			delete m_error5;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_error1==*m_error1a);
		}
		
		void testNotEqualSummary() {
			CPPUNIT_ASSERT(*m_error1!=*m_error2);
			CPPUNIT_ASSERT(!(*m_error1==*m_error2));
		}
		
		void testNotEqualDetail() {
			CPPUNIT_ASSERT(*m_error1!=*m_error3);
			CPPUNIT_ASSERT(!(*m_error1==*m_error3));
		}
		
		void testNotEqualSource() {
			CPPUNIT_ASSERT(*m_error1!=*m_error4);
			CPPUNIT_ASSERT(!(*m_error1==*m_error4));
		}
		
		void testNotEqualSeverity() {
			CPPUNIT_ASSERT(*m_error1!=*m_error5);
			CPPUNIT_ASSERT(!(*m_error1==*m_error5));
		}
		
		void testAssignment() {
			Error error = *m_error1;
			CPPUNIT_ASSERT(error==*m_error1);
		}

		void testCopy() {
			Error error(*m_error1);
			CPPUNIT_ASSERT(error==*m_error1);
		}


	private: 

		Error* buildError1() {
			return new Error("summary1", "detail1", "source1", Error::Severity::ERROR);
		}

		Error* buildError2() {
			return new Error("summary2", "detail1", "source1", Error::Severity::ERROR);
		}

		Error* buildError3() {
			return new Error("summary1", "detail2", "source1", Error::Severity::ERROR);
		}

		Error* buildError4() {
			return new Error("summary1", "detail1", "source2", Error::Severity::ERROR);
		}

		Error* buildError5() {
			return new Error("summary1", "detail1", "source1", Error::Severity::WARN);
		}


};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestError, "TestError");

}

