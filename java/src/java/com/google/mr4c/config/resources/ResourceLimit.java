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

package com.google.mr4c.config.resources;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import org.apache.hadoop.conf.Configuration;

public class ResourceLimit {

	private ResourceInfo m_resource;
	private int m_value;
	private LimitSource m_source;

	public ResourceLimit(
		ResourceInfo resource,
		int value,
		LimitSource source
	) {
		m_resource = resource;
		m_value = value;
		m_source = source;
	}

	public ResourceInfo getResource() {
		return m_resource;
	}

	public int getValue() {
		return m_value;
	}

	public LimitSource getSource() {
		return m_source;
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		ResourceLimit limit = (ResourceLimit) obj;
		if ( !ObjectUtils.equals(m_resource, limit.m_resource) ) return false;
		if ( !ObjectUtils.equals(m_value, limit.m_value) ) return false;
		if ( !ObjectUtils.equals(m_source, limit.m_source) ) return false;
		return true; 
	}

	public String toString() {
		return String.format(
			"resource=[%s]; " +
			"value=[%s]; " +
			"source=[%s]",
			m_resource,
			m_value,
			m_source
		);
	}

	public static ResourceLimit extractFrom(ResourceInfo resource, Configuration conf) {
		String strVal = conf.get(resource.getMaxHadoopName());
		if ( StringUtils.isEmpty(strVal) ) {
			return null;
		}
		int val = Integer.parseInt(strVal);
		return new ResourceLimit(resource, val, LimitSource.CONFIG);
	}

	public void applyTo(Configuration conf) {
		conf.set(m_resource.getMaxHadoopName(), Integer.toString(m_value));
	}

}
