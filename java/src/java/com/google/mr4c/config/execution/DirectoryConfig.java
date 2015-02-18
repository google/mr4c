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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class DirectoryConfig {

	private PatternMapperConfig mapper;
	private List<PatternMapperConfig> mappers = new ArrayList<PatternMapperConfig>();
	private boolean ignore=false;
	private boolean selfConfig=true;

	public DirectoryConfig() {}

	public DirectoryConfig(PatternMapperConfig mapper) {
		this.mapper = mapper;
	}

	public PatternMapperConfig getMapper() {
		validateSingleMapper();
		return this.mapper;
	}

	public boolean hasSingleMapper() {
		return this.mapper!=null;
	}

	public boolean hasMapperList() {
		return !hasSingleMapper();
	}

	public void addMapper(PatternMapperConfig mapper) {
		validateHasList();
		this.mappers.add(mapper);
	}

	public List<PatternMapperConfig> getMappers() {
		validateHasList();
		return this.mappers;
	}

	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}

	public boolean getIgnore() {
		return this.ignore;
	}

	public void setSelfConfig(boolean selfConfig) {
		this.selfConfig = selfConfig;
	}

	public boolean getSelfConfig() {
		return this.selfConfig;
	}

	public boolean isValid() {
		return hasSingleMapper() || hasMapperList() && !mappers.isEmpty();
	}

	private void validateSingleMapper() {
		if ( !hasSingleMapper() ) {
			throw new IllegalStateException("Config is mapper list");
		}
	}

	private void validateHasList() {
		if ( !hasMapperList() ) {
			throw new IllegalStateException("Config is single mapper");
		}
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		DirectoryConfig config = (DirectoryConfig) obj;
		if ( !ObjectUtils.equals(mapper, config.mapper) ) return false;
		if ( !ObjectUtils.equals(mappers, config.mappers) ) return false;
		if ( !ObjectUtils.equals(ignore, config.ignore) ) return false;
		return true; 
	}

	public int hashCode() {
		int total=0;
		if ( mapper==null ) {
			total+=mapper.hashCode();
		}
		total+=mappers.hashCode();
		if ( ignore ) total++;
		return total;
	}


	/////////////////////////////////////////////////////////
	// Support for legacy format
	//////////////////////////////////////////////////////////

	public static final String PROP_PATTERN = "mapper.pattern";
	public static final String PROP_DIMENSIONS = "mapper.dimensions";
	public static final String PROP_IGNORE = "mapper.ignore";
	public static final String PROP_SELF_CONFIG = "mapper.selfConfig";

	public static DirectoryConfig createFromProperties(Properties props) {
		String pattern = loadValue(props, PROP_PATTERN);
		List<String> dimensions = loadList(props, PROP_DIMENSIONS);
		PatternMapperConfig mapper = new PatternMapperConfig(pattern, dimensions);
		DirectoryConfig config = new DirectoryConfig(mapper);
		boolean ignore = Boolean.parseBoolean(loadValue(props, PROP_IGNORE, "false"));
		config.setIgnore(ignore);
		boolean selfConfig = Boolean.parseBoolean(loadValue(props, PROP_SELF_CONFIG, "true"));
		config.setSelfConfig(selfConfig);
		return config;
	}

	private static String loadValue(Properties props, String name) {
		return loadValue(props, name, null);
	}

	private static String loadValue(Properties props, String name, String defaultValue) {
		String data = props.getProperty(name);
		if ( data==null ) {
			if ( defaultValue==null ) {
				throw new IllegalArgumentException("Missing " + name);
			} else {
				data=defaultValue;
			}
		}
		return StringUtils.strip(data);
	}

	private static List<String> loadList(Properties props, String name) {
		String data = props.getProperty(name);
		if ( data==null ) {
			throw new IllegalArgumentException("Missing " + name);
		}
		data = StringUtils.strip(data);
		if ( StringUtils.isEmpty(data) ) {
			return Collections.emptyList();
		}
		String[] vals = StringUtils.split(data, ", ");
		return Arrays.asList(vals);
	}

}
