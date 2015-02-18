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
#include "metadata/metadata_api.h"
#include "util/util_api.h"

namespace MR4C {

class MetadataFieldImpl {

	friend class MetadataField;

	private :

		Primitive::Type m_type;
		
		union FieldData {
			bool m_boolean;
			char m_byte;
			int m_int;
			float m_float;
			double m_double;
			long double m_long_double;
			size_t m_size_t;
		} m_val;

		std::string m_string;


		MetadataFieldImpl() {}
		
		MetadataFieldImpl(const MetadataFieldImpl& field) {
			initFrom(field);
		}
		
		void initFrom(const MetadataFieldImpl& field) {
			m_type = field.m_type;
			switch ( field.m_type ) {
				case Primitive::BOOLEAN:
					m_val.m_boolean = field.m_val.m_boolean;
					break;
				case Primitive::BYTE:
					m_val.m_byte = field.m_val.m_byte;
					break;
				case Primitive::INTEGER:
					m_val.m_int = field.m_val.m_int;
					break;
				case Primitive::FLOAT:
					m_val.m_float = field.m_val.m_float;
					break;
				case Primitive::DOUBLE:
					m_val.m_double = field.m_val.m_double;
					break;
				case Primitive::LONG_DOUBLE:
					m_val.m_long_double = field.m_val.m_long_double;
					break;
				case Primitive::STRING:
					m_string = field.m_string;
					break;
				case Primitive::SIZE_T:
					m_val.m_size_t = field.m_val.m_size_t;
					break;
				default:
					MR4C_THROW(std::range_error, "unknown primitive type [" << m_type << "]"); 
			}
		}
		
		static MetadataFieldImpl* createBoolean(const bool val) {
			MetadataFieldImpl* field = new MetadataFieldImpl();
			field->m_type = Primitive::BOOLEAN;
			field->m_val.m_boolean = val;
			return field;
		}
		
		static MetadataFieldImpl* createInteger(const int val) {
			MetadataFieldImpl* field = new MetadataFieldImpl();
			field->m_type = Primitive::INTEGER;
			field->m_val.m_int = val;
			return field;
		}
		
		static MetadataFieldImpl* createByte(const char val) {
			MetadataFieldImpl* field = new MetadataFieldImpl();
			field->m_type = Primitive::BYTE;
			field->m_val.m_byte = val;
			return field;
		}
		
		static MetadataFieldImpl* createFloat(const float val) {
			MetadataFieldImpl* field = new MetadataFieldImpl();
			field->m_type = Primitive::FLOAT;
			field->m_val.m_float = val;
			return field;
		}
		
		static MetadataFieldImpl* createDouble(const double val) {
			MetadataFieldImpl* field = new MetadataFieldImpl();
			field->m_type = Primitive::DOUBLE;
			field->m_val.m_double = val;
			return field;
		}
		
		static MetadataFieldImpl* createLongDouble(const long double val) {
			MetadataFieldImpl* field = new MetadataFieldImpl();
			field->m_type = Primitive::LONG_DOUBLE;
			field->m_val.m_long_double = val;
			return field;
		}
		
		static MetadataFieldImpl* createString(const char* val) {
			return createString(std::string(val));
		}
		
		static MetadataFieldImpl* createString(const std::string& val) {
			MetadataFieldImpl* field = new MetadataFieldImpl();
			field->m_type = Primitive::STRING;
			field->m_string = val;
			return field;
		}
		
		static MetadataFieldImpl* createSize_t(const size_t val) {
			MetadataFieldImpl* field = new MetadataFieldImpl();
			field->m_type = Primitive::SIZE_T;
			field->m_val.m_size_t = val;
			return field;
		}
		
		bool getBooleanValue() const {
			assertType(Primitive::BOOLEAN);
			return m_val.m_boolean;
		}
		
		char getByteValue() const {
			assertType(Primitive::BYTE);
			return m_val.m_byte;
		}
		
		int getIntegerValue() const {
			assertType(Primitive::INTEGER);
			return m_val.m_int;
		}
		
		float getFloatValue() const {
			assertType(Primitive::FLOAT);
			return m_val.m_float;
		}
		
		double getDoubleValue() const {
			assertType(Primitive::DOUBLE);
			return m_val.m_double;
		}
		
		long double getLongDoubleValue() const {
			assertType(Primitive::LONG_DOUBLE);
			return m_val.m_long_double;
		}
		
		std::string getStringValue() const {
			assertType(Primitive::STRING);
			return m_string;
		}
		
		size_t getSize_tValue() const {
			assertType(Primitive::SIZE_T);
			return m_val.m_size_t;
		}
		
		Primitive::Type getPrimitiveType() const {
			return m_type;
		}
		
		bool operator==(const MetadataFieldImpl& field) const {
			if ( m_type!=field.m_type ) return false;
			switch ( m_type ) {
				case Primitive::BOOLEAN:
					return m_val.m_boolean==field.m_val.m_boolean;
				case Primitive::BYTE:
					return m_val.m_byte==field.m_val.m_byte;
				case Primitive::INTEGER:
					return m_val.m_int==field.m_val.m_int;
				case Primitive::FLOAT:
					return m_val.m_float==field.m_val.m_float;
				case Primitive::DOUBLE:
					return m_val.m_double==field.m_val.m_double;
				case Primitive::LONG_DOUBLE:
					return m_val.m_long_double==field.m_val.m_long_double;
				case Primitive::STRING:
					return m_string==field.m_string;
				case Primitive::SIZE_T:
					return m_val.m_size_t==field.m_val.m_size_t;
				default:
					MR4C_THROW(std::range_error, "unknown primitive type [" << m_type << "]"); 
			}
		}
		
		void assertType(Primitive::Type type) const {
			if ( m_type!=type ) {
				MR4C_THROW(std::logic_error, "Requested type [" << Primitive::enumToString(type) << "] from field of type [" << Primitive::enumToString(m_type) << "]");
			}
		}
		
		static MetadataFieldImpl* parseField(const std::string& str, Primitive::Type type) {
			switch ( type ) {
				case Primitive::BOOLEAN:
					return parseBoolean(str);
				case Primitive::BYTE:
					return parseByte(str);
				case Primitive::INTEGER:
					return parseInteger(str);
				case Primitive::FLOAT:
					return parseFloat(str);
				case Primitive::DOUBLE:
					return parseDouble(str);
				case Primitive::LONG_DOUBLE:
					return parseLongDouble(str);
				case Primitive::STRING:
					return parseString(str);
				case Primitive::SIZE_T:
					return parseSize_t(str);
				default:
					MR4C_THROW(std::range_error, "unknown primitive type [" << type << "]"); 
			}
		}
		
		static MetadataFieldImpl* parseBoolean(const std::string& str) {
			bool val = Primitive::fromString<bool>(str);
			return createBoolean(val);
		}
		
		static MetadataFieldImpl* parseByte(const std::string& str) {
			char val = Primitive::fromString<char>(str);
			return createByte(val);
		}
		
		static MetadataFieldImpl* parseInteger(const std::string& str) {
			int val = Primitive::fromString<int>(str);
			return createInteger(val);
		}
		
		static MetadataFieldImpl* parseFloat(const std::string& str) {
			float val = Primitive::fromString<float>(str);
			return createFloat(val);
		}
		
		static MetadataFieldImpl* parseDouble(const std::string& str) {
			double val = Primitive::fromString<double>(str);
			return createDouble(val);
		}
		
		static MetadataFieldImpl* parseLongDouble(const std::string& str) {
			long double val = Primitive::fromString<long double>(str);
			return createLongDouble(val);
		}
		
		static MetadataFieldImpl* parseString(const std::string& str) {
			return createString(str);
		}
		
		static MetadataFieldImpl* parseSize_t(const std::string& str) {
			size_t val = Primitive::fromString<size_t>(str);
			return createSize_t(val);
		}
		
		
		std::string toString() const {
			switch ( m_type ) {
				case Primitive::BOOLEAN:
					return Primitive::toString<bool>(m_val.m_boolean);
				case Primitive::BYTE:
					return Primitive::toString<char>(m_val.m_byte);
				case Primitive::INTEGER:
					return Primitive::toString<int>(m_val.m_int);
				case Primitive::FLOAT:
					return Primitive::toString<float>(m_val.m_float);
				case Primitive::DOUBLE:
					return Primitive::toString<double>(m_val.m_double);
				case Primitive::LONG_DOUBLE:
					return Primitive::toString<long double>(m_val.m_long_double);
				case Primitive::STRING:
					return m_string;
				case Primitive::SIZE_T:
					return Primitive::toString<size_t>(m_val.m_size_t);
				default:
					MR4C_THROW(std::range_error, "unknown primitive type [" << m_type << "]"); 
			}
		}

		std::string str() {
			MR4C_RETURN_STRING("type = " << Primitive::enumToString(m_type) << "; value = " << toString());
		}
		
};



MetadataField::MetadataField() {
	m_impl = new MetadataFieldImpl();
}

MetadataField::MetadataField(const MetadataField& field) {
	m_impl = new MetadataFieldImpl(*field.m_impl);
}

MetadataField MetadataField::createBoolean(const bool val) {
	MetadataField field;
	delete field.m_impl;
	field.m_impl = MetadataFieldImpl::createBoolean(val);
	return field;
}

MetadataField MetadataField::createInteger(const int val) {
	MetadataField field;
	delete field.m_impl;
	field.m_impl = MetadataFieldImpl::createInteger(val);
	return field;
}

MetadataField MetadataField::createByte(const char val) {
	MetadataField field;
	delete field.m_impl;
	field.m_impl = MetadataFieldImpl::createByte(val);
	return field;
}

MetadataField MetadataField::createFloat(const float val) {
	MetadataField field;
	delete field.m_impl;
	field.m_impl = MetadataFieldImpl::createFloat(val);
	return field;
}

MetadataField MetadataField::createDouble(const double val) {
	MetadataField field;
	delete field.m_impl;
	field.m_impl = MetadataFieldImpl::createDouble(val);
	return field;
}

MetadataField MetadataField::createLongDouble(const long double val) {
	MetadataField field;
	delete field.m_impl;
	field.m_impl = MetadataFieldImpl::createLongDouble(val);
	return field;
}

MetadataField MetadataField::createString(const char* val) {
	MetadataField field;
	delete field.m_impl;
	field.m_impl = MetadataFieldImpl::createString(val);
	return field;
}

MetadataField MetadataField::createString(const std::string& val) {
	MetadataField field;
	delete field.m_impl;
	field.m_impl = MetadataFieldImpl::createString(val);
	return field;
}

MetadataField MetadataField::createSize_t(const size_t val) {
	MetadataField field;
	delete field.m_impl;
	field.m_impl = MetadataFieldImpl::createSize_t(val);
	return field;
}

bool MetadataField::getBooleanValue() const {
	return m_impl->getBooleanValue();
}

char MetadataField::getByteValue() const {
	return m_impl->getByteValue();
}

int MetadataField::getIntegerValue() const {
	return m_impl->getIntegerValue();
}

float MetadataField::getFloatValue() const {
	return m_impl->getFloatValue();
}

double MetadataField::getDoubleValue() const {
	return m_impl->getDoubleValue();
}

long double MetadataField::getLongDoubleValue() const {
	return m_impl->getLongDoubleValue();
}

std::string MetadataField::getStringValue() const {
	return m_impl->getStringValue();
}

size_t MetadataField::getSize_tValue() const {
	return m_impl->getSize_tValue();
}

Primitive::Type MetadataField::getPrimitiveType() const {
	return m_impl->getPrimitiveType();
}

MetadataElement::Type MetadataField::getMetadataElementType() const {
	return MetadataElement::FIELD;
}

CloneableMetadataElement* MetadataField::clone() const {
	return new MetadataField(*this);
}

MetadataField& MetadataField::operator=(const MetadataField& field) {
	m_impl->initFrom(*field.m_impl);
	return *this;
}

bool MetadataField::operator==(const MetadataField& field) const {
	return *m_impl==*field.m_impl;
}

bool MetadataField::operator==(const MetadataElement& element) const {
	if ( element.getMetadataElementType()!=MetadataElement::FIELD) {
		return false;
	}
	const MetadataField& field = castToField(element);
	return operator==(field);
}

bool MetadataField::operator!=(const MetadataField& field) const {
	return !operator==(field);
}

bool MetadataField::operator!=(const MetadataElement& element) const {
	return !operator==(element);
}

std::string MetadataField::str() {
	return m_impl->str();
}

MetadataField::~MetadataField() {
	delete m_impl;
}


MetadataField MetadataField::parseField(const std::string& str, Primitive::Type type) {
	MetadataField field;
	delete field.m_impl;
	field.m_impl = MetadataFieldImpl::parseField(str, type);
	return field;
}

std::string MetadataField::toString() const {
	return m_impl->toString();
}

MetadataField* MetadataField::castToField(MetadataElement* element) {
	return MetadataElement::castElement<MetadataField>(element, MetadataElement::FIELD);
}

const MetadataField* MetadataField::castToField(const MetadataElement* element) {
	return MetadataElement::castElement<MetadataField>(element, MetadataElement::FIELD);
}

MetadataField& MetadataField::castToField(MetadataElement& element) {
	return MetadataElement::castElement<MetadataField>(element, MetadataElement::FIELD);
}

const MetadataField& MetadataField::castToField(const MetadataElement& element) {
	return MetadataElement::castElement<MetadataField>(element, MetadataElement::FIELD);
}


}
