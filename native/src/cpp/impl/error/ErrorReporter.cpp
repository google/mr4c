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


#include "error/error_api.h"

namespace MR4C {

class ErrorReporterImpl {

	friend class ErrorReporter;

	private:
		AlgorithmContext* m_context;
		std::string m_topic;
		JsonErrorSerializer* m_serializer;

		ErrorReporterImpl(AlgorithmContext& context, const std::string topic) {
			m_context = &context;
			m_topic = topic;
			m_serializer = new JsonErrorSerializer();
		}

		std::string getTopic() const {
			return m_topic;
		}

		void reportError(const Error& error) const {
			std::string content = m_serializer->serializeError(error);
			Message msg(
				m_topic,
				content,
				"application/json"
			);
			m_context->sendMessage(msg);
		}
			
		~ErrorReporterImpl() {
			delete m_serializer;
		}

};

ErrorReporter::ErrorReporter(AlgorithmContext& context, const std::string& topic) {
	m_impl = new ErrorReporterImpl(context, topic);
}

std::string ErrorReporter::getTopic() const {
	return m_impl->getTopic();
}

void ErrorReporter::reportError(const Error& error) const {
	m_impl->reportError(error);
}

ErrorReporter::~ErrorReporter() {
	delete m_impl;
}

}
