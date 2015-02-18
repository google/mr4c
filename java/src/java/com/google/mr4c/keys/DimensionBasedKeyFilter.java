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

public class DimensionBasedKeyFilter implements DataKeyFilter {

	private boolean m_extra;
	private boolean m_missing;
	private Map<DataKeyDimension,ElementFilter> m_filters = Collections.synchronizedMap( new HashMap<DataKeyDimension,ElementFilter>() );

	/**
	  * @param allowExtraDimensions if true, passes keys with dimensions
	  * this filter doesn't know about.
	  * @param allowMissingDimensions if true, passes keys that don't have
	  * all the dimensions this filter knows about.
	*/
	public DimensionBasedKeyFilter(boolean allowExtraDimensions, boolean allowMissingDimensions) {
		m_extra = allowExtraDimensions;
		m_missing = allowMissingDimensions;
	}

	public boolean filter(DataKey key) {

		// check for extra dimensions
		if ( !m_extra && !m_filters.keySet().containsAll(key.getDimensions()) ) {
			return false;
		}

		// check for missing dimensions
		if ( !m_missing && !key.getDimensions().containsAll(m_filters.keySet()) ) {
			return false;
		}

		for ( DataKeyElement element : key.getElements() ) {
			ElementFilter filter = m_filters.get(element.getDimension());
			if ( filter!=null && !filter.filter(element) ) {
				return false;
			}
		}
		return true;
	}

	public synchronized void addFilter(ElementFilter filter) {
		DataKeyDimension dim = filter.getDimension();
		if ( m_filters.containsKey(dim) ) {
			throw new IllegalArgumentException(String.format("Already have a filter for dim=[%s]", dim));
		}
		m_filters.put(dim,filter);
	}
				
}
