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

import com.google.mr4c.algorithm.Algorithm;
import com.google.mr4c.algorithm.AlgorithmEnvironment;
import com.google.mr4c.content.RelativeContentFactory;
import com.google.mr4c.message.Messages;
import com.google.mr4c.sources.ConfiguredExecutionSource;
import com.google.mr4c.sources.ExecutionSource;
import com.google.mr4c.util.MR4CLogging;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;

import org.slf4j.Logger;

public abstract class HadoopUtils {

	public static String buildJobName(String algoName, String exeConf) {
		return String.format("AlgoName=%s; ExecutionConfig=%s", algoName, exeConf);
	}

	public static MR4CMRJob initFromJob(JobConf job, boolean onCluster) throws IOException {
		MR4CMRJob bbJob = new MR4CMRJob(onCluster);
		bbJob.updateFrom(job);
		return bbJob;
	}

    public static ExecutionSource createSource( MR4CMRJob bbJob) throws IOException {
		URI exeConf = bbJob.getExecutionConfig();
		ConfiguredExecutionSource exeSrc = new ConfiguredExecutionSource(exeConf);
		exeSrc.loadConfigs();
        
        return exeSrc;
    }

	public static ExecutionSource initFromJobAndCreateSource(JobConf job, boolean onCluster) throws IOException {
		MR4CMRJob bbJob = initFromJob(job, onCluster);
		Logger log = MR4CLogging.getLogger(HadoopUtils.class);
		bbJob.getMR4CConfig().dumpConfig(log, false);
		return createSource( bbJob);
	}

	public static void applyToJobConf(Properties props, JobConf conf) {
		for ( String name : props.stringPropertyNames() ) {
			conf.set(name, props.getProperty(name));
		}
	}

	/**
	  * @param varMap apply environment variable values from this map
	  * @param vars apply existing values of these environment variables
	*/
	public static void applyEnvironmentVariables(JobConf conf, Map<String,String> varMap, String ... vars) {
		applyEnvironmentVariables(conf, varMap, Arrays.asList(vars));
	}
		
	/**
	  * @param varMap apply environment variable values from this map
	  * @param vars apply existing values of these environment variables
	*/
	public static void applyEnvironmentVariables(JobConf conf, Map<String,String> varMap, List<String> vars) {
		Map<String,String> allMap = new HashMap<String,String>(System.getenv());
		allMap.keySet().retainAll(vars); // only the env we wanted
		allMap.putAll(varMap);
		List<String> assigns = new ArrayList<String>();
		for ( String var : allMap.keySet() ) {
			String val = allMap.get(var);
			if ( !StringUtils.isEmpty(val) ) {
				assigns.add(var+"="+val);
			}
		}
		String value = StringUtils.join(assigns, ", ");
		conf.set(JobConf.MAPRED_MAP_TASK_ENV, value);
		conf.set(JobConf.MAPRED_REDUCE_TASK_ENV, value);
	}

	/**
	  * Generates human readable string with property name, value, and source
	*/
	public static String describeConfProp(Configuration conf, String name) {
		String val = conf.get(name);
		String[] srcs = conf.getPropertySources(name);
		String source = srcs==null ? "unknown" : Arrays.toString(srcs);
		return String.format("%s=%s; source: %s", name, val, source);
	}

	/**
	  * Generates human readable string with resource name and URI
	*/
	public static String describeResource(Configuration conf, String name) {
		URL url = conf.getResource(name);
		return String.format("Resource %s found at %s", name, url);
	}

	private static HadoopBinding s_binding;

	public synchronized static HadoopBinding getHadoopBinding() {
		if ( s_binding==null ) {
			s_binding = StaticHadoopBinder.createBinding();
		}
		return s_binding;
	}
}


