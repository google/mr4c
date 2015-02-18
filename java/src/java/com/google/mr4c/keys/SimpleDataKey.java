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

import java.util.Collections;
import java.util.Set;

/*package*/ class SimpleDataKey implements DataKey {

	private DataKeyElement m_element;
	private DataKeyDimension m_dim;

	/*package*/ SimpleDataKey(DataKeyElement element) {
		m_element = element;
		m_dim = element.getDimension();
	}

	public Set<DataKeyDimension> getDimensions() {
		return Collections.singleton(m_dim);
	}

	public boolean hasDimension(DataKeyDimension dim) {
		return m_dim.equals(dim);
	}

	public Set<DataKeyElement> getElements() {
		return Collections.singleton(m_element);
	}

	public DataKeyElement getElement(DataKeyDimension dim) {
		if ( !m_dim.equals(dim) ) {
			throw new IllegalArgumentException(String.format("DataKey doesn't have dimension=[%s]", dim));
		}
		return m_element;
	}

	public String toString() {
		return m_element.toString();
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( obj==null ) return false;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		SimpleDataKey key = (SimpleDataKey) obj;
		return m_element.equals(key.m_element);
	}

	public int hashCode() {
		return m_element.hashCode();
	}

	public int compareTo(DataKey key) {
		return DataKeyComparator.INSTANCE.compare(this,key);
	}

}
