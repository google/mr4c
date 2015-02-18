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

#include "error/error_api.h"
#include "serialize/json/json_api.h"

namespace MR4C {

class JsonErrorSerializerImpl {

	friend class JsonErrorSerializer;

	private:

		JsonErrorSerializerImpl() {}

		std::string serializeError(const Error& error) const {
			json_t* jsonSummary = JanssonUtil::toJsonString(error.getSummary());
			json_t* jsonDetail = JanssonUtil::toJsonString(error.getDetail());
			std::string strSeverity = Error::severityToString(error.getSeverity());
			json_t* jsonSeverity = JanssonUtil::toJsonString(strSeverity);
			json_t* jsonSource = JanssonUtil::toJsonString(error.getSource());
			json_t* jsonContent = json_object();
			json_object_set_new(jsonContent, "summary", jsonSummary);
			json_object_set_new(jsonContent, "detail", jsonDetail);
			json_object_set_new(jsonContent, "severity", jsonSeverity);
			json_object_set_new(jsonContent, "source", jsonSource);
			return JanssonUtil::toStringAndFree(jsonContent);
		}

		Error deserializeError(const std::string& json) const {
			json_t* jsonError = JanssonUtil::fromString(json);
			json_t* jsonSummary = json_object_get(jsonError, "summary");
			json_t* jsonDetail = json_object_get(jsonError, "detail");
			json_t* jsonSource = json_object_get(jsonError, "source");
			json_t* jsonSeverity = json_object_get(jsonError, "severity");
			std::string summary = JanssonUtil::fromJsonString(jsonSummary);
			std::string detail = JanssonUtil::fromJsonString(jsonDetail);
			std::string source = JanssonUtil::fromJsonString(jsonSource);
			std::string strSeverity = JanssonUtil::fromJsonString(jsonSeverity);
			Error::Severity severity = Error::severityFromString(strSeverity);
			json_decref(jsonError);
			return Error(summary, detail, source, severity);
		}

		~JsonErrorSerializerImpl() {}
};


JsonErrorSerializer::JsonErrorSerializer() {
	m_impl = new JsonErrorSerializerImpl();
}

std::string JsonErrorSerializer::serializeError(const Error& error) const {
	return m_impl->serializeError(error);
}

Error JsonErrorSerializer::deserializeError(const std::string& json) const {
	return m_impl->deserializeError(json);
}

JsonErrorSerializer::~JsonErrorSerializer() {
	delete m_impl;
}

}

