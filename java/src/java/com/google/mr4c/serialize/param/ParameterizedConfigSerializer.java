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

package com.google.mr4c.serialize.param;

import com.google.mr4c.config.ConfigUtils;
import com.google.mr4c.config.algorithm.AlgorithmConfig;
import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.Category;
import com.google.mr4c.config.diff.DiffConfig;
import com.google.mr4c.config.execution.DatasetConfig;
import com.google.mr4c.config.execution.DirectoryConfig;
import com.google.mr4c.config.execution.ExecutionConfig;
import com.google.mr4c.config.execution.LocationsConfig;
import com.google.mr4c.config.execution.MapConfig;
import com.google.mr4c.config.site.SiteConfig;
import com.google.mr4c.config.test.AlgoTestConfig;
import com.google.mr4c.serialize.ConfigSerializer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public class ParameterizedConfigSerializer implements ConfigSerializer {

	private ConfigSerializer m_serializer;
	private Properties m_params;

	public ParameterizedConfigSerializer(ConfigSerializer serializer) {
		m_serializer = serializer;
	}

	public ParameterizedConfigSerializer(ConfigSerializer serializer, Properties params) {
		this(serializer);
		m_params = params;
	}

	public void serializeAlgorithmConfig(AlgorithmConfig config, Writer writer) throws IOException {
		m_serializer.serializeAlgorithmConfig(config,writer);
	}

	public AlgorithmConfig deserializeAlgorithmConfig(Reader reader) throws IOException {
		return m_serializer.deserializeAlgorithmConfig(applyConfigProperties(reader));
	}

	public void serializeDatasetConfig(DatasetConfig config, Writer writer) throws IOException {
		m_serializer.serializeDatasetConfig(config,writer);
	}

	public DatasetConfig deserializeDatasetConfig(Reader reader) throws IOException {
		return m_serializer.deserializeDatasetConfig(applyConfigProperties(reader));
	}

	public void serializeExecutionConfig(ExecutionConfig config, Writer writer) throws IOException {
		m_serializer.serializeExecutionConfig(config,writer);
	}

	public ExecutionConfig deserializeExecutionConfig(Reader reader) throws IOException {
		return m_serializer.deserializeExecutionConfig(applyConfigProperties(reader));
	}

	public void serializeMapConfig(MapConfig config, Writer writer) throws IOException {
		m_serializer.serializeMapConfig(config,writer);
	}

	public MapConfig deserializeMapConfig(Reader reader) throws IOException {
		return m_serializer.deserializeMapConfig(applyConfigProperties(reader));
	}

	public void serializeDirectoryConfig(DirectoryConfig config, Writer writer) throws IOException {
		m_serializer.serializeDirectoryConfig(config,writer);
	}

	public DirectoryConfig deserializeDirectoryConfig(Reader reader) throws IOException {
		return m_serializer.deserializeDirectoryConfig(applyConfigProperties(reader));
	}

	public void serializeSiteConfig(SiteConfig config, Writer writer) throws IOException {
		m_serializer.serializeSiteConfig(config,writer);
	}

	public SiteConfig deserializeSiteConfig(Reader reader) throws IOException {
		return m_serializer.deserializeSiteConfig(applyConfigProperties(reader));
	}

	public void serializeDiffConfig(DiffConfig config, Writer writer) throws IOException {
		m_serializer.serializeDiffConfig(config,writer);
	}

	public DiffConfig deserializeDiffConfig(Reader reader) throws IOException {
		return m_serializer.deserializeDiffConfig(applyConfigProperties(reader));
	}

	public void serializeAlgoTestConfig(AlgoTestConfig config, Writer writer) throws IOException {
		m_serializer.serializeAlgoTestConfig(config,writer);
	}

	public AlgoTestConfig deserializeAlgoTestConfig(Reader reader) throws IOException {
		return m_serializer.deserializeAlgoTestConfig(applyConfigProperties(reader));
	}

	public void serializeLocationsConfig(LocationsConfig config, Writer writer) throws IOException {
		m_serializer.serializeLocationsConfig(config,writer);
	}

	public LocationsConfig deserializeLocationsConfig(Reader reader) throws IOException {
		return m_serializer.deserializeLocationsConfig(applyConfigProperties(reader));
	}

	public String getContentType() {
		return m_serializer.getContentType();
	}

	private Reader applyConfigProperties(Reader reader) throws IOException {
		StringWriter sw = new StringWriter();
		IOUtils.copy(reader,sw);
		String orig = sw.toString();
		Properties props = m_params==null ?
			MR4CConfig.getDefaultInstance().getCategory(Category.RUNTIME).getProperties(false) : 
			m_params;
		String result = ConfigUtils.applyProperties(orig, props, false);
		// NOTE: We would like to check that all the parameters were substituted here.
		// Unfortunately, the patterns used by PatternKeyFileMapper use the same variable delimiters
		return new StringReader(result);
	}

}
