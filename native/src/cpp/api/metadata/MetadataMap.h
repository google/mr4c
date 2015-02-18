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

#ifndef __MR4C_METADATA_MAP_H__
#define __MR4C_METADATA_MAP_H__

#include <set>
#include <string>

#include "MetadataElement.h"

namespace MR4C {

class MetadataMapImpl;

/**
  * A map of names to arbitrary metadata elements
*/

class MetadataMap : public MetadataElement {

	public:

		MetadataMap();

		/**
		  * Assign an element to a name.
		  * This class makes its own copy of the element.
		  * The element may be disposed of after the call returns
		*/
		void putElement(const std::string& name, const CloneableMetadataElement& element);

		/**
		  * Assign an element to a name.
		  * The element must be allocated on the heap with new.
		  * This class takes ownership of the element and is responsible
		  * for deleting it.
		*/
		void putElement(const std::string& name, MetadataElement* element);

		/**
		  * Returns a pointer to this object's internal copy of the element
		*/
		MetadataElement* getElement(const std::string& name) const;
	
		bool contains(const std::string& name) const;

		size_t getSize() const;

		std::set<std::string> getAllNames() const;

		MetadataElement::Type getMetadataElementType() const;

		bool operator==(const MetadataMap& map) const;

		bool operator==(const MetadataElement& element) const;

		bool operator!=(const MetadataMap& map) const;

		bool operator!=(const MetadataElement& element) const;

		~MetadataMap();

		static const MetadataMap* castToMap(const MetadataElement* element);

		static MetadataMap* castToMap(MetadataElement* element);

		static const MetadataMap& castToMap(const MetadataElement& element);

		static MetadataMap& castToMap(MetadataElement& element);

	private:

		MetadataMapImpl* m_impl;

		// prevent calling these
		MetadataMap(const MetadataMap& map);
		MetadataMap& operator=(const MetadataMap& map);

};

}
#endif

