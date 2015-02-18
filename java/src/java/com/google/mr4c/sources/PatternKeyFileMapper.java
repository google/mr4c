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

package com.google.mr4c.sources;

import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.util.CustomFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PatternKeyFileMapper implements DataKeyFileMapper {

	private String m_pattern;
	private Set<DataKeyDimension> m_dims;
	private Map<String,DataKeyDimension> m_dimMap;
	private boolean m_extra; // pattern has extra variables
	private CustomFormat m_format;
	// assume that dimension name is the subst. var name too

	public PatternKeyFileMapper( String pattern, DataKeyDimension... dims) {
		this(pattern, Arrays.asList(dims));
	}

	public PatternKeyFileMapper( String pattern, Collection<DataKeyDimension> dims) {
		m_pattern = pattern;
		m_dims = new HashSet<DataKeyDimension>(dims);
		m_format = CustomFormat.createInstance(pattern);
		buildDimensionMap();
		validatePattern();
	}

	private void buildDimensionMap() {
		m_dimMap = new HashMap<String,DataKeyDimension>();
		for ( DataKeyDimension dim : m_dims ) {
			m_dimMap.put(dim.getName(), dim);
		}
	}

	private void validatePattern() {
		if ( !m_format.getNameSet().containsAll(m_dimMap.keySet()) ) {
			throw new IllegalArgumentException(String.format("Pattern [%s] doesn't match provided dimensions [%s]", m_pattern, m_dims));
		}
		m_extra = !m_format.getNameSet().equals(m_dimMap.keySet());
	}

	public String getFileName(DataKey key) {
		if ( m_extra ) {
			throw new IllegalStateException(String.format("Can't use pattern with extra variables [%s] to format a key; declared dimensions are [%s]", m_pattern, m_dims));
		}
		if ( !key.getDimensions().equals(m_dims) ) {
			throw new IllegalArgumentException(String.format("Wrong key dimensions; expected [%s]; passed [%s]", m_dims, key.getDimensions()));
		}
		Map<String,String> vals = new HashMap<String,String>();
		for ( DataKeyDimension dim : key.getDimensions() ) {
			DataKeyElement ele = key.getElement(dim);
			vals.put(dim.getName(), ele.getIdentifier());
		}
		return m_format.format(vals);
	}

	public DataKey getKey(String name) {
		Map<String,String> vals = m_format.parse(name);
		Set<DataKeyElement> eles = new HashSet<DataKeyElement>();
		for ( String dimName : m_dimMap.keySet() ) {
			DataKeyDimension dim = m_dimMap.get(dimName);
			DataKeyElement ele = new DataKeyElement(vals.get(dimName),dim);
			eles.add(ele);
		}
		DataKey key = DataKeyFactory.newKey(eles);
		if ( !key.getDimensions().equals(m_dims) ) {
			throw new IllegalStateException(String.format("Wrong key dimensions; expected [%s]; parsed out [%s]", m_dims, key.getDimensions()));
		}
		return key;
	}

	public boolean canMapName(String name) {
		return m_format.matches(name);
	}

	public boolean canMapKey(DataKey key) {
		return key.getDimensions().equals(m_dims);
	}

}
