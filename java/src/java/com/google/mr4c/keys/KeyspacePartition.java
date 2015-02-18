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

public class KeyspacePartition {

	private Map<DataKeyDimension,DimensionPartition> m_indepParts = Collections.synchronizedMap( new HashMap<DataKeyDimension,DimensionPartition>() );
	private Map<DataKeyDimension,DimensionPartition> m_depParts = Collections.synchronizedMap( new HashMap<DataKeyDimension,DimensionPartition>() );

	public void addIndependentDimension(DimensionPartition dimPart) {
		m_indepParts.put(dimPart.getDimension(), dimPart);
	}

	public DimensionPartition getPartition(DataKeyDimension dim) {
		DimensionPartition part = m_indepParts.get(dim);
		if ( part==null ) {
			part = m_depParts.get(dim);
		}
		if ( part==null ) {
			throw new IllegalArgumentException(String.format("No partition provided for dimension [%s]", dim));
		}
		return part;
	}
		
	public void addDependentDimension(DimensionPartition dimPart) {
		m_depParts.put(dimPart.getDimension(), dimPart);
	}

	public DataKeyFilter getIndependentFilter() {
		return getFilter(false, false);
	}

	public DataKeyFilter getCompleteFilter() {
		return getFilter(true, false);
	}

	public DataKeyFilter getExtraDimensionsFilter() {
		return getFilter(true, true);
	}

	private DataKeyFilter getFilter(boolean complete, boolean extras) {
		DimensionBasedKeyFilter keyFilter = new DimensionBasedKeyFilter(extras, true);
		synchronized (m_indepParts) {
			for ( DimensionPartition dimPart : m_indepParts.values() ) {
				keyFilter.addFilter(dimPart.getFilter());
			}
		}
		if ( complete ) {
			synchronized (m_depParts) {
				for ( DimensionPartition dimPart : m_depParts.values() ) {
					keyFilter.addFilter(dimPart.getFilter());
				}
			}
		}
		return keyFilter;
	}

}

