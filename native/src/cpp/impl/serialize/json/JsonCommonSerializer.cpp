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

#include "keys/keys_api.h"
#include "serialize/json/json_api.h"

namespace MR4C {

json_t* JsonCommonSerializer::keyDimensionToJson(const DataKeyDimension& dim) {
	return JanssonUtil::toJsonString(dim.getName());
}

DataKeyDimension JsonCommonSerializer::jsonToKeyDimension(const json_t* jsonDim) {
	return DataKeyDimension(JanssonUtil::fromJsonString(jsonDim));
}
			
json_t* JsonCommonSerializer::keyElementToJson(const DataKeyElement& element) {
	json_t* jsonId = JanssonUtil::toJsonString(element.getIdentifier());
	json_t* jsonDim = keyDimensionToJson(element.getDimension());
	json_t* jsonElement = json_object();
	json_object_set_new(jsonElement, "identifier", jsonId);
	json_object_set_new(jsonElement, "dimension", jsonDim);
	return jsonElement;
}

DataKeyElement JsonCommonSerializer::jsonToKeyElement(const json_t* jsonElement) {
	JanssonUtil::assertJsonType(jsonElement, JSON_OBJECT);
	json_t* jsonId = json_object_get(jsonElement, "identifier");
	json_t* jsonDim = json_object_get(jsonElement, "dimension");
	std::string id = JanssonUtil::fromJsonString(jsonId);
	DataKeyDimension dim = jsonToKeyDimension(jsonDim);
	return DataKeyElement(id, dim);
}
			
}

