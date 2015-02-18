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

#include <vector>
#include <stdexcept>
#include <mutex>

#include "metadata/metadata_api.h"
#include "util/util_api.h"

namespace MR4C {

class MetadataListImpl {

	friend class MetadataList;

	private:

		std::vector<MetadataElement*> m_data;
		mutable std::mutex m_mutex;


		MetadataListImpl() {}

		void addElement(const CloneableMetadataElement& element) {
			addElement(element.clone());
		}

		void addElement(MetadataElement* element) {
		    std::unique_lock<std::mutex> lock(m_mutex);
			m_data.push_back(element);
			lock.unlock();
		}

		MetadataElement* getElement(size_t index) const {
		    std::unique_lock<std::mutex> lock(m_mutex); // Released when out of scope
			return m_data.at(index);
		}

		size_t getSize() const {
		    std::unique_lock<std::mutex> lock(m_mutex); // Released when out of scope
			return m_data.size();
		}

		bool operator==(const MetadataListImpl& list) const {
		    std::unique_lock<std::mutex> mylock(m_mutex, std::defer_lock); // Released when out of scope
		    std::unique_lock<std::mutex> otherlock(list.m_mutex, std::defer_lock); // Released when out of scope
		    std::lock(mylock, otherlock); // Lock both, prevent deadlock
			return compareVectorsOfPointers(m_data, list.m_data);
		}

		~MetadataListImpl() {
			deleteVectorOfPointers(m_data);
		} 

};


MetadataList::MetadataList() {
	m_impl = new MetadataListImpl();
}

void MetadataList::addElement(const CloneableMetadataElement& element) {
	m_impl->addElement(element);
}

void MetadataList::addElement(MetadataElement* element) {
	m_impl->addElement(element);
}

MetadataElement* MetadataList::getElement(size_t index) const {
	return m_impl->getElement(index);
}

size_t MetadataList::getSize() const {
	return m_impl->getSize();
}

MetadataElement::Type MetadataList::getMetadataElementType() const {
	return MetadataElement::LIST;
}

bool MetadataList::operator==(const MetadataList& list) const {
	return *m_impl==*list.m_impl;
}

bool MetadataList::operator==(const MetadataElement& element) const {
	if ( element.getMetadataElementType()!=MetadataElement::LIST) {
		return false;
	}
	const MetadataList& list = castToList(element);
	return operator==(list);
}

bool MetadataList::operator!=(const MetadataList& list) const {
	return !operator==(list);
}

bool MetadataList::operator!=(const MetadataElement& element) const {
	return !operator==(element);
}

MetadataList::~MetadataList() {
	delete m_impl;
} 

MetadataList* MetadataList::castToList(MetadataElement* element) {
	return MetadataElement::castElement<MetadataList>(element, MetadataElement::LIST);
}

const MetadataList* MetadataList::castToList(const MetadataElement* element) {
	return MetadataElement::castElement<MetadataList>(element, MetadataElement::LIST);
}

MetadataList& MetadataList::castToList(MetadataElement& element) {
	return MetadataElement::castElement<MetadataList>(element, MetadataElement::LIST);
}

const MetadataList& MetadataList::castToList(const MetadataElement& element) {
	return MetadataElement::castElement<MetadataList>(element, MetadataElement::LIST);
}

}
