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

#ifndef __MR4C_EXTERNAL_DATASET_CONTEXT_H__
#define __MR4C_EXTERNAL_DATASET_CONTEXT_H__

#include "dataset/dataset_api.h"

namespace MR4C {

class ExternalDatasetContextImpl;

class ExternalDatasetContext : public DatasetContext {

	public:

		ExternalDatasetContext(
			bool output,
			const CExternalDatasetCallbacks& callbacks,
			ExternalDatasetSerializer* serializer
		);

		DataFile* findDataFile(const DataKey& key) const;

		bool isOutput() const;

		bool isQueryOnly() const;

		void addDataFile(const DataKey& key, DataFile* file);

		std::string getDataFileName(const DataKey& key) const;

		RandomAccessFile* readFileAsRandomAccess(const DataKey& key) const;

		WritableRandomAccessFile* writeFileAsRandomAccess(const DataKey& key) const;


		~ExternalDatasetContext();

	private:

		ExternalDatasetContextImpl* m_impl;

		// prevent calling these
		ExternalDatasetContext(const ExternalDatasetContext& context);
		ExternalDatasetContext& operator=(const ExternalDatasetContext& context);

};

}
#endif



