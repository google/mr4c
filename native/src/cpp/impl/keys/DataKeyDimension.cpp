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

namespace MR4C {

class DataKeyDimensionImpl {

	friend class DataKeyDimension;

	private:

		std::string m_name;

		DataKeyDimensionImpl() {
			init();
		}

		DataKeyDimensionImpl(const DataKeyDimensionImpl& dim) {
			initFrom(dim);
		}

		DataKeyDimensionImpl(const std::string &name) {
			init();
			m_name = name;
		}

		void init() {}

		void initFrom(const DataKeyDimensionImpl& dim) {
			init();
			m_name = dim.m_name;
		}

		bool operator==(const DataKeyDimensionImpl& dim) const {
			return m_name==dim.m_name;
		}

		bool operator<(const DataKeyDimensionImpl& dim) const {
			return m_name<dim.m_name;
		}

		~DataKeyDimensionImpl() {}

};

DataKeyDimension::DataKeyDimension() {
	m_impl = new DataKeyDimensionImpl();
}

DataKeyDimension::DataKeyDimension(const DataKeyDimension& dim) {
	m_impl = new DataKeyDimensionImpl(*dim.m_impl);
}

DataKeyDimension::DataKeyDimension(const std::string &name) {
	m_impl = new DataKeyDimensionImpl(name);
}

std::string DataKeyDimension::getName() const {
	return m_impl->m_name;
}

std::string DataKeyDimension::str() const {
	return m_impl->m_name;
}

DataKeyDimension& DataKeyDimension::operator=(const DataKeyDimension& dim) {
	m_impl->initFrom(*dim.m_impl);
	return *this;
}

bool DataKeyDimension::operator==(const DataKeyDimension& dim) const {
	return *m_impl==*dim.m_impl;
}

bool DataKeyDimension::operator!=(const DataKeyDimension& dim) const {
	return !operator==(dim);
}

bool DataKeyDimension::operator<(const DataKeyDimension& dim) const {
	return *m_impl<*dim.m_impl;
}

DataKeyDimension::~DataKeyDimension() {
	delete m_impl;
}

std::ostream& operator<<(std::ostream& os, const DataKeyDimension& dim) {
	os << dim.str();
	return os;
}

}

