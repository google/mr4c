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

#include "serialize/json/json_api.h"
#include "util/util_api.h"

namespace MR4C {

std::string jsonTypeEnumToString(int type) {
	switch(type) {
		case JSON_OBJECT :
			return "OBJECT";
		case JSON_ARRAY :
			return "ARRAY";
		case JSON_STRING :
			return "STRING";
		case JSON_INTEGER :
			return "INTEGER";
		case JSON_REAL :
			return "REAL";
		case JSON_TRUE :
			return "TRUE";
		case JSON_FALSE :
			return "FALSE";
		case JSON_NULL :
			return "NULL";
		default:
			return "unknown";
	}
}

std::string JanssonUtil::toStringAndFree(json_t* jsonObject) {
	return toStringAndFree(jsonObject, 0);
}

std::string JanssonUtil::toStringAndFree(json_t* jsonObject, size_t flags) {
	char* jsonChars = json_dumps(jsonObject, flags);
	std::string jsonStr = std::string(jsonChars); 
	free(jsonChars);
	json_decref(jsonObject);
	return jsonStr;
}

json_t* JanssonUtil::fromString(const std::string& json) {
	json_error_t error;
	json_t* jsonObject = json_loads(json.c_str(), 0, &error);
	if ( jsonObject==NULL ) {
		MR4C_THROW( std::runtime_error, "Jansson error: " << error.text << "; line " << error.line << "; column " << error.column);
	}
	return jsonObject;
}

json_t* JanssonUtil::toJsonString(const std::string& str) {
	const char* chars = str.c_str();
	json_t* jsonString = json_string(chars);
	if ( jsonString==NULL ) {
		MR4C_THROW( std::runtime_error, "Jansson error: " << "toJsonString() failed for string=[" << str << "]");
	}
	return jsonString;
}

std::string JanssonUtil::fromJsonString(const json_t* jsonString) {
	assertJsonType(jsonString, JSON_STRING);
	const char* chars = json_string_value(jsonString);
	if ( chars==NULL ) {
		throw std::runtime_error("fromJsonString() failed");
	}
	return std::string(chars);
}


json_t* JanssonUtil::stringSetToJson(const std::set<std::string>& strs) {
	json_t* jsonArray = json_array();
	for ( std::set<std::string>::iterator iter = strs.begin(); iter!=strs.end(); iter++ ) {
		json_array_append_new(jsonArray, toJsonString(*iter));
	}
	return jsonArray;
}

std::set<std::string> JanssonUtil::stringSetFromJsonArray(const json_t* jsonArray) {
	std::set<std::string> strs;
	if ( jsonArray==NULL ) {
		return strs;
	}
	assertJsonType(jsonArray, JSON_ARRAY);
	size_t size = json_array_size(jsonArray);
	for (size_t i=0; i<size; i++ ) {
		json_t* jsonString = json_array_get(jsonArray,i);
		assertJsonType(jsonString, JSON_STRING);
		std::string str = fromJsonString(jsonString);
		strs.insert(str);
	}
	return strs;
}

void JanssonUtil::assertJsonType(const json_t* json, json_type type) {
	int actualType = json_typeof(json);
	if ( actualType!=type ) {
		std::string actualStr = jsonTypeEnumToString(actualType);
		std::string expectedStr = jsonTypeEnumToString(type);
		MR4C_THROW( std::runtime_error, "JSON object is type " << actualStr << "; expected " << expectedStr);
	}
}

}
