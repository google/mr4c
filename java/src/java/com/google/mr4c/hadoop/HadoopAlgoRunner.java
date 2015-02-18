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
import com.google.mr4c.AlgoRunner.AlgoRunnerConfig;
import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.MR4CConfigBuilder;
import com.google.mr4c.config.category.Category;
import com.google.mr4c.config.category.CoreConfig;
import com.google.mr4c.config.category.HadoopConfig;
import com.google.mr4c.config.resources.ResourceConfig;
import com.google.mr4c.config.resources.ResourceRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.slf4j.Logger;

public abstract class HadoopAlgoRunner {

	protected String[] m_fullArgs;
	protected String[] m_argsWithConfig;
	protected String[] m_finalArgs;
	protected JobConf m_jobConf;
	protected MR4CMRJob m_bbJob;
	protected MR4CGenericOptions m_genOpts;
	private Logger m_log;
	private AlgoRunner m_runner;

	/**
	  * Default resource found in the mr4c jar
	*/
	public static final String MR4C_DEFAULT_RESOURCE = "mr4c-hadoop-default.xml";

	/**
	  * Site resource on the file system (in classpath)
	*/
	public static final String MR4C_SITE_RESOURCE = "mr4c-hadoop-site.xml";

	static {
		Configuration.addDefaultResource(MR4C_DEFAULT_RESOURCE);
		Configuration.addDefaultResource("hdfs-default.xml");
		Configuration.addDefaultResource("mapred-default.xml");
		for ( String resource : HadoopUtils.getHadoopBinding().getDefaultResourcesToLoad() ) {
			Configuration.addDefaultResource(resource);
		}
	}

	protected HadoopAlgoRunner(String[] args, Logger log) {
		m_fullArgs = args;
		m_log = log;
		m_jobConf = new JobConf();
		m_bbJob = new MR4CMRJob(false); // not on cluster
	}

	protected HadoopAlgoRunner(JobConf jobConf, MR4CMRJob bbJob, Logger log) {
		m_fullArgs = new String[]{};
		m_log = log;
		m_jobConf = jobConf;
		m_bbJob = bbJob;
	}

	public void execute() throws IOException {
		buildJob(); 
		finalConfiguration();
		logConfiguration();
		submitJob();
	}

	void buildJob() throws IOException {
		m_genOpts = new MR4CGenericOptions();
		includeResources();
		extractGenericOptions();
		extractMR4CConfig();
		includeAlgorithmJars();
		doBuildJob();
		applyGenericOptions(m_genOpts);
		applyToConfiguration();
		requestResources();
	}

	JobConf getJobConf() {
		return m_jobConf;
	}

	/**
	  * Hook for subclasses to add their own configuration items
	*/
	protected abstract void doBuildJob() throws IOException;

	protected void applyGenericOptions(MR4CGenericOptions opts) throws IOException {
		String[] args = applyGenericOptions(opts.toGenericOptions());
		if ( args.length!=0 ) {
			throw new IllegalStateException(String.format("Left with unexpected extra args after parsing generic options: [%s]", Arrays.asList(args)));
		}
	}

	protected String[] applyGenericOptions(String[] args) throws IOException {
		m_log.info("Args passed to options parser: {}", Arrays.asList(args));
		MR4CGenericOptionsParser parser = new MR4CGenericOptionsParser(m_jobConf, args);
		m_log.info("Args returned from options parser: {}", Arrays.asList(parser.getRemainingArgs()));
		return parser.getRemainingArgs();
	}

	private void includeResources() throws IOException {
		m_jobConf.addResource(MR4C_SITE_RESOURCE);
		addResource("core-site.xml");
		addResource("mapred-site.xml");
		addResource("hdfs-site.xml");
		for ( String resource : HadoopUtils.getHadoopBinding().getResourcesToLoad() ) {
			addResource(resource);
		}
		String userResource = MR4CConfig.getDefaultInstance().getCategory(Category.HADOOP).getProperty(HadoopConfig.PROP_USER_RESOURCE);
		if ( userResource!=null ) {
			m_log.info("Adding user configuration resource [{}]", userResource);
			m_jobConf.addResource(new Path(userResource));
		}
	}

	private void addResource(String name) {
		Path path = new Path("/etc/hadoop/conf", name);
		m_jobConf.addResource(path);
	}

	private void includeAlgorithmJars() throws IOException {
		String jars = MR4CConfig.getDefaultInstance().getCategory(Category.HADOOP).getProperty(HadoopConfig.PROP_ALGORITHM_CLASSPATH);
		if (jars == null)
			return;
		for (String jar : StringUtils.split(jars, ':')) {
			if (jar != null && jar.length() > 0) {
				m_log.info("Including algorithm jar: {}", jar);
				m_genOpts.addJar(URI.create(jar));
			}
		}
	}

	private void extractGenericOptions() throws IOException {
		m_argsWithConfig = applyGenericOptions(m_fullArgs);
	}

	private void extractMR4CConfig() throws IOException {
		MR4CConfig config = MR4CConfig.getDefaultInstance();
		m_finalArgs = MR4CConfigBuilder.buildDefaultMR4CConfig(Arrays.asList(m_argsWithConfig)).toArray(new String[0]);
		m_log.info("Args after parsing out config: {}", Arrays.asList(m_finalArgs));
		boolean dumpToFile = config.getCategory(Category.CORE)
				.getProperty(CoreConfig.PROP_DUMP_PROPERTIES, "true").equals("true");
		config.dumpConfig(m_log, dumpToFile);
	}

	private void applyToConfiguration() throws IOException {
		m_bbJob.applyTo(m_jobConf); // make sure options in MR4CConfig override options in hadoop configuration
		m_bbJob.updateFrom(m_jobConf); // now capture all the hadoop options for validation
		m_bbJob.validate();
		validateCluster();
	}

	private void validateCluster() {
		Cluster cluster = Cluster.extractFromConfig(m_jobConf);
		if ( cluster!=null ) {
			m_log.info("Specified cluster is [{}]", cluster);
		} else {
			throw new IllegalArgumentException("No cluster provided");
		}
		cluster.fixHostname(m_jobConf);
	}

	private void requestResources() throws IOException {
		ResourceConfig resConf = new ResourceConfig();
		resConf.addStandardResources(m_bbJob.getMR4CConfig());
		// Not going to load the configured limits for now.  The defaults in yarn-default.xml are overriding the cluster derived limits.  Also not clear if what we are using was intended to be a client side config option
		//resConf.addStandardLimits(m_jobConf);
		addClusterLimits(resConf);
		resConf.resolveRequests();
		resConf.applyTo(m_jobConf);
		if ( resConf.isEmpty() ) {
			m_log.info("No resources requested");
		}
		for ( ResourceRequest request : resConf.getAllRequests() ) {
			logResourceRequest(request);
		}
	}

	private void addClusterLimits(ResourceConfig resConf) throws IOException {
		HadoopUtils.getHadoopBinding().addClusterLimits(m_jobConf, resConf);
	}

	private void logResourceRequest(ResourceRequest request) {
		String msg = String.format("Request for %s = %s", request.getResource().getResourceName(), request.getActual());
		if ( request.isLimited() ) {
			msg += String.format("; limited by %s", request.getLimit().getSource());
		}
		m_log.info(msg);
	}

	private void finalConfiguration() throws IOException {
		addJobName();
		String framework = HadoopUtils.getHadoopBinding().getMapReduceFrameworkName();
		if ( framework!=null ) {
			m_jobConf.set("mapreduce.framework.name", framework);
		}
		m_jobConf.setMapperClass(HadoopMapper.class);
		m_jobConf.setReducerClass(HadoopReducer.class);
		m_jobConf.setOutputKeyClass(Text.class);
		m_jobConf.setOutputValueClass(Text.class);
		m_jobConf.setInputFormat(HadoopInputFormat.class);
		m_jobConf.setOutputFormat(HadoopOutputFormat.class);
		m_jobConf.setNumReduceTasks(1);
		m_jobConf.setMaxMapAttempts(1);
	}

	/**
	  * m_bbJob must already include the execution config
	*/
	protected AlgoRunner getAlgoRunner() throws IOException {
		if ( m_runner==null ) {
			AlgoRunnerConfig config = new AlgoRunnerConfig(false);
			config.setConfFile(m_bbJob.getExecutionConfig());
			config.configure();
			m_runner = new AlgoRunner(config);
		}
		return m_runner;
	}

	private void addJobName() throws IOException {
		String name = HadoopUtils.buildJobName(
			getAlgoRunner().getAlgorithmConfig().getName(),
			m_bbJob.getExecutionConfig().toString()
		);
		m_jobConf.setJobName(name);
	}
	
	private void submitJob() throws IOException {
		// most of this method copies JobClient.runJob()
		// addition here is logging the job URI
		JobClient client = new JobClient(m_jobConf);
		RunningJob job = client.submitJob(m_jobConf);
		m_log.info("Job URL is [{}]" , job.getTrackingURL());
		try {
			if ( !client.monitorAndPrintJob(m_jobConf, job) ) {
				throw new IOException("Job failed!");
			}
		} catch (InterruptedException ie ) {
			Thread.currentThread().interrupt();
		}
	}	

	private void logConfiguration() {
		m_log.info("BEGIN LOG CONFIGURATION");
		logResource("core-default.xml");
		logResource("core-site.xml");
		logResource("hdfs-default.xml");
		logResource("hdfs-site.xml");
		logResource("mapred-default.xml");
		logResource("mapred-site.xml");
		for ( String resource : HadoopUtils.getHadoopBinding().getDefaultResourcesToLoad() ) {
			logResource(resource);
		}
		for ( String resource : HadoopUtils.getHadoopBinding().getResourcesToLoad() ) {
			logResource(resource);
		}
		logResource("mr4c-hadoop-default.xml");
		logResource("mr4c-hadoop-site.xml");
		logProp(Cluster.getJobTrackerPropertyName());
		logProp(Cluster.FILE_SYS_PROP);
		logProp(Cluster.DEPRECATED_FILE_SYS_PROP);
		logProp("mapreduce.framework.name");
		logProp("mapreduce.map.memory.mb");
		logProp("mapreduce.map.cpu.vcores");
		for ( String propName : HadoopUtils.getHadoopBinding().getPropertiesToLog() ) {
			logProp(propName);
		}
		m_log.info("END LOG CONFIGURATION");
	}

	private void logResource(String name) {
		m_log.info(HadoopUtils.describeResource(m_jobConf, name));
	}

	private void logProp(String name) {
		m_log.info(HadoopUtils.describeConfProp(m_jobConf, name));
	}

}
