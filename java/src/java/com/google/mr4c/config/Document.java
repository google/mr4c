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

package com.google.mr4c.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.commons.lang3.ObjectUtils;

/**
  * Very thin wrapper around arbitrary JSON content
*/
public class Document {

	private Gson m_gson = new Gson();
	private JsonParser m_parser = new JsonParser();
	private JsonElement m_content;

	public Document(String content) {
		if ( content==null ) {
			throw new IllegalArgumentException("content can't be null");
		}
		m_content = m_parser.parse(content);
	}

	public Document(JsonElement content) {
		if ( content==null ) {
			throw new IllegalArgumentException("content can't be null");
		}
		m_content = content;
	}

	public JsonElement getContent() {
		return m_content;
	}

	public String getContentAsString() {
		return m_gson.toJson(m_content);
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		Document doc = (Document) obj;
		if ( !ObjectUtils.equals(m_content, doc.m_content) ) return false;
		return true; 
	}

	public int hashCode() {
		return m_content.hashCode();
	}

	public String toString() {
		return m_content.toString();
	}

}
