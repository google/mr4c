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

package com.google.mr4c.content;

import com.google.mr4c.config.category.MR4CConfig;

import java.util.Properties;

import org.apache.hadoop.conf.Configuration;

import org.junit.*;
import static org.junit.Assert.*;

public class S3CredentialsTest {

	private S3Credentials m_cred1;
	private S3Credentials m_cred1a;
	private S3Credentials m_cred2;

	@Before public void setup() throws Exception {
		m_cred1 = buildS3Credentials1();
		m_cred1a = buildS3Credentials1();
		m_cred2 = buildS3Credentials2();
	}

	@Test public void testEqual() {
		assertEquals(m_cred1, m_cred1a);
	}

	@Test public void testNotEqual() {
		assertFalse(m_cred1.equals(m_cred2));
	}

	@Test public void testValid() {
		assertTrue(m_cred1.isValid());
	}

	@Test public void testInvalid() {
		assertFalse(m_cred2.isValid());
	}

	@Test public void testPropsUpdate() throws Exception {
		S3Credentials cred1 = buildS3Credentials1();
		Properties props = new Properties();
		cred1.applyTo(props);
		S3Credentials cred2 = S3Credentials.extractFrom(props);
		assertEquals(cred1, cred2);
	}
		
	@Test public void testConfigurationUpdate() throws Exception {
		S3Credentials cred1 = buildS3Credentials1();
		Configuration conf = new Configuration();
		conf.clear();
		cred1.applyTo(conf);
		S3Credentials cred2 = S3Credentials.extractFrom(conf);
		assertEquals(cred1, cred2);
	}
		
	@Test public void testMR4CConfigUpdate() throws Exception {
		S3Credentials cred1 = buildS3Credentials1();
		MR4CConfig bbConf = new MR4CConfig(false);
		bbConf.initStandardCategories();
		cred1.applyTo(bbConf);
		S3Credentials cred2 = S3Credentials.extractFrom(bbConf);
		assertEquals(cred1, cred2);
	}

	private S3Credentials buildS3Credentials1() {
		return new S3Credentials("id1", "secret1");
	}

	private S3Credentials buildS3Credentials2() {
		return new S3Credentials(null, null);
	}


}

