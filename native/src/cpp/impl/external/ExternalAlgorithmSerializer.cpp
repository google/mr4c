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

#include <log4cxx/logger.h>

#include "algorithm/algorithm_api.h"
#include "external/external_api.h"
#include "serialize/serialize_api.h"
#include "util/util_api.h"

using log4cxx::LoggerPtr;

namespace MR4C {

class ExternalAlgorithmSerializerImpl {

	friend class ExternalAlgorithmSerializer;

	private:
		
		LoggerPtr m_logger;
		AlgorithmSerializer* m_serializer;

		ExternalAlgorithmSerializerImpl(const SerializerFactory& factory) {
			
			m_logger = MR4CLogging::getLogger("external.ExternalAlgorithmSerializer");
			m_serializer = factory.createAlgorithmSerializer();
		}

		~ExternalAlgorithmSerializerImpl() {
			delete m_serializer;
		}

		void serializeAlgorithm(ExternalAlgorithm* extAlgo, const Algorithm& algo) const {
			std::string serAlgo = m_serializer->serializeAlgorithm(algo);
			extAlgo->setSerializedAlgorithm(serAlgo.c_str());
		}

		void deserializeAlgorithm(const ExternalAlgorithm& extAlgorithm, Algorithm& algo) const {
			m_serializer->deserializeAlgorithm(extAlgorithm.getSerializedAlgorithm(), algo);
		}

};

ExternalAlgorithmSerializer::ExternalAlgorithmSerializer(const SerializerFactory& factory) {
	m_impl = new ExternalAlgorithmSerializerImpl(factory);
}

ExternalAlgorithmSerializer::~ExternalAlgorithmSerializer() {
	delete m_impl;
}

void ExternalAlgorithmSerializer::serializeAlgorithm(ExternalAlgorithm* extAlgo, const Algorithm& algo) const {
	m_impl->serializeAlgorithm(extAlgo, algo);
}

void ExternalAlgorithmSerializer::deserializeAlgorithm(const ExternalAlgorithm& extAlgo, Algorithm& algo) const {
	m_impl->deserializeAlgorithm(extAlgo, algo);
}

}
