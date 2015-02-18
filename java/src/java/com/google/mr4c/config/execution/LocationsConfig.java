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

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

// NOTE - not intended to map directly to JSON
// Encapsulates locations can be a map or a list
public class LocationsConfig {

	private List<URI> list;
	private Map<String,URI> map;

	public LocationsConfig(List<URI> list) {
		if ( list==null ) {
			throw new IllegalArgumentException("list can't be null");
		}
		this.list = list;
	}

	public LocationsConfig(Map<String,URI> map) {
		if ( map==null ) {
			throw new IllegalArgumentException("map can't be null");
		}
		this.map = map;
	}

	public List<URI> getList() {
		return this.list;
	}

	public Map<String,URI> getMap() {
		return this.map;
	}

	public boolean hasList() {
		return this.list!=null;
	}

	public boolean hasMap() {
		return this.map!=null;
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		LocationsConfig config = (LocationsConfig) obj;
		if ( !ObjectUtils.equals(list, config.list) ) return false;
		if ( !ObjectUtils.equals(map, config.map) ) return false;

		return true; 
	}

	public int hashCode() {
		int hash=0;
		if ( hasMap() ) hash += map.hashCode();
		if ( hasList() ) hash += list.hashCode();
		return hash;
	}

}

