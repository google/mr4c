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

package com.google.mr4c.config.category;

public class HadoopConfig extends CategoryConfig {

	public static final String PROP_USER_RESOURCE = "user.conf";
	public static final String PROP_TASKS = "tasks";
	public static final String PROP_CLUSTER = "cluster";
	public static final String PROP_MR4C_JAR = "mr4c.jar";
	public static final String PROP_REMOTE = "remote";
	public static final String PROP_MIN_CORES = "cores.min";
	public static final String PROP_MAX_CORES = "cores.max";
	public static final String PROP_MIN_MEMORY = "memory.min";
	public static final String PROP_MAX_MEMORY = "memory.max";
	public static final String PROP_ALGORITHM_CLASSPATH = "algorithm.classpath";

	public HadoopConfig() {
		super(Category.HADOOP);
	}

}
