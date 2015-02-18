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

#include "algorithm/algorithm_api.h"
#include "dataset/dataset_api.h"
#include "keys/keys_api.h"
#include "metadata/metadata_api.h"
#include "util/util_api.h"

namespace MR4C {

class AlgorithmConfigImpl {

	friend class AlgorithmConfig;

	private:

		std::map<std::string,std::string> m_params;
		std::set<std::string> m_paramNames;

		AlgorithmConfigImpl() {}

		AlgorithmConfigImpl(const std::map<std::string,std::string>& params) {
			initFrom(params);
		}

		AlgorithmConfigImpl(const AlgorithmConfigImpl& config) {
			initFrom(config);
		}

		void initFrom(const AlgorithmConfigImpl& config) {
			initFrom(config.m_params);
		}

		void initFrom(const std::map<std::string,std::string>& params) {
			m_params = params;
			m_paramNames = keySet(params);
		}

	
		std::set<std::string> getAllParamNames() const {
			return m_paramNames;
		}

		bool hasConfigParam(const std::string& name) const {
			return m_params.count(name)!=0;
		}

		std::string getConfigParam(const std::string& name) const {
			if ( !hasConfigParam(name) ) {
				MR4C_THROW( std::invalid_argument, "Parameter [" << name << "] not available in AlgorithmConfig");
			}
			return m_params.find(name)->second;
		}

		bool getConfigParamAsBoolean(const std::string& name) const {
			return Primitive::fromString<bool>(getConfigParam(name));
		}

		int getConfigParamAsInt(const std::string& name) const {
			return Primitive::fromString<int>(getConfigParam(name));
		}

		double getConfigParamAsDouble(const std::string& name) const {
			return Primitive::fromString<double>(getConfigParam(name));
		}

		bool operator==(const AlgorithmConfigImpl& config) const {
			return m_params==config.m_params && m_paramNames==m_paramNames;
		}

		~AlgorithmConfigImpl() {} 

};

AlgorithmConfig::AlgorithmConfig() {
	m_impl = new AlgorithmConfigImpl();
}

AlgorithmConfig::AlgorithmConfig(const std::map<std::string,std::string>& params) {
	m_impl = new AlgorithmConfigImpl(params);
}

AlgorithmConfig::AlgorithmConfig(const AlgorithmConfig& config) {
	m_impl = new AlgorithmConfigImpl(*config.m_impl);
}

std::set<std::string> AlgorithmConfig::getAllParamNames() const {
	return m_impl->getAllParamNames();
}

bool AlgorithmConfig::hasConfigParam(const std::string& name) const {
	return m_impl->hasConfigParam(name);
}

std::string AlgorithmConfig::getConfigParam(const std::string& name) const {
	return m_impl->getConfigParam(name);
}

bool AlgorithmConfig::getConfigParamAsBoolean(const std::string& name) const {
	return m_impl->getConfigParamAsBoolean(name);
}

int AlgorithmConfig::getConfigParamAsInt(const std::string& name) const {
	return m_impl->getConfigParamAsInt(name);
}

double AlgorithmConfig::getConfigParamAsDouble(const std::string& name) const {
	return m_impl->getConfigParamAsDouble(name);
}

bool AlgorithmConfig::operator==(const AlgorithmConfig& config) const {
	return *m_impl==*config.m_impl;
}

bool AlgorithmConfig::operator!=(const AlgorithmConfig& config) const {
	return !operator==(config);
}

AlgorithmConfig& AlgorithmConfig::operator=(const AlgorithmConfig& config) {
	m_impl->initFrom(*config.m_impl);
	return *this;
}

AlgorithmConfig::~AlgorithmConfig() {
	delete m_impl;
}

}
