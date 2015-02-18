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

#ifndef __MR4C_ALGORITHM_H__
#define __MR4C_ALGORITHM_H__

#include <string>
#include "AlgorithmData.h"
#include "context/context_api.h"
#include "keys/keys_api.h"

namespace MR4C {

class AlgorithmImpl;

/**
  * Algorithm developers must extend this class to implement an algorithm.
  * Algorithm objects should be stateless.  If a stateful object is needed,
  * this should delegate to another class.
*/

class Algorithm {

	public:

		/**
		  * Algorithm developers must implement this method.  If the 
		  * algorithm fails, an exception should be thrown.  
		  * Exceptions should derive from std::exception.
		*/
		virtual void executeAlgorithm(AlgorithmData& data, AlgorithmContext& context) =0;

		/**
		  * equivalent to <code>addInputDataset(name, false, false)</code>
		*/
		void addInputDataset(const std::string& name);

		/**
		  * equivalent to <code>addInputDataset(name, optional, false)</code>
		*/
		void addInputDataset(const std::string& name, bool optional);

		void addInputDataset(const std::string& name, bool optional, bool excludeFromKeyspace);

		void addOutputDataset(const std::string& name);

		std::set<std::string> getInputDatasets() const;

		std::set<std::string> getRequiredInputDatasets() const;

		std::set<std::string> getOptionalInputDatasets() const;

		std::set<std::string> getExcludedInputDatasets() const;

		std::set<std::string> getOutputDatasets() const;

		bool isInputDatasetOptional(const std::string& name) const;

		bool isInputDatasetExcludedFromKeyspace(const std::string& name) const;

		/**
		  * Specify an expected dimension in the input data
		*/
		void addExpectedDimension(const DataKeyDimension& dim);

		std::set<DataKeyDimension> getExpectedDimensions() const;


		~Algorithm();

	protected:

		Algorithm();

	private:

		AlgorithmImpl* m_impl;
		

};

}

#endif
