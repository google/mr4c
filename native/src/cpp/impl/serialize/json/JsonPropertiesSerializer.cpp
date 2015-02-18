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

#include "util/util_api.h"
#include "serialize/json/json_api.h"

namespace MR4C {

class JsonPropertiesSerializerImpl {

	friend class JsonPropertiesSerializer;

	private:

		std::string serializeProperties(const Properties& props) const {
			json_t* jsonProperties = propsToJson(props);
			return toStringAndFree(jsonProperties);
		}

		Properties deserializeProperties(const std::string& json) const {
			json_t* jsonProperties = fromString(json);
			Properties props = jsonToProperties(jsonProperties);
			json_decref(jsonProperties);
			return props;
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

		json_t* propsToJson(const Properties& props) const {
			json_t* jsonProperties = json_object();
			std::set<std::string> names = props.getAllPropertyNames();
			for ( std::set<std::string>::iterator iter = names.begin(); iter!=names.end(); iter++ ) {
				std::string name = *iter;
				std::string val = props.getProperty(name);
				json_t* jsonVal = toJsonString(val);
				json_object_set_new(jsonProperties, name.c_str(), jsonVal);
			}
			return jsonProperties;
		}

		Properties jsonToProperties(/*const*/ json_t* jsonProperties) const {
			JanssonUtil::assertJsonType(jsonProperties, JSON_OBJECT);
			std::map<std::string,std::string> params;
			void* iter = json_object_iter(jsonProperties); // can't have const because of this call
			while (iter) {
				std::string name = std::string(json_object_iter_key(iter));
				std::string val = fromJsonString(json_object_iter_value(iter));
				params[name]=val;
				iter = json_object_iter_next(jsonProperties,iter);
			}
			return Properties(params);
		}
				
};


JsonPropertiesSerializer::JsonPropertiesSerializer() {
	m_impl = new JsonPropertiesSerializerImpl();
}

JsonPropertiesSerializer::~JsonPropertiesSerializer() {
	delete m_impl;
}

std::string JsonPropertiesSerializer::serializeProperties(const Properties& props) const {
	return m_impl->serializeProperties(props);
}

Properties JsonPropertiesSerializer::deserializeProperties(const std::string& json) const {
	return m_impl->deserializeProperties(json);
}

std::string JsonPropertiesSerializer::getContentType() const {
	return "application/json";
}

}


