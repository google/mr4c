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

namespace MR4C {

class ExternalDatasetImpl {

	friend class ExternalDataset;

	private:

		std::string m_name;
		std::string m_serializedDataset;
		std::vector<ExternalDataFile*> m_files;
		std::set<std::string> m_serFileKeys;
		CExternalDatasetCallbacks m_callbacks;

		ExternalDatasetImpl(const char* name) {
			m_name = name;
			nullCallbacks();
		}

		void nullCallbacks() {
			m_callbacks.findFileCallback=NULL;
			m_callbacks.addFileCallback=NULL;
			m_callbacks.getFileNameCallback=NULL;
			m_callbacks.isQueryOnlyCallback=NULL;
			m_callbacks.randomAccessSourceCallback=NULL;
			m_callbacks.randomAccessSinkCallback=NULL;
		}

		ExternalDatasetImpl(const char* name, const CExternalDatasetCallbacks& callbacks) {
			m_name = name;
			m_callbacks = callbacks;
		}

		const char* getName() const {
			return m_name.c_str();
		}

		const char* getSerializedDataset() const {
			return m_serializedDataset.c_str();
		}

		void setSerializedDataset(const char* serializedDataset) {
			m_serializedDataset = serializedDataset;
		}

		void addDataFile(ExternalDataFile* file) {
			m_files.push_back(file);
			m_serFileKeys.insert(file->getSerializedKey());
		}

		ExternalDataFile* getDataFile(int index) const {
			return m_files.at(index);
		}

		size_t getFileCount() const {
			return m_files.size();
		}

		const CExternalDatasetCallbacks& getCallbacks() const {
			return m_callbacks;
		}

		bool hasDataFile(const char* serializedKey) const {
			return m_serFileKeys.count(serializedKey)!=0;
		}

};


ExternalDataset::ExternalDataset() {}

void ExternalDataset::init(const char* name) {
	m_impl = new ExternalDatasetImpl(name);
}

void ExternalDataset::init(const char* name, const CExternalDatasetCallbacks& callbacks) {
	m_impl = new ExternalDatasetImpl(name, callbacks);
}

const char* ExternalDataset::getName() const {
	return m_impl->getName();
}

const char* ExternalDataset::getSerializedDataset() const {
	return m_impl->getSerializedDataset();
}

void ExternalDataset::setSerializedDataset(const char* serializedDataset) {
	m_impl->setSerializedDataset(serializedDataset);
}

void ExternalDataset::addDataFile(ExternalDataFile* file) {
	m_impl->addDataFile(file);
}

ExternalDataFile* ExternalDataset::getDataFile(int index) const {
	return m_impl->getDataFile(index);
}

size_t ExternalDataset::getFileCount() const {
	return m_impl->getFileCount();
}

const CExternalDatasetCallbacks& ExternalDataset::getCallbacks() const {
	return m_impl->getCallbacks();
}

bool ExternalDataset::hasDataFile(const char* serializedKey) const {
	return m_impl->hasDataFile(serializedKey);
}

ExternalDataset::~ExternalDataset() {
	delete m_impl;
}

}

