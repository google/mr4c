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

import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.Category;
import com.google.mr4c.config.category.CategoryConfig;
import com.google.mr4c.config.category.CoreConfig;
import com.google.mr4c.config.category.HadoopConfig;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

class MR4CArgumentParser {

	private String[] m_args;
	private String m_usage;
	private String m_jar;
	private URI m_exeConf;
	private String m_cluster;
	private Integer m_tasks;

	MR4CArgumentParser(String[] args, String usage) {
		m_args = args;
		m_usage = usage;
	}

	void parse() throws IOException {
		String[] args = stripEmptyArgs(m_args);
		if ( args.length>4  || args.length<2 ) {
			throw new IllegalArgumentException(m_usage);
		}
		m_jar = args[0];
		m_exeConf = URI.create(args[1]);
		if ( args.length==3 ) {
			if ( NumberUtils.isDigits(args[2]) ) {
				m_tasks = Integer.parseInt(args[2]);
			} else {
				m_cluster = args[2];
			}
		} else if ( args.length > 3 ) {
			m_cluster = args[2];
			m_tasks = Integer.parseInt(args[3]);
		} 
	}

	private String[] stripEmptyArgs(String args[]) {
		List<String> result = new ArrayList<String>();
		for ( String arg : args ) {
			if ( !StringUtils.isEmpty(arg) ) {
				result.add(arg);
			}
		}
		return result.toArray(new String[result.size()]);
	}

	String getJar() {
		return m_jar;
	}

	URI getExeConf() {
		return m_exeConf;
	}

	String getCluster() {
		return m_cluster;
	}

	Integer getTasks() {
		return m_tasks;
	}

	void apply(MR4CMRJob bbJob) {
		bbJob.setMR4CJar(m_jar);
		bbJob.setExecutionConfig(m_exeConf);
		
		if (m_tasks!=null ) {
			bbJob.setNumTasks(m_tasks);
		}
		if ( m_cluster!=null ) {
			bbJob.setClusterName(m_cluster);
		}
	}

}
