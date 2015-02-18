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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import com.google.mr4c.config.Document;
import com.google.mr4c.config.algorithm.AlgorithmConfig;
import com.google.mr4c.config.diff.DiffConfig;
import com.google.mr4c.config.execution.DatasetConfig;
import com.google.mr4c.config.execution.DirectoryConfig;
import com.google.mr4c.config.execution.ExecutionConfig;
import com.google.mr4c.config.execution.LocationsConfig;
import com.google.mr4c.config.execution.MapConfig;
import com.google.mr4c.config.site.SiteConfig;
import com.google.mr4c.serialize.ConfigSerializer;
import com.google.mr4c.config.test.AlgoTestConfig;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class JsonConfigSerializer implements ConfigSerializer {

	public void serializeAlgorithmConfig(AlgorithmConfig config, Writer writer) throws IOException {
		Gson gson = buildGson();
 		gson.toJson(config,writer);
	}

	public AlgorithmConfig deserializeAlgorithmConfig(Reader reader) throws IOException {
		Gson gson = buildGson();
		return gson.fromJson(reader, AlgorithmConfig.class);
	}


	public void serializeDatasetConfig(DatasetConfig config, Writer writer) throws IOException {
		Gson gson = buildGson();
 		gson.toJson(config,writer);
	}

	public DatasetConfig deserializeDatasetConfig(Reader reader) throws IOException {
		Gson gson = buildGson();
		return gson.fromJson(reader, DatasetConfig.class);
	}

	public void serializeExecutionConfig(ExecutionConfig config, Writer writer) throws IOException {
		Gson gson = buildGson();
 		gson.toJson(config,writer);
	}

	public ExecutionConfig deserializeExecutionConfig(Reader reader) throws IOException {
		Gson gson = buildGson();
		return gson.fromJson(reader, ExecutionConfig.class);
	}

	public void serializeMapConfig(MapConfig config, Writer writer) throws IOException {
		Gson gson = buildGson();
 		gson.toJson(config,writer);
	}

	public MapConfig deserializeMapConfig(Reader reader) throws IOException {
		Gson gson = buildGson();
		return gson.fromJson(reader, MapConfig.class);
	}

	public void serializeDirectoryConfig(DirectoryConfig config, Writer writer) throws IOException {
		Gson gson = buildGson();
 		gson.toJson(config,writer);
	}

	public DirectoryConfig deserializeDirectoryConfig(Reader reader) throws IOException {
		Gson gson = buildGson();
		return gson.fromJson(reader, DirectoryConfig.class);
	}

	public void serializeSiteConfig(SiteConfig config, Writer writer) throws IOException {
		Gson gson = buildGson();
 		gson.toJson(config,writer);
	}

	public SiteConfig deserializeSiteConfig(Reader reader) throws IOException {
		Gson gson = buildGson();
		return gson.fromJson(reader, SiteConfig.class);
	}

	public void serializeDiffConfig(DiffConfig config, Writer writer) throws IOException {
		Gson gson = buildGson();
 		gson.toJson(config,writer);
	}

	public DiffConfig deserializeDiffConfig(Reader reader) throws IOException {
		Gson gson = buildGson();
		return gson.fromJson(reader, DiffConfig.class);
	}

	public void serializeAlgoTestConfig(AlgoTestConfig config, Writer writer) throws IOException {
		Gson gson = buildGson();
 		gson.toJson(config,writer);
	}

	public AlgoTestConfig deserializeAlgoTestConfig(Reader reader) throws IOException {
		Gson gson = buildGson();
		return gson.fromJson(reader, AlgoTestConfig.class);
	}

	public void serializeLocationsConfig(LocationsConfig config, Writer writer) throws IOException {
		Gson gson = buildGson();
		if ( config.hasMap() ) {
 			gson.toJson(config.getMap(),writer);
		} else if ( config.hasList() ) {
 			gson.toJson(config.getList(),writer);
		} else {
			throw new IllegalArgumentException("LocationsConfig doesn't have any data");
		}
	}

	public LocationsConfig deserializeLocationsConfig(Reader reader) throws IOException {
		Gson gson = buildGson();
		JsonParser parser  = new JsonParser();
		JsonElement jsonEle = parser.parse(reader);
		if ( jsonEle.isJsonObject() ) {
			Map<String,URI> locationMap = gson.fromJson(jsonEle, new TypeToken<Map<String,URI>>(){}.getType());
			return new LocationsConfig(locationMap);
		} else if ( jsonEle.isJsonArray() ) {
			List<URI> locationList = gson.fromJson(jsonEle, new TypeToken<List<URI>>(){}.getType());
			return new LocationsConfig(locationList);
		} else {
			throw new IllegalArgumentException("Need JSON Array or Object to deserialize LocationsConfig");
		}
	}


	public String getContentType() {
		return "application/json";
	}

	private Gson buildGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		builder.registerTypeAdapter( Document.class, new DocumentSerializer());
		return builder.create();
	}

}

