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
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;


import org.slf4j.Logger;

public class LocalHadoopAlgoRunner extends HadoopAlgoRunner {

	private static final Logger s_log = MR4CLogging.getLogger(LocalHadoopAlgoRunner.class);

	public static final String USAGE =  "Usage: java LocalHadoopAlgoRunner jar_file exe_conf_uri [num_tasks]";

	public static void main(String[] args) throws Exception {
		LocalHadoopAlgoRunner runner = new LocalHadoopAlgoRunner(args);
		runner.execute();
	}
	
	public LocalHadoopAlgoRunner(String[] args) {
		super(args, s_log);
	}

	protected void doBuildJob() throws IOException {
		m_bbJob.setRemote(false);
		MR4CGenericOptions opts = new MR4CGenericOptions(); 
		MR4CArgumentParser parser = new MR4CArgumentParser(m_finalArgs, USAGE);
		parser.parse();
		if ( parser.getCluster()!=null ) {
			throw new IllegalStateException("LocalHadoopAlgoRunner shouldn't have a cluster specified");
		}
		parser.apply(m_bbJob);
		m_bbJob.includeEnvironmentVariable("LD_LIBRARY_PATH");
		opts.setCluster(Clusters.getLocalCluster());
		applyGenericOptions(opts);
	}

}
