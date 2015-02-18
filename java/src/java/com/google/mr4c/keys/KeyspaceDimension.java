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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class KeyspaceDimension {

	private DataKeyDimension m_dim;
	private SortedSet<DataKeyElement> m_elements = Collections.synchronizedSortedSet( new TreeSet<DataKeyElement>() );

	public KeyspaceDimension(DataKeyDimension dim) {
		m_dim = dim;
	}

	public DataKeyDimension getDimension() {
		return m_dim;
	}

	public void addElements(Collection<DataKeyElement> elements) {
		for ( DataKeyElement element : elements ) {
			addElement(element);
		}
	}

	public void addElement(DataKeyElement element) {
		if ( !element.getDimension().equals(m_dim) ) {
			throw new IllegalArgumentException(String.format("Expected dim=[%s]; got dim=[%s]", m_dim, element.getDimension()));
		}
		m_elements.add(element);
	}

	public List<DataKeyElement> getElements() {
		return new ArrayList<DataKeyElement>(m_elements);
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		KeyspaceDimension kd = (KeyspaceDimension) obj;
		return kd.m_elements.equals(m_elements);
	}

	// shouldn't use this as a key, just including because of overriding equals
	public int hashCode() {
		return m_elements.hashCode();
	}

}
			
