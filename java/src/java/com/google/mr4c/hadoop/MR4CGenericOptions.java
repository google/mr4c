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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import org.apache.hadoop.conf.Configuration;

public class MR4CGenericOptions {

	public static final String FILE_LIST_PROP = "tmpfiles";
	public static final String JAR_LIST_PROP = "tmpjars";

	private Cluster m_cluster;
	private List<String> m_files = new ArrayList<String>();
	private List<String> m_jars = new ArrayList<String>();


	public void setCluster(Cluster cluster) {
		m_cluster = cluster;
	}

	public Cluster getCluster() {
		return m_cluster;
	}

	public List<String> getFiles() {
		return m_files;
	}

	public List<String> getJars() {
		return m_jars;
	}
		
	public void addFiles(Collection<File> files) {
		for ( File file : files ) {
			addFile(file);
		}
	}

	public void addFile(File file) {
		m_files.add(file.getPath());
	}

	public void addFile(File file, String alias) {
		m_files.add(file.getPath()+"#"+alias);
	}

	public void addFile(URI file) {
		m_files.add(file.toString());
	}

	public void addFile(URI file, String alias) {
		m_files.add(file.toString()+"#"+alias);
	}

	public void addJars(Collection<URI> jars) {
		for ( URI jar : jars ) {
			addJar(jar);
		}
	}

	public void addJar(URI jar) {
		m_jars.add(jar.toString());
	}

	public String[] toGenericOptions() {
		List<String> args = new ArrayList<String>();
		if ( m_cluster!=null ) {
			args.add("-jt");
			args.add(m_cluster.getJobTracker());
			args.add("-fs");
			args.add(m_cluster.getFileSystem());
		}
		if ( !m_files.isEmpty() ) {
			args.add("-files");
			args.add(StringUtils.join(m_files, ","));
		}
		if ( !m_jars.isEmpty() ) {
			args.add("-libjars");
			args.add(StringUtils.join(m_jars, ","));
		}
		return args.toArray(new String[args.size()]);
	}

	public static MR4CGenericOptions extractFromConfig(Configuration conf) {
		MR4CGenericOptions opts = new MR4CGenericOptions();
		opts.setCluster(Cluster.extractFromConfig(conf));
		String fileList = conf.get(FILE_LIST_PROP);
		if ( fileList!=null ) {
			for ( String file : fileList.split(",") ) {
				opts.addFile(URI.create(file));
			}
		}
		String jarList = conf.get(JAR_LIST_PROP);
		if ( jarList != null ) {
			for ( String jar : jarList.split(",") ) {
				opts.addJar(URI.create(jar));
			}
		}
		return opts;
	}

	public String toString() {
		return String.format("Cluster=[%s]; Files=[%s]; Jars=[%s]", m_cluster, m_files, m_jars);
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		MR4CGenericOptions opts = (MR4CGenericOptions) obj;
		if ( !ObjectUtils.equals(m_cluster, opts.m_cluster) ) return false;
		if ( !ObjectUtils.equals(m_files, opts.m_files) ) return false;
		if ( !ObjectUtils.equals(m_jars, opts.m_jars) ) return false;
		return true;
	}

}
