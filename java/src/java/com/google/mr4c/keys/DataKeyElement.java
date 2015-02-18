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


public class DataKeyElement implements Comparable<DataKeyElement> {

	private String m_id;
	private DataKeyDimension m_dim;
	private int m_hash;

	public DataKeyElement(String id, DataKeyDimension dim) {
		m_id = id;
		m_dim = dim;
		computeHash();
	}

	public String getIdentifier() {
		return m_id;
	}
		
	public DataKeyDimension getDimension() {
		return m_dim;
	}
		
	public String toString() {
		return String.format("id=%s; dim=%s", m_id, m_dim);
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		DataKeyElement ele = (DataKeyElement) obj;
		if ( !m_dim.equals(ele.m_dim) ) return false;
		if ( !m_id.equals(ele.m_id) ) return false;
		return true;
	}

	public int hashCode() {
		return m_hash;
	}

	private void computeHash() {
		m_hash = m_id.hashCode() + m_dim.hashCode();
	}

	public int compareTo(DataKeyElement ele) {
		if ( !m_dim.equals(ele.getDimension()) ) {
			throw new IllegalArgumentException(String.format("Tried to compare DataKeyElements in different dimensions: [%s] and [%s]", m_dim, ele.getDimension() ));
		}
		return m_id.compareTo(ele.getIdentifier());
	}

}
