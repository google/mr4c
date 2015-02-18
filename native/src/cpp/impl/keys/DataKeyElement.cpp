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


#include "keys/keys_api.h"
#include "util/util_api.h"

namespace MR4C {

class DataKeyElementImpl {

	friend class DataKeyElement;

	private:
		std::string m_id;
		DataKeyDimension m_dim;

		DataKeyElementImpl(const std::string& id, const DataKeyDimension& dim) {
			init();
			m_id = id;
			m_dim = dim;
		}

		DataKeyElementImpl(const DataKeyElementImpl& element) {
			initFrom(element);
		}

		DataKeyElementImpl() {
			init();
		}


		void init() {
		}

		void initFrom(const DataKeyElementImpl&  element) {
			init();
			m_id = element.m_id;
			m_dim = element.m_dim;
		}

		bool operator==(const DataKeyElementImpl& element) const {
			return m_dim==element.m_dim && m_id==element.m_id;
		}

		bool operator<(const DataKeyElementImpl& element) const {
			if ( m_dim==element.m_dim ) {
				return m_id<element.m_id;
			} else {
				return m_dim<element.m_dim;
			}
		}

		std::string str() const {
			MR4C_RETURN_STRING(m_dim << "=" << m_id);
		}

		~DataKeyElementImpl() {}
};


DataKeyElement::DataKeyElement(const std::string& id, const DataKeyDimension& dim) {
	m_impl = new DataKeyElementImpl(id, dim);
}

DataKeyElement::DataKeyElement(const DataKeyElement& element) {
	m_impl = new DataKeyElementImpl(*element.m_impl);
}

DataKeyElement::DataKeyElement() {
	m_impl = new DataKeyElementImpl();
}

std::string DataKeyElement::getIdentifier() const {
	return m_impl->m_id;
}
		
DataKeyDimension DataKeyElement::getDimension() const {
	return m_impl->m_dim;
}
		

DataKeyElement& DataKeyElement::operator=(const DataKeyElement& element) {
	m_impl->initFrom(*element.m_impl);
	return *this;
}

bool DataKeyElement::operator==(const DataKeyElement& element) const {
	return *m_impl==*element.m_impl;
}

bool DataKeyElement::operator!=(const DataKeyElement&  element) const {
	return !operator==(element);
}

bool DataKeyElement::operator<(const DataKeyElement&  element) const {
	return *m_impl<*element.m_impl;
}

std::string DataKeyElement::str() const {
	return m_impl->str();
}

std::vector<std::string> DataKeyElement::toElementIds(const std::vector<DataKeyElement>& elements) {
	std::vector<std::string> ids;
	std::vector<DataKeyElement>::const_iterator iter = elements.begin();
	for ( ; iter!=elements.end(); iter++ ) {
		ids.push_back(iter->getIdentifier());
	}
	return ids;
}
			
DataKeyElement::~DataKeyElement() {
	delete m_impl;
}

std::ostream& operator<<(std::ostream& os, const DataKeyElement& element) {
	os << element.str();
	return os;
}


}
