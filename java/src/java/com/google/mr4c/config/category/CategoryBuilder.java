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

import com.google.mr4c.content.ContentFactories;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;

/**
  * Builds up a set of properties for category as directed from various sources
*/
public class CategoryBuilder {

	protected static final Logger s_log = MR4CLogging.getLogger(CategoryBuilder.class);

	private CategoryInfo m_category;
	private Properties m_props = new Properties();

	public CategoryBuilder(CategoryInfo category) {
		m_category = category;
	}

	public Properties getProperties() {
		return m_props;
	}


	/**
	  * Implement standard category precedence, in descending order:
	  * <ol>
	  * <li>External properties</li>
	  * <li>Command line arguments</li>
	  * <li>System properties</li>
	  * <li>Command line argument for list of property files</li>
	  * <li>System property for list of property files</li>
	  * <li>Environment variable for list of property files</li>
	  * <li>Deprecated system properties</li>
	  * </ol>
	*/
	public void buildStandardCategory(
		String argsFileList,
		Properties argsProps,
		CategorySystemData sysData
	) throws IOException {
		addProperties(sysData.getPropertiesFromDeprecatedNames());
		addPropertiesFromEnvironmentVariableFileList();
		addPropertiesFromSystemPropertyFileList();
		addPropertiesFromCommandLineArgumentFileList(argsFileList);
		addProperties(sysData.getPropertiesFromSystemProperties());
		addProperties(argsProps);
		addProperties(sysData.getPropertiesFromExternalNames());
	}

	public void addProperties(Properties props) {
		m_props.putAll(props);
	}

	public void addPropertiesFromCommandLineArgumentFileList(String fileList) throws IOException {
		addPropertiesFromFileList("command line argument " + m_category.getArgsPrefix(), fileList);
	}

	public void addPropertiesFromSystemPropertyFileList() throws IOException {
		String propName = m_category.getCategoryProperty();
		if ( StringUtils.isEmpty(propName) ) {
			return;
		}
		String files = System.getProperty(propName);
		addPropertiesFromFileList("system property " + propName, files);
	}

	public void addPropertiesFromEnvironmentVariableFileList() throws IOException {
		String varName = m_category.getEnvironmentVariable();
		if ( StringUtils.isEmpty(varName) ) {
			return;
		}
		String files = System.getenv(varName);
		addPropertiesFromFileList("environment variable " + varName, files);
	}

	/**
	  * Comma delimited files in order of precedence
	*/
	public void addPropertiesFromFileList(String source, String propFiles) throws IOException {
		if ( StringUtils.isEmpty(propFiles) ) {
			return;
		}
		s_log.info("Trying to load {} config properties from files specified by {}: [{}]", m_category.getCategoryName(), source, propFiles);

		List<URI> uris = new ArrayList<URI>();
		for ( String file : StringUtils.split(propFiles, ",") ) {
			file = file.trim();
			uris.add(URI.create(file));
		}
		addProperties(uris);
	}

	/**
	  * Files in order of precedence
	*/
	public void addProperties(List<URI> propFiles) throws IOException {
		propFiles = new ArrayList<URI>(propFiles);
		Collections.reverse(propFiles); // need to apply in reverse order of precedence
		for ( URI file : propFiles ) {
			addProperties(file);
		}
	}

	public void addProperties(URI propFile) throws IOException {
		Properties props = ContentFactories.readContentAsProperties(propFile);
		addProperties(props);
	}

}
