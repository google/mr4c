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

import com.google.mr4c.serialize.bean.DatasetBeanSerializer;
import com.google.mr4c.serialize.bean.dataset.DataFileBean;
import com.google.mr4c.serialize.bean.dataset.DatasetBean;
import com.google.mr4c.serialize.bean.keys.DataKeyBean;
import com.google.mr4c.serialize.bean.metadata.MetadataEntryBean;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class JsonDatasetBeanSerializer implements DatasetBeanSerializer {

	public void serializeDatasetBean(DatasetBean dataset, Writer writer) throws IOException {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();
 		gson.toJson(dataset,writer);
	}

	public DatasetBean deserializeDatasetBean(Reader reader) throws IOException {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter( MetadataEntryBean.class, new MetadataEntryBeanDeserializer());
		Gson gson = builder.create();
		return gson.fromJson(reader, DatasetBean.class);
	}

	public String serializeDataFileBean(DataFileBean file) {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();
 		return gson.toJson(file);
	}

	public DataFileBean deserializeDataFileBean(String serializedFile) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		return gson.fromJson(serializedFile, DataFileBean.class);
	}

	public String serializeDataKeyBean(DataKeyBean key) {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();
 		return gson.toJson(key);
	}

	public DataKeyBean deserializeDataKeyBean(String serializedKey) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		return gson.fromJson(serializedKey, DataKeyBean.class);
	}


	public String getContentType() {
		return "application/json";
	}


}

