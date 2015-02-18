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

package com.google.mr4c.config;

import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.MR4CConfigBuilder;
import com.google.mr4c.config.category.Category;
import com.google.mr4c.config.category.CoreConfig;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;

public abstract class MR4CRunnerConfig<SOURCE> {

	protected Logger m_log;
	private boolean m_dumpToFiles;
	private MR4CConfig m_bbConf;
	private SOURCE m_src;
	private List<String> m_args;
	private List<String> m_remainingArgs;
	private URI m_confFile;

	public MR4CRunnerConfig(Logger log, boolean dumpToFiles) {
		m_log = log;
		m_dumpToFiles = dumpToFiles;
	}

	public void setMR4CConfig(MR4CConfig bbConf) {
		m_bbConf = bbConf;
	}

	public void setCommandLineArguments(String ... args) throws IOException {
		setCommandLineArguments(Arrays.asList(args));
	}

	public void setCommandLineArguments(List<String> args) throws IOException {
		m_args = args;
	}

	public void setSource(SOURCE src) {
		m_src = src;
	}

	public void setConfFile(URI confFile) {
		m_confFile = confFile;
	}

	public MR4CConfig getMR4CConfig() {
		return m_bbConf;
	}

	public SOURCE getSource() {
		return m_src;
	}

	public List<String> getRemainingArgs() {
		return m_remainingArgs;
	}

	public void configure() throws IOException {
		if ( m_bbConf==null ) {
			m_bbConf = MR4CConfig.getDefaultInstance();
		}
		if ( m_args!=null ) {
			configureFromArgs();
		}
		boolean dumpToFiles = m_dumpToFiles && m_bbConf.getCategory(Category.CORE)
				.getProperty(CoreConfig.PROP_DUMP_PROPERTIES, "true").equals("true");
		m_bbConf.dumpConfig(m_log, dumpToFiles);
		if ( m_src==null ) {
			m_src = loadSource(m_confFile);
		}
	}

	private void configureFromArgs() throws IOException {
		MR4CConfigBuilder builder = new MR4CConfigBuilder(m_bbConf, m_args);
		builder.build();
		List<String> remainingArgs = builder.getRemainingArguments();
		m_confFile = URI.create(remainingArgs.get(0));
		m_remainingArgs = remainingArgs.subList(1, remainingArgs.size());
	}

	protected abstract SOURCE loadSource(URI confFile) throws IOException;

}
