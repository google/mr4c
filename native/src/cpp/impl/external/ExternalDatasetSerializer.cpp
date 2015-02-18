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
#include <sstream>
#include <iostream>
#include <set>
#include <log4cxx/logger.h>

#include "dataset/dataset_api.h"
#include "external/external_api.h"
#include "keys/keys_api.h"
#include "serialize/serialize_api.h"
#include "util/util_api.h"

using log4cxx::LoggerPtr;

namespace MR4C {

class ExternalDatasetSerializerImpl {

	friend class ExternalDatasetSerializer;

	private:
		
		LoggerPtr m_logger;
		DatasetSerializer* m_serializer;
		ExternalDatasetSerializer* m_this;

		ExternalDatasetSerializerImpl(
			const SerializerFactory& factory,
			ExternalDatasetSerializer* thisPtr
		) {
			
			m_logger = MR4CLogging::getLogger("external.ExternalDatasetSerializer");
			m_serializer = factory.createDatasetSerializer();
			m_this = thisPtr;
		}

		~ExternalDatasetSerializerImpl() {
			delete m_serializer;
		}

		void serializeDataset(ExternalDataset* extDataset, const Dataset& dataset) const {
			std::set<DataKey> fileKeys = dataset.getAllFileKeys();
			std::set<DataKey> metaKeys = dataset.getAllMetadataKeys();
			LOG4CXX_INFO(m_logger, "Serializing " << fileKeys.size() << " file keys and " << metaKeys.size() << " metadata keys");
			std::string serDataset = m_serializer->serializeDataset(dataset);
			extDataset->setSerializedDataset(serDataset.c_str());
			for ( std::set<DataKey>::iterator iter = fileKeys.begin(); iter!=fileKeys.end(); iter++ ) {
				DataKey key(*iter);
				DataFile* file = dataset.getDataFile(key);
				ExternalDataFile* extDataFile = serializeDataFile(key,*file, false);
				if ( !extDataset->hasDataFile(extDataFile->getSerializedKey()) ) {
					extDataset->addDataFile(extDataFile);
				}
			}
		}

		ExternalDataFile* serializeDataFile(const DataKey& key, DataFile& file, bool includeSerialized) const {
			std::string serKey = m_serializer->serializeDataKey(key);
			const char* name = file.getFileName().empty() ? NULL : file.getFileName().c_str();
			ExternalDataFile* extDataFile = new ExternalDataFile();
			extDataFile->init(serKey.c_str(), name);
			if ( includeSerialized ) {
				std::string serFile = m_serializer->serializeDataFile(file);
				extDataFile->setSerializedFile(serFile.c_str());
			}
			if ( file.getFileSource()!=NULL ) {
				ExternalDataFileSource* extSrc = new ExternalDataFileSource(file.getFileSource());
				extDataFile->setFileSource(extSrc);
			}
			return extDataFile;
		}

		std::string serializeDataKey(const DataKey& key) const {
			return m_serializer->serializeDataKey(key);
		}

		Dataset* deserializeDataset(const ExternalDataset& extDataset, bool output) {
			Dataset* dataset = m_serializer->deserializeDataset(extDataset.getSerializedDataset());
			std::set<DataKey> fileKeys = dataset->getAllFileKeys();
			std::set<DataKey> metaKeys = dataset->getAllMetadataKeys();
			LOG4CXX_INFO(m_logger, "Deserialized " << fileKeys.size() << " file keys and " << metaKeys.size() << " metadata keys");
			for ( size_t i=0; i<extDataset.getFileCount(); i++ ) {
				ExternalDataFile* extFile = extDataset.getDataFile(i);
				DataKey key = m_serializer->deserializeDataKey(extFile->getSerializedKey());
				DataFile* file = dataset->getDataFile(key);
				populateFile(*extFile, file);
			}
			DatasetContext* context = new ExternalDatasetContext(
				output,
				extDataset.getCallbacks(),
				m_this
			);
			dataset->setContext(context);
			return dataset;

		}

		DataFile* deserializeDataFile(const ExternalDataFile& extFile) const {
			DataFile* file = m_serializer->deserializeDataFile(extFile.getSerializedFile());
			populateFile(extFile, file);
			return file;
		}

		DataKey deserializeDataKey(const std::string& serializedKey) const {
			return m_serializer->deserializeDataKey(serializedKey);
		}

		void populateFile(const ExternalDataFile& extFile, DataFile* file) const {
			ExternalDataFileSource* extSrc = extFile.getFileSource();
			if ( extSrc!=NULL ) {
				std::shared_ptr<DataFileSource> src = extSrc->getFileSource();
				file->setFileSource(src);
			}
			if ( extFile.getFileName()!=NULL ) {
				file->setFileName(extFile.getFileName());
			}
		}

};

ExternalDatasetSerializer::ExternalDatasetSerializer(const SerializerFactory& factory) {
	m_impl = new ExternalDatasetSerializerImpl(factory, this);
}

ExternalDatasetSerializer::~ExternalDatasetSerializer() {
	delete m_impl;
}

void ExternalDatasetSerializer::serializeDataset(ExternalDataset* extDataset, const Dataset& dataset) const {
	return m_impl->serializeDataset(extDataset, dataset);
}

ExternalDataFile* ExternalDatasetSerializer::serializeDataFile(const DataKey& key, DataFile& file) const {
	return m_impl->serializeDataFile(key, file, true);
}

std::string ExternalDatasetSerializer::serializeDataKey(const DataKey& key) const {
	return m_impl->serializeDataKey(key);
}

Dataset* ExternalDatasetSerializer::deserializeDataset(const ExternalDataset& extDataset, bool output) const {
	return m_impl->deserializeDataset(extDataset, output);
}

DataFile* ExternalDatasetSerializer::deserializeDataFile(const ExternalDataFile& extFile) const {
	return m_impl->deserializeDataFile(extFile);
}

DataKey ExternalDatasetSerializer::deserializeDataKey(const std::string& serializedKey) const {
	return m_impl->deserializeDataKey(serializedKey);
}

}
