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

import com.google.mr4c.serialize.bean.BeanBasedAlgorithmSerializer;
import com.google.mr4c.serialize.bean.BeanBasedDatasetSerializer;
import com.google.mr4c.serialize.bean.BeanBasedKeyspaceSerializer;
import com.google.mr4c.serialize.json.JsonConfigSerializer;
import com.google.mr4c.serialize.json.JsonAlgorithmBeanSerializer;
import com.google.mr4c.serialize.json.JsonDatasetBeanSerializer;
import com.google.mr4c.serialize.json.JsonKeyspaceBeanSerializer;
import com.google.mr4c.serialize.json.JsonPropertiesSerializer;

import java.util.Map;
import java.util.HashMap;

import org.apache.hadoop.fs.Path;

public abstract class SerializerFactories {

	private static Map<String,SerializerFactory> s_factories = new HashMap<String,SerializerFactory>();

	public static SerializerFactory getSerializerFactory(String contentType) {
		if ( !s_factories.containsKey(contentType) ) {
			throw new IllegalArgumentException(String.format("No serializer factory for contentType=[%s]", contentType));
		}
		return s_factories.get(contentType);
	}

	private static class JsonSerializerFactory implements SerializerFactory {

		public AlgorithmSerializer createAlgorithmSerializer() {
			return new BeanBasedAlgorithmSerializer(
				new JsonAlgorithmBeanSerializer()
			);
		}
	
		public ConfigSerializer createConfigSerializer() {
			return new JsonConfigSerializer();
		}
	
		public DatasetSerializer createDatasetSerializer() {
			return new BeanBasedDatasetSerializer(
				new JsonDatasetBeanSerializer()
			);
		}
	
		public KeyspaceSerializer createKeyspaceSerializer() {
			return new BeanBasedKeyspaceSerializer(
				new JsonKeyspaceBeanSerializer()
			);
		}
	
		public PropertiesSerializer createPropertiesSerializer() {
			return new JsonPropertiesSerializer();
		}
	
		public String getContentType() {
			return "application/json";
		}
	}

	static { s_factories.put("application/json", new JsonSerializerFactory()); }
		
		
}

