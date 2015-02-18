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

#ifndef __MR4C_DATASET_SERIALIZER_H__
#define __MR4C_DATASET_SERIALIZER_H__

#include <map>

#include "dataset/dataset_api.h"
#include "metadata/metadata_api.h"
#include "Serializer.h"

namespace MR4C {

class DatasetSerializer : public Serializer {

	public :

		virtual std::string serializeDataset(const Dataset& dataset) const =0;

		virtual Dataset* deserializeDataset(const std::string& data) const =0;

		virtual std::string serializeDataFile(const DataFile& file) const =0;

		virtual DataFile* deserializeDataFile(const std::string& data) const =0;

		virtual std::string serializeDataKey(const DataKey& key) const =0;

		virtual DataKey deserializeDataKey(const std::string& data) const =0;

		virtual std::string serializeMetadata(const std::map<DataKey,MetadataMap*>& meta) const =0;

		virtual std::map<DataKey,MetadataMap*> deserializeMetadata(const std::string& data) const =0;
};

}
#endif

