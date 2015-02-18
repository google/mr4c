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
#include <map>
#include <string>
#include <stdexcept>
#include <mutex>

#include "util/util_api.h"

namespace MR4C {

class PropertiesImpl {

	friend class Properties;

	private:

		std::map<std::string,std::string> m_propMap;
		std::set<std::string> m_propNames;
		mutable std::recursive_mutex m_mutex;

		PropertiesImpl() {}

		PropertiesImpl(const std::map<std::string,std::string>& propMap) {
			initFrom(propMap);
		}

		PropertiesImpl(const PropertiesImpl& props) {
			initFrom(props);
		}

		void initFrom(const PropertiesImpl& props) {
			initFrom(props.m_propMap);
		}

		void initFrom(const std::map<std::string,std::string>& propMap) {
			m_propMap = propMap;
			m_propNames = keySet(propMap);
		}

	
		std::set<std::string> getAllPropertyNames() const {
			return m_propNames;
		}

		std::map<std::string,std::string> getPropertyMap() const {
			return m_propMap;
		}

		bool hasProperty(const std::string& name) const {
		    std::unique_lock<std::recursive_mutex> lock(m_mutex); // Released when out of scope
			return m_propMap.count(name)!=0;
		}

		std::string getProperty(const std::string& name) const {
		    std::unique_lock<std::recursive_mutex> lock(m_mutex); // Released when out of scope
			if ( !hasProperty(name) ) {
				MR4C_THROW( std::invalid_argument, "Property [" << name << "] not available in Properties");
			}
			return m_propMap.find(name)->second;
		}

		void setProperty(const std::string& name, const std::string& value) {
		    std::unique_lock<std::recursive_mutex> lock(m_mutex);
			m_propMap[name]=value;
			m_propNames.insert(name);
			lock.unlock();
		}


		void setAllProperties(const Properties& props) {
		    std::unique_lock<std::recursive_mutex> lock(m_mutex);
			mapAddAll(&m_propMap, props.getPropertyMap());
			std::set<std::string> newNames = props.getAllPropertyNames();
			m_propNames.insert(newNames.begin(), newNames.end());
			lock.unlock();
		}

		void setAllProperties(const std::map<std::string,std::string>& propMap) {
			setAllProperties(Properties(propMap));
		}


		bool operator==(const PropertiesImpl& props) const {
		    std::unique_lock<std::recursive_mutex> mylock(m_mutex, std::defer_lock); // Released when out of scope
		    std::unique_lock<std::recursive_mutex> otherlock(props.m_mutex, std::defer_lock); // Released when out of scope
		    std::lock(mylock, otherlock); // Acquires both, prevents deadlocks
			return m_propMap==props.m_propMap && m_propNames==m_propNames;
		}

		~PropertiesImpl() {} 

};

Properties::Properties() {
	m_impl = new PropertiesImpl();
}

Properties::Properties(const std::map<std::string,std::string>& propMap) {
	m_impl = new PropertiesImpl(propMap);
}

Properties::Properties(const Properties& props) {
	m_impl = new PropertiesImpl(*props.m_impl);
}

std::set<std::string> Properties::getAllPropertyNames() const {
	return m_impl->getAllPropertyNames();
}

std::map<std::string,std::string> Properties::getPropertyMap() const {
	return m_impl->getPropertyMap();
}

bool Properties::hasProperty(const std::string& name) const {
	return m_impl->hasProperty(name);
}

std::string Properties::getProperty(const std::string& name) const {
	return m_impl->getProperty(name);
}

void Properties::setProperty(const std::string& name, const std::string& value) {
	m_impl->setProperty(name,value);
}

void Properties::setAllProperties(const Properties& props) {
	m_impl->setAllProperties(props);
}

void Properties::setAllProperties(const std::map<std::string,std::string>& propMap) {
	m_impl->setAllProperties(propMap);
}

bool Properties::operator==(const Properties& props) const {
	return *m_impl==*props.m_impl;
}

bool Properties::operator!=(const Properties& props) const {
	return !operator==(props);
}

Properties& Properties::operator=(const Properties& props) {
	m_impl->initFrom(*props.m_impl);
	return *this;
}

Properties::~Properties() {
	delete m_impl;
}

}
