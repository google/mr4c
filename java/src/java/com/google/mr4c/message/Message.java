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

package com.google.mr4c.message;

public class Message {

	private String m_topic;
	private String m_content;
	private String m_contentType;

	public Message(
		String topic,
		String content,
		String contentType
	) {
		m_topic = topic;
		m_content = content;
		m_contentType = contentType;
	}

	public String getTopic() {
		return m_topic;
	}

	public String getContent() {
		return m_content;
	}

	public String getContentType() {
		return m_contentType;
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		Message msg = (Message) obj;
		if ( !m_topic.equals(msg.m_topic) ) return false;
		if ( !m_content.equals(msg.m_content) ) return false;
		if ( !m_contentType.equals(msg.m_contentType) ) return false;
		return true;
	}

	public String toString() {
		return String.format("topic=[%s]; content=[%s]; contentType=[%s]", m_topic, m_content, m_contentType);
	}


}
