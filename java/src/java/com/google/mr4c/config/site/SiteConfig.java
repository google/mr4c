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

package com.google.mr4c.config.site;

import com.google.mr4c.config.ConfigDescriptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SiteConfig {

	private Map<String,ClusterConfig> clusters = Collections.synchronizedMap( new HashMap<String,ClusterConfig>() );

	public SiteConfig() {}

	public void addCluster(String name, ClusterConfig cluster) {
		this.clusters.put(name,cluster);
	}

	public ClusterConfig getCluster(String name) {
		return this.clusters.get(name);
	}

	public Set<String> getClusterNames() {
		return this.clusters.keySet();
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		SiteConfig config = (SiteConfig) obj;
		if ( !clusters.equals(config.clusters) ) return false;
		return true; 
	}

	public int hashCode() {
		return clusters.hashCode();
	}

}
