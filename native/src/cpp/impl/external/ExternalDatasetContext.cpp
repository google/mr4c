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
#include <stdexcept>

#include "external/external_api.h"

namespace MR4C {

class ExternalDatasetContextImpl {

	friend class ExternalDatasetContext;

	private:

		bool m_output;
		bool m_queryOnly;
		CExternalDatasetCallbacks m_callbacks;
		ExternalDatasetSerializer* m_serializer;

		ExternalDatasetContextImpl(
			bool output,
			const CExternalDatasetCallbacks& callbacks,
			ExternalDatasetSerializer* serializer
		) {
			m_output = output;
			m_callbacks = callbacks;
			m_serializer = serializer;
			m_queryOnly = m_callbacks.isQueryOnlyCallback==NULL ?
				false : m_callbacks.isQueryOnlyCallback();
		}

		DataFile* findDataFile(const DataKey& key) const {
			std::string serKey = m_serializer->serializeDataKey(key);
			CExternalDataFilePtr extFileHandle = m_callbacks.findFileCallback(serKey.c_str());
			if ( extFileHandle==NULL ) {
				return NULL;
			}
			return m_serializer->deserializeDataFile(*(extFileHandle->file));
		}

		bool isOutput() const {
			return m_output;
		}

		bool isQueryOnly() const {
			return m_queryOnly;
		}

		void addDataFile(const DataKey& key, DataFile* file) {
			ExternalDataFile* extFile = m_serializer->serializeDataFile(key, *file);
			if ( file->hasContent() && file->getSize() > 100000 ) {
				extFile->setFileSource(NULL); // Do this so we get a sink and copy across in chunks
			}
			if ( !m_callbacks.addFileCallback(wrapExternalDataFile(extFile)) ) {
				throw std::runtime_error("Adding file failed");
			}
			ExternalDataFileSink* extSink = extFile->getFileSink();
			if ( extSink!=NULL ) {
				file->setFileSink(extSink->getFileSink());
			}
		}


		std::string getDataFileName(const DataKey& key) const {
			std::string serKey = m_serializer->serializeDataKey(key);
			const char* result = m_callbacks.getFileNameCallback(serKey.c_str());
			return result==NULL ? std::string() : std::string(result);
		}


		RandomAccessFile* readFileAsRandomAccess(const DataKey& key) const {
			std::string serKey = m_serializer->serializeDataKey(key);
			CExternalRandomAccessFileSourcePtr extSrcHandle = m_callbacks.randomAccessSourceCallback(serKey.c_str());
			if ( extSrcHandle==NULL ) {
				throw std::runtime_error("Obtaining random access source failed");
			}
			return extSrcHandle->src->getFileSource();
		}

		WritableRandomAccessFile* writeFileAsRandomAccess(const DataKey& key) const {
			std::string serKey = m_serializer->serializeDataKey(key);
			CExternalRandomAccessFileSinkPtr extSinkHandle = m_callbacks.randomAccessSinkCallback(serKey.c_str());
			if ( extSinkHandle==NULL ) {
				throw std::runtime_error("Obtaining random access sink failed");
			}
			return extSinkHandle->sink->getFileSink();
		}

};


ExternalDatasetContext::ExternalDatasetContext(
	bool output,
	const CExternalDatasetCallbacks& callbacks,
	ExternalDatasetSerializer* serializer
) {
	m_impl = new ExternalDatasetContextImpl(output, callbacks, serializer);
}

DataFile* ExternalDatasetContext::findDataFile(const DataKey& key) const {
	return m_impl->findDataFile(key);
}

bool ExternalDatasetContext::isOutput() const {
	return m_impl->isOutput();
}

bool ExternalDatasetContext::isQueryOnly() const {
	return m_impl->isQueryOnly();
}

void ExternalDatasetContext::addDataFile(const DataKey& key, DataFile* file) {
	return m_impl->addDataFile(key, file);
}

std::string ExternalDatasetContext::getDataFileName(const DataKey& key) const {
	return m_impl->getDataFileName(key);
}

RandomAccessFile* ExternalDatasetContext::readFileAsRandomAccess(const DataKey& key) const {
	return m_impl->readFileAsRandomAccess(key);
}


WritableRandomAccessFile* ExternalDatasetContext::writeFileAsRandomAccess(const DataKey& key) const {
	return m_impl->writeFileAsRandomAccess(key);
}

ExternalDatasetContext::~ExternalDatasetContext() {
	delete m_impl;
}

}

