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
#include "util/util_api.h"

namespace MR4C {

class TestMetadataArray : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestMetadataArray);
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

		bool* m_bool1;
		bool* m_bool2;
		MetadataArray m_boolArray1;
		MetadataArray m_boolArray1a;
		MetadataArray m_boolArray2;

		int *m_int1;
		int *m_int2;
		MetadataArray m_intArray1;
		MetadataArray m_intArray1a;
		MetadataArray m_intArray2;

		char *m_byte1;
		char *m_byte2;
		MetadataArray m_byteArray1;
		MetadataArray m_byteArray1a;
		MetadataArray m_byteArray2;

		float *m_float1;
		float *m_float2;
		MetadataArray m_floatArray1;
		MetadataArray m_floatArray1a;
		MetadataArray m_floatArray2;

		double *m_double1;
		double *m_double2;
		MetadataArray m_doubleArray1;
		MetadataArray m_doubleArray1a;
		MetadataArray m_doubleArray2;

		std::string *m_string1;
		std::string *m_string2;
		MetadataArray m_stringArray1;
		MetadataArray m_stringArray1a;
		MetadataArray m_stringArray2;

		size_t *m_size_t1;
		size_t *m_size_t2;
		MetadataArray m_size_tArray1;
		MetadataArray m_size_tArray1a;
		MetadataArray m_size_tArray2;


	public:

		void setUp() {
			setUpBoolean();
			setUpInteger();
			setUpByte();
			setUpFloat();
			setUpDouble();
			setUpString();
			setUpSize_t();
		}



		void tearDown() {
			tearDownBoolean();
			tearDownInteger();
			tearDownByte();
			tearDownFloat();
			tearDownDouble();
			tearDownString();
			tearDownSize_t();
		}

		void testBooleanEqual() {
			CPPUNIT_ASSERT(m_boolArray1==m_boolArray1a);
		}
		
		void testBooleanNotEqual() {
			CPPUNIT_ASSERT(m_boolArray1!=m_boolArray2);
			CPPUNIT_ASSERT(!(m_boolArray1==m_boolArray2));
		}
		
		void testBooleanValue() {
			bool* vals = m_boolArray1.getBooleanValue();
			CPPUNIT_ASSERT(compareArray(vals, m_bool1, m_boolArray1.getSize()));
		}

		void testBooleanCopy() {
			MetadataArray array(m_boolArray1);
			CPPUNIT_ASSERT(array==m_boolArray1);
		}

		void testIntegerEqual() {
			CPPUNIT_ASSERT(m_intArray1==m_intArray1a);
		}
		
		void testIntegerNotEqual() {
			CPPUNIT_ASSERT(m_intArray1!=m_intArray2);
			CPPUNIT_ASSERT(!(m_intArray1==m_intArray2));
		}
		
		void testIntegerValue() {
			int* vals = m_intArray1.getIntegerValue();
			CPPUNIT_ASSERT(compareArray(vals, m_int1, m_intArray1.getSize()));
		}

		void testIntegerCopy() {
			MetadataArray array(m_intArray1);
			CPPUNIT_ASSERT(array==m_intArray1);
		}



		void testByteEqual() {
			CPPUNIT_ASSERT(m_byteArray1==m_byteArray1a);
		}
		
		void testByteNotEqual() {
			CPPUNIT_ASSERT(m_byteArray1!=m_byteArray2);
			CPPUNIT_ASSERT(!(m_byteArray1==m_byteArray2));
		}
		
		void testByteValue() {
			char* vals = m_byteArray1.getByteValue();
			CPPUNIT_ASSERT(compareArray(vals, m_byte1, m_byteArray1.getSize()));
		}

		void testByteCopy() {
			MetadataArray array(m_byteArray1);
			CPPUNIT_ASSERT(array==m_byteArray1);
		}


		void testFloatEqual() {
			CPPUNIT_ASSERT(m_floatArray1==m_floatArray1a);
		}
		
		void testFloatNotEqual() {
			CPPUNIT_ASSERT(m_floatArray1!=m_floatArray2);
			CPPUNIT_ASSERT(!(m_floatArray1==m_floatArray2));
		}
		
		void testFloatValue() {
			float* vals = m_floatArray1.getFloatValue();
			CPPUNIT_ASSERT(compareArray(vals, m_float1, m_floatArray1.getSize()));
		}

		void testFloatCopy() {
			MetadataArray array(m_floatArray1);
			CPPUNIT_ASSERT(array==m_floatArray1);
		}

		void testDoubleEqual() {
			CPPUNIT_ASSERT(m_doubleArray1==m_doubleArray1a);
		}
		
		void testDoubleNotEqual() {
			CPPUNIT_ASSERT(m_doubleArray1!=m_doubleArray2);
			CPPUNIT_ASSERT(!(m_doubleArray1==m_doubleArray2));
		}
		
		void testDoubleValue() {
			double* vals = m_doubleArray1.getDoubleValue();
			CPPUNIT_ASSERT(compareArray(vals, m_double1, m_doubleArray1.getSize()));
		}

		void testDoubleCopy() {
			MetadataArray array(m_doubleArray1);
			CPPUNIT_ASSERT(array==m_doubleArray1);
		}


		void testStringEqual() {
			CPPUNIT_ASSERT(m_stringArray1==m_stringArray1a);
		}
		
		void testStringNotEqual() {
			CPPUNIT_ASSERT(m_stringArray1!=m_stringArray2);
			CPPUNIT_ASSERT(!(m_stringArray1==m_stringArray2));
		}
		
		void testStringValue() {
			std::string* vals = m_stringArray1.getStringValue();
			CPPUNIT_ASSERT(compareArray(vals, m_string1, m_stringArray1.getSize()));
		}

		void testStringCopy() {
			MetadataArray array(m_stringArray1);
			CPPUNIT_ASSERT(array==m_stringArray1);
		}

		void testSize_tEqual() {
			CPPUNIT_ASSERT(m_size_tArray1==m_size_tArray1a);
		}
		
		void testSize_tNotEqual() {
			CPPUNIT_ASSERT(m_size_tArray1!=m_size_tArray2);
			CPPUNIT_ASSERT(!(m_size_tArray1==m_size_tArray2));
		}
		
		void testSize_tValue() {
			size_t* vals = m_size_tArray1.getSize_tValue();
			CPPUNIT_ASSERT(compareArray(vals, m_size_t1, m_size_tArray1.getSize()));
		}

		void testSize_tCopy() {
			MetadataArray array(m_size_tArray1);
			CPPUNIT_ASSERT(array==m_size_tArray1);
		}


		void testCast() {
			MetadataElement& element = m_stringArray1;
			MetadataArray& array = MetadataArray::castToArray(element);
		}

	private:

		void setUpBoolean() {

			bool bool1[4] = {true, false, true, true};
			bool bool2[4] = {true, false, false, true};
			m_bool1 = copyArray<bool>(bool1, 4);
			m_bool2 = copyArray<bool>(bool2, 4);

			m_boolArray1 = MetadataArray::createBoolean(bool1, 4);
			m_boolArray1a = MetadataArray::createBoolean(bool1, 4);
			m_boolArray2 = MetadataArray::createBoolean(bool2, 4);
		}

		void setUpInteger() {

			int int1[3] = {8, 99, -456};
			int int2[3] = {8, 999, -456};
			m_int1 = copyArray<int>(int1, 3);
			m_int2 = copyArray<int>(int2, 3);

			m_intArray1 = MetadataArray::createInteger(int1,3);
			m_intArray1a = MetadataArray::createInteger(int1,3);
			m_intArray2 = MetadataArray::createInteger(int2,3);
		}

		void setUpByte() {
		
			char byte1[3] = {6, 66, 33};
			char byte2[3] = {6, 66, 0};
			m_byte1 = copyArray<char>(byte1, 3);
			m_byte2 = copyArray<char>(byte2, 3);
			
			m_byteArray1 = MetadataArray::createByte(byte1,3);
			m_byteArray1a = MetadataArray::createByte(byte1,3);
			m_byteArray2 = MetadataArray::createByte(byte2,3);
		}

		void setUpFloat() {
			float float1[3] = {-0.95f, 1.234f, 5555.01f };
			float float2[3] = {-0.95f, 1.23456f, 5555.01f };
			m_float1 = copyArray<float>(float1, 3);
			m_float2 = copyArray<float>(float2, 3);

			m_floatArray1 = MetadataArray::createFloat(float1,3);
			m_floatArray1a = MetadataArray::createFloat(float1,3);
			m_floatArray2 = MetadataArray::createFloat(float2,3);
		}

		void setUpDouble() {
			double double1[3] = {-0.95, 1.234, 5555.01 };
			double double2[3] = {-0.95, 1.23456, 5555.01 };
			m_double1 = copyArray<double>(double1, 3);
			m_double2 = copyArray<double>(double2, 3);

			m_doubleArray1 = MetadataArray::createDouble(double1,3);
			m_doubleArray1a = MetadataArray::createDouble(double1,3);
			m_doubleArray2 = MetadataArray::createDouble(double2,3);
		}

		void setUpString() {
			std::string string1[2] = { "val1", "val2" };
			std::string string2[2] = { "val1", "val22" };
			m_string1 = copyArray<std::string>(string1, 2);
			m_string2 = copyArray<std::string>(string2, 2);

			m_stringArray1 = MetadataArray::createString(string1,2);
			m_stringArray1a = MetadataArray::createString(string1,2);
			m_stringArray2 = MetadataArray::createString(string2,2);
		}

		void setUpSize_t() {

			size_t size_t1[3] = {1234567890123, 98765432109876, 5544332211001122};
			size_t size_t2[3] = {1234567890123, 123435, 5544332211001122};
			m_size_t1 = copyArray<size_t>(size_t1, 3);
			m_size_t2 = copyArray<size_t>(size_t2, 3);

			m_size_tArray1 = MetadataArray::createSize_t(size_t1,3);
			m_size_tArray1a = MetadataArray::createSize_t(size_t1,3);
			m_size_tArray2 = MetadataArray::createSize_t(size_t2,3);
		}

		void tearDownBoolean() {
			delete[] m_bool1;
			delete[] m_bool2;
		}

		void tearDownInteger() {
			delete[] m_int1;
			delete[] m_int2;
		}

		void tearDownByte() {
			delete[] m_byte1;
			delete[] m_byte2;
		}

		void tearDownFloat() {
			delete[] m_float1;
			delete[] m_float2;
		}

		void tearDownDouble() {
			delete[] m_double1;
			delete[] m_double2;
		}

		void tearDownString() {
			delete[] m_string1;
			delete[] m_string2;
		}

		void tearDownSize_t() {
			delete[] m_size_t1;
			delete[] m_size_t2;
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestMetadataArray, "TestMetadataArray");

}

