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

package com.google.mr4c.metadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class MetadataMap implements MetadataElement {

	private Map<String,MetadataElement> m_map;

	public MetadataMap() {
		m_map = Collections.synchronizedMap( new HashMap<String,MetadataElement>() );
	}

	public MetadataMap(Map<String,MetadataElement> map) {
		m_map = map;
	}

	public Map<String,MetadataElement> getMap() {
		return m_map;
	}

	public MetadataElementType getMetadataElementType() {
		return MetadataElementType.MAP;
	}

	public void accept(MetadataVisitor visitor) {
		visitor.visitMapPreIteration(this);
		for ( MetadataElement element : m_map.values() ) {
			element.accept(visitor);
		}
		visitor.visitMapPostIteration(this);
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !this.getClass().equals(obj.getClass()) ) return false;
		MetadataMap map = (MetadataMap) obj;
		return m_map.equals(map.m_map);
	}

	public int hashCode() {
		// might be slow, but this isn't supposed to be a key
		// just here for completeness, since we did equals
		return m_map.hashCode();
	}

}
