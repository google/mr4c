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


import com.google.mr4c.config.algorithm.AlgorithmConfig;
import com.google.mr4c.config.diff.DiffConfig;
import com.google.mr4c.config.execution.DatasetConfig;
import com.google.mr4c.config.execution.DirectoryConfig;
import com.google.mr4c.config.execution.ExecutionConfig;
import com.google.mr4c.config.execution.LocationsConfig;
import com.google.mr4c.config.execution.MapConfig;
import com.google.mr4c.config.site.SiteConfig;
import com.google.mr4c.config.test.AlgoTestConfig;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface ConfigSerializer extends Serializer {

	void serializeAlgorithmConfig(AlgorithmConfig config, Writer writer) throws IOException;

	AlgorithmConfig deserializeAlgorithmConfig(Reader reader) throws IOException;

	void serializeDatasetConfig(DatasetConfig config, Writer writer) throws IOException;

	DatasetConfig deserializeDatasetConfig(Reader reader) throws IOException;

	void serializeExecutionConfig(ExecutionConfig config, Writer writer) throws IOException;

	ExecutionConfig deserializeExecutionConfig(Reader reader) throws IOException;

	void serializeMapConfig(MapConfig config, Writer writer) throws IOException;

	MapConfig deserializeMapConfig(Reader reader) throws IOException;

	void serializeDirectoryConfig(DirectoryConfig config, Writer writer) throws IOException;

	DirectoryConfig deserializeDirectoryConfig(Reader reader) throws IOException;

	void serializeSiteConfig(SiteConfig config, Writer writer) throws IOException;

	SiteConfig deserializeSiteConfig(Reader reader) throws IOException;

	void serializeDiffConfig(DiffConfig config, Writer writer) throws IOException;

	DiffConfig deserializeDiffConfig(Reader reader) throws IOException;

	void serializeAlgoTestConfig(AlgoTestConfig config, Writer writer) throws IOException;

	AlgoTestConfig deserializeAlgoTestConfig(Reader reader) throws IOException;

	void serializeLocationsConfig(LocationsConfig config, Writer writer) throws IOException;

	LocationsConfig deserializeLocationsConfig(Reader reader) throws IOException;

}
