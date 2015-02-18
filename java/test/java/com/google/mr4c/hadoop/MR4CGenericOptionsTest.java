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
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class MR4CGenericOptionsTest {

	private MR4CGenericOptions m_opts;
	private List<String> m_expectedFiles;
	private List<String> m_expectedJars;

	@Before public void setup() {
		m_opts = new MR4CGenericOptions();
		m_expectedFiles = new ArrayList<String>();
		m_expectedJars = new ArrayList<String>();
	}

	@After public void teardown() throws Exception {
		MR4CGenericOptionsParser.cleanup();
	}

	@Test public void testClusterOnly() throws Exception {
		includeCluster();
		doTest();
	}

	@Test public void testFilesOnly() throws Exception {
		includeFiles();
		doTest();
	}

	@Test public void testJarsOnly() throws Exception {
		includeJars();
		doTest();
	}

	@Test public void testAllOptions() throws Exception {
		includeCluster();
		includeFiles();
		includeJars();
		doTest();
	}

	private void doTest() throws Exception {
		String[] args = m_opts.toGenericOptions();
		MR4CGenericOptionsParser parser = new MR4CGenericOptionsParser(args);
		assertTrue("No args left", parser.getRemainingArgs().length==0);	
		MR4CGenericOptions opts = MR4CGenericOptions.extractFromConfig(parser.getConfiguration());
		assertEquals("cluster", m_opts.getCluster(), opts.getCluster());
		assertEquals("files", m_expectedFiles, opts.getFiles());
		assertEquals("jars", m_expectedJars, opts.getJars());
	}

	private void includeCluster() {
		Cluster cluster = new Cluster("jobtracker:8021", "hdfs://namenode:8020");
		m_opts.setCluster(cluster);
	}

	private void includeFiles() {
		m_opts.addFile(new File("conf/log4j.properties"), "log4j");
		m_opts.addFile(URI.create("conf/log4cxx.properties"), "log4cxx");
		m_expectedFiles.add(toFileURI("conf/log4j.properties")+"#log4j");
		m_expectedFiles.add(toFileURI("conf/log4cxx.properties")+"#log4cxx");
	}

	private void includeJars() {
		m_opts.addJar(URI.create("test/resources/empty.jar"));
		m_expectedJars.add(toFileURI("test/resources/empty.jar"));
	}

	private String toFileURI(String path) {
		return new File(path).toURI().toString();
	}

}
