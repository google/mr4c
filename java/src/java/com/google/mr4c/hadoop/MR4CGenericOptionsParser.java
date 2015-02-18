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

package com.google.mr4c.hadoop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.util.GenericOptionsParser;

/**
  * Loose wrapper around GenericOptionsParser to allow placing arguments ahead
  * of the generic hadoop options.  All generic options must be in a single
  * block on the command line.  There can be regular command line arguments
  * before or after this block, but not mixed in to it.
*/
public class MR4CGenericOptionsParser {

	private String[] m_origArgs;
	private List<String> m_leadingArgs;
	private String[] m_toParse;
	private String[] m_remainingArgs;
	private Configuration m_conf;
	private GenericOptionsParser m_parser;
	private static List<String> s_genericOptions = Arrays.asList(
		"-jt",
		"-fs",
		"-D",
		"-conf",
		"-files",
		"-libjars",
		"-archives"
	);

	public MR4CGenericOptionsParser(String[] args) throws IOException {
		this(new Configuration(false), args);
	}
		
	public MR4CGenericOptionsParser(Configuration conf, String[] args) throws IOException {
		m_origArgs = args;
		m_conf = conf;
		parse();
	}

	/**
	  * This should be called after tests to get rid of FileSystem side effects
	*/
	public static void cleanup() throws IOException {
		// GenericOptionsParser will cache FileSystem objects based on the config it is updating
		FileSystem.closeAll();
	}

	public String[] getOriginalArgs() {
		return m_origArgs;
	}

	public String[] getRemainingArgs() {
		return m_remainingArgs;
	}

	public Configuration getConfiguration() {
		return m_conf;
	}

	private void parse() throws IOException {
		stripLeadingArgs();
		parseGenericOptions();
		buildRemainingArgs();
	}

	private void stripLeadingArgs() {
		// first arg starting with one of the generic options starts the hadoop block
		int index=0;
		for ( ; index<m_origArgs.length; index++ ) {
			if ( isGenericOption(m_origArgs[index]) ) {
				break;
			}
		}
		m_leadingArgs = Arrays.asList(m_origArgs).subList(0,index);
		m_toParse = Arrays.asList(m_origArgs).subList(index, m_origArgs.length).toArray(new String[0]);
	}

	private void parseGenericOptions() throws IOException {
		m_parser = new GenericOptionsParser(m_conf, m_toParse);
	}

	private void buildRemainingArgs() {
		List<String> remainingArgs = new ArrayList<String>();
		remainingArgs.addAll(m_leadingArgs);
		remainingArgs.addAll(Arrays.asList(m_parser.getRemainingArgs()));
		m_remainingArgs = remainingArgs.toArray(new String[0]);
	}

	private boolean isGenericOption(String opt) {
		for ( String genericOpt : s_genericOptions ) {
			if ( opt.startsWith(genericOpt) ) {
				return true;
			}
		}
		return false;
	}

	// might want to get rid of these props in conf:
	//	mapreduce.client.genericoptionsparser.used
	//	mapred.used.genericoptionsparser

}
