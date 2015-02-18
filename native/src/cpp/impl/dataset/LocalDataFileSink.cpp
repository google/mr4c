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

#include <cstdlib>
#include <fstream>
#include <iostream>
#include <stdexcept>
#include <string>
#include <mutex>
#include <log4cxx/logger.h>

#include "dataset/dataset_api.h"
#include "util/util_api.h"

using log4cxx::LoggerPtr;

namespace MR4C {

class LocalDataFileSinkImpl {

	friend class LocalDataFileSink;

	private:

		LoggerPtr m_logger;
		std::string m_path;
		std::ofstream m_stream;
		bool m_closed;
		std::mutex m_mutex;

		LocalDataFileSinkImpl(const std::string& path) {
			m_path = path;
			m_closed = false;
			m_logger = MR4CLogging::getLogger("dataset.LocalDataFileSink");
		}

		std::string getPath() const {
			return m_path;
		}

		void write(char* buf, size_t num) {
		    std::unique_lock<std::mutex> lock(m_mutex);
			assertNotClosed();
			ensureOpened();
			m_stream.write(buf, num);
			lock.unlock();
		}

		void close() {
		    std::unique_lock<std::mutex> lock(m_mutex);
			if ( !m_closed && m_stream.is_open() ) {
				m_stream.close();
			}
			m_closed = true;
			lock.unlock();
		}

		bool isClosed() const {
			return m_closed;
		}

		void ensureOpened() {
			if ( m_stream.is_open() ) {
				return;
			}
			m_stream.open(m_path.c_str(), std::ios::out | std::ios::binary | std::ios::trunc );
			if (!m_stream.is_open()) {
				LOG4CXX_ERROR(m_logger, "Couldn't open file [" << m_path << "]");
				MR4C_THROW(std::logic_error, "Couldn't open file [" << m_path << "]");
			}
		}

		void assertNotClosed() const {
			if ( m_closed ) {
				LOG4CXX_ERROR(m_logger, "Tried to write to already closed file [" << m_path << "]");
				MR4C_THROW(std::logic_error, "Tried to write to already closed file [" << m_path << "]");
			}
		}

		~LocalDataFileSinkImpl() {
			close();
		} 

};


LocalDataFileSink::LocalDataFileSink(const std::string& path) {
	m_impl = new LocalDataFileSinkImpl(path);
}

std::string LocalDataFileSink::getPath() const {
	return m_impl->getPath();
}

void LocalDataFileSink::write(char* buf, size_t num) {
	m_impl->write(buf, num);
}

void LocalDataFileSink::close() {
	m_impl->close();
}

bool LocalDataFileSink::isClosed() const {
	return m_impl->isClosed();
}

LocalDataFileSink::~LocalDataFileSink() {
	delete m_impl;
} 

}
