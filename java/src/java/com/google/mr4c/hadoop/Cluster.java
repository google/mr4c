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

import com.google.mr4c.config.site.ClusterConfig;

import org.apache.commons.lang3.StringUtils;

import org.apache.hadoop.conf.Configuration;

public class Cluster {

	private String m_tracker;
	private String m_fs;
	public static final String FILE_SYS_PROP = "fs.defaultFS";
	public static final String DEPRECATED_FILE_SYS_PROP = "fs.default.name";

	public static String getJobTrackerPropertyName() {
		return HadoopUtils.getHadoopBinding().getJobTrackerPropertyName();
	}

	public Cluster(String tracker, String fs) {
		m_tracker = tracker;
		m_fs = fs;
	}

	public Cluster(ClusterConfig clusterConfig) {
		m_tracker = clusterConfig.getJobTracker();
		m_fs = clusterConfig.getNameNode();
	}

	public static Cluster extractFromConfig(Configuration conf) {
		String tracker = conf.get(getJobTrackerPropertyName());
		String fs = conf.get(FILE_SYS_PROP);
		if ( fs==null ) {
			fs = conf.get(DEPRECATED_FILE_SYS_PROP);
		}
		if ( tracker==null || fs==null ) {
			return null;
		}
		return new Cluster(tracker,fs);
	}

	public String getJobTracker() {
		return m_tracker;
	}

	public String getFileSystem() {
		return m_fs;
	}

	public void applyToConfig(Configuration conf) {
		conf.set(getJobTrackerPropertyName(), m_tracker);
		conf.set(FILE_SYS_PROP, m_fs);
	}

	public void fixHostname(Configuration conf) {
		String propName = HadoopUtils.getHadoopBinding().getJobTrackerHostPropertyName();
		if ( propName!=null ) {
			String[] parts = m_tracker.split(":");
			conf.set(propName, parts[0]);
		}
	}

	public String toString() {
		return String.format("Job Tracker=[%s]; File System=[%s]", m_tracker, m_fs);
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( obj==null ) return false;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		Cluster cluster = (Cluster) obj;
		if ( !StringUtils.equals(m_tracker, cluster.m_tracker) ) return false;
		if ( !StringUtils.equals(m_fs, cluster.m_fs) ) return false;
		return true;
	}

	public int hashCode() {
		return m_tracker.hashCode() + m_fs.hashCode();
	}

}

