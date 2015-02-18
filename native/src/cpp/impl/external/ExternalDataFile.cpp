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

#include <string>
#include <iostream>
#include <memory>

#include "external/external_api.h"

using std::shared_ptr;

namespace MR4C {

class ExternalDataFileImpl {

	friend class ExternalDataFile;

	private:

		std::string m_key;
		std::string m_serializedFile;
		ExternalDataFileSource* m_src;
		ExternalDataFileSink* m_sink;
		std::string m_name;

		ExternalDataFileImpl(const char* key, const char* name) {
			m_key = key;
			if ( name!=NULL ) {
				m_name = name;
			}
			m_src = NULL;
			m_sink = NULL;
		}

		const char* getSerializedFile() const {
			return m_serializedFile.c_str();
		}

		void setSerializedFile(const char* serializedFile) {
			m_serializedFile = serializedFile;
		}


		ExternalDataFileSource* getFileSource() const {
			return m_src;
		}

		void setFileSource(ExternalDataFileSource* src) {
			m_src = src;
		}

		ExternalDataFileSink* getFileSink() const {
			return m_sink;
		}

		void setFileSink(ExternalDataFileSink* sink) {
			m_sink = sink;
		}

		const char* getSerializedKey() const {
			return m_key.c_str();
		}

		const char* getFileName() const {
			return m_name.c_str();
		}

};

ExternalDataFile::ExternalDataFile() {}

void ExternalDataFile::init(const char* key, const char* name) {
	m_impl = new ExternalDataFileImpl(key, name);
}

const char* ExternalDataFile::getSerializedFile() const {
	return m_impl->getSerializedFile();
}

void ExternalDataFile::setSerializedFile(const char* serializedFile) {
	m_impl->setSerializedFile(serializedFile);
}

ExternalDataFileSource* ExternalDataFile::getFileSource() const {
	return m_impl->getFileSource();
}

void ExternalDataFile::setFileSource(ExternalDataFileSource* src) {
	m_impl->setFileSource(src);
}

ExternalDataFileSink* ExternalDataFile::getFileSink() const {
	return m_impl->getFileSink();
}

void ExternalDataFile::setFileSink(ExternalDataFileSink* sink) {
	m_impl->setFileSink(sink);
}

const char* ExternalDataFile::getSerializedKey() const {
	return m_impl->getSerializedKey();
}

const char* ExternalDataFile::getFileName() const {
	return m_impl->getFileName();
}


ExternalDataFile::~ExternalDataFile() {
	delete m_impl;
}


}

