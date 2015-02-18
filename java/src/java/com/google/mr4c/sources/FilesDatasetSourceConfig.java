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

package com.google.mr4c.sources;

import com.google.gson.JsonParseException;

import com.google.mr4c.config.ConfigDescriptor;
import com.google.mr4c.content.ContentFactories;
import com.google.mr4c.config.execution.DirectoryConfig;
import com.google.mr4c.config.execution.PatternMapperConfig;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.serialize.ConfigSerializer;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.param.ParameterizedConfigSerializer;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;

public class FilesDatasetSourceConfig {

	public static final String PROP_PATTERN = "mapper.pattern";
	public static final String PROP_DIMENSIONS = "mapper.dimensions";
	public static final String PROP_IGNORE = "mapper.ignore";

	protected static final Logger s_log = MR4CLogging.getLogger(FilesDatasetSourceConfig.class);

	private DataKeyFileMapper m_mapper;
	private boolean m_ignoreExtra=false;
	private boolean m_selfConfig=true;
	private DirectoryConfig m_dirConfig;

	public static FilesDatasetSourceConfig load(ConfigDescriptor descriptor) throws IOException {
		if ( descriptor.hasFile() ) {
			return loadFromFile(descriptor);
		} else if ( descriptor.hasInline() ) {
			return loadInline(descriptor);
		} else if ( descriptor.hasName() ) {
			throw new IllegalArgumentException("Named configs not supported yet");
		} else {
			throw new IllegalArgumentException("ConfigDescriptor is empty");
		}
	}
	
	private static FilesDatasetSourceConfig loadFromFile(ConfigDescriptor descriptor) throws IOException {
		s_log.info("Loading files dataset source config from [{}]", descriptor.getConfigFile());
		return loadFromContent(descriptor);
	}

	private static FilesDatasetSourceConfig loadInline(ConfigDescriptor descriptor) throws IOException {
		s_log.info("Files dataset source config is inline");
		return loadFromContent(descriptor);
	}

	private static FilesDatasetSourceConfig loadFromContent(ConfigDescriptor descriptor) throws IOException {
		Reader reader = descriptor.getContent();
		try {
			return load(reader);
		} finally {
			reader.close();
		}
	}

	public static FilesDatasetSourceConfig load(Reader reader) throws IOException {
		DirectoryConfig dirConf = loadDirectoryConfig(reader);
		return toSourceConfig(dirConf);
	}
	
	private static DirectoryConfig loadDirectoryConfig(Reader reader) throws IOException {
		ConfigSerializer ser = SerializerFactories.getSerializerFactory("application/json").createConfigSerializer(); // assume json config for now
		ser = new ParameterizedConfigSerializer(ser);
		String content = IOUtils.toString(reader); // need to get this so we can read it twice
		try {
			return ser.deserializeDirectoryConfig(new StringReader(content));
		} catch ( JsonParseException jpe ) {
			s_log.warn("Json deserialization failed; trying as properties", jpe);
			Properties props = new Properties();
			props.load(new StringReader(content));
			return DirectoryConfig.createFromProperties(props);
		}
	}

	private static FilesDatasetSourceConfig toSourceConfig(DirectoryConfig dirConf) {
		FilesDatasetSourceConfig config = new FilesDatasetSourceConfig();
		config.m_dirConfig = dirConf;
		if ( dirConf.hasSingleMapper() ) {
			config.m_mapper = toMapper(dirConf.getMapper());
		} else if ( dirConf.hasMapperList() ) {
			config.m_mapper = toMapper(dirConf.getMappers());
		} else {
			throw new IllegalStateException("No mapper provided in source config");
		}
		config.m_ignoreExtra = dirConf.getIgnore();
		config.m_selfConfig = dirConf.getSelfConfig();
		return config;
	}

	private static DataKeyFileMapper toMapper(PatternMapperConfig config) {
		Set<DataKeyDimension> dims = new HashSet<DataKeyDimension>();
		for ( String dimName : config.getDimensions() ) {
			dims.add(new DataKeyDimension(dimName));
		}
		return new PatternKeyFileMapper(config.getPattern(), dims);
	}

	private static DataKeyFileMapper toMapper(List<PatternMapperConfig> configs) {
		CompositeKeyFileMapper mapper = new CompositeKeyFileMapper();
		for ( PatternMapperConfig config : configs ) {
			mapper.addMapper(toMapper(config));
		}
		return mapper;
	}

	public FilesDatasetSourceConfig() {}

	public DataKeyFileMapper getKeyFileMapper() {
		return m_mapper;
	}

	public void setKeyFileMapper(DataKeyFileMapper mapper) {
		m_mapper = mapper;
	}

	public boolean ignoreExtraFiles() {
		return m_ignoreExtra;
	}

	public void setIgnoreExtraFiles(boolean ignoreExtra) {
		m_ignoreExtra = ignoreExtra;
	}

	public boolean isSelfConfig() {
		return m_selfConfig;
	}

	public void setSelfConfig(boolean selfConfig) {
		m_selfConfig = selfConfig;
	}

	public DirectoryConfig getDirectoryConfig() {
		return m_dirConfig;
	}

	public void setDirectoryConfig(DirectoryConfig dirConfig) {
		m_dirConfig = dirConfig;
	}

}

