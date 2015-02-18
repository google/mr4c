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


#include "context/context_api.h"
#include "util/util_api.h"

namespace MR4C {

class MessageImpl {

	friend class Message;

	private:
		std::string m_topic;
		std::string m_content;
		std::string m_contentType;

		MessageImpl(
			const std::string& topic, 
			const std::string& content,
			const std::string& contentType
		) {
			init();
			m_topic = topic;
			m_content = content;
			m_contentType = contentType;
		}

		MessageImpl(const MessageImpl& msg) {
			initFrom(msg);
		}

		MessageImpl() {
			init();
		}


		void init() {
		}

		void initFrom(const MessageImpl&  msg) {
			init();
			m_topic = msg.m_topic;
			m_content = msg.m_content;
			m_contentType = msg.m_contentType;
		}

		bool operator==(const MessageImpl& msg) const {
			return
				m_topic==msg.m_topic && 
				m_content==msg.m_content && 
				m_contentType==msg.m_contentType;
		}

		std::string str() const {
			MR4C_RETURN_STRING("topic=[" << m_topic << "]; contentType=[" << m_contentType << "]; content=[" << m_content << "]");
		}

		~MessageImpl() {}
};


Message::Message(
	const std::string& topic, 
	const std::string& content,
	const std::string& contentType
) {
	m_impl = new MessageImpl(topic, content, contentType);
}

Message::Message(const Message& msg) {
	m_impl = new MessageImpl(*msg.m_impl);
}

Message::Message() {
	m_impl = new MessageImpl();
}

std::string Message::getTopic() const {
	return m_impl->m_topic;
}
		
std::string Message::getContentType() const {
	return m_impl->m_contentType;
}
		
std::string Message::getContent() const {
	return m_impl->m_content;
}
		
Message& Message::operator=(const Message& msg) {
	m_impl->initFrom(*msg.m_impl);
	return *this;
}

bool Message::operator==(const Message& msg) const {
	return *m_impl==*msg.m_impl;
}

bool Message::operator!=(const Message&  msg) const {
	return !operator==(msg);
}

std::string Message::str() const {
	return m_impl->str();
}

Message::~Message() {
	delete m_impl;
}

std::ostream& operator<<(std::ostream& os, const Message& msg) {
	os << msg.str();
	return os;
}


}

