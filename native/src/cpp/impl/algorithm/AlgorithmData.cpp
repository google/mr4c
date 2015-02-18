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
#include <string>
#include <stdexcept>
#include <iostream>
#include <log4cxx/logger.h>

#include "algorithm/algorithm_api.h"
#include "dataset/dataset_api.h"
#include "keys/keys_api.h"
#include "util/util_api.h"

using log4cxx::LoggerPtr;

namespace MR4C {

class AlgorithmDataImpl {

	friend class AlgorithmData;

	private:

		LoggerPtr m_logger;
		Keyspace m_keyspace;
		AlgorithmConfig m_config;
		std::map<std::string,Dataset*> m_input;
		std::set<std::string> m_inputNames;
		std::map<std::string,Dataset*> m_output;
		std::set<std::string> m_outputNames;

		AlgorithmDataImpl() {
			init();
		}

		void init() {
			m_logger = MR4CLogging::getLogger("algorithm.AlgorithmData");
		}

		void setKeyspace(const Keyspace& keyspace) {
			m_keyspace = keyspace;
		}

		const Keyspace& getKeyspace() const {
			return m_keyspace;
		}

		void generateKeyspaceFromInputDatasets() {
			LOG4CXX_INFO(m_logger, "Generating keyspace from input datasets");
			KeyspaceBuilder builder;
			std::map<std::string,Dataset*>::iterator iter = m_input.begin();
			for ( ; iter!=m_input.end(); iter++ ) {
				Dataset* dataset = iter->second;
				builder.addKeys(dataset->getAllFileKeys());
				builder.addKeys(dataset->getAllMetadataKeys());
			}
			m_keyspace = builder.toKeyspace();
		}

		void setConfig(const AlgorithmConfig& config) {
			m_config = config;
		}
			
		const AlgorithmConfig& getConfig() const {
			return m_config;
		}
		
		void addInputDataset(const std::string& name, Dataset* dataset) {
			if ( hasInputDataset(name) ) {
				MR4C_THROW(std::invalid_argument, "Already have an input dataset named [" << name << "]");
			}
			m_input[name] = dataset;
			m_inputNames.insert(name);
			dataset->setGlobalKeyspace(m_keyspace);
			dataset->generateKeyspaces();
		}

		void addOutputDataset(const std::string& name, Dataset* dataset) {
			if ( hasOutputDataset(name) ) {
				MR4C_THROW(std::invalid_argument, "Already have an output dataset named [" << name << "]");
			}
			m_output[name] = dataset;
			m_outputNames.insert(name);
		}

		Dataset* getInputDataset(const std::string& name) const {
			if ( !hasInputDataset(name) ) {
				LOG4CXX_ERROR(m_logger, "No input dataset named [" << name <<"]");
				MR4C_THROW( std::invalid_argument, "No input dataset named [" << name <<"]");
			}
			return m_input.find(name)->second;
		}
	
		bool hasInputDataset(const std::string& name) const {
			return m_input.count(name)!=0;
		}
		
		Dataset* getOutputDataset(const std::string& name) const {
			if ( !hasOutputDataset(name) ) {
				LOG4CXX_ERROR(m_logger, "No output dataset named [" << name <<"]");
				MR4C_THROW( std::invalid_argument, "No output dataset named [" << name <<"]");
			}
			return m_output.find(name)->second;
		}

		bool hasOutputDataset(const std::string& name) const {
			return m_output.count(name)!=0;
		}
		
		std::set<std::string> getInputDatasetNames() const {
			return m_inputNames;
		}

		std::set<std::string> getOutputDatasetNames() const {
			return m_outputNames;
		}


		bool operator==(const AlgorithmDataImpl& algoData) const {
			if ( m_keyspace!=algoData.m_keyspace) return false;
			if ( m_config!=algoData.m_config) return false;
			if ( !compareMapsOfPointers(m_input, algoData.m_input) ) return false;
			if ( !compareMapsOfPointers(m_output, algoData.m_output) ) return false;
			return true;
		}

		~AlgorithmDataImpl() {
			deleteMapOfPointers(m_input);
			deleteMapOfPointers(m_output);
		} 

};

AlgorithmData::AlgorithmData() {
	m_impl = new AlgorithmDataImpl();
}

AlgorithmData::~AlgorithmData() {
	delete m_impl;
} 

void AlgorithmData::setKeyspace(const Keyspace& keyspace) {
	m_impl->setKeyspace(keyspace);
}

const Keyspace& AlgorithmData::getKeyspace() const {
	return m_impl->getKeyspace();
}

void AlgorithmData::generateKeyspaceFromInputDatasets() {
	m_impl->generateKeyspaceFromInputDatasets();
}

void AlgorithmData::setConfig(const AlgorithmConfig& config) {
	m_impl->setConfig(config);
}

const AlgorithmConfig& AlgorithmData::getConfig() const {
	return m_impl->getConfig();
}

void AlgorithmData::addInputDataset(const std::string& name, Dataset* dataset) {
	m_impl->addInputDataset(name, dataset);
}

void AlgorithmData::addOutputDataset(const std::string& name, Dataset* dataset) {
	m_impl->addOutputDataset(name, dataset);
}

Dataset* AlgorithmData::getInputDataset(const std::string& name) const {
	return m_impl->getInputDataset(name);
}

bool AlgorithmData::hasInputDataset(const std::string& name) const {
	return m_impl->hasInputDataset(name);
}

Dataset* AlgorithmData::getOutputDataset(const std::string& name) const {
	return m_impl->getOutputDataset(name);
}

bool AlgorithmData::hasOutputDataset(const std::string& name) const {
	return m_impl->hasOutputDataset(name);
}

std::set<std::string> AlgorithmData::getInputDatasetNames() const {
	return m_impl->getInputDatasetNames();
}

std::set<std::string> AlgorithmData::getOutputDatasetNames() const {
	return m_impl->getOutputDatasetNames();
}

bool AlgorithmData::operator==(const AlgorithmData& algoData) const {
	return *m_impl==*algoData.m_impl;
}

bool AlgorithmData::operator!=(const AlgorithmData& algoData) const {
	return !operator==(algoData);
}

}
