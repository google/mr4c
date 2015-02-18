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

package com.google.mr4c.config;

import com.google.mr4c.content.ContentFactories;
import com.google.mr4c.util.CollectionUtils;
import com.google.mr4c.util.CustomFormat;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.text.StrSubstitutor;

import org.slf4j.Logger;

public class ConfigUtils {

	public static void logProperties( String desc, Logger log, Properties props) {
		if ( props.isEmpty() ) {
			log.info("No {} properties", desc);
			return;
		}
		log.info("Begin dump of {} properties", desc);
		log.info(CollectionUtils.toFileContent(props));
		log.info("End dump of {} properties", desc);
	}

	public static void dumpProperties(Properties props, String filePath) throws IOException {
		ContentFactories.writeContent(URI.create(filePath), props);
	}

	public static Properties resolveProperties(Properties props, boolean checkAll) {
		props = CollectionUtils.toTrimmedProperties(props);
		String template = CollectionUtils.toFileContent(props);
		String result = applyProperties(template, props, checkAll);
		return CollectionUtils.fromFileContent(result);
	}

	public static String applyProperties(String template, Properties props, boolean checkAll) {
		Properties trimmed = CollectionUtils.toTrimmedProperties(props);
		String result = StrSubstitutor.replace(template, trimmed);
		if ( checkAll ) {
			Set<String> missing = extractVariables(result);
			if ( !missing.isEmpty() ) {
				throw new IllegalStateException("No values found for parameters [" + missing + "]");
			}
		}
		return result;
	}

	public static Set<String> extractVariables(String content) {
		CustomFormat cf = CustomFormat.createInstance(content);
		return cf.getNameSet();
	}

	public static boolean containsVariables(String content) {
		Set<String> vars = extractVariables(content);
		return !vars.isEmpty();
	}

	public static Set<String> findUnresolvedProperties(Properties props) {
		Set<String> result = new HashSet<String>();
		for ( String name : props.stringPropertyNames() ) {
			String val = props.getProperty(name);
			if ( containsVariables(val) ) {
				result.add(name);
			}
		}
		return result;
	}
}
