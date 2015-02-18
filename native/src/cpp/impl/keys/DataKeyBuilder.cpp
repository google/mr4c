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

#include <set>
#include <mutex>

namespace MR4C {

class DataKeyBuilderImpl {

	friend class DataKeyBuilder;

	private:

		std::set<DataKeyElement> m_elements; 
		mutable std::mutex m_mutex;

		DataKeyBuilderImpl() {
			init();
		}
		
		DataKeyBuilderImpl(const DataKeyBuilderImpl& builder) {
			initFrom(builder);
		}
		
		void init() {
			m_elements.clear();
		}
		
		void initFrom(const DataKeyBuilderImpl& builder) {
			initFrom(builder.m_elements);
		}
		
		void initFrom(const std::set<DataKeyElement>& elements) {
			init();
			addElements(elements);
		}

		void addElement(const DataKeyElement& element) {
		    std::unique_lock<std::mutex> lock(m_mutex);
			m_elements.insert(element);
			lock.unlock();
		}
		
		void addElements(const std::set<DataKeyElement>& elements) {
		    std::unique_lock<std::mutex> lock(m_mutex);
			m_elements.insert(elements.begin(), elements.end());
			lock.unlock();
		}
		
		void addAllElements(const DataKey& key) {
			addElements(key.getElements());
		}
		
		DataKey toKey() const {
		    std::unique_lock<std::mutex> lock(m_mutex); // Released when out of scope
			return DataKey(m_elements);
		}
		
		~DataKeyBuilderImpl() {}
		
};

DataKeyBuilder::DataKeyBuilder() {
	m_impl = new DataKeyBuilderImpl();
}

DataKeyBuilder::DataKeyBuilder(const DataKeyBuilder& builder) {
	m_impl = new DataKeyBuilderImpl(*builder.m_impl);
}

void DataKeyBuilder::addElement(const DataKeyElement& element) {
	m_impl->addElement(element);
}

void DataKeyBuilder::addElements(const std::set<DataKeyElement>& elements) {
	m_impl->addElements(elements);
}

void DataKeyBuilder::addAllElements(const DataKey& key) {
	m_impl->addAllElements(key);
}

DataKey DataKeyBuilder::toKey() const {
	return m_impl->toKey();
}

DataKeyBuilder::~DataKeyBuilder() {
	delete m_impl;
}

DataKeyBuilder& DataKeyBuilder::operator=(const DataKeyBuilder& builder) {
	m_impl->initFrom(*builder.m_impl);
	return *this;
}

}
