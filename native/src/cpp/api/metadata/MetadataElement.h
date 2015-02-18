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

#ifndef __MR4C_METADATA_ELEMENT_H__
#define __MR4C_METADATA_ELEMENT_H__

#include <string>

namespace MR4C {

/**
  * Base class for the different types of metadata
*/

class MetadataElement {

	public :
		enum Type {

			/** Implemented by MetadataField */
			FIELD,

			/** Implemented by MetadataArray */
			ARRAY,

			/** Implemented by MetadataList */
			LIST,

			/** Implemented by MetadataMap */
			MAP,

			/** Implemented by MetadataKey */
			KEY 
		};

		/**
		  * Parses the string equivalent of the enum.
		  * For example:  "KEY" --> KEY
		*/
		static Type enumFromString(std::string strType);

		/**
		  * Returns the string equivalent of the enum.
		  * For example:  KEY --> "KEY"
		*/
		static std::string enumToString(Type type);

		virtual Type getMetadataElementType() const =0;

		virtual bool operator==(const MetadataElement& element) const =0;

		virtual bool operator!=(const MetadataElement& element) const =0;

	protected:

		template<typename T> static const T* castElement(const MetadataElement* element, Type type);

		template<typename T> static T* castElement(MetadataElement* element, Type type);

		template<typename T> static const T& castElement(const MetadataElement& element, Type type);

		template<typename T> static T& castElement(MetadataElement& element, Type type);
};

/**
  * Base class for types of metadata that may be cloned.
  * These classes are expected to be immutable as well.
*/

class CloneableMetadataElement : public MetadataElement {

	public:

		virtual CloneableMetadataElement* clone() const =0;

};

}
#endif

