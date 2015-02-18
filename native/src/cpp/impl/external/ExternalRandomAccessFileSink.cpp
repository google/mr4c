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

#include "dataset/dataset_api.h"
#include "external/external_api.h"

namespace MR4C {

class ExternalRandomAccessFileSinkImpl : public WritableRandomAccessFile, public ExternalRandomAccessFile {

	friend class ExternalRandomAccessFileSink;

	private:

		ExternalRandomAccessFileSinkImpl(const CExternalRandomAccessFileCallbacks& callbacks)
			: ExternalRandomAccessFile(callbacks) {}

		void write(char* buf, size_t num) {
		    std::unique_lock<std::mutex> lock = getLock();
			assertNotClosed();
			bool success = getCallbacks().writeCallback(buf, num);
			lock.unlock();
			if ( !success ) {
				MR4C_THROW(std::runtime_error, "Write callback failed");
			}
		}

		void setFileSize(size_t size) {
		    std::unique_lock<std::mutex> lock = getLock();
			assertNotClosed();
			bool success = getCallbacks().setSizeCallback(size);
			lock.unlock();
			if ( !success ) {
				MR4C_THROW(std::runtime_error, "Set file size callback failed");
			}
		}

};


ExternalRandomAccessFileSink::ExternalRandomAccessFileSink(const CExternalRandomAccessFileCallbacks& callbacks) {
	m_impl = new ExternalRandomAccessFileSinkImpl(callbacks);
}

WritableRandomAccessFile* ExternalRandomAccessFileSink::getFileSink() {
	return m_impl;
}

ExternalRandomAccessFileSink::~ExternalRandomAccessFileSink() {
	delete m_impl;
}


}

