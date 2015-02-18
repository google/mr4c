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

#include <map>
#include <memory>
#include <set>
#include <vector>
#include <stdexcept>
#include <iostream>
#include <typeinfo>
#include <mutex>
#include <log4cxx/logger.h>

#include "dataset/dataset_api.h"
#include "serialize/serialize_api.h"
#include "util/util_api.h"

using log4cxx::LoggerPtr;
using std::shared_ptr;

namespace MR4C {

class DatasetImpl {

	friend class Dataset;

	private:

		LoggerPtr m_logger;
		mutable std::map<DataKey,DataFile*> m_files; // want to be able to lazy load via context
		std::map<DataKey,MetadataMap*> m_meta;
		mutable std::set<DataKey> m_fileKeys; // want to be able to lazy load via context
		std::set<DataKey> m_metaKeys;
		mutable std::recursive_mutex m_filesMutex; // want the same thread to acquire recursively
		mutable std::recursive_mutex m_metaMutex; // want the same thread to acquire recursively
		std::unique_ptr<DatasetContext> m_context;
		const Keyspace* m_keyspace; // global keyspace
		Keyspace m_allKeyspace;
		Keyspace m_filesKeyspace;
		Keyspace m_metaKeyspace;
		DatasetSerializer* m_serializer;

		DatasetImpl() {
			init();
		}

		void init() {
			m_logger = MR4CLogging::getLogger("dataset.Dataset");
			m_keyspace=NULL;
			m_context.reset(new DatasetContext()); // default context to start
			SerializerFactory* factory = SerializerRegistry::instance().getSerializerFactory("application/json");
			m_serializer = factory->createDatasetSerializer();


		}

		bool hasDataFile(const DataKey& key) const {
		    std::unique_lock<std::recursive_mutex> lock(m_filesMutex); // Released when out of scope
			if ( hasDataFileLocal(key) ) {
				return true;
			}
			DataFile* file = m_context->findDataFile(key);
			if ( file!=NULL ) {
				m_files[key] = file;
				m_fileKeys.insert(key);
				return true;
			} else {
				return false;
			}
		}

		bool hasDataFileLocal(const DataKey& key) const {
			return m_files.count(key)!=0;
		}

		DataFile* getDataFile(const DataKey& key) const {
		    std::unique_lock<std::recursive_mutex> lock(m_filesMutex); // Released when out of scope
			if ( !m_context->isQueryOnly() ) {
				validateKey(key);
			}
			if ( !hasDataFile(key) ) {
				LOG4CXX_ERROR(m_logger, "No data file for key [" << key.str() << "]");
				MR4C_THROW(std::invalid_argument, "No data file for key [" << key.str() << "]");
			}
			return m_files.find(key)->second;
		}

		RandomAccessFile* getDataFileForRandomAccess(const DataKey& key) const {
			DataFile* file = getDataFile(key); // make sure exists, let it throw exception if not
			return m_context->readFileAsRandomAccess(key);
		}

		std::map<DataKey,MetadataMap*> getDataFileAsMetadata(const DataKey& key) const {
			DataFile* file = getDataFile(key);
			std::string content(file->getBytes(), file->getSize());
			return m_serializer->deserializeMetadata(content);
		}

		void addDataFile(const DataKey& key, DataFile* file) {
		    std::unique_lock<std::recursive_mutex> lock(m_filesMutex, std::defer_lock);
		    std::unique_lock<std::recursive_mutex> fileLock = file->getLock(false);
		    std::lock(lock, fileLock); // Acquire both, prevent deadlock

			if ( hasDataFileLocal(key) ) {
				LOG4CXX_ERROR(m_logger, "Tried to add file for key [" << key.str() << "] that already has a file");
				MR4C_THROW(std::invalid_argument, "File already exists for key [" << key.str() << "]");
			}
			m_files[key] = file;
			m_fileKeys.insert(key);
			if ( m_context->isOutput() ) {
				handleOutputDataFile(key, file);
			}

			fileLock.unlock();
			lock.unlock();
		}

		void handleOutputDataFile(const DataKey& key, DataFile* file) {
			m_context->addDataFile(key, file);
			if ( file->hasContent() ) {
				// File should come back without sink if it was already written
				if ( file->hasFileSink() ) {
					Dataset::copySourceToSink(
						file->getFileSource(),
						file->getFileSink()
					);
				}
				file->release();
			} else if ( !file->hasFileSink() ) {
				throw std::logic_error("Added file doesn't have source or sink");
			}
		}

		WritableRandomAccessFile* addDataFileForRandomAccess(const DataKey& key, DataFile* file) {
		    std::unique_lock<std::recursive_mutex> lock(m_filesMutex); // Released when out of scope
			if ( file->hasContent() ) {
				MR4C_THROW(std::logic_error, "Added random access file should not already have content for key [" << key.str() << "]");
			}
			addDataFile(key, file);
			return m_context->writeFileAsRandomAccess(key);
		}

		void addDataFileAsMetadata(const DataKey& key, const std::map<DataKey,MetadataMap*>& meta) {
			std::string str = m_serializer->serializeMetadata(meta);
			// copying into a buffer because we don't have const semantics on the DataFile constructor :-(
			char* data = new char[str.size()];
			size_t size = str.copy(data, str.size());
			DataFile* file = new DataFile(data, size, m_serializer->getContentType());
			addDataFile(key, file);
			Dataset::freeMetadata(meta);
		}

		std::set<DataKey> getAllFileKeys() const {
			return m_fileKeys;
		}

		bool hasMetadata(const DataKey& key) const {
			return m_meta.count(key)!=0;
		}

		MetadataMap* getMetadata(const DataKey& key) const {
		    std::unique_lock<std::recursive_mutex> lock(m_metaMutex); // Released when out of scope
			validateKey(key);
			if ( !hasMetadata(key) ) {
				LOG4CXX_ERROR(m_logger, "No metadata for key [" << key.str() << "]");
				MR4C_THROW(std::invalid_argument, "No metadata file for key [" << key.str() << "]");
			}
			return m_meta.find(key)->second;
		}

		void addMetadata(const DataKey& key, MetadataMap* meta) {
		    std::unique_lock<std::recursive_mutex> lock(m_metaMutex);
			if ( hasMetadata(key) ) {
				LOG4CXX_ERROR(m_logger, "Tried to add metadata mp for key [" << key.str() << "] that already has a metadata map");
				MR4C_THROW(std::invalid_argument, "Metadata map already exists for key [" << key.str() << "]");
			}
			m_meta[key]=meta;
			m_metaKeys.insert(key);
			lock.unlock();
		}

		std::set<DataKey> getAllMetadataKeys() const {
			return m_metaKeys;
		}

		void release() {
		    std::unique_lock<std::recursive_mutex> lock(m_filesMutex);
			LOG4CXX_INFO(m_logger, "Releasing all files in the dataset");
			for ( std::set<DataKey>::iterator iter = m_fileKeys.begin(); iter!=m_fileKeys.end(); iter++ ) {
				m_files.find(*iter)->second->release();
			}
			lock.unlock();
		}

		void setGlobalKeyspace(const Keyspace& keyspace) {
			m_keyspace = &keyspace;
		}

		void validateKey(const DataKey& key) const {
			if ( m_keyspace!=NULL ) {
				m_keyspace->validateKey(key);
			}
		}

		std::string getDataFileName(const DataKey& key) const {
			return m_context->getDataFileName(key);
		}

		void setContext(DatasetContext* context) {
			m_context.reset(context);
		}
		
		const Keyspace& getKeyspace() const {
			return m_allKeyspace;
		}
	
		const Keyspace& getFilesKeyspace() const {
			return m_filesKeyspace;
		}
	
		const Keyspace& getMetadataKeyspace() const {
			return m_metaKeyspace;
		}

		void generateKeyspaces() {
			KeyspaceBuilder builder;
			KeyspaceBuilder filesBuilder;
			KeyspaceBuilder metaBuilder;
			builder.addKeys(getAllFileKeys());
			filesBuilder.addKeys(getAllFileKeys());
			builder.addKeys(getAllMetadataKeys());
			metaBuilder.addKeys(getAllMetadataKeys());
			m_allKeyspace = builder.toKeyspace();
			m_filesKeyspace = filesBuilder.toKeyspace();
			m_metaKeyspace = metaBuilder.toKeyspace();
		}

		bool isQueryOnly() {
			return m_context->isQueryOnly();
		}

		bool operator==(const DatasetImpl& dataset) const {
			if ( !compareMapsOfPointers(m_files, dataset.m_files) ) return false;
			if ( !compareMapsOfPointers(m_meta, dataset.m_meta) ) return false;
			return true;
		}

		~DatasetImpl() {
			deleteMapOfPointers<DataKey,DataFile>(m_files);
			deleteMapOfPointers<DataKey,MetadataMap>(m_meta);
			delete m_serializer;
		} 

};


Dataset::Dataset() {
	m_impl = new DatasetImpl();
}

bool Dataset::hasDataFile(const DataKey& key) const {
	return m_impl->hasDataFile(key);
}

DataFile* Dataset::getDataFile(const DataKey& key) const {
	return m_impl->getDataFile(key);
}

RandomAccessFile* Dataset::getDataFileForRandomAccess(const DataKey& key) const {
	return m_impl->getDataFileForRandomAccess(key);
}

std::map<DataKey,MetadataMap*> Dataset::getDataFileAsMetadata(const DataKey& key) const {
	return m_impl->getDataFileAsMetadata(key);
}

void Dataset::addDataFile(const DataKey& key, DataFile* file) {
	m_impl->addDataFile(key,file);
}

WritableRandomAccessFile* Dataset::addDataFileForRandomAccess(const DataKey& key, DataFile* file) {
	return m_impl->addDataFileForRandomAccess(key,file);
}

void Dataset::addDataFileAsMetadata(const DataKey& key, const std::map<DataKey,MetadataMap*>& meta) {
	m_impl->addDataFileAsMetadata(key,meta);
}

std::set<DataKey> Dataset::getAllFileKeys() const {
	return m_impl->getAllFileKeys();
}

bool Dataset::hasMetadata(const DataKey& key) const {
	return m_impl->hasMetadata(key);
}

MetadataMap* Dataset::getMetadata(const DataKey& key) const {
	return m_impl->getMetadata(key);
}

void Dataset::addMetadata(const DataKey& key, MetadataMap* meta) {
	m_impl->addMetadata(key, meta);
}

std::set<DataKey> Dataset::getAllMetadataKeys() const {
	return m_impl->getAllMetadataKeys();
}

void Dataset::release() {
	m_impl->release();
}

void Dataset::setContext(DatasetContext* context) {
	m_impl->setContext(context);
}

void Dataset::setGlobalKeyspace(const Keyspace& keyspace) {
	m_impl->setGlobalKeyspace(keyspace);
}

std::string Dataset::getDataFileName(const DataKey& key) const {
	return m_impl->getDataFileName(key);
}

const Keyspace& Dataset::getKeyspace() const {
	return m_impl->getKeyspace();
}

const Keyspace& Dataset::getFilesKeyspace() const {
	return m_impl->getFilesKeyspace();
}

const Keyspace& Dataset::getMetadataKeyspace() const {
	return m_impl->getMetadataKeyspace();
}

void Dataset::generateKeyspaces() {
	m_impl->generateKeyspaces();
}

bool Dataset::isQueryOnly() {
	return m_impl->isQueryOnly();
}

bool Dataset::operator==(const Dataset& dataset) const {
	return *m_impl==*dataset.m_impl;
}

bool Dataset::operator!=(const Dataset& dataset) const {
	return !operator==(dataset);
}

Dataset::~Dataset() {
	delete m_impl;
} 


void Dataset::copySourceToSink(shared_ptr<DataFileSource> src, DataFileSink* sink) {
	size_t size = 10000;
	std::vector<char> buf(size);
	for ( ;; ) {
		size_t read = src->read(buf.data(), size);
		if ( read==0 ) {
			break;
		}
		sink->write(buf.data(), read);
	}
}

void Dataset::freeMetadata(std::map<DataKey,MetadataMap*> meta) {
	deleteMapOfPointers(meta);
}


}
