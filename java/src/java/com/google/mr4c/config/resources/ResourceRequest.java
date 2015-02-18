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

import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.CategoryConfig;

import org.apache.commons.lang3.ObjectUtils;

import org.apache.hadoop.conf.Configuration;

public class ResourceRequest {

	private ResourceInfo m_resource;
	private Integer m_min;
	private Integer m_max;
	private Integer m_actual;
	private ResourceLimit m_limit;
	private boolean m_limited=false;

	public ResourceRequest(
		ResourceInfo resource,
		Integer min,
		Integer max
	) {
		m_resource = resource;
		m_min = min;
		m_max = max;
		validateMinMax();
	}

	public ResourceInfo getResource() {
		return m_resource;
	}

	public Integer getMin() {
		return m_min;
	}

	public Integer getMax() {
		return m_max;
	}

	public Integer getActual() {
		return m_actual;
	}

	public synchronized void addLimit(ResourceLimit limit) {
		if ( !limit.getResource().getResourceName().equals(m_resource.getResourceName()) ) {
			throw new IllegalArgumentException(String.format("Resource mismatch: tried to add limit for [%s] to resource [%s]", limit.getResource().getResourceName(), m_resource.getResourceName()));
		}
		if ( m_limit==null || limit.getSource().outranks(m_limit.getSource()) ) {
			m_limit=limit;
		}
	}

	public ResourceLimit getLimit() {
		return m_limit;
	}

	public boolean isLimited() {
		return m_limited;
	}

	public synchronized void resolve() {
		validateLimit();
		if ( m_max!=null ) {
			if ( m_limit!=null &&  m_limit.getValue() < m_max ) {
				m_limited = true;
				m_actual = m_limit.getValue();
			} else {
				m_actual = m_max;
			}
		} else {
			m_actual = m_min;
		}
	}

	private void validateMinMax() {
		if ( m_min==null && m_max==null ) {
			throw new IllegalStateException(String.format("Either min or max must be provided for resource [%s]", m_resource.getResourceName()));
		}
		if ( m_max!=null && m_min!=null && m_max < m_min ) {
			throw new IllegalStateException(String.format("Min [%d] is greater than max [%d] for resource [%s]", m_min, m_max, m_resource.getResourceName()));
		}
	}

	private void validateLimit() {
		if ( m_min!=null && m_limit!=null && m_limit.getValue() < m_min ) {
			throw new IllegalStateException("Requested min is greater than limit");
		}
	}
		
	public void applyTo(MR4CConfig bbConf) {
		CategoryConfig catConf = bbConf.getCategory(m_resource.getConfigCategory());
		if ( m_min!=null ) {
			catConf.setProperty(m_resource.getMinConfigName(), m_min.toString());
		}
		if ( m_max!=null ) {
			catConf.setProperty(m_resource.getMaxConfigName(), m_max.toString());
		}
	}

	public static ResourceRequest extractFrom(ResourceInfo resource, MR4CConfig bbConf) {
		CategoryConfig catConf = bbConf.getCategory(resource.getConfigCategory());
		String strMin = catConf.getProperty(resource.getMinConfigName());
		String strMax = catConf.getProperty(resource.getMaxConfigName());
		if ( strMin==null && strMax==null ) {
			return null;
		}
		Integer min = toInt(strMin);
		Integer max = toInt(strMax);
		return new ResourceRequest(resource, min, max);
	}

	private static Integer toInt(String strInt) {
		return strInt==null ? null : Integer.parseInt(strInt);
	}

	public synchronized void applyTo(Configuration conf) {
		if ( m_actual!=null ) {
			conf.set(m_resource.getHadoopName(), m_actual.toString());
		}
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		ResourceRequest request = (ResourceRequest) obj;
		if ( !ObjectUtils.equals(m_resource, request.m_resource) ) return false;
		if ( !ObjectUtils.equals(m_min, request.m_min) ) return false;
		if ( !ObjectUtils.equals(m_max, request.m_max) ) return false;
		if ( !ObjectUtils.equals(m_actual, request.m_actual) ) return false;
		if ( !ObjectUtils.equals(m_limit, request.m_limit) ) return false;
		if ( !ObjectUtils.equals(m_limited, request.m_limited) ) return false;
		return true; 
	}

	public String toString() {
		return String.format(
			"resource=[%s]; " +
			"min=[%s]; " +
			"max=[%s]; " +
			"actual=[%s]; " +
			"limit=[%s]; " +
			"limited=[%s]",
			m_resource,
			m_min,
			m_max,
			m_actual,
			m_limit,
			m_limited
		);
	}

}
