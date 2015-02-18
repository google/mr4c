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

#ifndef __MR4C_EXTERNAL_ALGORITHM_DATA_H__
#define __MR4C_EXTERNAL_ALGORITHM_DATA_H__

#include "ExternalDataset.h"

namespace MR4C {


class ExternalAlgorithmDataImpl;

class ExternalAlgorithmData {

	public:

		ExternalAlgorithmData();

		void setSerializedKeyspace(const char* keyspace);

		bool hasKeyspace() const;

		const char* getSerializedKeyspace() const;

		void setSerializedConfig(const char* config);

		bool hasConfig() const;

		const char* getSerializedConfig() const;

		void addInputDataset(ExternalDataset* dataset);

		ExternalDataset* getInputDataset(int index) const;

		size_t getInputDatasetCount() const;

		bool hasInputDataset(const char* name);

		void addOutputDataset(ExternalDataset* dataset);

		ExternalDataset* getOutputDataset(int index) const;

		size_t getOutputDatasetCount() const;

		bool hasOutputDataset(const char* name);

		~ExternalAlgorithmData();

	private:

		ExternalAlgorithmDataImpl* m_impl;

		// prevent calling these
		ExternalAlgorithmData(const ExternalAlgorithmData& file);
		ExternalAlgorithmData& operator=(const ExternalAlgorithmData& file);

};

}
#endif



