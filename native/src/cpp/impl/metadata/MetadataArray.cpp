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

#include <iostream>
#include <stdexcept>
#include "metadata/metadata_api.h"
#include "util/util_api.h"

namespace MR4C {

class MetadataArrayImpl {

	friend class MetadataArray;

	private:
		Primitive::Type m_type;
		
		struct ArrayData {
			bool* m_boolean;
			char* m_byte;
			int* m_int;
			float* m_float;
			double* m_double;
			long double* m_long_double;
			size_t* m_size_t;
		} m_val;

		std::string* m_string;

		size_t m_size;


		MetadataArrayImpl() {
			init();
		}

		MetadataArrayImpl(const MetadataArrayImpl& array) {
			init();
			initFrom(array);
		}

		~MetadataArrayImpl() {
			freePointers();
		}

		void init() {
			m_val.m_boolean=NULL;
			m_val.m_int=NULL;
			m_val.m_byte=NULL;
			m_val.m_float=NULL;
			m_val.m_double=NULL;
			m_val.m_long_double=NULL;
			m_val.m_size_t=NULL;
			m_string=NULL;
			m_size=0;
		}

		void freePointers() {
			delete[] m_val.m_boolean;
			delete[] m_val.m_byte;
			delete[] m_val.m_int;
			delete[] m_val.m_float;
			delete[] m_val.m_double;
			delete[] m_val.m_long_double;
			delete[] m_val.m_size_t;
			delete[] m_string;
			m_val.m_boolean = NULL;
			m_val.m_byte = NULL;
			m_val.m_int = NULL;
			m_val.m_float = NULL;
			m_val.m_double = NULL;
			m_val.m_long_double = NULL;
			m_val.m_size_t = NULL;
			m_string = NULL;
		}

		void initFrom(const MetadataArrayImpl& array) {
			freePointers();
			m_type = array.m_type;
			m_size = array.m_size;
			switch ( array.m_type ) {
				case Primitive::BOOLEAN:
					m_val.m_boolean = copyArray<bool>(array.m_val.m_boolean, array.m_size);
					break;
				case Primitive::BYTE:
					m_val.m_byte = copyArray<char>(array.m_val.m_byte, array.m_size);
					break;
				case Primitive::INTEGER:
					m_val.m_int = copyArray<int>(array.m_val.m_int, array.m_size);
					break;
				case Primitive::FLOAT:
					m_val.m_float = copyArray<float>(array.m_val.m_float, array.m_size);
					break;
				case Primitive::DOUBLE:
					m_val.m_double = copyArray<double>(array.m_val.m_double, array.m_size);
					break;
				case Primitive::LONG_DOUBLE:
					m_val.m_long_double = copyArray<long double>(array.m_val.m_long_double, array.m_size);
					break;
				case Primitive::STRING:
					m_string = copyArray<std::string>(array.m_string, array.m_size);
					break;
				case Primitive::SIZE_T:
					m_val.m_size_t = copyArray<size_t>(array.m_val.m_size_t, array.m_size);
					break;
				default:
					MR4C_THROW(std::range_error, "unknown primitive type [" << array.m_type << "]"); 

			}
		}

		static MetadataArrayImpl* createBoolean(const bool* val, size_t size) {
			MetadataArrayImpl* array = new MetadataArrayImpl();
			array->m_type = Primitive::BOOLEAN;
			array->m_size = size;
			array->m_val.m_boolean = copyArray<bool>(val, size);
			return array;
		}

		static MetadataArrayImpl* createInteger(const int* val, size_t size) {
			MetadataArrayImpl* array = new MetadataArrayImpl();
			array->m_type = Primitive::INTEGER;
			array->m_size = size;
			array->m_val.m_int = copyArray<int>(val, size);
			return array;
		}

		static MetadataArrayImpl* createByte(const char* val, size_t size) {
			MetadataArrayImpl* array = new MetadataArrayImpl();
			array->m_type = Primitive::BYTE;
			array->m_size = size;
			array->m_val.m_byte = copyArray<char>(val, size);
			return array;
		}

		static MetadataArrayImpl* createFloat(const float* val, size_t size) {
			MetadataArrayImpl* array = new MetadataArrayImpl();
			array->m_type = Primitive::FLOAT;
			array->m_size = size;
			array->m_val.m_float = copyArray<float>(val, size);
			return array;
		}

		static MetadataArrayImpl* createDouble(const double* val, size_t size) {
			MetadataArrayImpl* array = new MetadataArrayImpl();
			array->m_type = Primitive::DOUBLE;
			array->m_size = size;
			array->m_val.m_double = copyArray<double>(val, size);
			return array;
		}

		static MetadataArrayImpl* createLongDouble(const long double* val, size_t size) {
			MetadataArrayImpl* array = new MetadataArrayImpl();
			array->m_type = Primitive::LONG_DOUBLE;
			array->m_size = size;
			array->m_val.m_long_double = copyArray<long double>(val, size);
			return array;
		}

		static MetadataArrayImpl* createString(const char** val, size_t size) {
			throw std::runtime_error("createString(char**) not implemented");
		}

		static MetadataArrayImpl* createString(const std::string* val, size_t size) {
			MetadataArrayImpl* array = new MetadataArrayImpl();
			array->m_type = Primitive::STRING;
			array->m_size = size;
			array->m_string = new std::string[size];
			array->m_string = copyArray<std::string>(val, size);
			return array;
		}

		static MetadataArrayImpl* createSize_t(const size_t* val, size_t size) {
			MetadataArrayImpl* array = new MetadataArrayImpl();
			array->m_type = Primitive::SIZE_T;
			array->m_size = size;
			array->m_val.m_size_t = copyArray<size_t>(val, size);
			return array;
		}


		bool* getBooleanValue() const {
			assertType(Primitive::BOOLEAN);
			return copyArray<bool>(m_val.m_boolean, m_size);
		}

		char* getByteValue() const {
			assertType(Primitive::BYTE);
			return copyArray<char>(m_val.m_byte, m_size);
		}

		int* getIntegerValue() const {
			assertType(Primitive::INTEGER);
			return copyArray<int>(m_val.m_int, m_size);
		}

		float* getFloatValue() const {
			assertType(Primitive::FLOAT);
			return copyArray<float>(m_val.m_float, m_size);
		}

		double* getDoubleValue() const {
			assertType(Primitive::DOUBLE);
			return copyArray<double>(m_val.m_double, m_size);
		}

		long double* getLongDoubleValue() const {
			assertType(Primitive::LONG_DOUBLE);
			return copyArray<long double>(m_val.m_long_double, m_size);
		}

		std::string* getStringValue() const {
			assertType(Primitive::STRING);
			return copyArray<std::string>(m_string, m_size);
		}

		size_t* getSize_tValue() const {
			assertType(Primitive::SIZE_T);
			return copyArray<size_t>(m_val.m_size_t, m_size);
		}

		Primitive::Type getPrimitiveType() const {
			return m_type;
		}

		size_t getSize() const {
			return m_size;
		}

		bool operator==(const MetadataArrayImpl& array) const {
			if ( m_type!=array.m_type ) return false;
			if ( m_size!=array.m_size ) return false;
			switch ( m_type ) {
				case Primitive::BOOLEAN:
					return compareArray<bool>(m_val.m_boolean, array.m_val.m_boolean, m_size);
				case Primitive::BYTE:
					return compareArray<char>(m_val.m_byte, array.m_val.m_byte, m_size);
				case Primitive::INTEGER:
					return compareArray<int>(m_val.m_int, array.m_val.m_int, m_size);
				case Primitive::FLOAT:
					return compareArray<float>(m_val.m_float, array.m_val.m_float, m_size);
				case Primitive::DOUBLE:
					return compareArray<double>(m_val.m_double, array.m_val.m_double, m_size);
				case Primitive::LONG_DOUBLE:
					return compareArray<long double>(m_val.m_long_double, array.m_val.m_long_double, m_size);
				case Primitive::STRING:
					return compareArray<std::string>(m_string, array.m_string, m_size);
				case Primitive::SIZE_T:
					return compareArray<size_t>(m_val.m_size_t, array.m_val.m_size_t, m_size);
				default:
					MR4C_THROW(std::range_error, "unknown primitive type [" << m_type << "]"); 
			}
		}

		void assertType(Primitive::Type type) const {
			if ( m_type!=type ) {
				MR4C_THROW(std::logic_error, "Requested type [" << Primitive::enumToString(type) << "] from array of type [" << Primitive::enumToString(m_type) << "]");
			}
		}

		static MetadataArrayImpl* parseArray(const std::string* str, Primitive::Type type, size_t size) {
			switch ( type ) {
				case Primitive::BOOLEAN:
					return parseBoolean(str,size);
				case Primitive::BYTE:
					return parseByte(str,size);
				case Primitive::INTEGER:
					return parseInteger(str,size);
				case Primitive::FLOAT:
					return parseFloat(str,size);
				case Primitive::DOUBLE:
					return parseDouble(str,size);
				case Primitive::LONG_DOUBLE:
					return parseLongDouble(str,size);
				case Primitive::STRING:
					return parseString(str,size);
				case Primitive::SIZE_T:
					return parseSize_t(str,size);
				default:
					MR4C_THROW(std::range_error, "unknown primitive type [" << type << "]"); 
			}
		}

		static MetadataArrayImpl* parseBoolean(const std::string* str, size_t size) {
			// can't use broken vector<bool> specialization here
			bool* vals = new bool[size];
			Primitive::fromString<bool>(str,vals, size);
			MetadataArrayImpl* array = createBoolean(vals,size);
			delete vals;
			return array;
		}

		static MetadataArrayImpl* parseByte(const std::string* str, size_t size) {
			std::vector<char> vals(size);
			Primitive::fromString<char>(str, vals.data(), size);
			return createByte(vals.data(),size);
		}

		static MetadataArrayImpl* parseInteger(const std::string* str, size_t size) {
			std::vector<int> vals(size);
			Primitive::fromString<int>(str,vals.data(), size);
			return createInteger(vals.data(),size);
		}

		static MetadataArrayImpl* parseFloat(const std::string* str, size_t size) {
			std::vector<float> vals(size);
			Primitive::fromString<float>(str,vals.data(), size);
			return createFloat(vals.data(),size);
		}

		static MetadataArrayImpl* parseDouble(const std::string* str, size_t size) {
			std::vector<double> vals(size);
			Primitive::fromString<double>(str,vals.data(), size);
			return createDouble(vals.data(),size);
		}

		static MetadataArrayImpl* parseLongDouble(const std::string* str, size_t size) {
			std::vector<long double> vals(size);
			Primitive::fromString<long double>(str,vals.data(), size);
			return createLongDouble(vals.data(),size);
		}

		static MetadataArrayImpl* parseString(const std::string* str, size_t size) {
			return createString(str,size);
		}

		static MetadataArrayImpl* parseSize_t(const std::string* str, size_t size) {
			std::vector<size_t> vals(size);
			Primitive::fromString<size_t>(str,vals.data(), size);
			return createSize_t(vals.data(),size);
		}

		std::string* toString() const {
			std::string* strs = new std::string[m_size];
			toString(strs);
			return strs;
		}

		void toString(std::string* strs) const {
			switch ( m_type ) {
				case Primitive::BOOLEAN:
					Primitive::toString<bool>(m_val.m_boolean, strs, m_size);
					break;
				case Primitive::BYTE:
					Primitive::toString<char>(m_val.m_byte, strs, m_size);
					break;
				case Primitive::INTEGER:
					Primitive::toString<int>(m_val.m_int, strs, m_size);
					break;
				case Primitive::FLOAT:
					Primitive::toString<float>(m_val.m_float, strs, m_size);
					break;
				case Primitive::DOUBLE:
					Primitive::toString<double>(m_val.m_double, strs, m_size);
					break;
				case Primitive::LONG_DOUBLE:
					Primitive::toString<long double>(m_val.m_long_double, strs, m_size);
					break;
				case Primitive::STRING:
					Primitive::toString<std::string>(m_string, strs, m_size);
					break;
				case Primitive::SIZE_T:
					Primitive::toString<size_t>(m_val.m_size_t, strs, m_size);
				default:
					MR4C_THROW(std::range_error, "unknown primitive type [" << m_type << "]"); 
			}
		}

		std::string str() {
			std::string strs[m_size];
			toString(strs);
			std::ostringstream ss;
			ss << "type = " << Primitive::enumToString(m_type) << "; values = [";
			for ( size_t i=0; i<m_size; i++ ) {
				ss << strs[i];
				if ( i<m_size-1 ) {
					ss << ", ";
				}
			}
			return ss.str();
		}

};


MetadataArray::MetadataArray() {
	m_impl = new MetadataArrayImpl();
}

MetadataArray::MetadataArray(const MetadataArray& array) {
	m_impl = new MetadataArrayImpl(*array.m_impl);
}

MetadataArray MetadataArray::createBoolean(const bool* val, size_t size) {
	MetadataArray array;
	delete array.m_impl;
	array.m_impl = MetadataArrayImpl::createBoolean(val, size);
	return array;
}

MetadataArray MetadataArray::createInteger(const int* val, size_t size) {
	MetadataArray array;
	delete array.m_impl;
	array.m_impl = MetadataArrayImpl::createInteger(val, size);
	return array;
}

MetadataArray MetadataArray::createByte(const char* val, size_t size) {
	MetadataArray array;
	delete array.m_impl;
	array.m_impl = MetadataArrayImpl::createByte(val, size);
	return array;
}

MetadataArray MetadataArray::createFloat(const float* val, size_t size) {
	MetadataArray array;
	delete array.m_impl;
	array.m_impl = MetadataArrayImpl::createFloat(val, size);
	return array;
}

MetadataArray MetadataArray::createDouble(const double* val, size_t size) {
	MetadataArray array;
	delete array.m_impl;
	array.m_impl = MetadataArrayImpl::createDouble(val, size);
	return array;
}
MetadataArray MetadataArray::createLongDouble(const long double* val, size_t size) {
	MetadataArray array;
	delete array.m_impl;
	array.m_impl = MetadataArrayImpl::createLongDouble(val, size);
	return array;
}

MetadataArray MetadataArray::createString(const char** val, size_t size) {
	MetadataArray array;
	delete array.m_impl;
	array.m_impl = MetadataArrayImpl::createString(val, size);
	return array;
}

MetadataArray MetadataArray::createString(const std::string* val, size_t size) {
	MetadataArray array;
	delete array.m_impl;
	array.m_impl = MetadataArrayImpl::createString(val, size);
	return array;
}

MetadataArray MetadataArray::createSize_t(const size_t* val, size_t size) {
	MetadataArray array;
	delete array.m_impl;
	array.m_impl = MetadataArrayImpl::createSize_t(val, size);
	return array;
}

bool* MetadataArray::getBooleanValue() const {
	return m_impl->getBooleanValue();
}

char* MetadataArray::getByteValue() const {
	return m_impl->getByteValue();
}

int* MetadataArray::getIntegerValue() const {
	return m_impl->getIntegerValue();
}

float* MetadataArray::getFloatValue() const {
	return m_impl->getFloatValue();
}

double* MetadataArray::getDoubleValue() const {
	return m_impl->getDoubleValue();
}

long double* MetadataArray::getLongDoubleValue() const {
	return m_impl->getLongDoubleValue();
}

std::string* MetadataArray::getStringValue() const {
	return m_impl->getStringValue();
}

size_t* MetadataArray::getSize_tValue() const {
	return m_impl->getSize_tValue();
}

Primitive::Type MetadataArray::getPrimitiveType() const {
	return m_impl->getPrimitiveType();
}

size_t MetadataArray::getSize() const {
	return m_impl->getSize();
}

MetadataElement::Type MetadataArray::getMetadataElementType() const {
	return MetadataElement::ARRAY;
}

CloneableMetadataElement* MetadataArray::clone() const {
	return new MetadataArray(*this);
}

MetadataArray& MetadataArray::operator=(const MetadataArray& array) {
	m_impl->initFrom(*array.m_impl);
	return *this;
}

bool MetadataArray::operator==(const MetadataArray& array) const {
	return *m_impl==*array.m_impl;
}

bool MetadataArray::operator==(const MetadataElement& element) const {
	if ( element.getMetadataElementType()!=MetadataElement::ARRAY) {
		return false;
	}
	const MetadataArray& array = castToArray(element);
	return operator==(array);
}

bool MetadataArray::operator!=(const MetadataArray& array) const {
	return !operator==(array);
}

bool MetadataArray::operator!=(const MetadataElement& element) const {
	return !operator==(element);
}

std::string MetadataArray::str() {
	return m_impl->str();
}

MetadataArray::~MetadataArray() {
	delete m_impl;
}

MetadataArray MetadataArray::parseArray(const std::string* str, Primitive::Type type, size_t size) {
	MetadataArray array;
	delete array.m_impl;
	array.m_impl = MetadataArrayImpl::parseArray(str, type, size);
	return array;
}

std::string* MetadataArray::toString() const {
	return m_impl->toString();
}

void MetadataArray::toString(std::string* strs) const {
	m_impl->toString(strs);
}

MetadataArray* MetadataArray::castToArray(MetadataElement* element) {
	return MetadataElement::castElement<MetadataArray>(element, MetadataElement::ARRAY);
}

const MetadataArray* MetadataArray::castToArray(const MetadataElement* element) {
	return MetadataElement::castElement<MetadataArray>(element, MetadataElement::ARRAY);
}

MetadataArray& MetadataArray::castToArray(MetadataElement& element) {
	return MetadataElement::castElement<MetadataArray>(element, MetadataElement::ARRAY);
}

const MetadataArray& MetadataArray::castToArray(const MetadataElement& element) {
	return MetadataElement::castElement<MetadataArray>(element, MetadataElement::ARRAY);
}


}
