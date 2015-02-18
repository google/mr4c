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

class TestPrimitive : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestPrimitive);
	CPPUNIT_TEST(testBooleanToString);
	CPPUNIT_TEST(testBooleanFromString);
	CPPUNIT_TEST(testByteToString);
	CPPUNIT_TEST(testByteFromString);
	CPPUNIT_TEST(testIntegerToString);
	CPPUNIT_TEST(testIntegerFromString);
	CPPUNIT_TEST(testFloats);
	CPPUNIT_TEST(testDoubleToString);
	CPPUNIT_TEST(testDoubleFromString);
	CPPUNIT_TEST(testLongDoubles);
	CPPUNIT_TEST(testLongDoubleToString);
	CPPUNIT_TEST(testLongDoubleFromString);
	CPPUNIT_TEST(testSize_tToString);
	CPPUNIT_TEST(testSize_tFromString);
	CPPUNIT_TEST_SUITE_END();

	private:

	public:

		void setUp() {}

		void tearDown() {}

		void testBooleanToString() {
			CPPUNIT_ASSERT(Primitive::toString<bool>(true)=="true");
			CPPUNIT_ASSERT(Primitive::toString<bool>(false)=="false");
		}
		
		void testBooleanFromString() {
			CPPUNIT_ASSERT(Primitive::fromString<bool>("true"));
			CPPUNIT_ASSERT(!Primitive::fromString<bool>("false"));
		}
		
		void testByteToString() {
			CPPUNIT_ASSERT(Primitive::toString<char>(123)=="123");
			CPPUNIT_ASSERT(Primitive::toString<char>(-67)=="-67");
		}
		
		void testByteFromString() {
			CPPUNIT_ASSERT(Primitive::fromString<char>("123")==123);
			CPPUNIT_ASSERT(Primitive::fromString<char>("-67")==-67);
		}
		
		void testIntegerToString() {
			CPPUNIT_ASSERT(Primitive::toString<int>(123456)=="123456");
			CPPUNIT_ASSERT(Primitive::toString<int>(-6789)=="-6789");
		}
		
		void testIntegerFromString() {
			CPPUNIT_ASSERT(Primitive::fromString<int>("123456")==123456);
			CPPUNIT_ASSERT(Primitive::fromString<int>("-6789")==-6789);
		}
		
		void testFloats() {
			CPPUNIT_ASSERT(123.456f==roundTripFloat(123.456f));
			CPPUNIT_ASSERT(-67.89f==roundTripFloat(-67.89f));
			CPPUNIT_ASSERT(4546.789f==roundTripFloat(4546.789f));
		}

		void testDoubleToString() {
			CPPUNIT_ASSERT(Primitive::toString<double>(123.456)=="123.456");
			CPPUNIT_ASSERT(Primitive::toString<double>(-67.89)=="-67.89");
			CPPUNIT_ASSERT(Primitive::toString<double>(4546.789)=="4546.789");
			CPPUNIT_ASSERT(Primitive::toString<double>(1234546.789)=="1234546.789");
		}
		
		void testDoubleFromString() {
			CPPUNIT_ASSERT(Primitive::fromString<double>("123.456")==123.456);
			CPPUNIT_ASSERT(Primitive::fromString<double>("-67.89")==-67.89);
			CPPUNIT_ASSERT(Primitive::fromString<double>("4546.789")==4546.789);
			CPPUNIT_ASSERT(Primitive::fromString<double>("1234546.789")==1234546.789);
		}
	
		void testLongDoubles() {
			CPPUNIT_ASSERT(123456789.123456L==roundTripLongDouble(123456789.123456L));
		}

		void testLongDoubleToString() {
			CPPUNIT_ASSERT(Primitive::toString<long double>(123456789.123456L)=="123456789.123456");
			CPPUNIT_ASSERT(Primitive::toString<long double>(-678987654.8998765L)=="-678987654.8998765");
		}
		
		void testLongDoubleFromString() {
			CPPUNIT_ASSERT(Primitive::fromString<long double>("123.45678901234567")==123.45678901234567L);
			CPPUNIT_ASSERT(Primitive::fromString<long double>("-67.890123456789012")==-67.890123456789012L);
		}
	
		void testSize_tToString() {
			CPPUNIT_ASSERT(Primitive::toString<size_t>(1234567890123)=="1234567890123");
		}
		
		void testSize_tFromString() {
			CPPUNIT_ASSERT(Primitive::fromString<size_t>("1234567890123")==1234567890123);
		}
		
	private:	
		float roundTripFloat(float f1)  {
			std::string str = Primitive::toString<float>(f1);
			return Primitive::fromString<float>(str);
		}

		long double roundTripLongDouble(long double ld1)  {
			std::string str = Primitive::toString<long double>(ld1);
			return Primitive::fromString<long double>(str);
		}

};

CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(TestPrimitive, "TestPrimitive");

}

