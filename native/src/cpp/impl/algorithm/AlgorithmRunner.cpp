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

#include <algorithm>
#include <vector>
#include <map>
#include <set>
#include <string>
#include <stdexcept>
#include <iostream>
#include <log4cxx/logger.h>

#include "algorithm/algorithm_api.h"
#include "util/util_api.h"

using log4cxx::LoggerPtr;

namespace MR4C {

class AlgorithmRunnerImpl {

	friend class AlgorithmRunner;

	private:

		LoggerPtr m_logger;
		std::string m_name;
		Algorithm* m_algo;

		AlgorithmRunnerImpl(const std::string& name) {
			m_logger = MR4CLogging::getLogger("algorithm.AlgorithmRunner");
			m_name = name;
			m_algo = AlgorithmRegistry::instance().getAlgorithm(name);
		}

		~AlgorithmRunnerImpl() {} 


		void executeAlgorithm(AlgorithmData& data, AlgorithmContext& context) const {
			LOG4CXX_INFO(m_logger, "Checking data passed to algorithm [" << m_name << "]");
			checkInputs(data);
			logConfig(data);
			logKeyspace(data);
			checkKeyspace(data);
			m_algo->executeAlgorithm(data, context);
		}

		// checking for expected dataset names
		void checkInputs(const AlgorithmData& data) const {
			std::set<std::string> expected = m_algo->getInputDatasets();
			std::set<std::string> required = m_algo->getRequiredInputDatasets();
			std::set<std::string> actual = data.getInputDatasetNames();
			checkDatasets(expected, required, actual);
		}
			
		void checkOutputs(const AlgorithmData& data) const {
			std::set<std::string> expected = m_algo->getOutputDatasets();
			std::set<std::string> actual = data.getOutputDatasetNames();
			// Java side handles outputs being optional, so all required to be available on this side
			checkDatasets(expected, expected, actual);
		}

		void checkDatasets(
			const std::set<std::string>& expected,
			const std::set<std::string>& required,
			const std::set<std::string>& actual
		) const {
			// check that all provided datasets are expected by the algorithm
			if ( !std::includes(expected.begin(), expected.end(), actual.begin(), actual.end()) ) {
				failCheck(expected, actual, "expected");
			}
			// check that provided datasets include all the required ones 
			if ( !std::includes(actual.begin(), actual.end(), required.begin(), required.end()) ) {
				failCheck(required, actual, "required");
			}
		}

		void failCheck(
			const std::set<std::string>& expected,
			const std::set<std::string>& actual,
			const std::string& name
		) const {
			std::ostringstream ss;
			ss << "Wrong output datasets for algorithm [" << m_name << "]; " << name << " = [" << setToString(expected, ",") << "]; actual = [" << setToString(actual, ",") << "]";
			LOG4CXX_ERROR(m_logger, ss.str());
			MR4C_THROW( std::invalid_argument, ss.str());
		}

		void logKeyspace(AlgorithmData& data) const {
			const Keyspace& keyspace = data.getKeyspace();
			std::set<DataKeyDimension> dims = keyspace.getDimensions();
			std::set<DataKeyDimension>::const_iterator iter = dims.begin();
			for ( ; iter!=dims.end(); iter++ ) {
				DataKeyDimension dim = *iter;
				KeyspaceDimension ksd = keyspace.getKeyspaceDimension(dim);
				std::vector<DataKeyElement> elements = ksd.getElements();
				LOG4CXX_INFO(m_logger, elements.size() << " elements of dimension [" << dim << "] : [" + vectorToString(DataKeyElement::toElementIds(elements), ",") + "]");
			}
		}

		void checkKeyspace(AlgorithmData& data) const {
			const Keyspace& keyspace = data.getKeyspace();
			std::set<DataKeyDimension> actualDims = keyspace.getDimensions();
			std::set<DataKeyDimension> expectedDims = m_algo->getExpectedDimensions();
			if ( expectedDims.empty() ) {
				return; // only want to check if the algo provided dimensions
			}

			if ( actualDims!=expectedDims ) {
				std::ostringstream ss;
				ss << "Wrong dimensions for algorithm [" << m_name << "]; expected = [" << setToString(expectedDims, ",") + "]; actual = [" << setToString(actualDims, ",") << "]";
				LOG4CXX_ERROR(m_logger, ss.str());
				MR4C_THROW( std::invalid_argument, ss.str());
			}

		}
			
		void logConfig(AlgorithmData& data) const {
			AlgorithmConfig config = data.getConfig();
			std::set<std::string> names = config.getAllParamNames();
			std::set<std::string>::const_iterator iter = names.begin();
			LOG4CXX_INFO(m_logger, "Begin config params");
			for ( ; iter!=names.end(); iter++ ) {
				std::string name = *iter;
				std::string val = config.getConfigParam(name);

				LOG4CXX_INFO(m_logger, "[" << name << "] = [" << val << "]");
			}
			LOG4CXX_INFO(m_logger, "End config params");
		}

};

AlgorithmRunner::AlgorithmRunner(const std::string name) {
	m_impl = new AlgorithmRunnerImpl(name);
}

void AlgorithmRunner::executeAlgorithm(AlgorithmData& data, AlgorithmContext& context) const {
	m_impl->executeAlgorithm(data, context);
}

AlgorithmRunner::~AlgorithmRunner() {
	delete m_impl;
}


}
