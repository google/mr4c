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

package com.google.mr4c.util;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
  * Allows a set of properties to be stored in the JVM system properties segregated into their own namespace.
*/

public class NamespacedProperties {

	private String m_prefix;
	private Properties m_props;
	private boolean m_sysBacked;

	/**
	  * Create new instance backed by system properties (Properties instance obtained by System.getProperties()
	*/
	public NamespacedProperties(String prefix) {
		this(prefix, System.getProperties(), true);
	}

	/**
	  *
	  * Create new instance backed by <code>props</code>  Changes to the <code>props</code> instance will be reflected in this object
	*/
	public NamespacedProperties(String prefix, Properties props) {
		this(prefix, props, false);
	}

	private NamespacedProperties(String prefix, Properties props, boolean sysBacked) {
		if ( !prefix.endsWith(".") ) {
			throw new IllegalArgumentException("Prefix must end with '.'");
		}
		m_prefix = prefix;
		m_props = props;
		m_sysBacked = sysBacked;
	}

	public boolean isBackedBySystemProperties() {
		return m_sysBacked;
	}
	
	/**
	  * returns true if the property is in this namespace
	*/
	public boolean isNamespacedProperty(String name) {
		return name.startsWith(m_prefix);
	}

	public String addPrefix(String name) {
		return m_prefix+name;
	}

	public String stripPrefix(String name) {
		validateNamespaced(name);
		return name.replaceFirst(m_prefix,"");
	}

	public String getProperty(String name, boolean prefix) {
		String prefixedName = toPrefixedName(name, prefix);
		validateNamespaced(prefixedName);
		return m_props.getProperty(prefixedName);
	}

	public String getProperty(String name, String defaultValue, boolean prefix) {
		String prefixedName = toPrefixedName(name, prefix);
		validateNamespaced(prefixedName);
		return m_props.getProperty(prefixedName, defaultValue);
	}

	/**
	  * Get all properties, optional to have the prefix in the property names
	*/
	public synchronized Properties getProperties(boolean prefix) {
		Properties props = extractProperties(toPropertySource(m_props));
		return prefix ? props : stripPrefix(props);
	}

	/**
	  * Set all properties, flag indicates if prefix is in the property names
	*/
	public synchronized void setProperties(Properties props, boolean prefix) {
		for ( String name : props.stringPropertyNames() ) {
			String val = props.getProperty(name);
			setProperty(name, val, prefix);
		}
	}

	public synchronized void setProperty(String name, String value, boolean prefix) {
		String prefixedName = toPrefixedName(name, prefix);
		validateNamespaced(prefixedName);
		m_props.setProperty(prefixedName,value);
	}

	public synchronized void clearProperty(String name, boolean prefix) {
		String prefixedName = toPrefixedName(name, prefix);
		validateNamespaced(prefixedName);
		m_props.remove(prefixedName);
	}

	/**
	  * Remove all properties in this namespace
	*/
	public synchronized void clear() {
		Properties props = getProperties(true);
		for ( String name : props.stringPropertyNames() ) {
			m_props.remove(name);
		}
	}

	/**
	  * Remove all properties in this namespace
	*/
	public void clear(Properties props) {
		Properties nsProps = getProperties(true);
		for ( String name : nsProps.stringPropertyNames() ) {
			props.remove(name);
		}
	}

	/**
	  * Extract all properties in this namespace that can be found in the provided property source
	*/
	public Properties extractProperties(Iterable<Map.Entry<String,String>> propSrc) {
		Properties result = new Properties();
		for ( Map.Entry<String,String> entry : propSrc ) {
			String name = entry.getKey();
			if ( isNamespacedProperty(name) ) {
				String val = entry.getValue();
				result.setProperty(name, val);
			}
		}
		return result;
	}

	/**
	  * Set all properties in this namespace that can be found in the provided property source
	*/
	public void setProperties(Iterable<Map.Entry<String,String>> propSrc) {
		setProperties(extractProperties(propSrc), true);
	}

	/**
	  * Strips the prefix for this namespace from all property names
	*/
	public Properties stripPrefix(Properties props) {
		Properties result = new Properties();
		for ( String name : props.stringPropertyNames() ) {
			validateNamespaced(name);
			String val = props.getProperty(name);
			name = stripPrefix(name);
			result.setProperty(name,val);
		}
		return result;
	}

	/**
	  * Adds the prefix for this namespace to all property names
	*/
	public Properties addPrefix(Properties props) {
		Properties result = new Properties();
		for ( String name : props.stringPropertyNames() ) {
			String val = props.getProperty(name);
			name = addPrefix(name);
			result.setProperty(name,val);
		}
		return result;
	}

	private static Iterable<Map.Entry<String,String>> toPropertySource(Properties props) {
		return CollectionUtils.toMap(props).entrySet();
	}

	private void validateNamespaced(String name) {
		if ( !isNamespacedProperty(name) ) {
			throw new IllegalArgumentException(String.format("[%s] is not a property in namespace [%s]", name, m_prefix));
		}
	}

	private String toPrefixedName(String name, boolean prefix) {
		return prefix ? name : addPrefix(name);
	}

}
