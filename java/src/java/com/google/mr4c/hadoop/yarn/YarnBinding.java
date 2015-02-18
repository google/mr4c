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

package com.google.mr4c.hadoop.yarn;

import com.google.mr4c.config.resources.LimitSource;
import com.google.mr4c.config.resources.Resource;
import com.google.mr4c.config.resources.ResourceConfig;
import com.google.mr4c.config.resources.ResourceLimit;
import com.google.mr4c.hadoop.HadoopBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.exceptions.YarnException;

public class YarnBinding implements HadoopBinding {

	public static final String FRAMEWORK_NAME = "yarn";
	public static final String JOB_TRACKER_PROP = "yarn.resourcemanager.address";
	public static final String YARN_HOST_NAME_PROP = "yarn.resourcemanager.hostname";

	public String getMapReduceFrameworkName() {
		return FRAMEWORK_NAME;
	}

	public String getJobTrackerPropertyName() {
		return JOB_TRACKER_PROP;
	}

	public String getJobTrackerHostPropertyName() {
		return YARN_HOST_NAME_PROP;
	}

	public List<String> getDefaultResourcesToLoad() {
		return Arrays.asList("yarn-default.xml");
	}

	public List<String> getResourcesToLoad() {
		return Arrays.asList("yarn-site.xml");
	}

	public List<String> getPropertiesToLog() {
		return Arrays.asList(
			"mapreduce.application.classpath",
			"yarn.application.classpath"
		);
	}

	public void addClusterLimits(JobConf jobConf, ResourceConfig resConf) throws IOException {
		if ( resConf.isEmpty() ) {
			return;
		}
		Configuration conf = new Configuration(jobConf);
		YarnClient client = YarnClient.createYarnClient();
		client.init(conf);
		client.start();
		try {
			for (ResourceLimit limit : computeResourceLimits(client.getNodeReports()) ) {
				resConf.addLimit(limit);
			}
		} catch ( YarnException ye ) {
			throw new IOException(ye);
		} finally {
			client.stop();
		}
	}

	public static List<ResourceLimit> computeResourceLimits(Iterable<NodeReport> reports) {
		if ( !reports.iterator().hasNext() ) {
			return Collections.emptyList();
		}

		List<ResourceLimit> limits = new ArrayList<ResourceLimit>();
		int cores = 0;
		int memory = 0;
		for ( NodeReport report : reports ) {
			org.apache.hadoop.yarn.api.records.Resource cap = report.getCapability();
			cores = Math.max(cores, cap.getVirtualCores());
			memory = Math.max(memory, cap.getMemory());
		}
		limits.add(new ResourceLimit(Resource.CORES, cores, LimitSource.CLUSTER));
		limits.add(new ResourceLimit(Resource.MEMORY, memory, LimitSource.CLUSTER));
		return limits;
	}


}

