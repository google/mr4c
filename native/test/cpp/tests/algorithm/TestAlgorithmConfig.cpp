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
#include "algorithm/algorithm_api.h"
#include "algorithm/AlgorithmDataTestUtil.h"
#include "dataset/dataset_api.h"

namespace MR4C {

class TestAlgorithmConfig : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestAlgorithmConfig);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqual);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST(testString);
	CPPUNIT_TEST(testBoolean);
	CPPUNIT_TEST(testInteger);
	CPPUNIT_TEST(testDouble);
	CPPUNIT_TEST(testNames);
	CPPUNIT_TEST_EXCEPTION(testNotFound, std::invalid_argument);
	CPPUNIT_TEST_SUITE_END();

	private:

		AlgorithmDataTestUtil m_util;
		AlgorithmConfig* m_config1;
		AlgorithmConfig* m_config1a;
		AlgorithmConfig* m_config2;

	public:

		void setUp() {
			m_config1 = m_util.buildAlgorithmConfig1();
			m_config1a = m_util.buildAlgorithmConfig1();
			m_config2 = m_util.buildAlgorithmConfig2();
		}

		void tearDown() {
			delete m_config1;
			delete m_config1a;
			delete m_config2;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_config1==*m_config1a);
		}
		
		void testNotEqual() {
			CPPUNIT_ASSERT(*m_config1!=*m_config2);
			CPPUNIT_ASSERT(!(*m_config1==*m_config2));
		}
		
		void testAssignment() {
			AlgorithmConfig config = *m_config1a;
			CPPUNIT_ASSERT(config==*m_config1a);
		}

		void testCopy() {
			AlgorithmConfig config(*m_config1a);
			CPPUNIT_ASSERT(config==*m_config1a);
		}


		void testString() {
			CPPUNIT_ASSERT(m_config1a->hasConfigParam("string"));
			CPPUNIT_ASSERT(m_config1a->getConfigParam("string")=="whatever");
		}

		void testBoolean() {
			CPPUNIT_ASSERT(m_config1a->hasConfigParam("boolean"));
			CPPUNIT_ASSERT(m_config1a->getConfigParamAsBoolean("boolean"));
		}

		void testInteger() {
			CPPUNIT_ASSERT(m_config1a->hasConfigParam("int"));
			CPPUNIT_ASSERT(m_config1a->getConfigParamAsInt("int")==123);
		}

		void testDouble() {
			CPPUNIT_ASSERT(m_config1a->hasConfigParam("double"));
			CPPUNIT_ASSERT(m_config1a->getConfigParamAsDouble("double")==4546.789);
		}

		void testNames() {
			std::set<std::string> names;
			names.insert("string");
			names.insert("boolean");
			names.insert("int");
			names.insert("double");
			CPPUNIT_ASSERT(m_config1a->getAllParamNames()==names);
		}

		void testNotFound() {
			std::string val = m_config1a->getConfigParam("whatever");
		}


};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestAlgorithmConfig, "TestAlgorithmConfig");

}


