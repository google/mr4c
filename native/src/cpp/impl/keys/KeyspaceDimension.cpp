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
#include <vector>
#include <stdexcept>
#include "keys/keys_api.h"
#include "util/util_api.h"

namespace MR4C {

class KeyspaceDimensionImpl {

	friend class KeyspaceDimension;

	private :

		DataKeyDimension m_dim; 
		std::vector<DataKeyElement> m_elements; 

		KeyspaceDimensionImpl() {
			init();
		}

		KeyspaceDimensionImpl(const DataKeyDimension& dim, const std::vector<DataKeyElement>& elements) {
			initFrom(dim, elements);
		}

		KeyspaceDimensionImpl(const KeyspaceDimensionImpl& ksd) {
			initFrom(ksd);
		}

		void init() {
			m_elements.clear();
		}

		void initFrom(const KeyspaceDimensionImpl& ksd) {
			initFrom(ksd.getDimension(), ksd.getElements());
		}

		void initFrom(const DataKeyDimension& dim, const std::vector<DataKeyElement>& elements) {
			init();
			m_dim = dim;
			std::vector<DataKeyElement>::const_iterator iter = elements.begin();
			for ( ; iter!=elements.end(); iter++ ) {
				addElement(*iter);
			}
		}


		DataKeyDimension getDimension() const {
			return m_dim;
		}
		
		std::vector<DataKeyElement> getElements() const {
			return m_elements;
		}
		
		size_t getElementCount() const {
			return m_elements.size();
		}

		DataKeyElement getElement(size_t index) const {
			return m_elements.at(index);
		}
		
		~KeyspaceDimensionImpl() {}
		
		bool operator==(const KeyspaceDimensionImpl& ksd) const {
			return m_dim==ksd.m_dim && m_elements==ksd.m_elements;
		}
		
		bool operator<(const KeyspaceDimensionImpl& ksd) const {
			if ( m_dim==ksd.m_dim ) {
				return m_elements<ksd.m_elements;
			} else {
				return m_dim<ksd.m_dim;
			}
		}

		std::string str() const {
			MR4C_RETURN_STRING("dimension [" << m_dim << "] : [" + vectorToString(DataKeyElement::toElementIds(m_elements), ",") + "]");
		}
	
		void addElement(const DataKeyElement& element) {
			if ( m_dim!=element.getDimension() ) {
				MR4C_THROW( std::invalid_argument, "Tried to add element with dimension [" << element.getDimension().getName() << "] to KeyspaceDimension for dimension [" << m_dim.getName() << "]");
			}
			m_elements.push_back(element);
		}
		
};

KeyspaceDimension::KeyspaceDimension() {
	m_impl = new KeyspaceDimensionImpl();

}

KeyspaceDimension::KeyspaceDimension(const KeyspaceDimension& ksd) {
	m_impl = new KeyspaceDimensionImpl(*ksd.m_impl);
}

KeyspaceDimension::KeyspaceDimension(const DataKeyDimension& dim, const std::vector<DataKeyElement>& elements) {
	m_impl = new KeyspaceDimensionImpl(dim, elements);
}

DataKeyDimension KeyspaceDimension::getDimension() const {
	return m_impl->getDimension();
}

std::vector<DataKeyElement> KeyspaceDimension::getElements() const {
	return m_impl->getElements();
}

size_t KeyspaceDimension::getElementCount() const {
	return m_impl->getElementCount();
}

DataKeyElement KeyspaceDimension::getElement(size_t index) const {
	return m_impl->getElement(index);
}

std::string KeyspaceDimension::str() const {
	return m_impl->str();
}

KeyspaceDimension::~KeyspaceDimension() {
	delete m_impl;
}

KeyspaceDimension& KeyspaceDimension::operator=(const KeyspaceDimension& ksd) {
	m_impl->initFrom(*ksd.m_impl);
	return *this;
}

bool KeyspaceDimension::operator==(const KeyspaceDimension& ksd) const {
	return *m_impl==*ksd.m_impl;
}

bool KeyspaceDimension::operator!=(const KeyspaceDimension& ksd) const {
	return !operator==(ksd);
}

bool KeyspaceDimension::operator<(const KeyspaceDimension& ksd) const {
	return *m_impl<*ksd.m_impl;
}

std::ostream& operator<<(std::ostream& os, const KeyspaceDimension& ksd) {
	os << ksd.str();
	return os;
}



}
