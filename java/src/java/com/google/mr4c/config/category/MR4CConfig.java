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

import com.google.mr4c.config.ConfigUtils;
import com.google.mr4c.util.NamespacedProperties;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;

public class MR4CConfig {

	private static MR4CConfig s_default;
	private Map<String,CategoryConfig> m_configs = new HashMap<String,CategoryConfig>();
	private boolean m_includeSysProps;


	private static Map<Category,Class<? extends CategoryConfig>> s_configClasses = new HashMap<Category,Class<? extends CategoryConfig>>();

	static {
		s_configClasses.put(Category.CORE, CoreConfig.class);
		s_configClasses.put(Category.STATS, StatsConfig.class);
	}

	public static synchronized MR4CConfig getDefaultInstance() {
		if ( s_default==null ) {
			initDefaultInstance();
		}
		return s_default;
	}

	private static void initDefaultInstance() {
		s_default = new MR4CConfig(true);
		s_default.initStandardCategories();
	}

	public MR4CConfig(boolean includeSysProps) {
		m_includeSysProps = includeSysProps;
	}

	public synchronized void addCategory(CategoryConfig catConf) {
		String name = catConf.getCategory().getCategoryName();
		if ( m_configs.containsKey(name) ) {
			throw new IllegalStateException(String.format("Config already contains category named [%s]", name));
		}
		m_configs.put(name, catConf);
	}

	public void addCategory(CategoryInfo category) {
		CategoryConfig catConf = new CategoryConfig(category);
		catConf.init(m_includeSysProps);
		addCategory(catConf);
	}

	public Collection<CategoryConfig> getAllCategoryConfigs() {
		return m_configs.values();
	}

	public void initStandardCategories() {
		for ( Category category : Category.values() ) {
			CategoryConfig catConf = newCategoryConfig(category);
			catConf.init(m_includeSysProps);
			addCategory(catConf);
		}
	}

	public CategoryConfig getCategory(Category category) {
		return getCategory(category.getCategoryName());
	}

	public CategoryConfig getCategory(String name) {
		CategoryConfig config = m_configs.get(name);
		if ( config==null ) {
			throw new IllegalStateException(String.format("No config category named [%s]", name));
		}
		return config;
	}

	/**
	  * Export all child configs as namespaced properties
	*/
	public synchronized Properties getProperties() {
		Properties props = new Properties();
		for ( CategoryConfig catConf : m_configs.values() ) {
			props.putAll(catConf.getProperties(true));
		}
		return props;
	}

	/**
	  * Import only properties for each child category's namespace from a source of properties
	*/
	public synchronized void importProperties(Iterable<Map.Entry<String,String>> propSrc) {
		for ( CategoryConfig catConf : m_configs.values() ) {
			catConf.importProperties(propSrc);
		}
	}

	/**
	  * Clone properties into a new configuration.  The new config will not be tied to system properties, but will reflect their resolution if the old config was.
	*/
	public static MR4CConfig clone(MR4CConfig config) {
		synchronized (config) {
			MR4CConfig newConfig  = new MR4CConfig(false);
			for ( CategoryConfig catConf : config.getAllCategoryConfigs() ) {
				CategoryConfig newCatConf = new CategoryConfig(catConf.getCategory());
				newCatConf.init(false);
				newCatConf.setProperties(catConf.getProperties(false));
				newConfig.addCategory(newCatConf);
			}
			return newConfig;
		}
	}

	private CategoryConfig newCategoryConfig(Category category) {
		Class<? extends CategoryConfig> confClass = s_configClasses.get(category);
		if ( confClass==null ) {
			return new CategoryConfig(category);
		}
		try {
			return confClass.newInstance();
		} catch ( Exception e ) {
			throw new IllegalStateException(e);
		}
	}

	public synchronized void dumpConfig(Logger log, boolean files) throws IOException {
		for ( CategoryConfig catConf : getAllCategoryConfigs() ) {
			Properties props = ConfigUtils.resolveProperties(catConf.getProperties(false), false);
			String name = catConf.getCategory().getCategoryName();
			ConfigUtils.logProperties(name, log, props);
			if ( files && !props.isEmpty() ) {
				String file = String.format("mr4c-resolved-%s.properties", name);
				ConfigUtils.dumpProperties(props, file);
			}
		}
	}
			
}
