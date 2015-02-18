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
#include "metadata/metadata_api.h"

namespace MR4C {

class TestMetadataField : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestMetadataField);
	CPPUNIT_TEST(testBooleanEqual);
	CPPUNIT_TEST(testBooleanNotEqual);
	CPPUNIT_TEST(testBooleanValue);
	CPPUNIT_TEST(testBooleanCopy);
	CPPUNIT_TEST(testIntegerEqual);
	CPPUNIT_TEST(testIntegerNotEqual);
	CPPUNIT_TEST(testIntegerValue);
	CPPUNIT_TEST(testIntegerCopy);
	CPPUNIT_TEST(testByteEqual);
	CPPUNIT_TEST(testByteNotEqual);
	CPPUNIT_TEST(testByteValue);
	CPPUNIT_TEST(testByteCopy);
	CPPUNIT_TEST(testFloatEqual);
	CPPUNIT_TEST(testFloatNotEqual);
	CPPUNIT_TEST(testFloatValue);
	CPPUNIT_TEST(testFloatCopy);
	CPPUNIT_TEST(testDoubleEqual);
	CPPUNIT_TEST(testDoubleNotEqual);
	CPPUNIT_TEST(testDoubleValue);
	CPPUNIT_TEST(testDoubleCopy);
	CPPUNIT_TEST(testStringEqual);
	CPPUNIT_TEST(testStringNotEqual);
	CPPUNIT_TEST(testStringValue);
	CPPUNIT_TEST(testStringCopy);
	CPPUNIT_TEST(testSize_tEqual);
	CPPUNIT_TEST(testSize_tNotEqual);
	CPPUNIT_TEST(testSize_tValue);
	CPPUNIT_TEST(testSize_tCopy);
	CPPUNIT_TEST(testCast);
	CPPUNIT_TEST_SUITE_END();

	private:
		MetadataField m_bool1;
		MetadataField m_bool1a;
		MetadataField m_bool2;
		MetadataField m_byte1;
		MetadataField m_byte1a;
		MetadataField m_byte2;
		MetadataField m_int1;
		MetadataField m_int1a;
		MetadataField m_int2;
		MetadataField m_float1;
		MetadataField m_float1a;
		MetadataField m_float2;
		MetadataField m_double1;
		MetadataField m_double1a;
		MetadataField m_double2;
		MetadataField m_string1;
		MetadataField m_string1a;
		MetadataField m_string2;
		MetadataField m_size_t1;
		MetadataField m_size_t1a;
		MetadataField m_size_t2;

	public:

		void setUp() {

			m_bool1 = MetadataField::createBoolean(true);
			m_bool1a = MetadataField::createBoolean(true);
			m_bool2 = MetadataField::createBoolean(false);

			m_int1 = MetadataField::createInteger(123);
			m_int1a = MetadataField::createInteger(123);
			m_int2 = MetadataField::createInteger(456);

			m_byte1 = MetadataField::createByte(12);
			m_byte1a = MetadataField::createByte(12);
			m_byte2 = MetadataField::createByte(45);

			m_float1 = MetadataField::createFloat(123.456f);
			m_float1a = MetadataField::createFloat(123.456f);
			m_float2 = MetadataField::createFloat(543.21f);

			m_double1 = MetadataField::createDouble(123.456);
			m_double1a = MetadataField::createDouble(123.456);
			m_double2 = MetadataField::createDouble(543.21);

			m_string1 = MetadataField::createString("val1");
			m_string1a = MetadataField::createString("val1");
			m_string2 = MetadataField::createString("val2");

			m_size_t1 = MetadataField::createSize_t(1234567890123);
			m_size_t1a = MetadataField::createSize_t(1234567890123);
			m_size_t2 = MetadataField::createSize_t(9876543210987);

		}

		void tearDown() {
		}

		void testBooleanEqual() {
			CPPUNIT_ASSERT(m_bool1==m_bool1a);
		}
		
		void testBooleanNotEqual() {
			CPPUNIT_ASSERT(m_bool1!=m_bool2);
			CPPUNIT_ASSERT(!(m_bool1==m_bool2));
		}
		
		void testBooleanValue() {
			CPPUNIT_ASSERT(m_bool1.getBooleanValue()==true);
			CPPUNIT_ASSERT(m_bool2.getBooleanValue()==false);
		}

		void testBooleanCopy() {
			MetadataField field(m_bool1);
			CPPUNIT_ASSERT(field==m_bool1);
		}

		void testIntegerEqual() {
			CPPUNIT_ASSERT(m_int1==m_int1a);
		}
		
		void testIntegerNotEqual() {
			CPPUNIT_ASSERT(m_int1!=m_int2);
			CPPUNIT_ASSERT(!(m_int1==m_int2));
		}
		
		void testIntegerValue() {
			CPPUNIT_ASSERT(m_int1.getIntegerValue()==123);
		}

		void testIntegerCopy() {
			MetadataField field(m_int1);
			CPPUNIT_ASSERT(field==m_int1);
		}

		void testByteEqual() {
			CPPUNIT_ASSERT(m_byte1==m_byte1a);
		}
		
		void testByteNotEqual() {
			CPPUNIT_ASSERT(m_byte1!=m_byte2);
			CPPUNIT_ASSERT(!(m_byte1==m_byte2));
		}
		
		void testByteValue() {
			CPPUNIT_ASSERT(m_byte1.getByteValue()==12);
		}

		void testByteCopy() {
			MetadataField field(m_byte1);
			CPPUNIT_ASSERT(field==m_byte1);
		}

		void testFloatEqual() {
			CPPUNIT_ASSERT(m_float1==m_float1a);
		}
		
		void testFloatNotEqual() {
			CPPUNIT_ASSERT(m_float1!=m_float2);
			CPPUNIT_ASSERT(!(m_float1==m_float2));
		}
		
		void testFloatValue() {
			CPPUNIT_ASSERT(m_float1.getFloatValue()==123.456f);
		}

		void testFloatCopy() {
			MetadataField field(m_float1);
			CPPUNIT_ASSERT(field==m_float1);
		}

		void testDoubleEqual() {
			CPPUNIT_ASSERT(m_double1==m_double1a);
		}
		
		void testDoubleNotEqual() {
			CPPUNIT_ASSERT(m_double1!=m_double2);
			CPPUNIT_ASSERT(!(m_double1==m_double2));
		}
		
		void testDoubleValue() {
			CPPUNIT_ASSERT(m_double1.getDoubleValue()==123.456);
		}

		void testDoubleCopy() {
			MetadataField field(m_double1);
			CPPUNIT_ASSERT(field==m_double1);
		}

		void testStringEqual() {
			CPPUNIT_ASSERT(m_string1==m_string1a);
		}
		
		void testStringNotEqual() {
			CPPUNIT_ASSERT(m_string1!=m_string2);
			CPPUNIT_ASSERT(!(m_string1==m_string2));
		}
		
		void testStringValue() {
			CPPUNIT_ASSERT(m_string1.getStringValue()=="val1");
		}

		void testStringCopy() {
			MetadataField field(m_string1);
			CPPUNIT_ASSERT(field==m_string1);
		}

		void testSize_tEqual() {
			CPPUNIT_ASSERT(m_size_t1==m_size_t1a);
		}
		
		void testSize_tNotEqual() {
			CPPUNIT_ASSERT(m_size_t1!=m_size_t2);
			CPPUNIT_ASSERT(!(m_size_t1==m_size_t2));
		}
		
		void testSize_tValue() {
			CPPUNIT_ASSERT(m_size_t1.getSize_tValue()==1234567890123);
		}

		void testSize_tCopy() {
			MetadataField field(m_size_t1);
			CPPUNIT_ASSERT(field==m_size_t1);
		}

		void testCast() {
			const MetadataElement& element = m_string1;
			const MetadataField& field = MetadataField::castToField(element);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestMetadataField, "TestMetadataField");

}

