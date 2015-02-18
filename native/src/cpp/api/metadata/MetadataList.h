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

#ifndef __MR4C_METADATA_LIST_H__
#define __MR4C_METADATA_LIST_H__

#include "MetadataElement.h"

namespace MR4C {

class MetadataListImpl;

/**
  * A sequential list of arbitrary metadata elements.
*/

class MetadataList : public MetadataElement {

	public:

		MetadataList();

		/**
		  * Add an element.
		  * This class makes its own copy of the element.
		  * The element may be disposed of after the call returns
		*/
		void addElement(const CloneableMetadataElement& element);

		/**
		  * Add an element.
		  * The element must be allocated on the heap with new.
		  * This class takes ownership of the element and is responsible
		  * for deleting it.
		*/
		void addElement(MetadataElement* element);

		/**
		  * Returns a pointer to this object's internal copy of the element
		*/
		MetadataElement* getElement(size_t index) const;

		size_t getSize() const;

		MetadataElement::Type getMetadataElementType() const;

		bool operator==(const MetadataList& list) const;

		bool operator==(const MetadataElement& element) const;

		bool operator!=(const MetadataList& list) const;

		bool operator!=(const MetadataElement& element) const;

		~MetadataList();

		static const MetadataList* castToList(const MetadataElement* element);

		static MetadataList* castToList(MetadataElement* element);

		static const MetadataList& castToList(const MetadataElement& element);

		static MetadataList& castToList(MetadataElement& element);


	private:

		MetadataListImpl* m_impl;

		// prevent calling these
		MetadataList(const MetadataList& list);
		MetadataList& operator=(const MetadataList& list);


};

}
#endif

