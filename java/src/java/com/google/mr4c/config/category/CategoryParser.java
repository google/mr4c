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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
  * Parses options for a category out of a series of command lines
*/
public class CategoryParser {

	private String m_prefix;
	private Properties m_props = new Properties();
	private String m_files;
	private Pattern m_filePattern;
	private Pattern m_propPattern;
	private Pattern m_defPropPattern;

	public CategoryParser(String prefix) {
		m_prefix = prefix;
		buildFilePattern();
		buildPropPattern();
		buildDefaultPropPattern();
	}

	public List<String> parseNormal(String ... args) {
		return parseNormal(Arrays.asList(args));
	}

	/**
	  * Call to extract prefixed options
	  * @return remaining arguments
	*/
	public List<String> parseNormal(List<String> args) {
		return doParse(args, true);
	}

	/**
	  * Call to extract key=value arguments without a prefix
	  * @return remaining arguments
	*/
	public List<String> parseDefault(String ... args) {
		return parseDefault(Arrays.asList(args));
	}

	public List<String> parseDefault(List<String> args) {
		return doParse(args, false);
	}

	public Properties getProperties() {
		return m_props;
	}

	public String getFileList() {
		return m_files;
	}

	private synchronized List<String> doParse(List<String> args, boolean normal) {
		List<String> remainingArgs = new ArrayList<String>();
		Pattern propPattern = normal ? m_propPattern : m_defPropPattern;
		for ( String arg : args ) {
			arg = arg.trim();
			if ( normal && checkFileMatch(arg) ) {
				continue;
			}
			if ( checkPropMatch(propPattern, arg) ) {
				continue;
			}
			remainingArgs.add(arg);
		}
		return remainingArgs;
	}


	private boolean checkFileMatch(String arg) {
		// NOTE: multiple file args results in the last one being taken
		Matcher matcher = m_filePattern.matcher(arg);
		if ( matcher.matches() ) {
			m_files = matcher.group(1);
			return true;
		} else {
			return false;
		}
	} 

	private boolean checkPropMatch(Pattern pattern, String arg) {
		Matcher matcher = pattern.matcher(arg);
		if ( matcher.matches() ) {
			String name = matcher.group(1);
			String val = matcher.group(2);
			m_props.setProperty(name, val);
			return true;
		} else {
			return false;
		}
	} 

	private void buildFilePattern() {
		String regex = m_prefix + "=(.+)";
		m_filePattern = Pattern.compile(regex);
	}

	private void buildPropPattern() {
		String regex = m_prefix + "(.+)=(.+)";
		m_propPattern = Pattern.compile(regex);
	}

	private void buildDefaultPropPattern() {
		String regex = "(.+)=(.+)";
		m_defPropPattern = Pattern.compile(regex);
	}

}


