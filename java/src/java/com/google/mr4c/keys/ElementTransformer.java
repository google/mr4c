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
import java.util.HashMap;
import java.util.Map;

public class ElementTransformer {

	private DataKeyDimension m_dim;
	private DataKeyDimension m_mapTo;
	private DataKeyDimension m_targetDim;
	Map<String,DataKeyElement> m_vals = Collections.synchronizedMap( new HashMap<String,DataKeyElement>() );

	// no dimension change
	public ElementTransformer(DataKeyDimension dim) {
		m_dim = dim;
		m_targetDim = dim;
	}

	public ElementTransformer( DataKeyDimension dim, DataKeyDimension mapTo ) {
		this(dim);
		m_dim = dim;
		m_mapTo = mapTo;
		if ( mapTo!=null ) {
			m_targetDim = mapTo;
		}
	}

	public void addValueTransform(String id, String mapToId) {
		m_vals.put(id, new DataKeyElement(mapToId, m_targetDim) );
	}

	public DataKeyDimension getDimension() {
		return m_dim;
	}

	public boolean transformsDimension() {
		return m_mapTo!=null;
	}

	public boolean transformsValues() {
		return !m_vals.isEmpty();
	}

	public DataKeyElement transformElement(DataKeyElement element) {
		assertDimension(element.getDimension(), m_dim);
		String id = element.getIdentifier();
		DataKeyElement newElement = m_vals.get(id);
		if ( newElement==null ) { // no value change
			if ( m_mapTo==null ) { // no change at all
				newElement = element;
			} else {
				newElement = new DataKeyElement(id, m_mapTo);
			}
		}
		return newElement;
	}

	private void assertDimension(DataKeyDimension dim, DataKeyDimension expected) {
		if ( !dim.equals(expected) ) {
			throw new IllegalStateException(String.format("Got dimension [%s]; expected dimension [%s]", dim, expected));
		}
	}

}

