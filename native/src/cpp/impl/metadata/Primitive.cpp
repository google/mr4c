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

#include <map>
#include <string>
#include <sstream>
#include <iomanip>
#include <stdexcept>

#include "metadata/metadata_api.h"
#include "util/util_api.h"

namespace MR4C {

class PrimitiveImpl {

	friend class Primitive;

	private:

		std::map<std::string,Primitive::Type> m_stringToEnum;
		std::map<Primitive::Type,std::string> m_enumToString;

		static PrimitiveImpl& instance() {
			static PrimitiveImpl s_instance;
			return s_instance;
		}
		
		PrimitiveImpl() {
			mapType(Primitive::BOOLEAN, "BOOLEAN");
			mapType(Primitive::BYTE, "BYTE");
			mapType(Primitive::INTEGER, "INTEGER");
			mapType(Primitive::FLOAT, "FLOAT");
			mapType(Primitive::DOUBLE, "DOUBLE");
			mapType(Primitive::STRING, "STRING");
			mapType(Primitive::SIZE_T, "SIZE_T");
			mapType(Primitive::LONG_DOUBLE, "LONG_DOUBLE");
		}
		
		void mapType(Primitive::Type type, const std::string& strType) {
			m_stringToEnum[strType] = type;
			m_enumToString[type] = strType;
		}
		
		
		
		Primitive::Type enumFromString(std::string strType) {
			if ( m_stringToEnum.count(strType)==0 ) {
				MR4C_THROW(std::invalid_argument, "No primitive type named [" << strType << "]");
			}
			return instance().m_stringToEnum[strType];
		}
		
		
		std::string enumToString(Primitive::Type type) {
			if ( m_enumToString.count(type)==0 ) {
				MR4C_THROW(std::invalid_argument, "No primitive type enum = " << type);
			}
			return instance().m_enumToString[type];
		}
		
		
		template<typename T> std::string toString(const T& val) {
		    std::ostringstream ss;
		    ss << std::boolalpha << std::setprecision(16) << val;
		    return ss.str();
		}
		
		template<typename T> T fromString(const std::string& str) {
		    std::istringstream ss(str);
		    T val;
		    ss >> std::boolalpha >> std::setprecision(16) >> val;
		    return val;
		}
		
		template<typename T> void toString(const T* vals, std::string* strs, size_t size) {
			for ( size_t i=0; i<size; i++ ) {
				strs[i] = toString<T>(vals[i]);
			}
		}
		
		template<typename T> void fromString(const std::string* strs, T* vals, size_t size ) {
			for ( size_t i=0; i<size; i++ ) {
				vals[i] = fromString<T>(strs[i]);
			}
		}

};

// specializations for char representing a byte
template<> std::string PrimitiveImpl::toString(const char& val) {
	return PrimitiveImpl::toString<int>((int)val);
}

template<> char PrimitiveImpl::fromString(const std::string& str) {
	return (char) PrimitiveImpl::fromString<int>(str);
}


Primitive::Type Primitive::enumFromString(std::string strType) {
	return PrimitiveImpl::instance().enumFromString(strType);
}


std::string Primitive::enumToString(Primitive::Type type) {
	return PrimitiveImpl::instance().enumToString(type);
}


template<typename T> std::string Primitive::toString(const T& val) {
	return PrimitiveImpl::instance().toString<T>(val);
}

template<typename T> T Primitive::fromString(const std::string& str) {
	return PrimitiveImpl::instance().fromString<T>(str);
}

template<typename T> void Primitive::toString(const T* vals, std::string* strs, size_t size) {
	PrimitiveImpl::instance().toString<T>(vals,strs,size);
}

template<typename T> void Primitive::fromString(const std::string* strs, T* vals, size_t size ) {
	PrimitiveImpl::instance().fromString<T>(strs,vals,size);
}


// These are all the known instantiations of the template
// They are included here to avoid having to put implementations into header files

template std::string Primitive::toString<bool>(const bool& val);
template bool Primitive::fromString<bool>(const std::string& str);
template void Primitive::toString<bool>(const bool* vals, std::string* strs, size_t size);
template void Primitive::fromString<bool>(const std::string* strs, bool* vals, size_t size );
template std::string Primitive::toString<char>(const char& val);
template char Primitive::fromString<char>(const std::string& str);
template void Primitive::toString<char>(const char* vals, std::string* strs, size_t size);
template void Primitive::fromString<char>(const std::string* strs, char* vals, size_t size );

template std::string Primitive::toString<int>(const int& val);
template int Primitive::fromString<int>(const std::string& str);
template void Primitive::toString<int>(const int* vals, std::string* strs, size_t size);
template void Primitive::fromString<int>(const std::string* strs, int* vals, size_t size );

template std::string Primitive::toString<float>(const float& val);
template float Primitive::fromString<float>(const std::string& str);
template void Primitive::toString<float>(const float* vals, std::string* strs, size_t size);
template void Primitive::fromString<float>(const std::string* strs, float* vals, size_t size );

template std::string Primitive::toString<double>(const double& val);
template double Primitive::fromString<double>(const std::string& str);
template void Primitive::toString<double>(const double* vals, std::string* strs, size_t size);
template void Primitive::fromString<double>(const std::string* strs, double* vals, size_t size );

template std::string Primitive::toString<long double>(const long double& val);
template long double Primitive::fromString<long double>(const std::string& str);
template void Primitive::toString<long double>(const long double* vals, std::string* strs, size_t size);
template void Primitive::fromString<long double>(const std::string* strs, long double* vals, size_t size );

template std::string Primitive::toString<std::string>(const std::string& val);
template std::string Primitive::fromString<std::string>(const std::string& str);
template void Primitive::toString<std::string>(const std::string* vals, std::string* strs, size_t size);
template void Primitive::fromString<std::string>(const std::string* strs, std::string* vals, size_t size );

template std::string Primitive::toString<size_t>(const size_t& val);
template size_t Primitive::fromString<size_t>(const std::string& str);
template void Primitive::toString<size_t>(const size_t* vals, std::string* strs, size_t size);
template void Primitive::fromString<size_t>(const std::string* strs, size_t* vals, size_t size );



}
