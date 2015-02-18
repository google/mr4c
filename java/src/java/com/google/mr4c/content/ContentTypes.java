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

package com.google.mr4c.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FilenameUtils;

public abstract class ContentTypes {

	public static final String DEFAULT_TYPE = "application/octet-stream";
	public static final String DEFAULT_SUFFIX = "bin";

	private static Map<String,String> s_typeToCanonicalSuffix = new HashMap<String,String>();
	private static Map<String,List<String>> s_typeToSuffixes = new HashMap<String,List<String>>();
	private static Map<String,String> s_suffixToType = new HashMap<String,String>();

	private static void add(String type, String suffix, boolean canonical) {
		s_suffixToType.put(suffix,type);
		if ( canonical ) {
			s_typeToCanonicalSuffix.put(type,suffix);
		}
		List<String> suffixes = s_typeToSuffixes.get(type);
		if ( suffixes==null ) {
			suffixes = new ArrayList<String>();
			s_typeToSuffixes.put(type, suffixes);
		}
		suffixes.add(suffix);
	}

	static {
		add("image/jp2", "jp2", true);
		add("image/jp2", "jpc", false);
		add("image/jp2", "j2k", false);
		add("image/jpg", "jpg", true);
		add("image/jpg", "jpeg", false);
		add("image/tiff", "tif", true);
		add("image/png", "png", true);
		add("application/json", "json", true);
		add(DEFAULT_TYPE, DEFAULT_SUFFIX, true);
	}

	public static String getContentTypeForName(String name) {
		return getContentTypeForSuffix(extractSuffix(name));
	}

	public static String getContentTypeForSuffix(String suffix) {
		String type = s_suffixToType.get(suffix);
		if ( type==null ) {
			type = DEFAULT_TYPE;
		}
		return type;
	}
	
	public static String getSuffix(String contentType) {
		String suffix = s_typeToCanonicalSuffix.get(contentType);
		if ( suffix==null ) {
			suffix = DEFAULT_SUFFIX;
		}
		return suffix;
	}

	public static Collection<String> getSuffixes(String contentType) {
		Collection<String> suffixes = s_typeToSuffixes.get(contentType);
		if ( suffixes==null ) {
			throw new IllegalArgumentException(String.format("No suffix for content type=[%s]", contentType));
		}
		return suffixes;
	}

	public static String appendSuffix(String name, String contentType) {
		return name + "." + getSuffix(contentType);
	}

	public static Collection<String> appendSuffixes(String name, String contentType) {
		Collection<String> result = new ArrayList<String>();
		for ( String suffix : getSuffixes(contentType) ) {
			result.add(name + "." + suffix);
		}
		return result;
	}

	private static String extractSuffix(String name) {
		return FilenameUtils.getExtension(name);
	}

	public static Collection<String> filterByContentType(Collection<String> fileNames, final String contentType, final boolean canonicalOnly) {
		return CollectionUtils.predicatedCollection(fileNames, new Predicate() {
			public boolean evaluate(Object obj) {
				String name = (String)obj;
				if ( canonicalOnly ) {
					String suffix = ContentTypes.getSuffix(contentType);
					return FilenameUtils.isExtension(name, suffix);
				} else {
					Collection<String> suffixes = ContentTypes.getSuffixes(contentType);
					return FilenameUtils.isExtension(name, suffixes);
				}
			}
		} );
	}


	
}
