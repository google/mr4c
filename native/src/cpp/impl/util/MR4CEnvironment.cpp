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
#include <cstdlib>
#include <iostream>
#include <stdexcept>
#include <mutex>

#include "util/util_api.h"

namespace MR4C {


class EnvSets {

	friend class MR4CEnvironment;
	friend class MR4CEnvironmentImpl;

	private:

		std::map<std::string,MR4CEnvironment::EnvSet> m_stringToEnum;
		std::map<MR4CEnvironment::EnvSet,std::string> m_enumToString;
		std::map<MR4CEnvironment::EnvSet,std::string> m_prefixes;

		static EnvSets& instance() {
			static EnvSets s_instance;
			return s_instance;
		}

		EnvSets() {
			mapEnvSet(MR4CEnvironment::RUNTIME, "RUNTIME", "mr4c.runtime.");
			mapEnvSet(MR4CEnvironment::JAVA, "JAVA", "mr4c.java.");
			mapEnvSet(MR4CEnvironment::CUSTOM, "CUSTOM", "mr4c.custom.");
			mapEnvSet(MR4CEnvironment::RAW, "RAW", "");
		}

		// making sure these are private
		EnvSets(const EnvSets& envSets);
		EnvSets& operator=(const EnvSets& envSets);

		void mapEnvSet(MR4CEnvironment::EnvSet envSet, const std::string& strEnvSet, const std::string& prefix) {
			m_stringToEnum[strEnvSet] = envSet;
			m_enumToString[envSet] = strEnvSet;
			m_prefixes[envSet] = prefix;
		}

		MR4CEnvironment::EnvSet enumFromString(std::string strEnvSet) {
			if ( m_stringToEnum.count(strEnvSet)==0 ) {
				MR4C_THROW(std::invalid_argument, "No environment set named [" << strEnvSet << "]");
			}
			return m_stringToEnum[strEnvSet];
		}

		std::string enumToString(MR4CEnvironment::EnvSet envSet) {
			if ( m_enumToString.count(envSet)==0 ) {
				MR4C_THROW(std::invalid_argument, "No environment set enum = " << envSet);
			}
			return m_enumToString[envSet];
		}

		std::string getVariablePrefix(MR4CEnvironment::EnvSet envSet) {
			if ( m_prefixes.count(envSet)==0 ) {
				MR4C_THROW(std::invalid_argument, "No environment set enum = " << envSet);
			}
			return m_prefixes[envSet];
		}

		std::string getVariableName(MR4CEnvironment::EnvSet envSet, const std::string& name) {
			MR4C_RETURN_STRING(getVariablePrefix(envSet) << name);
		}

};


class MR4CEnvironmentImpl {


	friend class MR4CEnvironment;

	private:

		std::map<MR4CEnvironment::EnvSet,Properties> m_propSets;
		mutable std::mutex m_mutex;

		MR4CEnvironmentImpl() {
			m_propSets[MR4CEnvironment::RUNTIME] = Properties();
			m_propSets[MR4CEnvironment::JAVA] = Properties();
			m_propSets[MR4CEnvironment::CUSTOM] = Properties();
			m_propSets[MR4CEnvironment::RAW] = Properties();
		}

		void init() {
		}

		void addPropertySet(MR4CEnvironment::EnvSet envSet, const Properties& props) {
		    std::unique_lock<std::mutex> lock(m_mutex);
			Properties existing = m_propSets[envSet];
			existing.setAllProperties(props);
			m_propSets[envSet] = existing;

			std::set<std::string> names = props.getAllPropertyNames();
			std::set<std::string>::iterator iter = names.begin();
			for ( ; iter!=names.end(); iter++ ) {
				std::string name = *iter;
				std::string val = props.getProperty(name);
				std::string fullName = EnvSets::instance().getVariableName(envSet, name);
				setenv(fullName.c_str(), val.c_str(), true);
			}
			lock.unlock();
		}

		Properties getProperties(MR4CEnvironment::EnvSet envSet) const {
		    std::unique_lock<std::mutex> lock(m_mutex); // Released when out of scope
			if ( m_propSets.count(envSet)==0 ) {
				MR4C_THROW( std::invalid_argument, "No environment set eunm = " << envSet);
			}
			return m_propSets.find(envSet)->second;
		}

		~MR4CEnvironmentImpl() {} 

};


MR4CEnvironment& MR4CEnvironment::instance() {
	static MR4CEnvironment s_instance;
	return s_instance;
}

MR4CEnvironment::MR4CEnvironment() {
	m_impl = new MR4CEnvironmentImpl();
}

MR4CEnvironment::~MR4CEnvironment() {
	delete m_impl;
} 

MR4CEnvironment::EnvSet MR4CEnvironment::enumFromString(const std::string& strEnvSet) {
	return EnvSets::instance().enumFromString(strEnvSet);
}

std::string MR4CEnvironment::enumToString(EnvSet envSet) {
	return EnvSets::instance().enumToString(envSet);
}

void MR4CEnvironment::addPropertySet(EnvSet envSet, const Properties& props) {
	m_impl->addPropertySet(envSet, props);
}

Properties MR4CEnvironment::getProperties(EnvSet envSet) const {
	return m_impl->getProperties(envSet);
}

std::string MR4CEnvironment::getVariablePrefix(EnvSet envSet) {
	return EnvSets::instance().getVariablePrefix(envSet);
}

std::string MR4CEnvironment::getVariableName(EnvSet envSet, const std::string& name) {
	return EnvSets::instance().getVariableName(envSet, name);
}


}
