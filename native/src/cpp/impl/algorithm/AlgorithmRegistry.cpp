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

#include <vector>
#include <iostream>
#include <stdexcept>
#include <log4cxx/logger.h>

#include "algorithm/algorithm_api.h"
#include "util/util_api.h"

using log4cxx::LoggerPtr;

namespace MR4C {

class AlgorithmRegistryImpl {

	friend class AlgorithmRegistry;

	private:

		std::map<std::string,Algorithm*> m_algos;

		AlgorithmRegistryImpl() {}

		LoggerPtr getLogger() const {
			// NOTE: doing this on-demand to avoid initialization contention
			return MR4CLogging::getLogger("algorithm.AlgorithmRegistry");
		}

		void registerAlgorithm(const std::string& name, Algorithm* algorithm) {
			if (hasAlgorithm(name)) { 
				LOG4CXX_ERROR(getLogger(), "Already have a registered algorithm named [" << name << "]");
				MR4C_THROW( std::invalid_argument, "Already have a registered algorithm named [" << name << "]");
			}
			m_algos[name] = algorithm;
			LOG4CXX_INFO(getLogger(), "Registered algorithm [" << name << "]");
		}

		bool hasAlgorithm(const std::string& name) const {
			return m_algos.count(name)!=0;
		}

		Algorithm* getAlgorithm(const std::string& name) const {
			if ( !hasAlgorithm(name) ) {
				LOG4CXX_ERROR(getLogger(), "No algorithm named [" << name << "] is registered");
				MR4C_THROW( std::invalid_argument, "No algorithm named [" << name << "] is registered");
			}
			return m_algos.find(name)->second;
		}

		~AlgorithmRegistryImpl() {} 

};


AlgorithmRegistry& AlgorithmRegistry::instance() {
	static AlgorithmRegistry s_instance;
	return s_instance;
}

AlgorithmRegistry::AlgorithmRegistry() {
	m_impl = new AlgorithmRegistryImpl();
}

AlgorithmRegistry::~AlgorithmRegistry() {
	delete m_impl;
} 

void AlgorithmRegistry::registerAlgorithm(const std::string& name, Algorithm* algorithm) {
	m_impl->registerAlgorithm(name, algorithm);
}

bool AlgorithmRegistry::hasAlgorithm(const std::string& name) const {
	return m_impl->hasAlgorithm(name);
}

Algorithm* AlgorithmRegistry::getAlgorithm(const std::string& name) const {
	return m_impl->getAlgorithm(name);
}


}
