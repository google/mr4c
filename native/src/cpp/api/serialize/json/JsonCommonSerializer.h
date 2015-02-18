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

#ifndef __MR4C_JSON_COMMON_SERIALIZER_H__
#define __MR4C_JSON_COMMON_SERIALIZER_H__

#include <string>
#include <jansson.h>

#include "dataset/dataset_api.h"

namespace MR4C {

class JsonCommonSerializer {

	public:

		static json_t* keyDimensionToJson(const DataKeyDimension& dim);

		static DataKeyDimension jsonToKeyDimension(const json_t* jsonDim);

		static json_t* keyElementToJson(const DataKeyElement& element);

		static DataKeyElement jsonToKeyElement(const json_t* jsonElement);

	private:

		// prevent calling these
		JsonCommonSerializer();
		JsonCommonSerializer(const JsonCommonSerializer& ser);
		JsonCommonSerializer& operator=(const JsonCommonSerializer& ser);


};

}
#endif

