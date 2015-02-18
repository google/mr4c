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
#include <map>
#include <string>

#include "algorithm/algorithm_api.h"
#include "dataset/dataset_api.h"
#include "keys/keys_api.h"

namespace MR4C {

class AlgorithmImpl {

	friend class Algorithm;

	private:

		std::set<std::string> m_inputs;
		std::set<std::string> m_optionalInputs;
		std::set<std::string> m_requiredInputs;
		std::set<std::string> m_excludedInputs;
		std::set<std::string> m_outputs;
		std::set<DataKeyDimension> m_dims;

		AlgorithmImpl() {}

		void addInputDataset(const std::string& name, bool optional, bool excludeFromKeyspace) {
			m_inputs.insert(name);
			if ( optional ) {
				m_optionalInputs.insert(name);
			} else {
				m_requiredInputs.insert(name);
			}
			if ( excludeFromKeyspace ) {
				m_excludedInputs.insert(name);
			}
		}

		void addOutputDataset(const std::string& name) {
			m_outputs.insert(name);
		}

		std::set<std::string> getInputDatasets() const {
			return m_inputs;
		}

		std::set<std::string> getRequiredInputDatasets() const {
			return m_requiredInputs;
		}

		std::set<std::string> getOptionalInputDatasets() const {
			return m_optionalInputs;
		}

		std::set<std::string> getExcludedInputDatasets() const {
			return m_excludedInputs;
		}

		std::set<std::string> getOutputDatasets() const {
			return m_outputs;
		}

		bool isInputDatasetOptional(const std::string& name) const {
			return m_optionalInputs.count(name)!=0;
		}

		bool isInputDatasetExcluded(const std::string& name) const {
			return m_excludedInputs.count(name)!=0;
		}

		void addExpectedDimension(const DataKeyDimension& dim) {
			m_dims.insert(dim);
		}

		std::set<DataKeyDimension> getExpectedDimensions() const {
			return m_dims;
		}

		~AlgorithmImpl() {} 

};

Algorithm::Algorithm() {
	m_impl = new AlgorithmImpl();
}

Algorithm::~Algorithm() {
	delete m_impl;
} 

void Algorithm::addInputDataset(const std::string& name) {
	m_impl->addInputDataset(name, false, false);
}

void Algorithm::addInputDataset(const std::string& name, bool optional) {
	m_impl->addInputDataset(name, optional, false);
}

void Algorithm::addInputDataset(const std::string& name, bool optional, bool excludeFromKeyspace) {
	m_impl->addInputDataset(name, optional, excludeFromKeyspace);
}

void Algorithm::addOutputDataset(const std::string& name) {
	m_impl->addOutputDataset(name);
}

std::set<std::string> Algorithm::getInputDatasets() const {
	return m_impl->getInputDatasets();
}

std::set<std::string> Algorithm::getRequiredInputDatasets() const {
	return m_impl->getRequiredInputDatasets();
}

std::set<std::string> Algorithm::getOptionalInputDatasets() const {
	return m_impl->getOptionalInputDatasets();
}

std::set<std::string> Algorithm::getExcludedInputDatasets() const {
	return m_impl->getExcludedInputDatasets();
}

std::set<std::string> Algorithm::getOutputDatasets() const {
	return m_impl->getOutputDatasets();
}

bool Algorithm::isInputDatasetOptional(const std::string& name) const {
	return m_impl->isInputDatasetOptional(name);
}

void Algorithm::addExpectedDimension(const DataKeyDimension& dim) {
	m_impl->addExpectedDimension(dim);
}

std::set<DataKeyDimension> Algorithm::getExpectedDimensions() const {
	return m_impl->getExpectedDimensions();
}

}
