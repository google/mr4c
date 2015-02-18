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

import com.google.mr4c.config.site.MR4CSite;
import com.google.mr4c.config.site.ClusterConfig;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public abstract class Clusters {

	private static Map<String,Cluster> s_clusters = new HashMap<String,Cluster>();

	public static final String LOCAL = "local";

	public static Cluster getLocalCluster() throws IOException {
		return getCluster(LOCAL);
	}

	public static Cluster getCluster(String name) throws IOException {
		ClusterConfig clusterConfig = MR4CSite.getSiteConfig().getCluster(name);
		if ( clusterConfig!=null ) {
			return new Cluster(clusterConfig);
		}
		// check built-ins
		Cluster cluster = s_clusters.get(name);
		if ( cluster==null ) {
			throw new IllegalArgumentException("No cluster named=["+name+"]");
		}
		return cluster;
	}

	// Package visible to allow adding test clusters
	/*package*/ static void addCluster(String name, String tracker, String fs) {
		s_clusters.put(name, new Cluster(tracker,fs));
	}

	static {
		addCluster(LOCAL, "localhost:8021", "hdfs://localhost:8020");
		// other clusters in site.json file

	}

}
