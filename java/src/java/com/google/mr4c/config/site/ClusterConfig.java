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

public class ClusterConfig {

	private String jobTracker;
	private String nameNode;

	// for gson
	private ClusterConfig() {}

	public ClusterConfig(String jobTracker, String nameNode) {
		this.jobTracker = jobTracker;
		this.nameNode = nameNode;
	}

	public String getJobTracker() {
		return this.jobTracker;
	}

	public String getNameNode() {
		return this.nameNode;
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		ClusterConfig config = (ClusterConfig) obj;
		if ( !jobTracker.equals(config.jobTracker) ) return false;
		if ( !nameNode.equals(config.nameNode) ) return false;
		return true; 
	}

	public int hashCode() {
		return jobTracker.hashCode() +
			nameNode.hashCode();
	}

}
