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
#include <map>
#include <vector>
#include <mutex>

namespace MR4C {

class KeyspaceBuilderImpl {

	friend class KeyspaceBuilder;

	private:

		std::set<DataKeyDimension> m_dims; 
		std::map<DataKeyDimension,std::set<DataKeyElement>*> m_dimMap;
		mutable std::mutex m_mutex;

		KeyspaceBuilderImpl() {
			init();
		}
		
		void init() {
			m_dims.clear();
			m_dimMap.clear();
		}
	
		void addKey(const DataKey& key) {
			std::set<DataKeyElement> elements = key.getElements();
			std::set<DataKeyElement>::iterator iter = elements.begin();
			for ( ; iter!=elements.end(); iter++ ) {
				addElement(*iter);
			}
		}

		void addKeys(const std::set<DataKey>& keys) {
			std::set<DataKey>::iterator iter = keys.begin();
			for ( ; iter!=keys.end(); iter++ ) {
				addKey(*iter);
			}
		}

		void addElement(DataKeyElement element) {
			DataKeyDimension dim = element.getDimension();
			std::set<DataKeyElement>* dimElements = NULL;
			std::unique_lock<std::mutex> lock(m_mutex);
			if ( m_dimMap.count(dim)==0 ) {
				m_dims.insert(dim);
				dimElements = new std::set<DataKeyElement>();
				m_dimMap[dim] = dimElements;
			} else {
				dimElements = m_dimMap.find(dim)->second;
			}
			dimElements->insert(element);
			lock.unlock();
		}
	
		
		Keyspace toKeyspace() const {
		    std::unique_lock<std::mutex> lock(m_mutex);
			std::set<KeyspaceDimension> ksds;
			std::set<DataKeyDimension>::iterator iter = m_dims.begin();
			for ( ; iter!=m_dims.end(); iter++ ) {
				DataKeyDimension dim = *iter;
				std::set<DataKeyElement>* elements = m_dimMap.find(dim)->second;
				std::vector<DataKeyElement> orderedElements(elements->begin(), elements->end());
				KeyspaceDimension ksd(dim,orderedElements);
				ksds.insert(ksd);
			}	
			lock.unlock();
			return Keyspace(ksds);
		}
		
		~KeyspaceBuilderImpl() {
			// freeing the sets we used
			std::map<DataKeyDimension,std::set<DataKeyElement>*>::iterator iter = m_dimMap.begin();
			for ( ; iter!=m_dimMap.end(); iter++ ) {
				delete iter->second;
			}

		}
		
};

KeyspaceBuilder::KeyspaceBuilder() {
	m_impl = new KeyspaceBuilderImpl();
}

void KeyspaceBuilder::addKey(const DataKey& key) {
	m_impl->addKey(key);
}

void KeyspaceBuilder::addKeys(const std::set<DataKey>& keys) {
	m_impl->addKeys(keys);
}

Keyspace KeyspaceBuilder::toKeyspace() const {
	return m_impl->toKeyspace();
}

KeyspaceBuilder::~KeyspaceBuilder() {
	delete m_impl;
}

}
