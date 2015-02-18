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

#include "keys/keys_api.h"
#include "serialize/json/json_api.h"

namespace MR4C {

class JsonKeyspaceSerializerImpl {

	friend class JsonKeyspaceSerializer;

	private:

		std::string serializeKeyspace(const Keyspace& keyspace) const {
			json_t* jsonKeyspace = keyspaceToJson(keyspace);
			return toStringAndFree(jsonKeyspace);
		}

		Keyspace deserializeKeyspace(const std::string& json) const {
			json_t* jsonKeyspace = fromString(json);
			Keyspace keyspace = jsonToKeyspace(jsonKeyspace);
			json_decref(jsonKeyspace);
			return keyspace;
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

		json_t* keyspaceToJson(const Keyspace& keyspace) const {
			json_t* jsonKeyspace = json_object();
			json_t* jsonDimensions = json_array();
			std::set<DataKeyDimension> dimensions = keyspace.getDimensions();
			for ( std::set<DataKeyDimension>::iterator iter = dimensions.begin(); iter!=dimensions.end(); iter++ ) {
				DataKeyDimension dim = *iter;
				KeyspaceDimension ksd = keyspace.getKeyspaceDimension(dim);
				json_t* jsonDimension = keyspaceDimensionToJson(ksd);
				json_array_append_new(jsonDimensions, jsonDimension);
			}
			json_object_set_new(jsonKeyspace, "dimensions", jsonDimensions);
			return jsonKeyspace;
		}

		Keyspace jsonToKeyspace(const json_t* jsonKeyspace) const {
			JanssonUtil::assertJsonType(jsonKeyspace, JSON_OBJECT);
			json_t* jsonDimensions = json_object_get(jsonKeyspace, "dimensions");
			JanssonUtil::assertJsonType(jsonDimensions, JSON_ARRAY);
			size_t size = json_array_size(jsonDimensions);
			std::set<KeyspaceDimension> dimensions;
			for ( size_t i=0; i<size; i++ ) {
				json_t* jsonDimension = json_array_get(jsonDimensions, i);
				dimensions.insert(jsonToKeyspaceDimension(jsonDimension));
			}
			return Keyspace(dimensions);
		}





		json_t* keyspaceDimensionToJson(const KeyspaceDimension& ksd) const {
			json_t* jsonKsd = json_object();
			DataKeyDimension dim = ksd.getDimension();
			json_t* jsonDimension = JsonCommonSerializer::keyDimensionToJson(dim);
			json_t* jsonElements = json_array();
			std::vector<DataKeyElement> elements = ksd.getElements();
			for ( std::vector<DataKeyElement>::const_iterator iter = elements.begin(); iter!=elements.end(); iter++ ) {
				DataKeyElement element = *iter;
				json_t* jsonElement = JsonCommonSerializer::keyElementToJson(element);
				json_array_append_new(jsonElements, jsonElement);
			}
			json_object_set_new(jsonKsd, "dimension", jsonDimension);
			json_object_set_new(jsonKsd, "elements", jsonElements);
			return jsonKsd;
		}

		KeyspaceDimension jsonToKeyspaceDimension(const json_t* jsonKsd) const {
			JanssonUtil::assertJsonType(jsonKsd, JSON_OBJECT);
			json_t* jsonDimension = json_object_get(jsonKsd, "dimension");
			DataKeyDimension dim = JsonCommonSerializer::jsonToKeyDimension(jsonDimension);
			json_t* jsonElements = json_object_get(jsonKsd, "elements");
			JanssonUtil::assertJsonType(jsonElements, JSON_ARRAY);
			size_t size = json_array_size(jsonElements);
			std::vector<DataKeyElement> elements;
			for ( size_t i=0; i<size; i++ ) {
				json_t* jsonElement = json_array_get(jsonElements, i);
				elements.push_back(JsonCommonSerializer::jsonToKeyElement(jsonElement));
			}
			return KeyspaceDimension(dim,elements);
		}

};


JsonKeyspaceSerializer::JsonKeyspaceSerializer() {
	m_impl = new JsonKeyspaceSerializerImpl();
}

JsonKeyspaceSerializer::~JsonKeyspaceSerializer() {
	delete m_impl;
}

std::string JsonKeyspaceSerializer::serializeKeyspace(const Keyspace& keyspace) const {
	return m_impl->serializeKeyspace(keyspace);
}

Keyspace JsonKeyspaceSerializer::deserializeKeyspace(const std::string& json) const {
	return m_impl->deserializeKeyspace(json);
}

std::string JsonKeyspaceSerializer::getContentType() const {
	return "application/json";
}

}


