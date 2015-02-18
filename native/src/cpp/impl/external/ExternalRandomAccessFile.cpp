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

#include "dataset/dataset_api.h"
#include "external/external_api.h"

namespace MR4C {

class ExternalRandomAccessFileImpl {

	friend class ExternalRandomAccessFile;

	private:

		bool m_closed;
		CExternalRandomAccessFileCallbacks m_callbacks;
		std::mutex m_mutex;

		ExternalRandomAccessFileImpl(const CExternalRandomAccessFileCallbacks& callbacks) {
			m_closed = false;
			m_callbacks = callbacks;
		}

		size_t read(char* buf, size_t num) {
		    std::unique_lock<std::mutex> lock = getLock();
			assertNotClosed();
			size_t numRead;
			bool success = m_callbacks.readCallback(buf, num, &numRead);
			lock.unlock();
			if ( !success ) {
				MR4C_THROW(std::runtime_error, "Write callback failed");
			}
			return numRead;
		}

		size_t getFileSize() {
		    std::unique_lock<std::mutex> lock = getLock();
			assertNotClosed();
			size_t size;
			bool success = m_callbacks.getSizeCallback(&size);
			lock.unlock();
			if ( !success ) {
				MR4C_THROW(std::runtime_error, "Get file size callback failed");
			}
			return size;
		}

		size_t getLocation() {
		    std::unique_lock<std::mutex> lock = getLock();
			assertNotClosed();
			size_t loc;
			bool success = m_callbacks.getLocationCallback(&loc);
			lock.unlock();
			if ( !success ) {
				MR4C_THROW(std::runtime_error, "Get file location callback failed");
			}
			return loc;
		}

		void setLocation(size_t loc) {
		    std::unique_lock<std::mutex> lock = getLock();
			assertNotClosed();
			bool success = m_callbacks.setLocationCallback(loc);
			lock.unlock();
			if ( !success ) {
				MR4C_THROW(std::runtime_error, "Set file location callback failed");
			}
		}

		void setLocationFromEnd(size_t loc) {
		    std::unique_lock<std::mutex> lock = getLock();
			assertNotClosed();
			bool success = m_callbacks.setLocationFromEndCallback(loc);
			lock.unlock();
			if ( !success ) {
				MR4C_THROW(std::runtime_error, "Set file location from end callback failed");
			}
		}


		void skipForward(size_t num) {
		    std::unique_lock<std::mutex> lock = getLock();
			assertNotClosed();
			bool success = m_callbacks.skipForwardCallback(num);
			lock.unlock();
			if ( !success ) {
				MR4C_THROW(std::runtime_error, "Skip forward callback failed");
			}
		}

		void skipBackward(size_t num) {
		    std::unique_lock<std::mutex> lock = getLock();
			assertNotClosed();
			bool success = m_callbacks.skipBackwardCallback(num);
			lock.unlock();
			if ( !success ) {
				MR4C_THROW(std::runtime_error, "Skip backward callback failed");
			}
		}

		void close() {
		    std::unique_lock<std::mutex> lock = getLock();
			if ( !m_closed ) {
				m_callbacks.closeCallback();
				m_closed = true;
			}
			lock.unlock();
		}

		bool isClosed() const {
			return m_closed;
		}

		void assertNotClosed() const {
			if ( m_closed ) {
				throw std::logic_error("Random access file already closed");
			}
		}

		std::unique_lock<std::mutex> getLock() {
		    return std::unique_lock<std::mutex>(m_mutex);
		}

		const CExternalRandomAccessFileCallbacks& getCallbacks() const {
			return m_callbacks;
		}

};


ExternalRandomAccessFile::ExternalRandomAccessFile(const CExternalRandomAccessFileCallbacks& callbacks) {
	m_impl = new ExternalRandomAccessFileImpl(callbacks);
}

size_t ExternalRandomAccessFile::read(char* buf, size_t num) {
	return m_impl->read(buf, num);
}

size_t ExternalRandomAccessFile::getLocation() {
	return m_impl->getLocation();
}

void ExternalRandomAccessFile::setLocation(size_t loc) {
	m_impl->setLocation(loc);
}

void ExternalRandomAccessFile::setLocationFromEnd(size_t loc) {
	m_impl->setLocationFromEnd(loc);
}

void ExternalRandomAccessFile::skipForward(size_t num) {
	m_impl->skipForward(num);
}

void ExternalRandomAccessFile::skipBackward(size_t num) {
	m_impl->skipBackward(num);
}

size_t ExternalRandomAccessFile::getFileSize() {
	return m_impl->getFileSize();
}

void ExternalRandomAccessFile::close() {
	return m_impl->close();
}

bool ExternalRandomAccessFile::isClosed() const {
	return m_impl->isClosed();
}

void ExternalRandomAccessFile::assertNotClosed() const {
	return m_impl->assertNotClosed();
}

std::unique_lock<std::mutex> ExternalRandomAccessFile::getLock() {
    return m_impl->getLock();
}

const CExternalRandomAccessFileCallbacks& ExternalRandomAccessFile::getCallbacks() const {
	return m_impl->getCallbacks();
}

ExternalRandomAccessFile::~ExternalRandomAccessFile() {
	delete m_impl;
}


}


