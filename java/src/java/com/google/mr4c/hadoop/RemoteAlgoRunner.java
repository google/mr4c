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

import com.google.mr4c.AlgoRunner;
import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.Category;
import com.google.mr4c.config.category.CategoryConfig;
import com.google.mr4c.config.category.CustomConfig;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;


import org.slf4j.Logger;

public class RemoteAlgoRunner extends HadoopAlgoRunner {

	private static final Logger s_log = MR4CLogging.getLogger(RemoteAlgoRunner.class);

	public static final String USAGE = "Usage: java RemoteAlgoRunner jar_file exe_conf_uri [cluster_name] [num_tasks]";

	public static void main(String[] args) throws Exception {
		checkForLauncherProps();
		RemoteAlgoRunner runner = new RemoteAlgoRunner(args);
		runner.execute();
	}

	// kinda hacky, keep for now
	private static void checkForLauncherProps() {
		String jobid = System.getProperty("oozie.launcher.job.id");
		if ( StringUtils.isEmpty(jobid) ) {
			return;
		}
		String taskid = System.getProperty("hadoop.tasklog.taskid");
		if ( StringUtils.isEmpty(taskid) ) {
			taskid = "launcher"; // fallback
		}
		CategoryConfig catConf = MR4CConfig.getDefaultInstance().getCategory(Category.CUSTOM);
		catConf.setProperty(CustomConfig.PROP_JOBID, jobid);
		catConf.setProperty(CustomConfig.PROP_TASKID, taskid); 
	}
	
	public RemoteAlgoRunner(String[] args) {
		super(args, s_log);
	}

	protected void doBuildJob() throws IOException {
		m_bbJob.setRemote(true);
		MR4CArgumentParser parser = new MR4CArgumentParser(m_finalArgs, USAGE);
		parser.parse();
		parser.apply(m_bbJob);
		addFiles();
	}

	private void addFiles() throws IOException {
		m_genOpts.addFile(m_bbJob.getExecutionConfig(), MR4CMRJob.REMOTE_EXE_CONF);
		m_genOpts.addFiles(getAlgoRunner().getAlgorithm().getRequiredFiles());
		URI log4jFile = MR4CLogging.instance().findLog4jConfigFile(true);
		if ( log4jFile!=null ) {
			m_genOpts.addFile(log4jFile, "log4j.properties");
		}
		URI log4cxxFile = MR4CLogging.instance().findLog4cxxConfigFile(true);
		if ( log4cxxFile!=null ) {
			m_genOpts.addFile(log4cxxFile, "log4cxx.properties");
		}
	}

}
