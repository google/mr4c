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

#include <cmath>
#include <cstdlib>
#include <iostream>
#include <stdexcept>
#include <mutex>
#include <log4cxx/logger.h>

#include "dataset/dataset_api.h"
#include "util/util_api.h"

using log4cxx::LoggerPtr;

namespace MR4C {

class SimpleDataFileSourceImpl {

	friend class SimpleDataFileSource;

	private:

		LoggerPtr m_logger;
		char* m_bytes;
		size_t m_size;
		DataFile::Allocation m_alloc;
		bool m_released;
		size_t m_loc;
		mutable std::mutex m_mutex;

		SimpleDataFileSourceImpl(char* bytes, size_t size, DataFile::Allocation alloc) {
			init();
			m_bytes = bytes;
			m_size = size;
			m_alloc = alloc;
			m_released = false;
			m_loc=0;
		}

		char* getFileBytes() const {
		    std::unique_lock<std::mutex> lock(m_mutex); // Released when out of scope
			assertNotReleased();
			return m_bytes;
		}

		size_t getFileSize() const {
			assertNotReleased();
			return m_size;
		}

		size_t read(char* buf, size_t num) {
		    std::unique_lock<std::mutex> lock(m_mutex); // Released when out of scope
			assertNotReleased();
			if ( m_loc==m_size ) {
				return 0;
			}
			int numRead = avail(num);
			copyArray(m_bytes+m_loc, buf, numRead);
			m_loc+=numRead;
			return numRead;
		}

		size_t skip(size_t num) {
		    std::unique_lock<std::mutex> lock(m_mutex); // Released when out of scope
			assertNotReleased();
			int numSkipped = avail(num);
			m_loc+=numSkipped;
			return numSkipped;
		}

		size_t avail(size_t num) {
			return fmin(m_size - m_loc, num);
		}

		void release() {
		    std::unique_lock<std::mutex> lock(m_mutex);
			if ( !m_released ) {
				freeBytes();
				m_released=true;
			}
			lock.unlock();
		}

		void freeBytes() {
			LOG4CXX_INFO(m_logger, "Freeing " << m_size << " byte file");
			switch (m_alloc) {
				case DataFile::MALLOC :
					free(m_bytes);
					break;
				case DataFile::NEW :
					delete[] m_bytes;
					break;
				default:
					// should never happen?
					MR4C_THROW(std::logic_error, "Unknown value [" << m_alloc << "] for allocation flag");
			}
		}

		bool isReleased() const {
			return m_released;
		}

		~SimpleDataFileSourceImpl() {
			release();
		} 

		void init() {
			m_logger = MR4CLogging::getLogger("dataset.SimpleDataFileSource");
			
		}

		void assertNotReleased() const {
			if ( m_released ) {
				LOG4CXX_ERROR(m_logger, "Tried to access data source that has already been released");
				throw std::logic_error("Data source already released");
			}
		}
};


SimpleDataFileSource::SimpleDataFileSource(char* bytes, size_t size, DataFile::Allocation alloc) {
	m_impl = new SimpleDataFileSourceImpl(bytes, size, alloc);
}

char* SimpleDataFileSource::getFileBytes() const {
	return m_impl->getFileBytes();
}

size_t SimpleDataFileSource::getFileSize() const {
	return m_impl->getFileSize();
}

size_t SimpleDataFileSource::read(char* buf, size_t num) {
	return m_impl->read(buf, num);
}

size_t SimpleDataFileSource::skip(size_t num) {
	return m_impl->skip(num);
}

void SimpleDataFileSource::release() {
	m_impl->release();
}

bool SimpleDataFileSource::isReleased() const {
	return m_impl->isReleased();
}

SimpleDataFileSource::~SimpleDataFileSource() {
	delete m_impl;
} 

}
