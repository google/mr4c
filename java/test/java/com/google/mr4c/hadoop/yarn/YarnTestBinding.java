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

package com.google.mr4c.hadoop.yarn;

import com.google.mr4c.AlgoRunner;
import com.google.mr4c.hadoop.MR4CMRJob;
import com.google.mr4c.hadoop.Cluster;
import com.google.mr4c.hadoop.HadoopAlgoRunner;
import com.google.mr4c.hadoop.HadoopTestBinding;
import com.google.mr4c.hadoop.HadoopTestUtils;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MiniMRClientCluster;
import org.apache.hadoop.mapred.MiniMRClientClusterFactory;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.mapreduce.v2.jobhistory.JHAdminConfig;

import org.slf4j.Logger;

public class YarnTestBinding implements HadoopTestBinding {

	private static final Logger s_log = MR4CLogging.getLogger(YarnTestBinding.class);

	private MiniMRClientCluster m_mrCluster;

	public JobConf createTestMRJobConf() throws IOException {
		if ( m_mrCluster==null ) {
			startMrCluster();
		}
		JobConf job = new JobConf(m_mrCluster.getConfig());
		job.setJarByClass(AlgoRunner.class);
		return job;
	}

	// copied from source of MiniHadoopClusterManager
	private void startMrCluster() throws IOException {
		Configuration conf = new JobConf();
		FileSystem.setDefaultUri(conf, HadoopTestUtils.getTestDFS().getUri());
		conf.setBoolean(YarnConfiguration.YARN_MINICLUSTER_FIXED_PORTS, true);
		conf.setBoolean(JHAdminConfig.MR_HISTORY_MINICLUSTER_FIXED_PORTS, true);
		String addr = MiniYARNCluster.getHostname() + ":0";
		conf.set(YarnConfiguration.RM_ADDRESS, addr);
		conf.set(JHAdminConfig.MR_HISTORY_ADDRESS, addr);
		m_mrCluster = MiniMRClientClusterFactory.create(
			HadoopTestUtils.class,
			"MR4CTests",
			1, // num node managers
			conf
		);

		// make sure startup is finished
		for ( int i=0; i<60; i++ ) {
			String newAddr = m_mrCluster.getConfig().get(YarnConfiguration.RM_ADDRESS);
			if ( newAddr.equals(addr) ) {
				s_log.warn("MiniYARNCluster startup not complete");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
					throw new IOException(ie);
				}
			} else {
				s_log.info("MiniYARNCluster now available at {}", newAddr);
				return;
			}
		}
		throw new IOException("MiniYARNCluster taking too long to startup");

	}

	public void runMiniMRJob(String name, MR4CMRJob bbJob) throws IOException {
		JobConf job = createTestMRJobConf();
		bbJob.applyTo(job); // Do this to keep any overrides from being lost (e.g. num tasks)
		MiniMRAlgoRunner runner = new MiniMRAlgoRunner(job, bbJob);
		runner.execute();
	}

	private static class MiniMRAlgoRunner extends HadoopAlgoRunner {

		private static final Logger s_log = MR4CLogging.getLogger(MiniMRAlgoRunner.class);
		private Cluster m_cluster;

		MiniMRAlgoRunner(JobConf jobConf, MR4CMRJob bbJob) {
			super(jobConf, bbJob, s_log);

			// capture cluster, its going to be lost when resources are loaded
			m_cluster = Cluster.extractFromConfig(jobConf);
		}

		protected void doBuildJob() throws IOException {
			m_cluster.applyToConfig(m_jobConf);
			m_bbJob.setRemote(false);
			m_bbJob.includeEnvironmentVariable("LD_LIBRARY_PATH");
			String classpath = System.getProperty("java.class.path");
			m_jobConf.set("yarn.application.classpath",classpath);
			m_jobConf.set("mapreduce.application.classpath",classpath);

			// MR app needs this to launch java :-(
			m_bbJob.specifyEnvironmentVariable("JAVA_HOME", System.getProperty("java.home"));

			// YARN needs this to launch a container :-(
			m_jobConf.set(
				"yarn.app.mapreduce.am.env",
					String.format(
						"JAVA_HOME=%s",
						System.getProperty("java.home")
					)
			); 
		}

	}

	public void shutdownMRCluster() {
		if ( m_mrCluster!=null ) {
			try {
				m_mrCluster.stop();
			} catch ( IOException ioe ) {
				throw new IllegalStateException(ioe);
			}
			m_mrCluster=null;
		}
	}

	public boolean expectedDeprecatedFileSysProp() {
		return false;
	}


}


