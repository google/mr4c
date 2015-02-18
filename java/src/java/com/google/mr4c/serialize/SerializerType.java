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

package com.google.mr4c.serialize;

import java.util.Map;
import java.util.HashMap;

public enum SerializerType {

	JSON("application/json");

	private String m_contentType;

	private static Map<String,SerializerType> s_contentTypeMap = new HashMap<String,SerializerType>();

	static {
		mapToContentType(JSON);
	}

	private static void mapToContentType(SerializerType type) {
		s_contentTypeMap.put(type.getContentType(), type);
	}

	SerializerType(String contentType) {
		m_contentType = contentType;
	}

	public String getContentType() {
		return m_contentType;
	}

	public static boolean hasContentType(String contentType) {
		return s_contentTypeMap.containsKey(contentType);
	}

	public static SerializerType getByContentType(String contentType) {
		if (!hasContentType(contentType) ) {
			throw new IllegalArgumentException(String.format("No serializers for content type =[%s]", contentType));
		}
		return s_contentTypeMap.get(contentType);
	}
}

