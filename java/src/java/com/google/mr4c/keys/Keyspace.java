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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Keyspace {

	private Map<DataKeyDimension,KeyspaceDimension> m_dims = Collections.synchronizedMap( new HashMap<DataKeyDimension,KeyspaceDimension>() );

	public Set<DataKeyDimension> getDimensions() {
		return m_dims.keySet();
	}

	public boolean hasDimension(DataKeyDimension dim) {
		return m_dims.containsKey(dim);
	}

	public KeyspaceDimension getKeyspaceDimension(DataKeyDimension dim) {
		if ( !hasDimension(dim) ) {
			throw new IllegalArgumentException(String.format("Dimension [%s] not in keyspace", dim));
		}
		return m_dims.get(dim);
	}

	public synchronized void addKeyspaceDimension(KeyspaceDimension kd) {
		DataKeyDimension dim = kd.getDimension();
		if ( hasDimension(dim) ) {
			throw new IllegalArgumentException(String.format("Dimension [%s] already in keyspace", dim));
		}
		m_dims.put(dim, kd);
	}

	public void addKeys(Collection<DataKey> keys) {
		for ( DataKey key : keys ) {
			addKey(key);
		}
	}

	public synchronized void addKey(DataKey key) {
		for ( DataKeyElement element : key.getElements() ) {
			DataKeyDimension dim = element.getDimension();
			KeyspaceDimension kd = getKeyspaceDimensionInternal(dim);
			kd.addElement(element);
		}
	}

	private KeyspaceDimension getKeyspaceDimensionInternal(DataKeyDimension dim) {
		KeyspaceDimension kd = m_dims.get(dim);
		if ( kd==null ) {
			kd = new KeyspaceDimension(dim);
			m_dims.put(dim, kd);
		}
		return kd;
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		Keyspace keyspace = (Keyspace) obj;
		return keyspace.m_dims.equals(m_dims);
	}

	// shouldn't use this as a key, just including because of overriding equals
	public int hashCode() {
		return m_dims.hashCode();
	}

}
