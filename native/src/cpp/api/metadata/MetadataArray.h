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

#ifndef __MR4C_METADATA_ARRAY_H__
#define __MR4C_METADATA_ARRAY_H__

#include <string>

#include "Primitive.h"
#include "MetadataElement.h"

namespace MR4C {

class MetadataArrayImpl;

/**
  * An array of primitive values of a single type.
  * This class is immutable.
  * Use is as follows:
  *     - call createXXX(val[]) to create a new array
  *     - call getXXXValue() to recover the the array of primitive values
  *     - call toString() to get an array of string representations of the primitive value
  *     - call parseArray(str[],type) to recover the array from the string representations
  *     .
  * Notes on usage:
  *     - The arrays passed in the createXXX and parseArray methods are copied,
  *       and may be disposed of after the method returns.
  *     - The arrays returned by getXXXValue are copies allocated on the heap
  *       with new[].  They should be disposed of with delete[] when no longer
  *       needed.
  *     - Very large arrays should probably be files, and not metadata.
  *     .
*/

class MetadataArray : public CloneableMetadataElement {

	public:

		MetadataArray();

		MetadataArray(const MetadataArray& array);

		static MetadataArray createBoolean(const bool* val, size_t size);

		static MetadataArray createInteger(const int* val, size_t size);

		static MetadataArray createByte(const char* val, size_t size);

		static MetadataArray createFloat(const float* val, size_t size);

		static MetadataArray createDouble(const double* val, size_t size);

		static MetadataArray createLongDouble(const long double* val, size_t size);

		static MetadataArray createString(const char** val, size_t size);

		static MetadataArray createString(const std::string* val, size_t size);

		static MetadataArray createSize_t(const size_t* val, size_t size);

		bool* getBooleanValue() const;

		char* getByteValue() const;

		int* getIntegerValue() const;

		float* getFloatValue() const;

		double* getDoubleValue() const;

		long double* getLongDoubleValue() const;

		std::string* getStringValue() const;

		size_t* getSize_tValue() const;

		Primitive::Type getPrimitiveType() const;

		MetadataElement::Type getMetadataElementType() const;

		virtual CloneableMetadataElement* clone() const;
		
		size_t getSize() const;

		MetadataArray& operator=(const MetadataArray& array);

		bool operator==(const MetadataArray& array) const;

		bool operator==(const MetadataElement& element) const;

		bool operator!=(const MetadataArray& array) const;

		bool operator!=(const MetadataElement& element) const;

		/**
		  * Returns a formatted representation of the array.  The type
		  * is followed by a comma separated list of the values.
		*/
		std::string str();

		~MetadataArray();

		static MetadataArray parseArray(const std::string* str, Primitive::Type type, size_t size);

		/**
		  * NOTE: The array of returned strings is allocated on the heap
		  * with new[] -  it should be disposed of with delete[].
		*/
		std::string* toString() const;

		/**
		  * NOTE: The caller must allocate an array of at least
		  * getSize() string pointers.
		*/
		void toString(std::string* strs) const;

		static const MetadataArray* castToArray(const MetadataElement* element);

		static MetadataArray* castToArray(MetadataElement* element);

		static const MetadataArray& castToArray(const MetadataElement& element);

		static MetadataArray& castToArray(MetadataElement& element);

	private:
		MetadataArrayImpl* m_impl;

};

}
#endif

