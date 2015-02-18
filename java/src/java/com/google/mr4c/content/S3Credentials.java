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
import com.google.mr4c.config.category.Category;
import com.google.mr4c.config.category.CategoryConfig;
import com.google.mr4c.config.category.S3Config;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import org.apache.hadoop.conf.Configuration;

public class S3Credentials {

	public static final String PROP_NATIVE_ID = "fs.s3n.awsAccessKeyId";
	public static final String PROP_NATIVE_SECRET = "fs.s3n.awsSecretAccessKey";
	public static final String PROP_BLOCK_ID = "fs.s3.awsAccessKeyId";
	public static final String PROP_BLOCK_SECRET = "fs.s3.awsSecretAccessKey";

	private String m_id;
	private String m_secret;

	public S3Credentials(String id, String secret) {
		m_id = id;
		m_secret = secret;
	}

	public String getID() {
		return m_id;
	}

	public String getSecret() {
		return m_secret;
	}

	public boolean isValid() {
		return !(StringUtils.isEmpty(m_id) || StringUtils.isEmpty(m_secret) );
	}

	public void applyTo(Configuration conf) {
		conf.set(PROP_NATIVE_ID, m_id);
		conf.set(PROP_NATIVE_SECRET, m_secret);
		conf.set(PROP_BLOCK_ID, m_id);
		conf.set(PROP_BLOCK_SECRET, m_secret);
	}

	public static S3Credentials extractFrom(Configuration conf) {
		
		String id = conf.get(PROP_NATIVE_ID);
		String secret = conf.get(PROP_NATIVE_SECRET);
		S3Credentials cred = new S3Credentials(id,secret);
		if ( !cred.isValid() ) {
			id = conf.get(PROP_BLOCK_ID);
			secret = conf.get(PROP_BLOCK_SECRET);
		}
		cred = new S3Credentials(id,secret);
		return cred.isValid() ? cred : null;
	}

	public void applyTo(Properties props) {
		props.setProperty(PROP_NATIVE_ID, m_id);
		props.setProperty(PROP_NATIVE_SECRET, m_secret);
		props.setProperty(PROP_BLOCK_ID, m_id);
		props.setProperty(PROP_BLOCK_SECRET, m_secret);
	}

	public static S3Credentials extractFrom(Properties props) {
		String id = props.getProperty(PROP_NATIVE_ID);
		String secret = props.getProperty(PROP_NATIVE_SECRET);
		S3Credentials cred = new S3Credentials(id,secret);
		if ( !cred.isValid() ) {
			id = props.getProperty(PROP_BLOCK_ID);
			secret = props.getProperty(PROP_BLOCK_SECRET);
		}
		cred = new S3Credentials(id,secret);
		return cred.isValid() ? cred : null;
	}

	public void applyTo(MR4CConfig config) {
		CategoryConfig catConf = config.getCategory(Category.S3);
		catConf.setProperty(S3Config.PROP_ID, m_id);
		catConf.setProperty(S3Config.PROP_SECRET, m_secret);
	}

	public static S3Credentials extractFrom(MR4CConfig config) {
		CategoryConfig catConf = config.getCategory(Category.S3);
		S3Credentials cred = new S3Credentials(
			catConf.getProperty(S3Config.PROP_ID),
			catConf.getProperty(S3Config.PROP_SECRET)
		);
		return cred.isValid() ? cred : null;
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		S3Credentials cred = (S3Credentials) obj;
		if ( !StringUtils.equals(m_id, cred.m_id) ) return false;
		if ( !StringUtils.equals(m_secret, cred.m_secret) ) return false;
		return true;
	}

	public String toString() {
		return String.format("id=[%s]; secret=[%s]", m_id, m_secret);
	}
}

