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
import com.google.mr4c.config.category.CoreConfig;
import com.google.mr4c.config.category.CustomConfig;
import com.google.mr4c.config.category.HadoopConfig;
import com.google.mr4c.content.S3Credentials;

import java.net.URI;

import org.apache.hadoop.mapred.JobConf;

import org.junit.*;
import static org.junit.Assert.*;

public class MR4CMRJobTest {

	private MR4CConfig m_sourceConfig;
	private MR4CMRJob m_sourceMRJob;
	private MR4CMRJob m_localMRJob;
	private MR4CMRJob m_remoteMRJob;
	private JobConf m_localJobConf;
	private JobConf m_remoteJobConf;
	private MR4CConfig m_expectedLocalConfig;
	private MR4CConfig m_expectedRemoteConfig;

	private String m_jar="path/to/bbjar/file.jar";
	private String m_clusterName="mrjobtest";
	private String m_jobTracker="myhost:8021";
	private String m_fileSystem="hdfs://myhost:8020";
	private Cluster m_cluster;
	private String m_exeConf="path/to/exeConf.json";
	private String m_rootDir="path/to/root";
	private String m_libPath="path/1:path/2";
	private String m_log4j="path/to/log4j.properties";
	private String m_s3ID="s3id";
	private String m_s3Secret="s3secret";
	private String m_launcherJobID = "launcherJobID";
	private String m_launcherTaskID = "launcherTaskID";
	private String m_mapredJobID = "mapredJobID";
	private String m_mapredTaskID = "mapredTaskID";

	@Before public void setup() throws Exception {
		buildSourceConfig();
		buildSourceMRJob();
		buildLocalMRJob();
		buildRemoteMRJob();
		buildLocalJobConf();
		buildRemoteJobConf();
		buildExpectedLocalConfig();
		buildExpectedRemoteConfig();
		addCluster();
	}

	@Test public void testExport() throws Exception {
		JobConf jobConf = newJobConf();
		m_sourceMRJob.applyTo(jobConf);
		assertEquals(m_jar, jobConf.getJar());
		Cluster cluster = Cluster.extractFromConfig(jobConf);
		assertEquals(m_cluster, cluster);
		assertEquals("5", jobConf.get(MR4CMRJob.PROP_TASKS));
	}

	@Test public void testLocalImport() throws Exception {
		doUpdateTest(m_localMRJob, m_localJobConf, m_expectedLocalConfig);
	}

	@Test public void testRemoteImport() throws Exception {
		doUpdateTest(m_remoteMRJob, m_remoteJobConf, m_expectedRemoteConfig);
	}

	private void doUpdateTest(
		MR4CMRJob targetJob,
		JobConf jobConf,
		MR4CConfig expectedConfig
	) throws Exception {
		m_sourceMRJob.applyTo(jobConf);
		targetJob.updateFrom(jobConf);
		assertEquals(
			expectedConfig.getProperties(),
			targetJob.getMR4CConfig().getProperties()
		);
	}

	private void buildSourceConfig() {
		m_sourceConfig = newConfig();
		populateCommonConfig(m_sourceConfig);
		populateCommonLocalConfig(m_sourceConfig);
	}

	private void buildSourceMRJob() {
		m_sourceMRJob = new MR4CMRJob(m_sourceConfig, false);
		m_sourceMRJob.setRemote(true);
		m_sourceMRJob.setMR4CJar(m_jar);
		m_sourceMRJob.setNumTasks(5);
		m_sourceMRJob.setClusterName(m_clusterName);
		m_sourceMRJob.setExecutionConfig(URI.create(m_exeConf));
	}

	private void buildLocalMRJob() {
		m_localMRJob = new MR4CMRJob(newConfig(), false);
	}

	private void buildRemoteMRJob() {
		m_remoteMRJob = new MR4CMRJob(newConfig(), true);
	}

	private void buildLocalJobConf() {
		m_localJobConf = newJobConf();
		m_localJobConf.set(MR4CMRJob.PROP_LAUNCHER_JOBID, m_launcherJobID);
		m_localJobConf.set(MR4CMRJob.PROP_LAUNCHER_TASKID, m_launcherTaskID);
	}

	private void buildRemoteJobConf() {
		m_remoteJobConf = newJobConf();
		m_remoteJobConf.set(MR4CMRJob.PROP_MAPRED_JOBID, m_mapredJobID);
		m_remoteJobConf.set(MR4CMRJob.PROP_MAPRED_TASKID, m_mapredTaskID);
	}

	private void buildExpectedLocalConfig() {
		m_expectedLocalConfig = newConfig();
		populateCommonConfig(m_expectedLocalConfig);
		populateCommonExpectedConfig(m_expectedLocalConfig);
		populateCommonLocalConfig(m_expectedLocalConfig);
		// These will be set on bbjob instance
		m_expectedLocalConfig.getCategory(Category.HADOOP).setProperty(HadoopConfig.PROP_MR4C_JAR, m_jar);
		m_expectedLocalConfig.getCategory(Category.CORE).setProperty(CoreConfig.PROP_EXE_CONF, m_exeConf);
		m_expectedLocalConfig.getCategory(Category.CUSTOM).setProperty(CustomConfig.PROP_JOBID, m_launcherJobID);
		m_expectedLocalConfig.getCategory(Category.CUSTOM).setProperty(CustomConfig.PROP_TASKID, m_launcherTaskID);
	}

	private void buildExpectedRemoteConfig() {
		m_expectedRemoteConfig = newConfig();
		populateCommonConfig(m_expectedRemoteConfig);
		populateCommonExpectedConfig(m_expectedRemoteConfig);
		m_expectedRemoteConfig.getCategory(Category.CORE).setProperty(CoreConfig.PROP_EXE_CONF, MR4CMRJob.REMOTE_EXE_CONF);
		m_expectedRemoteConfig.getCategory(Category.CUSTOM).setProperty(CustomConfig.PROP_JOBID, m_mapredJobID);
		m_expectedRemoteConfig.getCategory(Category.CUSTOM).setProperty(CustomConfig.PROP_TASKID, m_mapredTaskID);
	}

	private void populateCommonConfig(MR4CConfig config) {
		new S3Credentials(m_s3ID, m_s3Secret).applyTo(config);
		// some random stuff
		config.getCategory(Category.TOPICS).setProperty("topic1", "http://whatever");
		config.getCategory(Category.ALGO).setProperty("param1", "val1");
		config.getCategory(Category.ALGO).setProperty("param2", "val2");
		config.getCategory(Category.RUNTIME).setProperty("mybase", "some_base_path");
	}

	private void populateCommonLocalConfig(MR4CConfig config) {
		config.getCategory(Category.CORE).setProperty(CoreConfig.PROP_LOG4J_CONF, m_log4j);
		config.getCategory(Category.CORE).setProperty(CoreConfig.PROP_LIB_PATH, m_libPath);
		config.getCategory(Category.CORE).setProperty(CoreConfig.PROP_ROOT_DIR, m_rootDir);
	}

	private void populateCommonExpectedConfig(MR4CConfig config) {
		config.getCategory(Category.HADOOP).setProperty(HadoopConfig.PROP_TASKS, "5");
		config.getCategory(Category.HADOOP).setProperty(HadoopConfig.PROP_REMOTE, "true");
		config.getCategory(Category.HADOOP).setProperty(HadoopConfig.PROP_CLUSTER, m_clusterName);
	}
		
	private MR4CConfig newConfig() {
		MR4CConfig config = new MR4CConfig(false);
		config.initStandardCategories();
		return config;
	}

	private JobConf newJobConf() {
		JobConf jobConf = new JobConf(false);
		jobConf.clear(); // just in case;
		return jobConf;
	}

	private void addCluster() throws Exception {
		Clusters.addCluster(m_clusterName, m_jobTracker, m_fileSystem);
		m_cluster = Clusters.getCluster(m_clusterName);
	}

}
