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
#include <stdexcept>
#include <mutex>

#include "metadata/metadata_api.h"
#include "util/util_api.h"

namespace MR4C {

class MetadataMapImpl {

friend class MetadataMap;

	private:

		std::map<std::string,MetadataElement*> m_data;
		std::set<std::string> m_names;
		mutable std::recursive_mutex m_mutex;

		MetadataMapImpl() {}

		void putElement(const std::string& name, const CloneableMetadataElement& element) {
			putElement(name, element.clone());
		}

		void putElement(const std::string& name, MetadataElement* element) {
		    std::unique_lock<std::recursive_mutex> lock(m_mutex);
			m_data[name] = element;
			m_names.insert(name);
			lock.unlock();
		}

		MetadataElement* getElement(const std::string& name) const {
		    std::unique_lock<std::recursive_mutex> lock(m_mutex); // Released when out of scope
			if ( !contains(name) ) {
				MR4C_THROW( std::invalid_argument, "Name [" << name << "] not found in map");
			}
			return m_data.find(name)->second;
		}

		bool contains(const std::string& name) const {
		    std::unique_lock<std::recursive_mutex> lock(m_mutex); // Released when out of scope
			return m_data.count(name)!=0;

		}

		std::set<std::string> getAllNames() const {
			return m_names;
		}

		bool operator==(const MetadataMapImpl& map) const {
		    std::unique_lock<std::recursive_mutex> mylock(m_mutex, std::defer_lock); // Released when out of scope
		    std::unique_lock<std::recursive_mutex> otherlock(map.m_mutex, std::defer_lock); // Released when out of scope
		    std::lock(mylock, otherlock); // Locks both, prevents deadlocks
			return compareMapsOfPointers(m_data, map.m_data);

		}

		~MetadataMapImpl() {
			deleteMapOfPointers(m_data);
		}

};




MetadataMap::MetadataMap() {
	m_impl = new MetadataMapImpl();
}

void MetadataMap::putElement(const std::string& name, const CloneableMetadataElement& element) {
	m_impl->putElement(name,element);
}

void MetadataMap::putElement(const std::string& name, MetadataElement* element) {
	m_impl->putElement(name,element);
}

MetadataElement* MetadataMap::getElement(const std::string& name) const {
	return m_impl->getElement(name);
}

bool MetadataMap::contains(const std::string& name) const {
	return m_impl->contains(name);
}

std::set<std::string> MetadataMap::getAllNames() const {
	return m_impl->getAllNames();
}

MetadataElement::Type MetadataMap::getMetadataElementType() const {
	return MetadataElement::MAP;
}

bool MetadataMap::operator==(const MetadataMap& map) const {
	return *m_impl==*map.m_impl;
}


bool MetadataMap::operator==(const MetadataElement& element) const {
	if ( element.getMetadataElementType()!=MetadataElement::MAP) {
		return false;
	}
	const MetadataMap& map = castToMap(element);
	return operator==(map);
}

bool MetadataMap::operator!=(const MetadataMap& map) const {
	return !operator==(map);
}

bool MetadataMap::operator!=(const MetadataElement& element) const {
	return !operator==(element);
}



MetadataMap::~MetadataMap() {
	delete m_impl;
}

MetadataMap* MetadataMap::castToMap(MetadataElement* element) {
	return MetadataElement::castElement<MetadataMap>(element, MetadataElement::MAP);
}

const MetadataMap* MetadataMap::castToMap(const MetadataElement* element) {
	return MetadataElement::castElement<MetadataMap>(element, MetadataElement::MAP);
}

MetadataMap& MetadataMap::castToMap(MetadataElement& element) {
	return MetadataElement::castElement<MetadataMap>(element, MetadataElement::MAP);
}

const MetadataMap& MetadataMap::castToMap(const MetadataElement& element) {
	return MetadataElement::castElement<MetadataMap>(element, MetadataElement::MAP);
}

}
