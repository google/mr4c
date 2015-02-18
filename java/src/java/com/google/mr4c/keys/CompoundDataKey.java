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

package com.google.mr4c.keys;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*package */ class CompoundDataKey implements DataKey {

	private Map<DataKeyDimension,DataKeyElement> m_eles = new HashMap<DataKeyDimension,DataKeyElement>();
	private int m_hash;

	/*package*/ CompoundDataKey(DataKeyElement ... eles ) {
		this(Arrays.asList(eles));
	}

	/*package*/ CompoundDataKey(Collection<DataKeyElement> eles ) {
		for ( DataKeyElement ele : eles ) {
			DataKeyDimension dim = ele.getDimension();
			if (m_eles.containsKey(dim) ) {
				throw new IllegalArgumentException(String.format("Found two elements with dimension=[%s]", dim));
			}
			m_eles.put(dim,ele);
		}
		m_hash = m_eles.hashCode();
	}

	public Set<DataKeyDimension> getDimensions() {
		return m_eles.keySet();
	}

	public boolean hasDimension(DataKeyDimension dim) {
		return m_eles.containsKey(dim);
	}

	public Set<DataKeyElement> getElements() {
		return new HashSet<DataKeyElement>(m_eles.values());
	}

	public DataKeyElement getElement(DataKeyDimension dim) {
		if ( !hasDimension(dim) ) {
			throw new IllegalArgumentException(String.format("Key doesn't have dimension=[%s]", dim));
		}
		return m_eles.get(dim);
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( obj==null ) return false;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		CompoundDataKey key = (CompoundDataKey) obj;
		return key.m_eles.equals(m_eles);
	}

	public int hashCode() {
		return m_hash;
	}

	public String toString() {
		return m_eles.values().toString();
	}

	public int compareTo(DataKey key) {
		return DataKeyComparator.INSTANCE.compare(this,key);
	}

}

