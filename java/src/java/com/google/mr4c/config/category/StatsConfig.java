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

public class StatsConfig extends CategoryConfig {

	public static final String PROP_CLIENT = "client";
	public static final String PROP_STATSD_HOST = "statsd.host";
	public static final String PROP_STATSD_PORT = "statsd.port";
	public static final String PROP_STATSD_FLUSH = "statsd.flush";

	// Deprecated Properties
	public static final String PROP_CLIENT_OLD = "mr4c.stats.client";
	public static final String PROP_STATSD_HOST_OLD = "mr4c.statsd.host";
	public static final String PROP_STATSD_PORT_OLD = "mr4c.statsd.port";
	public static final String PROP_STATSD_FLUSH_OLD = "mr4c.statsd.flush";

	public StatsConfig() {
		super(Category.STATS);
	}

	protected void customInit() {
		addDeprecatedProperty(PROP_CLIENT, PROP_CLIENT_OLD);
		addDeprecatedProperty(PROP_STATSD_HOST, PROP_STATSD_HOST_OLD);
		addDeprecatedProperty(PROP_STATSD_PORT, PROP_STATSD_PORT_OLD);
		addDeprecatedProperty(PROP_STATSD_FLUSH, PROP_STATSD_FLUSH_OLD);
	}

}
