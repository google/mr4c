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

public abstract class SiteConfigTestUtils {

	public static SiteConfig buildSiteConfig1() {
		SiteConfig siteConfig = new SiteConfig();
		siteConfig.addCluster("cluster1", new ClusterConfig("jt1", "nn1"));
		siteConfig.addCluster("cluster2", new ClusterConfig("jt2", "nn2"));
		return siteConfig;
	}

	public static SiteConfig buildSiteConfig2() {
		SiteConfig siteConfig = new SiteConfig();
		siteConfig.addCluster("cluster1", new ClusterConfig("jt1", "nn1"));
		siteConfig.addCluster("cluster2", new ClusterConfig("jt3", "nn3"));
		return siteConfig;
	}

}
