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


#include <string>
#include <stdexcept>
#include <map>
#include "metadata/metadata_api.h"
#include "util/util_api.h"

namespace MR4C {


class MetadataTypes {

	friend class MetadataElement;

	private:

		std::map<std::string,MetadataElement::Type> m_stringToEnum;
		std::map<MetadataElement::Type,std::string> m_enumToString;

		static MetadataTypes& instance() {
			static MetadataTypes s_instance;
			return s_instance;
		}

		MetadataTypes() {
			mapType(MetadataElement::FIELD, "FIELD");
			mapType(MetadataElement::ARRAY, "ARRAY");
			mapType(MetadataElement::LIST, "LIST");
			mapType(MetadataElement::MAP, "MAP");
			mapType(MetadataElement::KEY, "KEY");
		}

		// making sure these are private
		MetadataTypes(const MetadataTypes& types);
		MetadataTypes& operator=(const MetadataTypes& types);

		void mapType(MetadataElement::Type type, const std::string& strType) {
			m_stringToEnum[strType] = type;
			m_enumToString[type] = strType;
		}

		MetadataElement::Type enumFromString(std::string strType) {
			if ( m_stringToEnum.count(strType)==0 ) {
				MR4C_THROW(std::invalid_argument, "No metadata element type named [" << strType << "]");
			}
			return m_stringToEnum[strType];
		}


		std::string enumToString(MetadataElement::Type type) {
			if ( m_enumToString.count(type)==0 ) {
				MR4C_THROW(std::invalid_argument, "No metadata element type enum = " << type);
			}
			return m_enumToString[type];
		}

		void validateCast( MetadataElement::Type actualType, MetadataElement::Type targetType) {
			if ( actualType!=targetType ) {
				MR4C_THROW(std::runtime_error, "Illegal metadata cast attempt: " << enumToString(actualType) << " to " << enumToString(targetType));
			}

		}


	};

MetadataElement::Type MetadataElement::enumFromString(std::string strType) {
	return MetadataTypes::instance().enumFromString(strType);
}


std::string MetadataElement::enumToString(MetadataElement::Type type) {
	return MetadataTypes::instance().enumToString(type);
}



template<typename T> const T* MetadataElement::castElement(const MetadataElement* element, Type type) {
	MetadataTypes::instance().validateCast(element->getMetadataElementType(), type);
	return dynamic_cast<const T*>(element);
}

template<typename T> T* MetadataElement::castElement(MetadataElement* element, Type type) {
	MetadataTypes::instance().validateCast(element->getMetadataElementType(), type);
	return dynamic_cast<T*>(element);
}

template<typename T> const T& MetadataElement::castElement(const MetadataElement& element, Type type) {
	MetadataTypes::instance().validateCast(element.getMetadataElementType(), type);
	return dynamic_cast<const T&>(element);
}

template<typename T> T& MetadataElement::castElement(MetadataElement& element, Type type) {
	MetadataTypes::instance().validateCast(element.getMetadataElementType(), type);
	return dynamic_cast<T&>(element);
}


// These are all the possible instantiations of the above templates
// They are included here to avoid having to put implementations into header files

template const MetadataField* MetadataElement::castElement<MetadataField>(const MetadataElement* element, Type type);

template const MetadataArray* MetadataElement::castElement<MetadataArray>(const MetadataElement* element, Type type);

template const MetadataKey* MetadataElement::castElement<MetadataKey>(const MetadataElement* element, Type type);

template const MetadataList* MetadataElement::castElement<MetadataList>(const MetadataElement* element, Type type);

template const MetadataMap* MetadataElement::castElement<MetadataMap>(const MetadataElement* element, Type type);



template MetadataField* MetadataElement::castElement<MetadataField>(MetadataElement* element, Type type);

template MetadataArray* MetadataElement::castElement<MetadataArray>(MetadataElement* element, Type type);

template MetadataKey* MetadataElement::castElement<MetadataKey>(MetadataElement* element, Type type);

template MetadataList* MetadataElement::castElement<MetadataList>(MetadataElement* element, Type type);

template MetadataMap* MetadataElement::castElement<MetadataMap>(MetadataElement* element, Type type);



template const MetadataField& MetadataElement::castElement<MetadataField>(const MetadataElement& element, Type type);

template const MetadataArray& MetadataElement::castElement<MetadataArray>(const MetadataElement& element, Type type);

template const MetadataKey& MetadataElement::castElement<MetadataKey>(const MetadataElement& element, Type type);

template const MetadataList& MetadataElement::castElement<MetadataList>(const MetadataElement& element, Type type);

template const MetadataMap& MetadataElement::castElement<MetadataMap>(const MetadataElement& element, Type type);



template MetadataField& MetadataElement::castElement<MetadataField>(MetadataElement& element, Type type);

template MetadataArray& MetadataElement::castElement<MetadataArray>(MetadataElement& element, Type type);

template MetadataKey& MetadataElement::castElement<MetadataKey>(MetadataElement& element, Type type);

template MetadataList& MetadataElement::castElement<MetadataList>(MetadataElement& element, Type type);

template MetadataMap& MetadataElement::castElement<MetadataMap>(MetadataElement& element, Type type);

}

