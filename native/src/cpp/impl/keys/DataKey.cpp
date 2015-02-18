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
#include <sstream>
#include <map>
#include <set>
#include <stdexcept>

#include "keys/keys_api.h"
#include "util/util_api.h"

namespace MR4C {

class DataKeyImpl {

	friend class DataKey;

	std::set<DataKeyDimension> m_dimensions; 
	std::set<DataKeyElement> m_elements; 
	std::map<DataKeyDimension,DataKeyElement> m_map; 

		DataKeyImpl() {
			init();
		}

		DataKeyImpl(const DataKeyElement& element) {
			init();
			addElement(element);
		}

		DataKeyImpl(
			const DataKeyElement& element1,
			const DataKeyElement& element2
		) {
			init();
			addElement(element1);
			addElement(element2);
		}

		DataKeyImpl(
			const DataKeyElement& element1,
			const DataKeyElement& element2,
			const DataKeyElement& element3
		) {
			addElement(element1);
			addElement(element2);
			addElement(element3);
		}

		DataKeyImpl(const std::set<DataKeyElement>& elements) {
			initFrom(elements);
		}

		DataKeyImpl(const DataKeyImpl& key) {
			initFrom(key);
		}

		void init() {
			m_map.clear();
			m_elements.clear();
			m_dimensions.clear();
		}

		void initFrom(const DataKeyImpl& key) {
			initFrom(key.getElements());
		}

		void initFrom(const std::set<DataKeyElement>& elements) {
			init();
			std::set<DataKeyElement>::iterator iter = elements.begin();
			for ( ; iter!=elements.end(); iter++ ) {
				addElement(*iter);
			}
		}


		std::set<DataKeyDimension> getDimensions() const {
			return m_dimensions;
		}
		
		size_t getElementCount() const {
			return m_elements.size();
		}
		
		bool hasDimension(const DataKeyDimension& dim) const {
			return (m_dimensions.find(dim)!=m_dimensions.end() );
		}
		
		bool hasElement(const DataKeyElement& element) const {
			return (m_elements.find(element)!=m_elements.end() );
		}
		
		std::set<DataKeyElement> getElements() const {
			return m_elements;
		}
		
		DataKeyElement getElement(const DataKeyDimension& dim) const {
			if ( !hasDimension(dim) ) {
				MR4C_THROW( std::invalid_argument, "Dimension [" << dim.getName() << "] not found in key");

			}
			return m_map.find(dim)->second;
		}
	
		std::string str() const {
			return setToString(m_elements, ",");
		}

		std::string toName(const std::string& delim) const {
			std::ostringstream ss;
			bool first=true;
			for ( std::set<DataKeyElement>::iterator iter = m_elements.begin(); iter!=m_elements.end(); iter++ ) {
				if ( first ) {
					first = false;
				} else {
					ss << "__"; 
				}
				ss << iter->getIdentifier();
			}
			return ss.str();
		}

		~DataKeyImpl() {}
		
		bool operator==(const DataKeyImpl& key) const {
			return m_elements==key.m_elements;
		}
		
		bool operator<(const DataKeyImpl& key) const {
			return m_elements<key.m_elements;
		}
		
		
		void addElement(const DataKeyElement& element) {
			DataKeyDimension dim = element.getDimension();
			if (m_dimensions.find(dim)!=m_dimensions.end() ) {
				MR4C_THROW( std::invalid_argument, "Tried to add two elements with dimension [" << dim.getName() << "] to key");
			}
			m_dimensions.insert(dim);
			m_elements.insert(element);
			m_map[dim] = element;
		}
		
};

DataKey::DataKey() {
	m_impl = new DataKeyImpl();

}

DataKey::DataKey(const DataKeyElement& element) {
	m_impl = new DataKeyImpl(element);
}

DataKey::DataKey(
	const DataKeyElement& element1,
	const DataKeyElement& element2
) {
	m_impl = new DataKeyImpl(element1, element2);
}

DataKey::DataKey(
	const DataKeyElement& element1,
	const DataKeyElement& element2,
	const DataKeyElement& element3
) {
	m_impl = new DataKeyImpl(element1, element2, element3);
}

DataKey::DataKey(const std::set<DataKeyElement>& elements) {
	m_impl = new DataKeyImpl(elements);
}

DataKey::DataKey(const DataKey& key) {
	m_impl = new DataKeyImpl(*key.m_impl);
}

std::set<DataKeyDimension> DataKey::getDimensions() const {
	return m_impl->getDimensions();
}

size_t DataKey::getElementCount() const {
	return m_impl->getElementCount();
}

bool DataKey::hasDimension(const DataKeyDimension& dim) const {
	return m_impl->hasDimension(dim);
}

bool DataKey::hasElement(const DataKeyElement& element) const {
	return m_impl->hasElement(element);
}

std::set<DataKeyElement> DataKey::getElements() const {
	return m_impl->getElements();
}

DataKeyElement DataKey::getElement(const DataKeyDimension& dim) const {
	return m_impl->getElement(dim);
}

std::string DataKey::str() const {
	return m_impl->str();
}

std::string DataKey::toName(const std::string& delim) const {
	return m_impl->toName(delim);
}

DataKey::~DataKey() {
	delete m_impl;
}

DataKey& DataKey::operator=(const DataKey& key) {
	m_impl->initFrom(*key.m_impl);
	return *this;
}

bool DataKey::operator==(const DataKey& key) const {
	return *m_impl==*key.m_impl;
}

bool DataKey::operator!=(const DataKey& key) const {
	return !operator==(key);
}

bool DataKey::operator<(const DataKey& key) const {
	return *m_impl<*key.m_impl;
}

std::ostream& operator<<(std::ostream& os, const DataKey& key) {
	os << key.str();
	return os;
}


}
