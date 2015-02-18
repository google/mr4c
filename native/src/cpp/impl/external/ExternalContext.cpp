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

#include <string>

#include "external/external_api.h"

namespace MR4C {

class ExternalContextImpl : public Logger, public ProgressReporter, public MessageConsumer  {

	friend class ExternalContext;

	private:

		CExternalContextCallbacks m_callbacks;

		ExternalContextImpl(const CExternalContextCallbacks& callbacks) {
			m_callbacks=callbacks;
		}

		void log(Logger::LogLevel level, const std::string& msg) {
			std::string strLevel = Logger::enumToString(level);
			m_callbacks.logCallback(strLevel.c_str(), msg.c_str()); 
		}

		void progress(float percentDone, const std::string& msg) {
			m_callbacks.progressCallback(percentDone, msg.c_str());
		}

		void receiveMessage(const Message& msg) {
			CExternalContextMessage extMsg;
			extMsg.topic = msg.getTopic().c_str();
			extMsg.content = msg.getContent().c_str();
			extMsg.contentType = msg.getContentType().c_str();
			m_callbacks.messageCallback(extMsg);
		}

		void reportFailure(const char* msg) {
			m_callbacks.failureCallback(msg);
		}

		void initContext(AlgorithmContext& context) {
			context.registerLogger(this);
			context.registerProgressReporter(this);
			context.registerMessageConsumer(this);
		}

};

ExternalContext::ExternalContext(const CExternalContextCallbacks& callbacks) {
	m_impl = new ExternalContextImpl(callbacks);
}

void ExternalContext::initContext(AlgorithmContext& context) {
	m_impl->initContext(context);
}

void ExternalContext::reportFailure(const char* msg) {
	m_impl->reportFailure(msg);
}

ExternalContext::~ExternalContext() {
	delete m_impl;
}

}

