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
#include <iostream>
#include <string>
#include <stdexcept>
#include <memory>
#include <mutex>
#include <log4cxx/logger.h>

#include "dataset/dataset_api.h"
#include "util/util_api.h"

using log4cxx::LoggerPtr;
using std::shared_ptr;

namespace MR4C {

class DataFileImpl {

	friend class DataFile;

	private:

		LoggerPtr m_logger;
		std::string m_contentType;
		shared_ptr<DataFileSource> m_src;
		DataFileSink* m_sink;
		std::string m_name;
		mutable std::recursive_mutex m_mutex;

		DataFileImpl(char* bytes, size_t size, const std::string& contentType, DataFile::Allocation alloc) {
			init();
			m_src = shared_ptr<DataFileSource>(new SimpleDataFileSource(bytes, size, alloc));
			m_contentType = contentType;
		}

		DataFileImpl(shared_ptr<DataFileSource>& src, const std::string& contentType) {
			init();
			m_src = src;
			m_contentType = contentType;
		}

		DataFileImpl(const std::string& path, const std::string& contentType) {
			init();
			m_src = shared_ptr<DataFileSource>(new LocalDataFileSource(path));
			m_contentType = contentType;
		}

		DataFileImpl(const std::string& contentType) {
			init();
			m_contentType = contentType;
		}

		void setFileSource(shared_ptr<DataFileSource>& src) {
			if ( hasFileSource() ) {
				LOG4CXX_ERROR(m_logger, "Tried to add data source to a data file that already has a source");
				throw std::invalid_argument("File already has a data source");
			}
			m_src = src;
		}
	
		shared_ptr<DataFileSource> getFileSource() {
			return m_src;
		}

		bool hasFileSource() const {	
			return m_src.get()!=NULL;
		}

		void setFileSink(DataFileSink* sink) {
			if ( hasFileSink() ) {
				LOG4CXX_ERROR(m_logger, "Tried to add data sink to a data file that already has a sink");
				throw std::invalid_argument("File already has a data sink");
			}
			m_sink = sink;
		}

		DataFileSink* getFileSink() {
			return m_sink;
		}

		bool hasFileSink() const {	
			return m_sink!=NULL;
		}

		std::string getContentType() const {
			return m_contentType;
		}

		char* getBytes() const {
		    std::unique_lock<std::recursive_mutex> lock(m_mutex);
			assertHasSource();
			return m_src->getFileBytes();
		}

		size_t getSize() const {
		    std::unique_lock<std::recursive_mutex> lock(m_mutex);
			assertHasSource();
			return m_src->getFileSize();
		}

		size_t read(char* buf, size_t num) {
		    std::unique_lock<std::recursive_mutex> lock(m_mutex);
			assertHasSource();
			return m_src->read(buf, num);
		}

		size_t skip(size_t num) {
		    std::unique_lock<std::recursive_mutex> lock(m_mutex);
			assertHasSource();
			return m_src->skip(num);
		}

		void write(char* buf, size_t num) {
		    std::unique_lock<std::recursive_mutex> lock(m_mutex);
			assertHasSink();
			m_sink->write(buf, num);
			lock.unlock();
		}

		std::string getFileName() {
			return m_name;
		}

		void setFileName(const std::string& name) {
			m_name = name;
		}

		void release() {
		    std::unique_lock<std::recursive_mutex> lock(m_mutex); // Don't release in the middle of a read/write
			if ( hasFileSource() ) {
				m_src->release();
			}
			if ( hasFileSink() ) {
				m_sink->close();
			}
			lock.unlock();
		}

		bool hasContent() const {
			return hasFileSource() && !m_src->isReleased();
		}

		bool operator==(const DataFileImpl& file) const {
			if (m_contentType!=file.m_contentType) return false;
			std::unique_lock<std::recursive_mutex> mylock(m_mutex, std::defer_lock); // Released when out of scope
			std::unique_lock<std::recursive_mutex> otherlock(file.m_mutex, std::defer_lock); // Released when out of scope
			std::lock(mylock, otherlock); // Acquire both locks, prevent deadlock
			return compareSources(*this,file);
		}

		static bool compareSources(const DataFileImpl& file1, const DataFileImpl& file2) {
			if ( file1.hasContent()!=file2.hasContent() ) {
				return false;
			} else if ( !file1.hasContent() ) {
				return true; // neither has content
			} else {
				return *file1.m_src==*file2.m_src;
			}
		}

		~DataFileImpl() {
			// do nothing - src will release when no refs left
		} 

		void init() {
			m_logger = MR4CLogging::getLogger("dataset.DataFile");
			m_src=shared_ptr<DataFileSource>();
			m_sink=NULL;
		}

		void assertHasSource() const {
			if ( m_src==NULL ) {
				LOG4CXX_ERROR(m_logger, "Tried to access data file with no source");
				throw std::logic_error("File doesn't have a data source");
			}
		}

		void assertHasSink() const {
			if ( m_sink==NULL ) {
				LOG4CXX_ERROR(m_logger, "Tried to write to data file with no sink");
				throw std::logic_error("File doesn't have a data sink");
			}
		}

		std::unique_lock<std::recursive_mutex> getLock(bool doAcquire) {
		    if (doAcquire)
		        return std::unique_lock<std::recursive_mutex>(m_mutex);
		    return std::unique_lock<std::recursive_mutex>(m_mutex, std::defer_lock);
		}

};


DataFile::DataFile(char* bytes, size_t size, const std::string& contentType, Allocation alloc) {
	m_impl = new DataFileImpl(bytes, size, contentType, alloc);
}

DataFile::DataFile(shared_ptr<DataFileSource>& src, const std::string& contentType) {
	m_impl = new DataFileImpl(src, contentType);
}

DataFile::DataFile(const std::string& path, const std::string& contentType) {
	m_impl = new DataFileImpl(path, contentType);
}

DataFile::DataFile(const std::string& contentType) {
	m_impl = new DataFileImpl(contentType);
}


void DataFile::setFileSource(shared_ptr<DataFileSource>& src) {
	m_impl->setFileSource(src);
}

shared_ptr<DataFileSource> DataFile::getFileSource() {
	return m_impl->getFileSource();
}

bool DataFile::hasFileSource() const {
	return m_impl->hasFileSource();
}

bool DataFile::hasFileSink() const {
	return m_impl->hasFileSink();
}

void DataFile::setFileSink(DataFileSink* sink) {
	m_impl->setFileSink(sink);
}

DataFileSink* DataFile::getFileSink() {
	return m_impl->getFileSink();
}

std::string DataFile::getContentType() const {
	return m_impl->getContentType();
}

char* DataFile::getBytes() const {
	return m_impl->getBytes();
}

size_t DataFile::getSize() const {
	return m_impl->getSize();
}

size_t DataFile::read(char* buf, size_t num) {
	return m_impl->read(buf, num);
}

size_t DataFile::skip(size_t num) {
	return m_impl->skip(num);
}

void DataFile::write(char* buf, size_t num) {
	m_impl->write(buf, num);
}

std::string DataFile::getFileName() {
	return m_impl->getFileName();
}

void DataFile::setFileName(const std::string& name) {
	m_impl->setFileName(name);
}

void DataFile::release() {
	m_impl->release();
}

bool DataFile::hasContent() const {
	return m_impl->hasContent();
}

std::unique_lock<std::recursive_mutex> DataFile::getLock(bool doAcquire) {
    return m_impl->getLock(doAcquire);
}

bool DataFile::operator==(const DataFile& file) const {
	return *m_impl==*file.m_impl; 
}

bool DataFile::operator!=(const DataFile& file) const {
	return !operator==(file);
}

DataFile::~DataFile() {
	delete m_impl;
} 

}
