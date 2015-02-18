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

package com.google.mr4c.stats;

import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.Category;
import com.google.mr4c.config.category.CategoryConfig;
import com.google.mr4c.config.category.StatsConfig;
import com.google.mr4c.util.MR4CLogging;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;

public abstract class MR4CStats {

	protected static final Logger s_log = MR4CLogging.getLogger(MR4CStats.class);

	public static final String STATSD_NAME = "statsd";

	private static StatsClient s_client;

	public static synchronized StatsClient getClient() {
		if ( s_client==null ) {
			initClient();
		}
		return s_client;
	}

	private static void initClient() {
		CategoryConfig catConf = MR4CConfig.getDefaultInstance().getCategory(Category.STATS);
		String type = catConf.getProperty(StatsConfig.PROP_CLIENT);
		if ( StringUtils.isEmpty(type) ) {
			s_client = new NoOpStatsClient();
		} else if ( STATSD_NAME.equals(type) ) {
			s_client = createStatsdClient(catConf);
		} else {
			throw new IllegalArgumentException(String.format("No stats client type named [%s]", type));
		}
	}
	
	private static StatsdClient createStatsdClient(CategoryConfig catConf) {
		String host = catConf.getProperty(StatsConfig.PROP_STATSD_HOST, "localhost");
		int port = Integer.parseInt(catConf.getProperty(StatsConfig.PROP_STATSD_PORT, "8125"));
		int flush = Integer.parseInt(catConf.getProperty(StatsConfig.PROP_STATSD_FLUSH, "10"));
	
		s_log.info("Creating Statsd client for [{}:{}] ", host, port);

		try {
			StatsdClient client = new StatsdClient(host, port);
			// Just going to push immediately - we may have short run times, plus the timer thread prevents exiting
			// Uncomment these two lines to switch to a periodic flush
			//client.enableMultiMetrics(true);
			//client.startFlushTimer(1000*flush);
			return client;
		} catch ( Exception e ) {
			throw new IllegalArgumentException(e);
		}

	}

}
