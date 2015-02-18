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

package com.google.mr4c.config.category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
  * Builds MR4CConfig from command line
*/
public class MR4CConfigBuilder {

	private MR4CConfig m_config;
	private List<String> m_args;
	private List<String> m_remainingArgs;
	private Map<String,CategoryParser> m_parsers = new HashMap<String,CategoryParser>();

	/**
	  * Builds default MR4CConfig instance from command line
	*/
	public static List<String> buildDefaultMR4CConfig(List<String> args) throws IOException {
		MR4CConfigBuilder builder = new MR4CConfigBuilder(MR4CConfig.getDefaultInstance(), args);
		builder.build();
		return builder.getRemainingArguments();
	}
		
	public MR4CConfigBuilder(MR4CConfig config, String ... args) {
		this(config, Arrays.asList(args));
	}

	public MR4CConfigBuilder(MR4CConfig config, List<String> args) {
		m_config = config;
		m_args = args;
	}

	public List<String> getRemainingArguments() {
		return m_remainingArgs;
	}

	public synchronized void build() throws IOException {
		parseArgs();
		buildCategories();
	}

	private void parseArgs() {
		List<String> args = new ArrayList<String>(m_args);
		CategoryParser defParser = null;
		for ( CategoryConfig catConf : m_config.getAllCategoryConfigs() ) {
			CategoryInfo category = catConf.getCategory();
			if ( !parsable(category) ) {
				continue;
			}
			CategoryParser parser = new CategoryParser(category.getArgsPrefix());
			args = parser.parseNormal(args);
			m_parsers.put(category.getCategoryName(), parser);
			if ( category.isDefaultCategory() ) {
				if ( defParser!=null ) {
					throw new IllegalStateException("Can't parse with more than one default category");
				}
				defParser = parser;
			}
		}
		if ( defParser!=null ) {
			args = defParser.parseDefault(args);
		}
		m_remainingArgs = args;
	}

	private void buildCategories() throws IOException {
		for ( CategoryConfig catConf : m_config.getAllCategoryConfigs() ) {
			CategoryInfo category = catConf.getCategory();
			CategoryBuilder builder = new CategoryBuilder(category);
			CategoryParser parser = m_parsers.get(category.getCategoryName());
			String files = parser==null ? null : parser.getFileList();
			Properties props = parser==null ? new Properties() : parser.getProperties();
			builder.buildStandardCategory(
				files,
				props,
				catConf
			);
			catConf.setProperties(builder.getProperties());
		}
	}

	private boolean parsable(CategoryInfo category) {
		return category.isDefaultCategory() || !StringUtils.isEmpty(category.getArgsPrefix());
	}

}
