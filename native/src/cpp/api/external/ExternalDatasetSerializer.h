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

#ifndef __MR4C_EXTERNAL_DATASET_SERIALIZER_H__
#define __MR4C_EXTERNAL_DATASET_SERIALIZER_H__

#include <string>
#include "dataset/dataset_api.h"
#include "external/external_api.h"
#include "serialize/serialize_api.h"

namespace MR4C {

class ExternalDatasetSerializerImpl;

class ExternalDatasetSerializer {

	public :

		ExternalDatasetSerializer(const SerializerFactory& factory);

		~ExternalDatasetSerializer();
		
		void serializeDataset(ExternalDataset* extDataset, const Dataset& dataset) const;

		ExternalDataFile* serializeDataFile(const DataKey& key, DataFile& file) const;

		std::string serializeDataKey(const DataKey& key) const; 

		Dataset* deserializeDataset(const ExternalDataset& extDataset, bool output) const;

		DataFile* deserializeDataFile(const ExternalDataFile& extDataFile) const;

		DataKey deserializeDataKey(const std::string& serializedKey) const; 

	private :

		ExternalDatasetSerializerImpl* m_impl; 
		

};

}
#endif

