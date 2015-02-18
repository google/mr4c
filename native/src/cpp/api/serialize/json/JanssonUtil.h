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

#ifndef __MR4C_JANSSON_UTIL_H__
#define __MR4C_JANSSON_UTIL_H__

#include <set>
#include <string>
#include <jansson.h>

namespace MR4C {

class JanssonUtil {

	public:

		static std::string toStringAndFree(json_t* jsonObject);

		static std::string toStringAndFree(json_t* jsonObject, size_t flags);

		static json_t* fromString(const std::string& json);

		static json_t* toJsonString(const std::string& str);

		static std::string fromJsonString(const json_t* jsonString);

		static json_t* stringSetToJson(const std::set<std::string>& strs);

		static std::set<std::string> stringSetFromJsonArray(const json_t* jsonArray);

		static void assertJsonType(const json_t* json, json_type type);

	private:

		// prevent calling these
		JanssonUtil();
		JanssonUtil(const JanssonUtil& util);
		JanssonUtil& operator=(const JanssonUtil& util);


};

}
#endif

