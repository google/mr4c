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

import com.google.mr4c.keys.DataKey;

public class MetadataKey implements MetadataElement {

	private DataKey m_key;

	public MetadataKey(DataKey key) {
		m_key = key;
	}

	public DataKey getKey() {
		return m_key;
	}

	public MetadataElementType getMetadataElementType() {
		return MetadataElementType.KEY;
	}

	public void accept(MetadataVisitor visitor) {
		visitor.visitKey(this);
	}

	public String toString() {
		return m_key.toString();
	}
	
	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !this.getClass().equals(obj.getClass()) ) return false;
		MetadataKey key = (MetadataKey) obj;
		return m_key.equals(key.m_key);
	}

	public int hashCode() {
		return m_key.hashCode();
	}

}
