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

class LocalDataFileSourceImpl {

	friend class LocalDataFileSource;

	private:

		LoggerPtr m_logger;
		std::string m_path;
		mutable char* m_bytes;
		mutable size_t m_size;
		mutable bool m_released;
		std::ifstream m_stream;
		mutable std::mutex m_mutex;

		LocalDataFileSourceImpl(const std::string& path) {
			m_path = path;
			m_logger = MR4CLogging::getLogger("dataset.LocalDataFileSource");
			m_bytes = NULL;
			m_size = -1;
			m_released = false;
		}


		std::string getPath() const {
			return m_path;
		}

		char* getFileBytes() const {
		    std::unique_lock<std::mutex> lock(m_mutex); // Released when out of scope
			ensureContent();
			return m_bytes;
		}

		size_t getFileSize() const {
		    std::unique_lock<std::mutex> lock(m_mutex); // Released when out of scope
			ensureSize();
			return m_size;
		}

		virtual size_t read(char* buf, size_t num) {
		    std::unique_lock<std::mutex> lock(m_mutex); // Released when out of scope
			ensureStream();
			m_stream.read (buf, num);
			return m_stream.gcount();
		}

		virtual size_t skip(size_t num) {
		    std::unique_lock<std::mutex> lock(m_mutex); // Released when out of scope
			ensureStream();
			m_stream.ignore(num);
			return m_stream.gcount();
		}

		void ensureContent() const {
			if ( m_bytes==NULL ) {
				readFile(true);
				m_released = false;
			}
		}

		void ensureSize() const {
			if ( m_size==-1 ) {
				readFile(false);
				m_released = false;
			}
		}

		void ensureStream() {
			if ( !m_stream.is_open() ) {
				open(m_stream);
				m_released = false;
			}
		}

		void readFile(bool readBytes) const {
			std::ifstream stream;
			open(stream);
			stream.seekg(0, std::ios::end);
			m_size = stream.tellg();
			if ( readBytes ) {
				stream.seekg(0, std::ios::beg);
				m_bytes = new char[m_size];
				stream.read (m_bytes, m_size);
			}
			close(stream);
		}

		void open(std::ifstream& stream) const {
			stream.open(m_path.c_str(), std::ios::in|std::ios::binary);
			if (!stream.is_open()) {
				LOG4CXX_ERROR(m_logger, "Couldn't open file [" << m_path << "]");
				MR4C_THROW(std::logic_error, "Couldn't open file [" << m_path << "]");
			}
			stream.exceptions(std::ifstream::badbit);
		}

		void close(std::ifstream& stream) const {
			stream.close();
		}

		void release() {
		    std::unique_lock<std::mutex> lock(m_mutex);
			if ( m_bytes!=NULL ) {
				delete[] m_bytes;
				m_bytes=NULL;
			}
			m_size=-1;
			if ( m_stream.is_open() ) {
				m_stream.close();
			}
			m_released=true;
			lock.unlock();
		}

		bool isReleased() const {
			return m_released;
		}

		~LocalDataFileSourceImpl() {
			release();
		} 

};


LocalDataFileSource::LocalDataFileSource(const std::string& path) {
	m_impl = new LocalDataFileSourceImpl(path);
}

std::string LocalDataFileSource::getPath() const {
	return m_impl->getPath();
}

char* LocalDataFileSource::getFileBytes() const {
	return m_impl->getFileBytes();
}

size_t LocalDataFileSource::getFileSize() const {
	return m_impl->getFileSize();
}

size_t LocalDataFileSource::read(char* buf, size_t num) {
	return m_impl->read(buf, num);
}

size_t LocalDataFileSource::skip(size_t num) {
	return m_impl->skip(num);
}

void LocalDataFileSource::release() {
	m_impl->release();
}

bool LocalDataFileSource::isReleased() const {
	return m_impl->isReleased();
}

LocalDataFileSource::~LocalDataFileSource() {
	delete m_impl;
} 

}
