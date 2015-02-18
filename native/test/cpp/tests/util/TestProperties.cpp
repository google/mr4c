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
#include "util/util_api.h"

namespace MR4C {

class TestProperties : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestProperties);
	CPPUNIT_TEST(testEqual);
	CPPUNIT_TEST(testNotEqual);
	CPPUNIT_TEST(testAssignment);
	CPPUNIT_TEST(testCopy);
	CPPUNIT_TEST(testNames);
	CPPUNIT_TEST(testGet);
	CPPUNIT_TEST_EXCEPTION(testNotFound, std::invalid_argument);
	CPPUNIT_TEST(testSetAllProperties);
	CPPUNIT_TEST_SUITE_END();

	private:

		Properties* m_props1;
		Properties* m_props1a;
		Properties* m_props2;

	public:

		void setUp() {
			m_props1 = buildProperties1();
			m_props1a = buildProperties1();
			m_props2 = buildProperties2();
		}

		void tearDown() {
			delete m_props1;
			delete m_props1a;
			delete m_props2;
		}

		void testEqual() {
			CPPUNIT_ASSERT(*m_props1==*m_props1a);
		}
		
		void testNotEqual() {
			CPPUNIT_ASSERT(*m_props1!=*m_props2);
			CPPUNIT_ASSERT(!(*m_props1==*m_props2));
		}
		
		void testAssignment() {
			Properties props = *m_props1a;
			CPPUNIT_ASSERT(props==*m_props1a);
		}

		void testCopy() {
			Properties props(*m_props1a);
			CPPUNIT_ASSERT(props==*m_props1a);
		}


		void testGet() {
			checkGet("prop1", "val1");
			checkGet("prop2", "val2");
			checkGet("prop3", "val3");
		}

		void checkGet(const std::string& name, const std::string& val) {
			CPPUNIT_ASSERT(m_props1a->hasProperty(name));
			CPPUNIT_ASSERT(m_props1a->getProperty(name)==val);
		}

		void testNames() {
			std::set<std::string> names;
			names.insert("prop1");
			names.insert("prop2");
			names.insert("prop3");
			CPPUNIT_ASSERT(m_props1a->getAllPropertyNames()==names);
		}

		void testNotFound() {
			std::string val = m_props1a->getProperty("whatever");
		}

		void testSetAllProperties() {
			std::map<std::string,std::string> map;
			map["prop2"] = "other_val";
			map["prop4"] = "val4";
			m_props1a->setAllProperties(map);
			Properties expected;
			expected.setProperty("prop1", "val1");
			expected.setProperty("prop2", "other_val");
			expected.setProperty("prop3", "val3");
			expected.setProperty("prop4", "val4");
			CPPUNIT_ASSERT(expected==*m_props1a);
		}

		Properties* buildProperties1() {
			Properties* props = new Properties();
			props->setProperty("prop1", "val1");
			props->setProperty("prop2", "val2");
			props->setProperty("prop3", "val3");
			return props;
		}

		Properties* buildProperties2() {
			Properties* props = new Properties();
			props->setProperty("prop1", "whatever");
			props->setProperty("prop2", "val2");
			props->setProperty("prop3", "val3");
			return props;
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestProperties, "TestProperties");

}


