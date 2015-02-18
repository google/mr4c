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

import com.google.mr4c.config.resources.ResourceConfig;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.mapred.JobConf;

public interface HadoopBinding {

	String getMapReduceFrameworkName();

	String getJobTrackerPropertyName();

	String getJobTrackerHostPropertyName();

	List<String> getDefaultResourcesToLoad();

	List<String> getResourcesToLoad();

	List<String> getPropertiesToLog();

	void addClusterLimits(JobConf jobConf, ResourceConfig resConf) throws IOException;

}

