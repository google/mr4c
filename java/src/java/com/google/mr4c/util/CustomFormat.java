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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.MatchResult;

import org.apache.commons.lang3.text.StrSubstitutor;

/**
  * Parsing and formatting for a template with named variables.
  * Variables are specified as ${name}, e.g. "file${num}.jpg"
*/
public class CustomFormat {

	/**
	  * Default regular expression for variable values when parsing
	  */
	public static final String VALUE_REGEX = ".*";

	private static final String VAR_REGEX = "\\$\\{([^\\}]*)\\}";

	private String m_pattern;
	private String m_regex;
	List<String> m_nameList; // names in the order they appear in the original pattern
	Set<String> m_nameSet; 
	

	public static CustomFormat createInstance(String pattern) {
		Map<String,String> empty = Collections.emptyMap();
		return createInstance(pattern, empty);
	}

	/**
	  * Create a CustomFormat with custom regular expressions for some variables
	*/
	public static CustomFormat createInstance(String pattern, Map<String,String> regexMap) {
		CustomFormat format = new CustomFormat();
		format.m_nameList = extractNames(pattern);
		format.m_nameSet = new HashSet<String>(format.m_nameList);
		format.m_pattern = pattern;
		Map<String,String> varMap = new HashMap<String,String>();
		for ( String name : format.m_nameSet ) {
			String regex = String.format("(%s)", regexMap.containsKey(name) ? regexMap.get(name) : VALUE_REGEX);
			varMap.put(name,regex);
		}
		format.m_regex = StrSubstitutor.replace(pattern,varMap);
		return format;
	}

	private static List<String> extractNames(String pattern) {
		List<String> names = new ArrayList<String>();
		Scanner scanner = new Scanner(pattern);
		while ( scanner.findWithinHorizon(VAR_REGEX, 0) !=null ) {
			MatchResult result = scanner.match();
			String val = result.group(1);
			names.add(val);
		}
		return names;
	}

	private CustomFormat() {}

	public List<String> getNameList() {
		return Collections.unmodifiableList(m_nameList);
	}

	public Set<String> getNameSet() {
		return Collections.unmodifiableSet(m_nameSet);
	}

	public boolean matches(String str) {
		Scanner scanner = new Scanner(str);
		String check = scanner.findWithinHorizon(m_regex, 0);
		return str.equals(check);
	}

	// returns map of names to values
	public Map<String,String> parse(String str) {
		if ( !matches(str) ) {
			throw new IllegalArgumentException(String.format("[%s] doesn't match pattern [%s]", str, m_pattern));
		}
		Scanner scanner = new Scanner(str);
		scanner.findWithinHorizon(m_regex, 0);
		MatchResult result = scanner.match();
		Map<String,String> vals = new HashMap<String,String>();
		if ( result.groupCount()!=m_nameList.size() ) {
			// this shouldn't be able to happen
			throw new IllegalStateException(String.format("[%s] doesn't match pattern [%s]; found %d matches, expected %d", str, m_pattern, result.groupCount(), m_nameList.size()));
		}
		for (int i=1; i<=result.groupCount(); i++) {
			String name = m_nameList.get(i-1);
			String val = result.group(i);
			if ( vals.containsKey(name) ) {
				if ( !vals.get(name).equals(val) ) {
					throw new IllegalArgumentException(String.format("[%s]doesnt match pattern [%s]; variable [%s] has values [%s] and [%s]", str, m_pattern, name, val, vals.get(name)));
				}
			}
			vals.put(name,result.group(i));
		}
		return vals;
	}

	// takes map of codes to values
	public String format(Map<String,String> vals) {
		if ( !vals.keySet().equals(m_nameSet) ) {
			throw new IllegalArgumentException(String.format("Expected variables are [%s]; passed [%s]", m_nameSet, vals.keySet()));
		}
		return StrSubstitutor.replace(m_pattern,vals);
	}

}
