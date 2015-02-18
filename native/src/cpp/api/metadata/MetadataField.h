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

#ifndef __MR4C_METADATA_FIELD_H__
#define __MR4C_METADATA_FIELD_H__

#include <string>

#include "Primitive.h"
#include "MetadataElement.h"

namespace MR4C {

class MetadataFieldImpl;

/**
  * A single primitive value.
  * This class is immutable.
  * Use is as follows:
  *     - call createXXX(val) to create a new field
  *     - call getXXXValue() to recover the primitive value
  *     - call toString() to get the string representation of the primitive value
  *     - call parseField(str,type) to recover the field from the string representation
*/

class MetadataField : public CloneableMetadataElement {

	public:

		MetadataField();

		MetadataField(const MetadataField& field);

		static MetadataField createBoolean(const bool val);

		static MetadataField createInteger(const int val);

		static MetadataField createByte(const char val);

		static MetadataField createFloat(const float val);

		static MetadataField createDouble(const double val);

		static MetadataField createLongDouble(const long double val);

		static MetadataField createString(const char* val);

		static MetadataField createString(const std::string& val);

		static MetadataField createSize_t(const size_t val);

		bool getBooleanValue() const;

		char getByteValue() const;

		int getIntegerValue() const;

		float getFloatValue() const;

		double getDoubleValue() const;

		long double getLongDoubleValue() const;

		std::string getStringValue() const;

		size_t getSize_tValue() const;

		Primitive::Type getPrimitiveType() const;

		MetadataElement::Type getMetadataElementType() const;

		virtual CloneableMetadataElement* clone() const;
		
		MetadataField& operator=(const MetadataField& field);

		bool operator==(const MetadataField& field) const;

		bool operator==(const MetadataElement& element) const;

		bool operator!=(const MetadataField& field) const;

		bool operator!=(const MetadataElement& element) const;

		/**
		  * Returns a formatted representation of the field, including
		  * value and type.
		*/
		std::string str();

		~MetadataField();

		static MetadataField parseField(const std::string& str, Primitive::Type type);

		std::string toString() const;

		static const MetadataField* castToField(const MetadataElement* element);

		static MetadataField* castToField(MetadataElement* element);

		static const MetadataField& castToField(const MetadataElement& element);

		static MetadataField& castToField(MetadataElement& element);


	private:
		MetadataFieldImpl* m_impl;


};

}
#endif

