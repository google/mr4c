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

package com.google.mr4c.config.execution;

import com.google.mr4c.config.ConfigDescriptor;
import com.google.mr4c.config.Document;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class ConfigTestUtils {

	public static MapConfig buildMapConfig1() {
		MapConfig mapConfig = new MapConfig();
		mapConfig.addDimension(new DimensionConfig("frame", "FRAME"));
		mapConfig.addDimension(new DimensionConfig("sensor", "SENSOR"));
		DimensionConfig dimConfig = new DimensionConfig("type", "IMAGE_TYPE");
		dimConfig.addValue( new ValueConfig("pan", "PAN") );
		dimConfig.addValue( new ValueConfig("ms", "MS") );
		dimConfig.addValue( new ValueConfig("mask", "MS_MASK") );
		mapConfig.addDimension(dimConfig);
		return mapConfig;
	}

	public static MapConfig buildMapConfig2() {
		MapConfig mapConfig = new MapConfig();
		mapConfig.addDimension(new DimensionConfig("frame", "FRAME"));
		mapConfig.addDimension(new DimensionConfig("sensor", "SENSOR"));
		DimensionConfig dimConfig = new DimensionConfig("type");
		dimConfig.addValue( new ValueConfig("pan", "PAN") );
		dimConfig.addValue( new ValueConfig("ms", "MS") );
		mapConfig.addDimension(dimConfig);
		return mapConfig;
	}

	public static DatasetConfig buildDatasetConfig1() throws URISyntaxException {

		ConfigDescriptor srcConfig = new ConfigDescriptor("datasetConfig1");
		URI location = new URI("file:///some/path");
		ConfigDescriptor mapConfig = new ConfigDescriptor("mapConfig1");
		DatasetConfig config =  new DatasetConfig( "scheme1", location);
		config.setSourceConfig(srcConfig);
		config.setMapConfig(mapConfig);
		return config;
	}

	public static DatasetConfig buildDatasetConfig2() throws URISyntaxException {

		ConfigDescriptor srcConfig = new ConfigDescriptor("datasetConfig2");
		URI location = new URI("file:///some/path");
		DatasetConfig config =  new DatasetConfig( "scheme2", location);
		config.setSourceConfig(srcConfig);
		return config;
	}

	public static DatasetConfig buildDatasetConfig3() throws URISyntaxException {
		Document inline = new Document("{ \"key1\" : \"val1\" }");
		ConfigDescriptor srcConfig = new ConfigDescriptor(inline);
		URI location = new URI("file:///some/path");
		DatasetConfig config =  new DatasetConfig( "scheme2", location);
		config.setSourceConfig(srcConfig);
		return config;
	}

	public static ExecutionConfig buildExecutionConfig1() throws URISyntaxException {
		ConfigDescriptor algoConfig = new ConfigDescriptor("algo1");
		ExecutionConfig config = new ExecutionConfig(algoConfig);
		config.addInputDataset("input1", buildDatasetConfig1());
		config.addInputDataset("input2", buildDatasetConfig2());
		config.addOutputDataset("output", buildDatasetConfig1());
		Properties params = config.getParameters();
		params.setProperty("prop1", "val1");
		params.setProperty("prop2", "val2");
		return config;
	}

	public static ExecutionConfig buildExecutionConfig2() throws URISyntaxException {
		ConfigDescriptor algoConfig = new ConfigDescriptor("algo2");
		ExecutionConfig config = new ExecutionConfig(algoConfig);
		config.addInputDataset("input1", buildDatasetConfig1());
		config.addInputDataset("input2", buildDatasetConfig2());
		config.addOutputDataset("output1", buildDatasetConfig1());
		config.addOutputDataset("output2", buildDatasetConfig2());
		Properties params = config.getParameters();
		params.setProperty("prop3", "val3");
		return config;
	}

	public static PatternMapperConfig buildPatternMapperConfig1() throws URISyntaxException {
		return  new PatternMapperConfig("img${dim1}_${dim2}.${dim3}", "dim1", "dim2", "dim3");
	}

	public static PatternMapperConfig buildPatternMapperConfig2() throws URISyntaxException {
		return  new PatternMapperConfig("img${dim1}_${dim2}.jpg", "dim1", "dim2");
	}

	public static DirectoryConfig buildDirectoryConfig1() throws URISyntaxException {
		DirectoryConfig config = new DirectoryConfig(buildPatternMapperConfig1());
		config.setIgnore(true);
		return config;
	}

	public static DirectoryConfig buildDirectoryConfig2() throws URISyntaxException {
		DirectoryConfig config = new DirectoryConfig();
		config.addMapper(buildPatternMapperConfig1());
		config.addMapper(buildPatternMapperConfig2());
		config.setIgnore(false);
		return config;
	}

	public static LocationsConfig buildLocationsConfig1() {
		return new LocationsConfig(
			Arrays.asList(
				URI.create("file:///file1"),
				URI.create("file:///file2"),
				URI.create("file:///file3")
			)
		);
	}

	public static LocationsConfig buildLocationsConfig2() {
		Map<String,URI> map = new HashMap<String,URI>();
		map.put("key1", URI.create("file:///file1"));
		map.put("key2", URI.create("file:///file2"));
		map.put("key3", URI.create("file:///file3"));
		return new LocationsConfig(map);
	}

}
