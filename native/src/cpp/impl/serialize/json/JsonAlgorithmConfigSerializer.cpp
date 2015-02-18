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

#include <iostream>
#include <sstream>
#include <stdexcept>
#include <jansson.h>

#include "algorithm/algorithm_api.h"
#include "serialize/json/json_api.h"

namespace MR4C {

class JsonAlgorithmConfigSerializerImpl {

	friend class JsonAlgorithmConfigSerializer;

	private:

		std::string serializeAlgorithmConfig(const AlgorithmConfig& config) const {
			json_t* jsonAlgorithmConfig = configToJson(config);
			return toStringAndFree(jsonAlgorithmConfig);
		}

		AlgorithmConfig deserializeAlgorithmConfig(const std::string& json) const {
			json_t* jsonAlgorithmConfig = fromString(json);
			AlgorithmConfig config = jsonToAlgorithmConfig(jsonAlgorithmConfig);
			json_decref(jsonAlgorithmConfig);
			return config;
		}

		std::string toStringAndFree(json_t* jsonObject) const {
			return JanssonUtil::toStringAndFree(jsonObject);
		}

		json_t* fromString(const std::string& json) const {
			return JanssonUtil::fromString(json);
		}

		json_t* toJsonString(const std::string& str) const {
			return JanssonUtil::toJsonString(str);
		}

		std::string fromJsonString(const json_t* jsonString) const {
			return JanssonUtil::fromJsonString(jsonString);
		}

		json_t* configToJson(const AlgorithmConfig& config) const {
			json_t* jsonAlgorithmConfig = json_object();
			std::set<std::string> names = config.getAllParamNames();
			for ( std::set<std::string>::iterator iter = names.begin(); iter!=names.end(); iter++ ) {
				std::string name = *iter;
				std::string val = config.getConfigParam(name);
				json_t* jsonVal = toJsonString(val);
				json_object_set_new(jsonAlgorithmConfig, name.c_str(), jsonVal);
			}
			return jsonAlgorithmConfig;
		}

		AlgorithmConfig jsonToAlgorithmConfig(/*const*/ json_t* jsonAlgorithmConfig) const {
			JanssonUtil::assertJsonType(jsonAlgorithmConfig, JSON_OBJECT);
			std::map<std::string,std::string> params;
			void* iter = json_object_iter(jsonAlgorithmConfig); // can't have const because of this call
			while (iter) {
				std::string name = std::string(json_object_iter_key(iter));
				std::string val = fromJsonString(json_object_iter_value(iter));
				params[name]=val;
				iter = json_object_iter_next(jsonAlgorithmConfig,iter);
			}
			return AlgorithmConfig(params);
		}
				
};


JsonAlgorithmConfigSerializer::JsonAlgorithmConfigSerializer() {
	m_impl = new JsonAlgorithmConfigSerializerImpl();
}

JsonAlgorithmConfigSerializer::~JsonAlgorithmConfigSerializer() {
	delete m_impl;
}

std::string JsonAlgorithmConfigSerializer::serializeAlgorithmConfig(const AlgorithmConfig& config) const {
	return m_impl->serializeAlgorithmConfig(config);
}

AlgorithmConfig JsonAlgorithmConfigSerializer::deserializeAlgorithmConfig(const std::string& json) const {
	return m_impl->deserializeAlgorithmConfig(json);
}

std::string JsonAlgorithmConfigSerializer::getContentType() const {
	return "application/json";
}

}


