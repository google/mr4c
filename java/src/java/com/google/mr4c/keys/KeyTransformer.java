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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

public class KeyTransformer {

	Map<DataKeyDimension,ElementTransformer> m_dims = new HashMap<DataKeyDimension,ElementTransformer>();

	public synchronized void addDimension(ElementTransformer eleTrans) {
		DataKeyDimension dim = eleTrans.getDimension();
		if ( m_dims.containsKey(dim) ) {
			throw new IllegalArgumentException(String.format("Already have a transformer for dimension [%s]", dim));
		}
		m_dims.put(dim, eleTrans);
	}

	public synchronized DataKey transformKey(DataKey key) {
		if ( !CollectionUtils.containsAny(key.getDimensions(), m_dims.keySet()) ) {
			return key; // no dim in this key has a transform
		}
		Set<DataKeyElement> newElements = new HashSet<DataKeyElement>();
		for ( DataKeyDimension dim : key.getDimensions() ) {
			DataKeyElement element = key.getElement(dim);
			ElementTransformer trans = m_dims.get(dim);
			DataKeyElement newElement = trans==null ?
				element :
				trans.transformElement(element);
			newElements.add(newElement);
		}
		return DataKeyFactory.newKey(newElements);
	}
				
}
