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

#ifndef __MR4C_EXTERNAL_DATASET_H__
#define __MR4C_EXTERNAL_DATASET_H__

#include "ExternalDataFile.h"

namespace MR4C {


class ExternalDatasetImpl;

class ExternalDataset {

	public:

		ExternalDataset();

		void init(const char* name);

		void init(const char* name, const CExternalDatasetCallbacks& callbacks);

		const char* getName() const;

		const char* getSerializedDataset() const;

		void setSerializedDataset(const char* serializedDataset);

		void addDataFile(ExternalDataFile* file);

		ExternalDataFile* getDataFile(int index) const;

		size_t getFileCount() const;

		const CExternalDatasetCallbacks& getCallbacks() const;
	
		bool hasDataFile(const char* serializedKey) const;

		~ExternalDataset();

	private:

		ExternalDatasetImpl* m_impl;

		// prevent calling these
		ExternalDataset(const ExternalDataset& file);
		ExternalDataset& operator=(const ExternalDataset& file);

};

}
#endif



