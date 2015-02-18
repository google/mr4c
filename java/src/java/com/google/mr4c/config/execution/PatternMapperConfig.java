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

package com.google.mr4c.config.execution;

import com.google.mr4c.config.ConfigDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

public class PatternMapperConfig {

	private String pattern;
	private List<String> dimensions = Collections.synchronizedList( new ArrayList<String>() );

	// for gson	
	private PatternMapperConfig() {}

	public PatternMapperConfig(String pattern) {
		this.pattern = pattern;
	}

	public PatternMapperConfig(String pattern, String ... dimensions) {
		this(pattern, Arrays.asList(dimensions));
	}

	public PatternMapperConfig(String pattern, Collection<String> dimensions) {
		this.pattern = pattern;
		this.dimensions.addAll(dimensions);
	}

	public String getPattern() {
		return this.pattern;
	}

	public void addDimension(String dim) {
		this.dimensions.add(dim);
	}

	public List<String> getDimensions() {
		return this.dimensions;
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		PatternMapperConfig config = (PatternMapperConfig) obj;
		if ( !ObjectUtils.equals(pattern, config.pattern) ) return false;
		if ( !ObjectUtils.equals(dimensions, config.dimensions) ) return false;
		return true; 
	}

	public int hashCode() {
		return pattern.hashCode() + dimensions.hashCode();
	}

}
