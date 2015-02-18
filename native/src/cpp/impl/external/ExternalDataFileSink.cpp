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
#include <mutex>

#include "external/external_api.h"

namespace MR4C {

class ExternalDataFileSinkImpl : public DataFileSink {

	friend class ExternalDataFileSink;

	private:

		bool m_closed;
		CExternalDataSinkCallbacks m_callbacks;
		std::mutex m_mutex;

		ExternalDataFileSinkImpl(const CExternalDataSinkCallbacks& callbacks) {
			m_closed = false;
			m_callbacks = callbacks;
		}

		void write(char* buf, size_t num) {
		    std::unique_lock<std::mutex> lock(m_mutex);
			assertNotClosed();
			bool success = m_callbacks.writeCallback(buf, num);
			if ( !success ) { 
				throw std::runtime_error("Write callback failed");
			}
			lock.unlock();
		}

		void close() {
		    std::unique_lock<std::mutex> lock(m_mutex);
			if ( !m_closed ) {
				m_callbacks.closeCallback();
				m_closed = true;
			}
			lock.unlock();
		}

		bool isClosed() const {
			return m_closed;
		}

		~ExternalDataFileSinkImpl() {
		} 

		void assertNotClosed() const {
			if ( m_closed ) {
				throw std::logic_error("Data sink already closed");
			}
		}


};


ExternalDataFileSink::ExternalDataFileSink(const CExternalDataSinkCallbacks& callbacks) {
	m_impl = new ExternalDataFileSinkImpl(callbacks);
}

DataFileSink* ExternalDataFileSink::getFileSink() {
	return m_impl;
}

ExternalDataFileSink::~ExternalDataFileSink() {
	delete m_impl;
}


}

