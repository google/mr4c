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
import java.util.HashSet;
import java.util.Set;

public class BasicElementFilter implements ElementFilter {

	private DataKeyDimension m_dim;
	private Set<DataKeyElement> m_elements = Collections.synchronizedSet( new HashSet<DataKeyElement>() );

	public BasicElementFilter(DataKeyDimension dim) {
		m_dim = dim;
	}

	public boolean filter(DataKeyElement element) {
		if ( !element.getDimension().equals(m_dim) ) {
			throw new IllegalArgumentException(String.format("Tried to filter element of dimension [%s] with filter for dimension [%s]", element.getDimension(), m_dim));
		}
		return m_elements.contains(element);
	}

	public DataKeyDimension getDimension() {
		return m_dim;
	}

	public void addElement(DataKeyElement element) {
		if ( !element.getDimension().equals(m_dim) ) {
			throw new IllegalArgumentException(String.format("Tried to add element of dimension [%s] to filter for dimension [%s]", element.getDimension(), m_dim));
		}
		m_elements.add(element);
	}

	public void addElements(DataKeyElement... elements ) {
		addElements(Arrays.asList(elements));
	}

	public void addElements(Collection<DataKeyElement> elements) {
		for ( DataKeyElement element : elements ) {
			addElement(element);
		}
	}

}

