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
import com.google.mr4c.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.apache.hadoop.mapred.JobConf;

public class MR4CMRJob {

	public static final String PROP_TASKS = "mapred.map.tasks";
	public static final String PROP_MAPRED_TASKID = "mapred.task.id";
	public static final String PROP_MAPRED_JOBID = "mapred.job.id";
	public static final String PROP_LAUNCHER_TASKID = "launcher.task.id";
	public static final String PROP_LAUNCHER_JOBID = "launcher.job.id";

	public static final String REMOTE_EXE_CONF = "exeConf.json";

	private boolean m_onCluster;
	private MR4CConfig m_config;
	private List<String> m_envVars = new ArrayList<String>();
	private Map<String,String> m_envVarMap = new HashMap<String,String>();

	public MR4CMRJob(boolean onCluster) {
		this(MR4CConfig.getDefaultInstance(), onCluster);
	}

	public MR4CMRJob(MR4CConfig config, boolean onCluster) {
		m_config = config;
		m_onCluster = onCluster;
	}	

	public MR4CConfig getMR4CConfig() {
		return m_config;
	}

	/**
	  * Where this instance is created
	*/
	public boolean isOnCluster() {
		return m_onCluster;
	}

	public boolean isRemote() {
		return isRemote(m_config);
	}

	private boolean isRemote(MR4CConfig bbConf) {
		String strVal = getProperty(bbConf, Category.HADOOP, HadoopConfig.PROP_REMOTE);
		return StringUtils.isEmpty(strVal) ? false : Boolean.parseBoolean(strVal);
	}

	public void setRemote(Boolean remote) {
		setRemote(m_config, remote);
	}

	private void setRemote(MR4CConfig bbConf, Boolean remote) {
		if ( remote!=null ) {
			setProperty(bbConf, Category.HADOOP, HadoopConfig.PROP_REMOTE, remote.toString());
		} else {
			clearProperty(bbConf, Category.HADOOP, HadoopConfig.PROP_REMOTE);
		}
	}

	public URI getExecutionConfig() {
		return getExecutionConfig(m_config);
	}

	private URI getExecutionConfig(MR4CConfig bbConf) {
		String strConf = getProperty(bbConf, Category.CORE, CoreConfig.PROP_EXE_CONF);
		return StringUtils.isEmpty(strConf) ? null : URI.create(strConf);
	}

	public void setExecutionConfig(URI exeConf) {
		setExecutionConfig(m_config, exeConf);
	}

	private void setExecutionConfig(MR4CConfig bbConf, URI exeConf) {
		if ( exeConf!=null ) {
			setProperty(bbConf, Category.CORE, CoreConfig.PROP_EXE_CONF, exeConf.toString());
		} else {
			clearProperty(bbConf, Category.CORE, CoreConfig.PROP_EXE_CONF);
		}
	}

	public Integer getNumTasks() {
		return getNumTasks(m_config);
	}

	private Integer getNumTasks(MR4CConfig bbConf) {
		String strTasks = getProperty(bbConf, Category.HADOOP, HadoopConfig.PROP_TASKS);
		return StringUtils.isEmpty(strTasks) ? null : Integer.parseInt(strTasks);
	}

	public void setNumTasks(Integer tasks) {
		setNumTasks(m_config, tasks);
	}

	private void setNumTasks(MR4CConfig bbConf, Integer tasks) {
		if ( tasks!=null ) {
			setProperty(bbConf, Category.HADOOP, HadoopConfig.PROP_TASKS, tasks.toString());
		} else {
			clearProperty(bbConf, Category.HADOOP, HadoopConfig.PROP_TASKS);
		}
	}

	public String getMR4CJar() {
		return getMR4CJar(m_config);
	}

	private String getMR4CJar(MR4CConfig bbConf) {
		return getProperty(bbConf, Category.HADOOP, HadoopConfig.PROP_MR4C_JAR);
	}

	public void setMR4CJar(String jar) {
		setMR4CJar(m_config, jar);
	}

	public void setMR4CJar(MR4CConfig bbConf, String jar) {
		if ( jar!=null ) {
			setProperty(bbConf, Category.HADOOP, HadoopConfig.PROP_MR4C_JAR, jar);
		} else {
			clearProperty(bbConf, Category.HADOOP, HadoopConfig.PROP_MR4C_JAR);
		}
	}

	public String getClusterName() {
		return getClusterName(m_config);
	}

	private String getClusterName(MR4CConfig bbConf) {
		return getProperty(bbConf, Category.HADOOP, HadoopConfig.PROP_CLUSTER);
	}

	public void setClusterName(String cluster) {
		setClusterName(m_config, cluster);
	}

	public void setClusterName(MR4CConfig bbConf, String cluster) {
		if ( cluster!=null ) {
			setProperty(bbConf, Category.HADOOP, HadoopConfig.PROP_CLUSTER, cluster);
		} else {
			clearProperty(bbConf, Category.HADOOP, HadoopConfig.PROP_CLUSTER);
		}
	}

	public List<String> getIncludedEnvironmentVariables() {
		return m_envVars;
	}

	public void includeEnvironmentVariable(String var) {
		m_envVars.add(var);
	}

	public Map<String,String> getSpecifiedEnvironmentVariables() {
		return m_envVarMap;
	}

	public void specifyEnvironmentVariable(String var, String val) {
		m_envVarMap.put(var,val);
	}

	public void updateFrom(JobConf jobConf) {

		MR4CConfig bbConf = new MR4CConfig(false);
		bbConf.initStandardCategories();

		// pull in all mr4c namespaced properties from the job conf
		// some of these will be overriden by hadoop properties in the job conf
		bbConf.importProperties(jobConf);
	
		// Don't pick up cluster or env vars - only exported

		setMR4CJar(bbConf, jobConf.getJar());
		importProperty(bbConf, jobConf, Category.HADOOP, HadoopConfig.PROP_TASKS, PROP_TASKS);

		S3Credentials cred = S3Credentials.extractFrom(jobConf);
		if ( cred!=null ) {
			cred.applyTo(bbConf);
		}


		importProperty(bbConf, jobConf, Category.CUSTOM, CustomConfig.PROP_JOBID, m_onCluster ? PROP_MAPRED_JOBID : PROP_LAUNCHER_JOBID);
		importProperty(bbConf, jobConf, Category.CUSTOM, CustomConfig.PROP_TASKID, m_onCluster ? PROP_MAPRED_TASKID : PROP_LAUNCHER_TASKID);

		if ( (isRemote(m_config) || isRemote(bbConf)) && m_onCluster ) {
			// don't want to pick these up from the job submission environment
			clearProperty(bbConf, Category.CORE, CoreConfig.PROP_EXE_CONF);
			clearProperty(bbConf, Category.CORE, CoreConfig.PROP_LOG4J_CONF);
			clearProperty(bbConf, Category.CORE, CoreConfig.PROP_LIB_PATH);
			clearProperty(bbConf, Category.CORE, CoreConfig.PROP_ROOT_DIR);
			clearProperty(bbConf, Category.HADOOP, HadoopConfig.PROP_MR4C_JAR);
			bbConf.getCategory(Category.CORE).setProperty(CoreConfig.PROP_EXE_CONF, REMOTE_EXE_CONF);
		}

		// finally have what we want, apply to config
		m_config.importProperties(CollectionUtils.toMap(bbConf.getProperties()).entrySet());

	}

	public void applyTo(JobConf jobConf) throws IOException {

		// push all mr4c namespaced properties to the job conf
		HadoopUtils.applyToJobConf(m_config.getProperties(), jobConf);

		String jar = getMR4CJar(m_config);
		if ( !StringUtils.isEmpty(jar) ) {
			jobConf.setJar(jar);
		}

		exportProperty(m_config, jobConf, Category.HADOOP, HadoopConfig.PROP_TASKS, PROP_TASKS);

		String clusterName = getClusterName(m_config);
		if ( !StringUtils.isEmpty(clusterName) ) {
			Cluster cluster = Clusters.getCluster(clusterName);
			cluster.applyToConfig(jobConf);
		}
			
		S3Credentials cred = S3Credentials.extractFrom(m_config);
		if ( cred!=null ) {
			cred.applyTo(jobConf);
		}

		// Don't export task and job id's, Hadoop should set those

		if ( !m_envVars.isEmpty() || !m_envVarMap.isEmpty() ) {
			HadoopUtils.applyEnvironmentVariables(jobConf, m_envVarMap, m_envVars);
		}

	}

	public void validate() {
		if ( getProperty(m_config, Category.CORE, CoreConfig.PROP_EXE_CONF)==null ) {
			throw new IllegalStateException("Missing execution config file");
		}
		if ( getProperty(m_config, Category.HADOOP, HadoopConfig.PROP_MR4C_JAR)==null ) {
			throw new IllegalStateException("Missing mr4c jar file location");
		}
	}

	private void exportProperty(MR4CConfig bbConf, JobConf jobConf, Category category, String name, String hadoopName) {
		String val = bbConf.getCategory(category).getProperty(name);
		if ( !StringUtils.isEmpty(val) ) {
			jobConf.set(hadoopName,val);
		}
	}

	private void importProperty(MR4CConfig bbConf, JobConf jobConf, Category category, String name, String hadoopName) {
		setProperty(bbConf, category, name, jobConf.get(hadoopName));
	}

	private String getProperty(MR4CConfig bbConf, Category category, String name) {
		return bbConf.getCategory(category).getProperty(name);
	}

	private void setProperty(MR4CConfig bbConf, Category category, String name, String val) {
		if ( !StringUtils.isEmpty(val) ) {
			bbConf.getCategory(category).setProperty(name, val);
		}
	}

	private void clearProperty(MR4CConfig bbConf, Category category, String name) {
		bbConf.getCategory(category).clearProperty(name);
	}

	public String toString() {
		return String.format(
			"onCluster = [%s];" +
			" envVars = [%s];" +
			" envVarMap = [%s]",
			m_onCluster,
			m_envVars,
			m_envVarMap
			// not dumping config, its a load
		);
	}
}
