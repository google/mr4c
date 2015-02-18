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

#include <jansson.h>

#include "algorithm/algorithm_api.h"
#include "serialize/json/json_api.h"
#include "util/util_api.h"

namespace MR4C {

class JsonAlgorithmSerializerImpl {

	friend class JsonAlgorithmSerializer;

	private:

		std::string serializeAlgorithm(const Algorithm& algo) const {
			json_t* jsonAlgo = json_object();

			json_t* jsonInputs = stringSetToJson(algo.getInputDatasets());
			json_t* jsonOptional = stringSetToJson(algo.getOptionalInputDatasets());
			json_t* jsonExcluded = stringSetToJson(algo.getExcludedInputDatasets());
			json_t* jsonOutputs = stringSetToJson(algo.getOutputDatasets());
			json_t* jsonDimensions = dimensionsToJson(algo.getExpectedDimensions());
	
			json_object_set_new(jsonAlgo, "inputs", jsonInputs);
			json_object_set_new(jsonAlgo, "optionalInputs", jsonOptional);
			json_object_set_new(jsonAlgo, "excludedInputs", jsonExcluded);
			json_object_set_new(jsonAlgo, "outputs", jsonOutputs);
			json_object_set_new(jsonAlgo, "dimensions", jsonDimensions);

			return JanssonUtil::toStringAndFree(jsonAlgo);
		}

		void deserializeAlgorithm(const std::string& json, Algorithm& algo) const {
			json_t* jsonAlgo = JanssonUtil::fromString(json);
			json_t* jsonInputs = json_object_get(jsonAlgo, "inputs");
			json_t* jsonOptional = json_object_get(jsonAlgo, "optionalInputs");
			json_t* jsonExcluded = json_object_get(jsonAlgo, "excludedInputs");
			json_t* jsonOutputs = json_object_get(jsonAlgo, "outputs");
			json_t* jsonDimensions = json_object_get(jsonAlgo, "dimensions");

			addInputAlgorithmsFromJson(algo, jsonInputs, jsonOptional, jsonExcluded);
			addOutputAlgorithmsFromJson(algo, jsonOutputs);
			addDimensionsFromJson(algo, jsonDimensions);

			json_decref(jsonAlgo);
		}

		json_t* dimensionsToJson(const std::set<DataKeyDimension>& dims) const {
			std::set<std::string> strDims;
			for ( std::set<DataKeyDimension>::iterator iter = dims.begin(); iter!=dims.end(); iter++ ) {
				strDims.insert(iter->getName());
			}
			return stringSetToJson(strDims);
		}

		void addInputAlgorithmsFromJson(
			Algorithm& algo,
			const json_t* jsonInputs,
			const json_t* jsonOptional,
			const json_t* jsonExcluded
		) const {
		
			std::set<std::string> inputs = stringSetFromJsonArray(jsonInputs);
			std::set<std::string> optional = stringSetFromJsonArray(jsonOptional);
			std::set<std::string> excluded = stringSetFromJsonArray(jsonExcluded);
			for ( std::set<std::string>::iterator iter = inputs.begin(); iter!=inputs.end(); iter++ ) {
				std::string input = *iter;
				bool isOptional = optional.count(input)>0;
				bool isExcluded = excluded.count(input)>0;
				algo.addInputDataset(input, isOptional, isExcluded);
			}
		}

		void addOutputAlgorithmsFromJson(
			Algorithm& algo,
			const json_t* jsonOutputs
		) const {
			std::set<std::string> outputs = stringSetFromJsonArray(jsonOutputs);
			for ( std::set<std::string>::iterator iter = outputs.begin(); iter!=outputs.end(); iter++ ) {
				algo.addOutputDataset(*iter);
			}
		}
				
		void addDimensionsFromJson(
			Algorithm& algo,
			const json_t* jsonDimensions
		) const {
			std::set<std::string> strDims = stringSetFromJsonArray(jsonDimensions);
			for ( std::set<std::string>::iterator iter = strDims.begin(); iter!=strDims.end(); iter++ ) {
				DataKeyDimension dim(*iter);
				algo.addExpectedDimension(dim);
			}
		}
				
		json_t* stringSetToJson(const std::set<std::string>& strs) const {
			return JanssonUtil::stringSetToJson(strs);
		}

		std::set<std::string> stringSetFromJsonArray(const json_t* jsonArray) const {
			return JanssonUtil::stringSetFromJsonArray(jsonArray);
		}

};


JsonAlgorithmSerializer::JsonAlgorithmSerializer() {
	m_impl = new JsonAlgorithmSerializerImpl();
}

JsonAlgorithmSerializer::~JsonAlgorithmSerializer() {
	delete m_impl;
}

std::string JsonAlgorithmSerializer::serializeAlgorithm(const Algorithm& algo) const {
	return m_impl->serializeAlgorithm(algo);
}

void JsonAlgorithmSerializer::deserializeAlgorithm(const std::string& json, Algorithm& algo) const {
	m_impl->deserializeAlgorithm(json, algo);
}

std::string JsonAlgorithmSerializer::getContentType() const {
	return "application/json";
}

}


