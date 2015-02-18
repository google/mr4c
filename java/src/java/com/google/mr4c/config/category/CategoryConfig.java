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

import com.google.mr4c.util.MR4CLogging;
import com.google.mr4c.util.NamespacedProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CategoryConfig implements CategorySystemData {

	private CategoryInfo m_category;
	private NamespacedProperties m_props;
	private boolean m_includeSysProps;
	private NamespacedProperties m_sysProps;
	private Map<String,String> m_deprecatedMap =
			Collections.synchronizedMap( new HashMap<String,String>() ); // name in this space ---> deprecated name
	private Map<String,String> m_externalMap =
			Collections.synchronizedMap( new HashMap<String,String>() ); // name in this space ---> external name

	public CategoryConfig(CategoryInfo category) {
		m_category = category;
	}

	public void init(boolean includeSysProps) {
		m_includeSysProps = includeSysProps;
		m_props = new NamespacedProperties(m_category.getPropertiesPrefix(), new Properties());
		m_sysProps = new NamespacedProperties(m_category.getPropertiesPrefix());
		customInit();
	}

	protected void customInit() {}

	public CategoryInfo getCategory() {
		return m_category;
	}

	public boolean includesSystemProperties() {
		return m_includeSysProps;
	}

	public String getProperty(String name, String defaultValue) {
		String val = getProperty(name);
		return val==null ? defaultValue : val;
	}

	public String getProperty(String name) {
		String val = getExternalProperty(name);
		if ( val!=null ) {
			return val;
		}
		val = m_props.getProperty(name, false);
		if ( val!=null ) {
			return val;
		}
		val = getDefaultProperty(name);
		if ( val!=null ) {
			return val;
		}
		return getDeprecatedProperty(name);
	}

	/**
	  * returns a copy of the properties in this config
	*/
	public Properties getProperties(boolean prefix) {
		Properties props = new Properties();
		props.putAll(getPropertiesFromDeprecatedNames());
		props.putAll(getPropertiesFromSystemProperties());
		props.putAll(m_props.getProperties(false));
		props.putAll(getPropertiesFromExternalNames());
		if ( prefix ) {
			props = m_props.addPrefix(props);
		}
		return props;
	}

	public void setProperty(String name, String val) {
		m_props.setProperty(name, val, false);
		setExternalProperty(name, val);
	}

	public void clearProperty(String name) {
		m_props.clearProperty(name, false);
	}

	public void clear() {
		m_props.clear();
	}

	/**
	  * clears properties from this category's namespace from <code>props</code>
	*/
	public void clearProperties(Properties props) {
		m_props.clear(props);
	}

	/**
	  * Add all properties in <code>props</code> to this config
	*/
	public void setProperties(Properties props) {
		m_props.setProperties(props, false);
		updateExternalProperties();
	}

	/**
	  * Import only properties for this category's namespace from a source of properties
	*/	
	public void importProperties(Iterable<Map.Entry<String,String>> propSrc) {
		Properties props = m_props.extractProperties(propSrc);
		props = m_props.stripPrefix(props);
		setProperties(props);
	}
	
	public Properties getPropertiesFromDeprecatedNames() {
		Properties props = new Properties();
		synchronized (m_deprecatedMap) {
			for (String name : m_deprecatedMap.keySet() ) {
				String val = getDeprecatedProperty(name);
				if ( val!=null ) {
					props.setProperty(name, val);
				}
			}
		}
		return props;
	}

	public Properties getPropertiesFromSystemProperties() {
		Properties props = new Properties();
		if ( m_includeSysProps ) {
			props.putAll(m_sysProps.getProperties(false));
		}
		return props;
	}

	public Properties getPropertiesFromExternalNames() {
		Properties props = new Properties();
		synchronized (m_externalMap) {
			for (String name : m_externalMap.keySet() ) {
				String val = getExternalProperty(name);
				if ( val!=null ) {
					props.setProperty(name, val);
				}
			}
		}
		return props;
	}

	/**
	  * Specify that a property is tied to a deprecated property
	*/
	protected void addDeprecatedProperty(String name, String depName){ 
		m_deprecatedMap.put(name, depName);
	}

	/**
	  * Specify that a property is tied to an externally defined property name
	*/
	protected void addExternalProperty(String name, String extName){ 
		m_externalMap.put(name, extName);
	}

	private String getExternalProperty(String name) {
		if ( !m_includeSysProps ) {
			return null;
		}
		String extName = m_externalMap.get(name);
		return extName==null ?
			null :
			System.getProperty(extName);
	}

	private void setExternalProperty(String name, String value) {
		if ( !m_includeSysProps ) {
			return;
		}
		String extName = m_externalMap.get(name);
		if ( extName!=null ) {
			System.setProperty(extName, value);
		}
	}

	private String getDefaultProperty(String name) {
		if ( !m_includeSysProps ) {
			return null;
		}
		return m_sysProps.getProperty(name, false);
	}

	private String getDeprecatedProperty(String name) {
		if ( !m_includeSysProps ) {
			return null;
		}
		String depName = m_deprecatedMap.get(name);
		return depName==null ?
			null :
			System.getProperty(depName);
	}

	private void updateExternalProperties() {
		if ( !m_includeSysProps ) {
			return;
		}
		synchronized (m_externalMap) {
			for (String name : m_externalMap.keySet() ) {
				String val = m_props.getProperty(name,false);
				if ( val!=null ) {
					String extName = m_externalMap.get(name);
					System.setProperty(extName, val);
				}
			}
		}
	}

}
