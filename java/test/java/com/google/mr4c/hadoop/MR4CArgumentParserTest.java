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

import java.net.URI;

import org.junit.*;
import static org.junit.Assert.*;

public class MR4CArgumentParserTest {

	private static String USAGE = "some usage message";

	@Test public void testParserAllArgs() throws Exception {
		String jar = "mr4c.jar";
		String uri = "input.conf";
		String cluster = "dev";
		Integer tasks = 5;
		String[] args = new String[] { jar, uri, cluster, Integer.toString(tasks) };
		MR4CArgumentParser parser = new MR4CArgumentParser(args, USAGE);
		parser.parse();
		assertEquals(jar, parser.getJar());
		assertEquals(URI.create(uri),parser.getExeConf());
		assertEquals(tasks, parser.getTasks());
		assertEquals(cluster, parser.getCluster());
	}

	@Test public void testParserNoClusterNoTasks() throws Exception {
		String jar = "mr4c.jar";
		String uri = "input.conf";
		String[] args = new String[] { jar, uri };
		MR4CArgumentParser parser = new MR4CArgumentParser(args, USAGE);
		parser.parse();
		assertEquals(jar, parser.getJar());
		assertEquals(URI.create(uri),parser.getExeConf());
		assertNull(parser.getCluster());
		assertNull(parser.getTasks());
	}

	@Test public void testParserNoCluster() throws Exception {
		String jar = "mr4c.jar";
		String uri = "input.conf";
		Integer tasks = 5;
		String[] args = new String[] { jar, uri, Integer.toString(tasks) };
		MR4CArgumentParser parser = new MR4CArgumentParser(args, USAGE);
		parser.parse();
		assertEquals(jar, parser.getJar());
		assertEquals(URI.create(uri),parser.getExeConf());
		assertEquals(tasks, parser.getTasks());
		assertNull(parser.getCluster());
	}

	@Test public void testParserNoTasks() throws Exception {
		String jar = "mr4c.jar";
		String uri = "input.conf";
		String cluster = "dev";
		String[] args = new String[] { jar, uri, cluster };
		MR4CArgumentParser parser = new MR4CArgumentParser(args, USAGE);
		parser.parse();
		assertEquals(jar, parser.getJar());
		assertEquals(URI.create(uri),parser.getExeConf());
		assertNull(parser.getTasks());
		assertEquals(cluster, parser.getCluster());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testParserMissingArgs() throws Exception {
		String[] args = new String[] { "arg1" };
		MR4CArgumentParser parser = new MR4CArgumentParser(args, USAGE);
		parser.parse();
	}

	@Test(expected=IllegalArgumentException.class)
	public void testParserTooManyArgs() throws Exception {
		String[] args = new String[] { "arg1", "arg2", "arg3", "arg4", "arg5" };
		MR4CArgumentParser parser = new MR4CArgumentParser(args, USAGE);
		parser.parse();
	}

}
