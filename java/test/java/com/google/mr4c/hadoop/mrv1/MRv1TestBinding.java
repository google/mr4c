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

package com.google.mr4c.hadoop.mrv1;

import com.google.mr4c.AlgoRunner;
import com.google.mr4c.hadoop.MR4CMRJob;
import com.google.mr4c.hadoop.HadoopAlgoRunner;
import com.google.mr4c.hadoop.HadoopTestBinding;
import com.google.mr4c.hadoop.HadoopTestUtils;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MiniMRCluster;

import org.slf4j.Logger;

public class MRv1TestBinding implements HadoopTestBinding {

	private MiniMRCluster m_mrCluster;

	public synchronized JobConf createTestMRJobConf() throws IOException {
		if ( m_mrCluster==null ) {
			startMRCluster();
		}
		JobConf job = m_mrCluster.createJobConf();
		job.setJarByClass(AlgoRunner.class);
		return job;
	}

	private void startMRCluster() throws IOException {

		FileSystem fs = HadoopTestUtils.getTestDFS();
		m_mrCluster = new MiniMRCluster(
			1, // # of task trackers
			fs.getUri().toString(), // name node
			1 // # of directories
		);

	}

	public void runMiniMRJob(String name, MR4CMRJob bbJob) throws IOException {
		JobConf job = createTestMRJobConf();
		bbJob.applyTo(job); // Do this to keep any overrides from being lost (e.g. num tasks)
		MiniMRAlgoRunner runner = new MiniMRAlgoRunner(job, bbJob);
		runner.execute();
	}

	private static class MiniMRAlgoRunner extends HadoopAlgoRunner {

		private static final Logger s_log = MR4CLogging.getLogger(MiniMRAlgoRunner.class);
		MiniMRAlgoRunner(JobConf jobConf, MR4CMRJob bbJob) {
			super(jobConf, bbJob, s_log);
		}

		protected void doBuildJob() throws IOException {
			m_bbJob.setRemote(false);
			m_bbJob.includeEnvironmentVariable("LD_LIBRARY_PATH");
		}

	}
		
	public synchronized void shutdownMRCluster() {
		if ( m_mrCluster!=null ) {
			m_mrCluster.shutdown();
			m_mrCluster=null;
		}
	}

	public boolean expectedDeprecatedFileSysProp() {
		//return true;
		return false;
	}
}

