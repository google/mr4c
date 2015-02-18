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

#ifndef __MR4C_EXTERNAL_ALGORITHM_DATA_SERIALIZER_H__
#define __MR4C_EXTERNAL_ALGORITHM_DATA_SERIALIZER_H__

#include "ExternalDatasetSerializer.h"
#include "algorithm/algorithm_api.h"
#include "external/external_api.h"

namespace MR4C {

class ExternalAlgorithmDataSerializerImpl;

class ExternalAlgorithmDataSerializer {

	public :

		ExternalAlgorithmDataSerializer(const SerializerFactory& factory);

		~ExternalAlgorithmDataSerializer();
		
		void serializeInputData( const AlgorithmData& algoData, ExternalAlgorithmData& extAlgoData) const;

		void deserializeInputData( AlgorithmData& algoData, const ExternalAlgorithmData& extAlgoData) const;

		void serializeOutputData( const AlgorithmData& algoData, ExternalAlgorithmData& extAlgoData) const;

		void deserializeOutputData( AlgorithmData& algoData, const ExternalAlgorithmData& extAlgoData) const;

	private :

		ExternalAlgorithmDataSerializerImpl* m_impl; 
		

};

}
#endif

