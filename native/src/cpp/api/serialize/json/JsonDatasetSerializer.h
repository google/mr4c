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

#ifndef __MR4C_JSON_DATASET_SERIALIZER_H__
#define __MR4C_JSON_DATASET_SERIALIZER_H__

#include <string>

#include "dataset/dataset_api.h"
#include "serialize/serialize_api.h"

namespace MR4C {

class JsonDatasetSerializerImpl;

class JsonDatasetSerializer : public DatasetSerializer {

	public:

		JsonDatasetSerializer();

		~JsonDatasetSerializer();

		std::string serializeDataset(const Dataset& dataset) const;

		Dataset* deserializeDataset(const std::string& data) const;

		std::string serializeDataFile(const DataFile& file) const;

		DataFile* deserializeDataFile(const std::string& data) const;

		std::string serializeDataKey(const DataKey& key) const;

		DataKey deserializeDataKey(const std::string& data) const;

		std::string serializeMetadata(const std::map<DataKey,MetadataMap*>& meta) const;

		std::map<DataKey,MetadataMap*> deserializeMetadata(const std::string& data) const;

		std::string getContentType() const;

	private:

		JsonDatasetSerializerImpl* m_impl;

		// prevent calling these
		JsonDatasetSerializer(const JsonDatasetSerializer& serializer);
		JsonDatasetSerializer& operator=(const JsonDatasetSerializer& serializer);


};

}
#endif

