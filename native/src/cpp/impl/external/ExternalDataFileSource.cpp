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

class ExternalDataFileSourceImpl : public DataFileSource {

	friend class ExternalDataFileSource;

	private:

		bool m_released;
		CExternalDataSourceCallbacks m_callbacks;

		ExternalDataFileSourceImpl(const CExternalDataSourceCallbacks& callbacks) {
			m_released = false;
			m_callbacks = callbacks;
		}

		char* getFileBytes() const {
			assertNotReleased();
			char* bytes = m_callbacks.getBytesCallback();
			if ( bytes==NULL ) {
				throw std::runtime_error("GetBytes callback failed");
			}
			return bytes;
		}

		size_t getFileSize() const {
			assertNotReleased();
			size_t size;
			bool success = m_callbacks.getSizeCallback(&size);
			if ( !success ) {
				throw std::runtime_error("GetFileSize callback failed");
			}
			return size;
		}

		size_t read(char* buf, size_t num) {
			assertNotReleased();
			size_t numRead;
			bool success = m_callbacks.readCallback(buf, num, &numRead);
			if ( !success ) {
				throw std::runtime_error("Read callback failed");
			}
			return numRead;
		}

		size_t skip(size_t num) {
			assertNotReleased();
			size_t numSkipped;
			bool success = m_callbacks.skipCallback(num, &numSkipped);
			if ( !success ) {
				throw std::runtime_error("Skip callback failed");
			}
			return numSkipped;
		}

		void release() {
			if ( !m_released ) {
				m_callbacks.releaseCallback();
				// allowing file to be released and then re-accessed if permitted on the Java side
				//m_released = true;
			}
		}

		bool isReleased() const {
			return m_released;
		}

		void assertNotReleased() const {
			if ( m_released ) {
				throw std::logic_error("Data source already released");
			}
		}

	public:
		~ExternalDataFileSourceImpl() {
		} 


};


ExternalDataFileSource::ExternalDataFileSource(const CExternalDataSourceCallbacks& callbacks) {
	m_impl = std::shared_ptr<DataFileSource>(new ExternalDataFileSourceImpl(callbacks));
}

ExternalDataFileSource::ExternalDataFileSource(std::shared_ptr<DataFileSource> src) {
	m_impl = src;
}

std::shared_ptr<DataFileSource> ExternalDataFileSource::getFileSource() const {
	return m_impl;
}

const char* ExternalDataFileSource::getBytes() const {
	return m_impl->getFileBytes();
}

size_t ExternalDataFileSource::getSize() const {
	return m_impl->getFileSize();
}

ExternalDataFileSource::~ExternalDataFileSource() {
}


}

