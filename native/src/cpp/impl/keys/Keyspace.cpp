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
#include <set>
#include <stdexcept>
#include <iostream>
#include <log4cxx/logger.h>

#include "keys/keys_api.h"
#include "util/util_api.h"

using log4cxx::LoggerPtr;

namespace MR4C {

class KeyspaceImpl {

	friend class Keyspace;

	private :

		LoggerPtr m_logger;
		std::set<DataKeyDimension> m_dimensions; 
		std::set<KeyspaceDimension> m_ksds; 
		std::map<DataKeyDimension,KeyspaceDimension> m_map; 

		KeyspaceImpl() {
			init();
		}

		KeyspaceImpl(const std::set<KeyspaceDimension>& dims) {
			initFrom(dims);
		}

		KeyspaceImpl(const KeyspaceImpl& keyspace) {
			initFrom(keyspace);
		}

		void init() {
			m_logger = MR4CLogging::getLogger("dataset.Dataset");

			m_map.clear();
			m_ksds.clear();
			m_dimensions.clear();
		}

		void initFrom(const KeyspaceImpl& keyspace) {
			initFrom(keyspace.m_ksds);
		}

		void initFrom(const std::set<KeyspaceDimension>& dims) {
			init();
			std::set<KeyspaceDimension>::const_iterator iter = dims.begin();
			for ( ; iter!=dims.end(); iter++ ) {
				addDimension(*iter);
			}
		}


		std::set<DataKeyDimension> getDimensions() const {
			return m_dimensions;
		}
		
		size_t getDimensionCount() const {
			return m_ksds.size();
		}
		
		bool hasDimension(const DataKeyDimension& dim) const {
			return (m_dimensions.find(dim)!=m_dimensions.end() );
		}
	
		bool includesKey(const DataKey& key) const {
			std::set<DataKeyDimension> dims = key.getDimensions();
			std::set<DataKeyDimension>::const_iterator iter = dims.begin();
			std::set<DataKeyDimension>::const_iterator end = dims.end();
			for ( ; iter!=end; iter++ ) {
				if ( !hasDimension(*iter) ) {
					return false;
				}
			}
			return true;
		}

		void validateKey(const DataKey& key) const {
			if ( !includesKey(key) ) {
				LOG4CXX_ERROR(m_logger, "Keyspace does not include key [" << key.str() << "]; keyspace dimensions are [" << setToString(m_dimensions, ",") << "]");
				MR4C_THROW(std::invalid_argument, "Keyspace does not include key [" << key.str() << "]; keyspace dimensions are [" << setToString(m_dimensions, ",") << "]");
			}
		}
	
		KeyspaceDimension getKeyspaceDimension(const DataKeyDimension& dim) const {
			if ( !hasDimension(dim) ) {
				MR4C_THROW( std::invalid_argument, "Dimension [" << dim.getName() << "] not found in keyspace");
			}
			return m_map.find(dim)->second;
		}
		
		~KeyspaceImpl() {}
	
		std::string str() const {
			return setToString(m_ksds, ",");
		}
	
		bool operator==(const KeyspaceImpl& keyspace) const {
			return m_ksds==keyspace.m_ksds;
		}
		
		bool operator<(const KeyspaceImpl& keyspace) const {
			return m_ksds<keyspace.m_ksds;
		}
		
		
		void addDimension(const KeyspaceDimension& ksd) {
			DataKeyDimension dim = ksd.getDimension();
			if (m_dimensions.find(dim)!=m_dimensions.end() ) {
				MR4C_THROW( std::invalid_argument, "Tried to add two KeysapceDimension objects with dimension [" << dim.getName() << "] to Keyspace");
			}
			m_dimensions.insert(dim);
			m_ksds.insert(ksd);
			m_map[dim] = ksd;
		}
		
};

Keyspace::Keyspace() {
	m_impl = new KeyspaceImpl();

}

Keyspace::Keyspace(const std::set<KeyspaceDimension>& dims) {
	m_impl = new KeyspaceImpl(dims);
}

Keyspace::Keyspace(const Keyspace& keyspace) {
	m_impl = new KeyspaceImpl(*keyspace.m_impl);
}

std::set<DataKeyDimension> Keyspace::getDimensions() const {
	return m_impl->getDimensions();
}

size_t Keyspace::getDimensionCount() const {
	return m_impl->getDimensionCount();
}

bool Keyspace::hasDimension(const DataKeyDimension& dim) const {
	return m_impl->hasDimension(dim);
}

bool Keyspace::includesKey(const DataKey& key) const {
	return m_impl->includesKey(key);
}

void Keyspace::validateKey(const DataKey& key) const {
	return m_impl->validateKey(key);
}

KeyspaceDimension Keyspace::getKeyspaceDimension(const DataKeyDimension& dim) const {
	return m_impl->getKeyspaceDimension(dim);
}

std::string Keyspace::str() const {
	return m_impl->str();
}

Keyspace::~Keyspace() {
	delete m_impl;
}

Keyspace& Keyspace::operator=(const Keyspace& keyspace) {
	m_impl->initFrom(*keyspace.m_impl);
	return *this;
}

bool Keyspace::operator==(const Keyspace& keyspace) const {
	return *m_impl==*keyspace.m_impl;
}

bool Keyspace::operator!=(const Keyspace& keyspace) const {
	return !operator==(keyspace);
}

bool Keyspace::operator<(const Keyspace& keyspace) const {
	return *m_impl<*keyspace.m_impl;
}


std::ostream& operator<<(std::ostream& os, const Keyspace& keyspace) {
	os << keyspace.str();
	return os;
}

}
