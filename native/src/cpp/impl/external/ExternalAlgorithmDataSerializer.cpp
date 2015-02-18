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
#include <stdexcept>
#include <iostream>
#include <log4cxx/logger.h>

#include "algorithm/algorithm_api.h"
#include "dataset/dataset_api.h"
#include "external/external_api.h"
#include "serialize/serialize_api.h"
#include "util/util_api.h"

using log4cxx::LoggerPtr;

namespace MR4C {

class ExternalAlgorithmDataSerializerImpl {

	friend class ExternalAlgorithmDataSerializer;

	private:

		LoggerPtr m_logger;
		ExternalDatasetSerializer* m_datasetSerializer;
		KeyspaceSerializer* m_keyspaceSerializer;
		AlgorithmConfigSerializer* m_configSerializer;

		ExternalAlgorithmDataSerializerImpl(const SerializerFactory& factory) {
			m_logger = MR4CLogging::getLogger("external.ExternalAlgorithmDataSerializer");

			m_datasetSerializer = new ExternalDatasetSerializer(factory);
			m_keyspaceSerializer = factory.createKeyspaceSerializer();
			m_configSerializer = factory.createAlgorithmConfigSerializer();
		}
		
		~ExternalAlgorithmDataSerializerImpl() {
			delete m_datasetSerializer;
			delete m_keyspaceSerializer;
			delete m_configSerializer;
		}

		void serializeInputData( const AlgorithmData& algoData, ExternalAlgorithmData& extAlgoData) const {
			LOG4CXX_INFO(m_logger, "Begin serializing input data");
			serializeKeyspace(algoData, extAlgoData);
			serializeConfig(algoData, extAlgoData);
			initExternalInputDatasets(algoData, extAlgoData);
			serializeInputDatasets(algoData, extAlgoData);
			LOG4CXX_INFO(m_logger, "Done serializing input data");
		}

		void serializeKeyspace( const AlgorithmData& algoData, ExternalAlgorithmData& extAlgoData) const {
			LOG4CXX_INFO(m_logger, "Serializing keyspace");
			std::string serKeyspace = m_keyspaceSerializer->serializeKeyspace(algoData.getKeyspace());
			extAlgoData.setSerializedKeyspace(serKeyspace.c_str());
		}

		void serializeConfig( const AlgorithmData& algoData, ExternalAlgorithmData& extAlgoData) const {
			LOG4CXX_INFO(m_logger, "Serializing config params");
			std::string serConfig = m_configSerializer->serializeAlgorithmConfig(algoData.getConfig());
			extAlgoData.setSerializedConfig(serConfig.c_str());
		}

		void initExternalInputDatasets( const AlgorithmData& algoData, ExternalAlgorithmData& extAlgoData) const {
			std::set<std::string> names = algoData.getInputDatasetNames();
			for ( std::set<std::string>::iterator iter = names.begin(); iter!=names.end(); iter++ ) {
				std::string name(*iter);
				if ( !extAlgoData.hasInputDataset(name.c_str()) ) {
					ExternalDataset* extDataset = new ExternalDataset();
					extDataset->init(name.c_str());
					extAlgoData.addInputDataset(extDataset);
				}
			}
		}

		void serializeInputDatasets( const AlgorithmData& algoData, ExternalAlgorithmData& extAlgoData) const {
			int count = extAlgoData.getInputDatasetCount();
			for ( int index=0; index<count; index++ ) {
				ExternalDataset* extDataset = extAlgoData.getInputDataset(index);
				std::string name = extDataset->getName();
				LOG4CXX_INFO(m_logger, "Serializing input dataset [" << name << "]");
				Dataset* dataset = algoData.getInputDataset(name);
				m_datasetSerializer->serializeDataset(extDataset, *dataset);
			}
		}

		void serializeOutputData( const AlgorithmData& algoData, ExternalAlgorithmData& extAlgoData) const {
			LOG4CXX_INFO(m_logger, "Begin serializing output data");
			initExternalOutputDatasets(algoData, extAlgoData);
			int count = extAlgoData.getOutputDatasetCount();
			for ( int index=0; index<count; index++ ) {
				ExternalDataset* extDataset = extAlgoData.getOutputDataset(index);
				std::string name = extDataset->getName();
				LOG4CXX_INFO(m_logger, "Serializing output dataset [" << name << "]");
				Dataset* dataset = algoData.getOutputDataset(name);
				m_datasetSerializer->serializeDataset(extDataset, *dataset);
			}
			LOG4CXX_INFO(m_logger, "Done serializing output data");
		}

		void initExternalOutputDatasets( const AlgorithmData& algoData, ExternalAlgorithmData& extAlgoData) const {
			std::set<std::string> names = algoData.getOutputDatasetNames();
			for ( std::set<std::string>::iterator iter = names.begin(); iter!=names.end(); iter++ ) {
				std::string name(*iter);
				if ( !extAlgoData.hasOutputDataset(name.c_str()) ) {
					ExternalDataset* extDataset = new ExternalDataset();
					extDataset->init(name.c_str());
					extAlgoData.addOutputDataset(extDataset);
				}
			}
		}

		void deserializeInputData( AlgorithmData& algoData, const ExternalAlgorithmData& extAlgoData) const {
			LOG4CXX_INFO(m_logger, "Begin deserializing input data");
			deserializeKeyspace(algoData, extAlgoData);
			deserializeConfig(algoData, extAlgoData);
			deserializeInputDatasets(algoData, extAlgoData);
			LOG4CXX_INFO(m_logger, "Done deserializing input data");
		}

		void deserializeKeyspace( AlgorithmData& algoData, const ExternalAlgorithmData& extAlgoData) const {
			LOG4CXX_INFO(m_logger, "Deserializing keyspace");
			Keyspace keyspace;
			if ( !extAlgoData.hasKeyspace() ) {
				throw std::invalid_argument("missing keyspace");
			}
			keyspace = m_keyspaceSerializer->deserializeKeyspace(extAlgoData.getSerializedKeyspace());
			algoData.setKeyspace(keyspace);
		}

		void deserializeConfig( AlgorithmData& algoData, const ExternalAlgorithmData& extAlgoData) const {
			LOG4CXX_INFO(m_logger, "Deserializing config params");
			AlgorithmConfig config;
			if ( !extAlgoData.hasConfig() ) {
				throw std::invalid_argument("missing config");
			}
			config = m_configSerializer->deserializeAlgorithmConfig(extAlgoData.getSerializedConfig());
			algoData.setConfig(config);
		}

		void deserializeInputDatasets( AlgorithmData& algoData, const ExternalAlgorithmData& extAlgoData) const {
			for ( size_t i=0; i<extAlgoData.getInputDatasetCount(); i++ ) {
				ExternalDataset* extDataset = extAlgoData.getInputDataset(i);
				std::string name = extDataset->getName();
				LOG4CXX_INFO(m_logger, "Deserializing input dataset [" << name << "]");
				Dataset* dataset = m_datasetSerializer->deserializeDataset(*extDataset, false);
				algoData.addInputDataset(name,dataset);
			}
		}

		void deserializeOutputData( AlgorithmData& algoData, const ExternalAlgorithmData& extAlgoData) const {
			LOG4CXX_INFO(m_logger, "Begin deserializing output data");
			for ( size_t i=0; i<extAlgoData.getOutputDatasetCount(); i++ ) {
				ExternalDataset* extDataset = extAlgoData.getOutputDataset(i);
				std::string name = extDataset->getName();
				LOG4CXX_INFO(m_logger, "Deserializing output dataset [" << name << "]");
				Dataset* dataset = m_datasetSerializer->deserializeDataset(*extDataset, true);
				algoData.addOutputDataset(name,dataset);
			}
			LOG4CXX_INFO(m_logger, "Done deserializing output data");
		}

};

ExternalAlgorithmDataSerializer::ExternalAlgorithmDataSerializer(const SerializerFactory& factory) {
	m_impl = new ExternalAlgorithmDataSerializerImpl(factory);
}

ExternalAlgorithmDataSerializer::~ExternalAlgorithmDataSerializer() {
	delete m_impl;
}
		
void ExternalAlgorithmDataSerializer::serializeInputData( const AlgorithmData& algoData, ExternalAlgorithmData& extAlgoData) const {
	m_impl->serializeInputData(algoData, extAlgoData);
}

void ExternalAlgorithmDataSerializer::deserializeInputData( AlgorithmData& algoData, const ExternalAlgorithmData& extAlgoData) const {
	m_impl->deserializeInputData(algoData, extAlgoData);
}

void ExternalAlgorithmDataSerializer::serializeOutputData( const AlgorithmData& algoData, ExternalAlgorithmData& extAlgoData) const {
	m_impl->serializeOutputData(algoData, extAlgoData);
}

void ExternalAlgorithmDataSerializer::deserializeOutputData( AlgorithmData& algoData, const ExternalAlgorithmData& extAlgoData) const {
	m_impl->deserializeOutputData(algoData, extAlgoData);
}



}
