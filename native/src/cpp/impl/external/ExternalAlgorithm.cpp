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

class ExternalAlgorithmImpl {

	friend class ExternalAlgorithm;

	private:

		std::string m_name;
		std::string m_serializedAlgo;

		ExternalAlgorithmImpl(const char* name) {
			m_name = name;
		}

		const char* getName() const {
			return m_name.c_str(); 
		}

		const char* getSerializedAlgorithm() const {
			return m_serializedAlgo.c_str();
		}

		void setSerializedAlgorithm(const char* serializedAlgo) {
			m_serializedAlgo = serializedAlgo;
		}

		~ExternalAlgorithmImpl() {
		} 

};


ExternalAlgorithm::ExternalAlgorithm(const char* name) {
	m_impl = new ExternalAlgorithmImpl(name);
}

const char* ExternalAlgorithm::getName() const {
	return m_impl->getName();
}

const char* ExternalAlgorithm::getSerializedAlgorithm() const {
	return m_impl->getSerializedAlgorithm();
}

void ExternalAlgorithm::setSerializedAlgorithm(const char* serializedAlgo) {
	m_impl->setSerializedAlgorithm(serializedAlgo);
}

ExternalAlgorithm::~ExternalAlgorithm() {
	delete m_impl;
}

}

