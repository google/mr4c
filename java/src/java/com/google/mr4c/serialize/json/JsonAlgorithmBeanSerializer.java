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

package com.google.mr4c.serialize.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.mr4c.serialize.bean.AlgorithmBeanSerializer;
import com.google.mr4c.serialize.bean.algorithm.AlgorithmSchemaBean;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class JsonAlgorithmBeanSerializer implements AlgorithmBeanSerializer {

	public void serializeAlgorithmSchemaBean(AlgorithmSchemaBean algoSchema, Writer writer) throws IOException {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();
 		gson.toJson(algoSchema,writer);
	}

	public AlgorithmSchemaBean deserializeAlgorithmSchemaBean(Reader reader) throws IOException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		return gson.fromJson(reader, AlgorithmSchemaBean.class);
	}

	public String getContentType() {
		return "application/json";
	}


}

