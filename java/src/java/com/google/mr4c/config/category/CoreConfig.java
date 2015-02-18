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

public class CoreConfig extends CategoryConfig {

	public static final String PROP_LOG4J_CONF = "log4j.conf";
	public static final String PROP_LOG4CXX_CONF = "log4cxx.conf";
	public static final String PROP_EXE_CONF = "exe.conf";
	public static final String PROP_LIB_PATH = "lib.path";
	public static final String PROP_SITE_CONF = "site.conf";
	public static final String PROP_ROOT_DIR = "root.dir";
	public static final String PROP_DUMP_PROPERTIES = "dump.properties";

	// Deprecated Properties
	public static final String PROP_LOG4J_CONF_OLD = "mr4c.log4j";
	public static final String PROP_SITE_CONF_OLD = "mr4c.site";
	public static final String PROP_ROOT_DIR_OLD = "mr4c.root";

	// External Properties
	public static final String PROP_LIB_PATH_EXT = "jna.library.path";

	public CoreConfig() {
		super(Category.CORE);
	}

	protected void customInit() {
		addDeprecatedProperty(PROP_LOG4J_CONF, PROP_LOG4J_CONF_OLD);
		addDeprecatedProperty(PROP_SITE_CONF, PROP_SITE_CONF_OLD);
		addDeprecatedProperty(PROP_ROOT_DIR, PROP_ROOT_DIR_OLD);
		addExternalProperty(PROP_LIB_PATH, PROP_LIB_PATH_EXT);
		setProperty(PROP_ROOT_DIR, System.getProperty("user.dir"));
	}

}
