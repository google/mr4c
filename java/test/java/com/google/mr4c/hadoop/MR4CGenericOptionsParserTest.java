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

import org.apache.hadoop.conf.Configuration;

import org.junit.*;
import static org.junit.Assert.*;

public class MR4CGenericOptionsParserTest {

	private String[] m_inputArgs;
	private String[] m_expectedParsedArgs;
	private Configuration m_expectedConf;
	private Configuration m_emptyConf;


	@Before public void setup() throws Exception {
		buildInputArguments();
		buildExpectedParsedArguments();
		buildExpectedConfiguration();
		buildEmptyConfiguration();
	}

	@After public void teardown() throws Exception {
		MR4CGenericOptionsParser.cleanup();
	}

	@Test public void testParser() throws Exception {
		MR4CGenericOptionsParser parser = new MR4CGenericOptionsParser(m_inputArgs);
		assertEquals("parsed args", m_expectedParsedArgs, parser.getRemainingArgs());
		HadoopTestUtils.assertEquals(m_expectedConf, parser.getConfiguration()); 
	}

	@Test public void testParserNoConf() throws Exception {
		MR4CGenericOptionsParser parser = new MR4CGenericOptionsParser(m_expectedParsedArgs);
		assertEquals("parsed args", m_expectedParsedArgs, parser.getRemainingArgs());
		HadoopTestUtils.assertEquals(m_emptyConf, parser.getConfiguration());
	}
		
	private void buildInputArguments() {
		m_inputArgs = new String[] {
			"arg1", "arg2",
			"-D", "prop1=val1",
			"-D", "prop2=val2",
			"-fs", "hdfs://localhost:8020",
			"-jt", "localhost:8021",
			"-files", "conf/log4j.properties",
			"mr4c.runtime.prop1=val1", "args3"
		};
	}

	private void buildExpectedConfiguration() {
		m_expectedConf = new Configuration(false);
		m_expectedConf.set("prop1", "val1");
		m_expectedConf.set("prop2", "val2");
		m_expectedConf.set(Cluster.FILE_SYS_PROP, "hdfs://localhost:8020");
		if ( HadoopTestUtils.getHadoopTestBinding().expectedDeprecatedFileSysProp() ) {
			m_expectedConf.set(Cluster.DEPRECATED_FILE_SYS_PROP, "hdfs://localhost:8020"); 
		}
		m_expectedConf.set(Cluster.getJobTrackerPropertyName(), "localhost:8021");
		m_expectedConf.set(MR4CGenericOptions.FILE_LIST_PROP, toFileURI("conf/log4j.properties"));
		HadoopTestUtils.addParserUsedProperty(m_expectedConf);
	}

	private void buildEmptyConfiguration() {
		m_emptyConf = new Configuration(false);
		HadoopTestUtils.addParserUsedProperty(m_emptyConf);
	}

	private void buildExpectedParsedArguments() {
		m_expectedParsedArgs = new String[] {
			"arg1", "arg2", "mr4c.runtime.prop1=val1", "args3"
		};
	}

	private String toFileURI(String path) {
		return new File(path).toURI().toString();
	}

}
