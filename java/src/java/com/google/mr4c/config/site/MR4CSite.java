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

import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.Category;
import com.google.mr4c.config.category.CoreConfig;
import com.google.mr4c.content.ContentFactories;
import com.google.mr4c.serialize.ConfigSerializer;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;

public abstract class MR4CSite {

	protected static final Logger s_log = MR4CLogging.getLogger(MR4CSite.class);

	public static final String PROP_SITE_FILE = "mr4c.site";
	public static final String DEFAULT_SITE_FILE = "/etc/mr4c/site.json";
	private static SiteConfig s_config;

	public synchronized static SiteConfig getSiteConfig() throws IOException {
		if ( s_config==null ) {
			loadSiteConfig();
		}
		return s_config;
	}

	private static void loadSiteConfig() throws IOException {
		URI siteConf = findSiteConf();
		s_log.info("Reading site config from [{}]", siteConf);
		ConfigSerializer ser = SerializerFactories.getSerializerFactory("application/json").createConfigSerializer(); // assume json config for now
		Reader reader = ContentFactories.readContentAsReader(siteConf);
		try {
			s_config = ser.deserializeSiteConfig(reader);
		} finally {
			reader.close();
		}
	}

	private static URI findSiteConf() {
		String file = MR4CConfig.getDefaultInstance().getCategory(Category.CORE).getProperty(CoreConfig.PROP_SITE_CONF);
		if ( StringUtils.isEmpty(file) ) {
			file = DEFAULT_SITE_FILE;
		}
		return URI.create(file);
	}

}
