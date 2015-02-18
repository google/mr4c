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

import java.net.URI;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

public class DatasetConfig {

	private String scheme;
	private URI location;
	private ConfigDescriptor locations; // optional
	private URI stageLocation; //optional
	private ConfigDescriptor srcConfig; //optional
	private ConfigDescriptor mapConfig; // optional
	private boolean queryOnly=false;

	public DatasetConfig(
		String scheme,
		URI location
	) {
		this.scheme = scheme;
		this.location = location;
	}

	public String getScheme() {
		return this.scheme;
	}

	public URI getLocation() {
		return this.location;
	}

	public ConfigDescriptor getLocations() {
		return this.locations;
	}

	public void setLocations(ConfigDescriptor locations) {
		this.locations=locations;
	}

	public URI getStageLocation() {
		return this.stageLocation;
	}

	public void setStageLocation(URI stageLocation) {
		this.stageLocation=stageLocation;
	}

	public ConfigDescriptor getSourceConfig() {
		return this.srcConfig;
	}

	public void setSourceConfig(ConfigDescriptor srcConfig) {
		this.srcConfig=srcConfig;
	}

	public ConfigDescriptor getMapConfig() {
		return this.mapConfig;
	}

	public void setMapConfig(ConfigDescriptor mapConfig) {
		this.mapConfig=mapConfig;
	}

	public boolean getQueryOnly() {
		return this.queryOnly;
	}

	public void setQueryOnly(boolean queryOnly) {
		this.queryOnly=queryOnly;
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		DatasetConfig config = (DatasetConfig) obj;
		if ( !scheme.equals(config.scheme) ) return false;
		if ( !location.equals(config.location) ) return false;
		if ( !srcConfig.equals(config.srcConfig) ) return false;
		if ( !ObjectUtils.equals(mapConfig, config.mapConfig) ) return false;
		if ( !ObjectUtils.equals(queryOnly, config.queryOnly) ) return false;
		return true; 
	}

	public int hashCode() {
		return scheme.hashCode() +
			location.hashCode() +
			srcConfig.hashCode();
	}

}

