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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.hadoop.conf.Configuration;

public class ResourceConfig {

	private Map<String,ResourceRequest> m_requests = Collections.synchronizedMap( new HashMap<String,ResourceRequest>() );

	public boolean isEmpty() {
		return m_requests.isEmpty();
	}

	public Collection<ResourceRequest> getAllRequests() {
		return m_requests.values();
	}

	public ResourceRequest getRequest(ResourceInfo resource) {
		return getRequest(resource.getResourceName());
	}

	public ResourceRequest getRequest(String name) {
		return m_requests.get(name);
	}

	public synchronized void addRequest(ResourceRequest request) {
		String name = request.getResource().getResourceName();
		if ( m_requests.containsKey(name) ) {
			throw new IllegalStateException(String.format("Config already contains request for resource named [%s]", name));
		}
		m_requests.put(request.getResource().getResourceName(), request);
	}

	public void addRequest(ResourceInfo resource, MR4CConfig bbConf) {
		ResourceRequest request = ResourceRequest.extractFrom(resource, bbConf);
		if ( request!=null ) {
			addRequest(request);
		}
	}

	public synchronized void addStandardResources(MR4CConfig bbConf) {
		for ( Resource resource : Resource.values() ) {
			addRequest(resource, bbConf);
		}
	}
	
	public void addLimit(ResourceLimit limit) {
		ResourceRequest request = getRequest(limit.getResource());
		if ( request!=null ) {
			request.addLimit(limit);
		}
	}

	public void addLimit(ResourceInfo resource, Configuration conf) {
		ResourceLimit limit = ResourceLimit.extractFrom(resource, conf);
		if ( limit!=null ) {
			addLimit(limit);
		}
	}

	public void addStandardLimits(Configuration conf) {
		for ( Resource resource : Resource.values() ) {
			addLimit(resource, conf);
		}
	}

	public void applyTo(MR4CConfig bbConf) {
		for ( ResourceRequest request : m_requests.values() ) {
			request.applyTo(bbConf);
		}
	}

	public void applyTo(Configuration conf) {
		for ( ResourceRequest request : m_requests.values() ) {
			request.applyTo(conf);
		}
	}

	public void resolveRequests() {
		for ( ResourceRequest request : m_requests.values() ) {
			request.resolve();
		}
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		ResourceConfig config = (ResourceConfig) obj;
		if ( !ObjectUtils.equals(m_requests, config.m_requests) ) return false;
		return true; 
	}

	public String toString() {
		return String.format(
			"requests=[%s]",
			m_requests
		);
	}


}
