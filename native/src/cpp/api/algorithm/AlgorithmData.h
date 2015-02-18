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

#ifndef __MR4C_ALGORITHM_DATA_H__
#define __MR4C_ALGORITHM_DATA_H__

#include <string>
#include <set>
#include "algorithm/AlgorithmConfig.h"
#include "dataset/dataset_api.h"
#include "keys/keys_api.h"

namespace MR4C {

class AlgorithmDataImpl;

/**
  * Root object for access to all data used by an executing algorithm.
  * NOTE: the methods to add datasets are for use by the framework only
*/

class AlgorithmData {

	public:

		AlgorithmData();

		void setKeyspace(const Keyspace& keyspace);

		/**
		  * Returns the keyspace containing all the elements found in
		  * the input datasets.  Return value is a reference valid for
		  * the lifetime of this object.
		*/
		const Keyspace& getKeyspace() const;

		/**
		  * This will overwrite the keyspace this object was created with
		*/
		void generateKeyspaceFromInputDatasets();

		void setConfig(const AlgorithmConfig& config);

		/**
		  * Returns per-run configuration parameters.  Return value is
		  * a reference valid for the lifetime of this object.
		*/
		const AlgorithmConfig& getConfig() const;

		void addInputDataset(const std::string& name, Dataset* dataset);

		void addOutputDataset(const std::string& name, Dataset* dataset);

		Dataset* getInputDataset(const std::string& name) const;

		bool hasInputDataset(const std::string& name) const;

		Dataset* getOutputDataset(const std::string& name) const;

		bool hasOutputDataset(const std::string& name) const;

		std::set<std::string> getInputDatasetNames() const;

		std::set<std::string> getOutputDatasetNames() const;

		bool operator==(const AlgorithmData& algoData) const;

		bool operator!=(const AlgorithmData& algoData) const;

		~AlgorithmData();

	private:

		AlgorithmDataImpl* m_impl;

		// prevent calling these
		AlgorithmData(const AlgorithmData& reg);
		AlgorithmData& operator=(const AlgorithmData& reg);

};

}

#endif
