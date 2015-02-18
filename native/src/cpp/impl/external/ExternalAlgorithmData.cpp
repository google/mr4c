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
#include <string>

#include "external/external_api.h"
#include "util/util_api.h"

namespace MR4C {

class ExternalAlgorithmDataImpl {

	friend class ExternalAlgorithmData;

	private:

		std::string m_keyspace;
		std::string m_config;
		std::vector<ExternalDataset*> m_inputDatasets;
		std::vector<ExternalDataset*> m_outputDatasets;
		std::set<std::string> m_inputNames;
		std::set<std::string> m_outputNames;


		ExternalAlgorithmDataImpl() {}

		void setSerializedKeyspace(const char* keyspace) {
			m_keyspace = keyspace;
		}

		bool hasKeyspace() const {
			return !m_keyspace.empty();
		}

		const char* getSerializedKeyspace() const {
			return m_keyspace.c_str();
		}

		void setSerializedConfig(const char* config) {
			m_config = config;
		}

		bool hasConfig() const {
			return !m_config.empty();
		}

		const char* getSerializedConfig() const {
			return m_config.c_str();
		}

		void addInputDataset(ExternalDataset* dataset) {
			m_inputDatasets.push_back(dataset);
			m_inputNames.insert(dataset->getName());
		}

		ExternalDataset* getInputDataset(int index) const {
			return m_inputDatasets.at(index); 
		}

		size_t getInputDatasetCount() const {
			return m_inputDatasets.size();
		}

		bool hasInputDataset(const char* name) {
			return m_inputNames.count(name)!=0;
		}

		void addOutputDataset(ExternalDataset* dataset) {
			m_outputDatasets.push_back(dataset);
			m_outputNames.insert(dataset->getName());
		}

		ExternalDataset* getOutputDataset(int index) const {
			return m_outputDatasets.at(index); 
		}

		size_t getOutputDatasetCount() const {
			return m_outputDatasets.size();
		}

		bool hasOutputDataset(const char* name) {
			return m_outputNames.count(name)!=0;
		}

		~ExternalAlgorithmDataImpl() {
			deleteVectorOfPointers(m_inputDatasets);
			deleteVectorOfPointers(m_outputDatasets);
		} 

};


ExternalAlgorithmData::ExternalAlgorithmData() {
	m_impl = new ExternalAlgorithmDataImpl();
}

void ExternalAlgorithmData::setSerializedKeyspace(const char* keyspace) {
	m_impl-> setSerializedKeyspace(keyspace);
}

bool ExternalAlgorithmData::hasKeyspace() const {
	return m_impl->hasKeyspace();
}

const char* ExternalAlgorithmData::getSerializedKeyspace() const {
	return m_impl->getSerializedKeyspace();
}

void ExternalAlgorithmData::setSerializedConfig(const char* config) {
	m_impl-> setSerializedConfig(config);
}

bool ExternalAlgorithmData::hasConfig() const {
	return m_impl->hasConfig();
}

const char* ExternalAlgorithmData::getSerializedConfig() const {
	return m_impl->getSerializedConfig();
}

void ExternalAlgorithmData::addInputDataset(ExternalDataset* dataset) {
	m_impl->addInputDataset(dataset);
}

ExternalDataset* ExternalAlgorithmData::getInputDataset(int index) const {
	return m_impl->getInputDataset(index);
}

size_t ExternalAlgorithmData::getInputDatasetCount() const {
	return m_impl->getInputDatasetCount();
}

bool ExternalAlgorithmData::hasInputDataset(const char* name) {
	return m_impl->hasInputDataset(name);
}

void ExternalAlgorithmData::addOutputDataset(ExternalDataset* dataset) {
	m_impl->addOutputDataset(dataset);
}

ExternalDataset* ExternalAlgorithmData::getOutputDataset(int index) const {
	return m_impl->getOutputDataset(index);
}

size_t ExternalAlgorithmData::getOutputDatasetCount() const {
	return m_impl->getOutputDatasetCount();
}

bool ExternalAlgorithmData::hasOutputDataset(const char* name) {
	return m_impl->hasOutputDataset(name);
}

ExternalAlgorithmData::~ExternalAlgorithmData() {
	delete m_impl;
}

}

