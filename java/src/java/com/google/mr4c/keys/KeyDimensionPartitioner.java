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

import com.google.mr4c.util.Partitioner;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KeyDimensionPartitioner {

	private Keyspace m_keyspace;
	private int m_numPartitions;

	private Partitioner<DataKeyDimension> m_partitioner;

	private Set<DataKeyDimension> m_canSplit = new HashSet<DataKeyDimension>();
	private Set<DataKeyDimension> m_dependent = new HashSet<DataKeyDimension>();

	public KeyDimensionPartitioner(Keyspace keyspace, int numPartitions) {
		m_keyspace = keyspace;
		m_numPartitions = numPartitions;
	}

	public synchronized void canSplit(DataKeyDimension dim) {
		m_canSplit.add(dim);
	}

	public synchronized void dependent(DataKeyDimension dim) {
		m_dependent.add(dim);
	}


	public Map<DataKeyDimension,Integer> getComputedPartitions() {
		return m_partitioner.getComputedPartitions();
	}

	public synchronized void partition() {

		m_partitioner = new Partitioner<DataKeyDimension>(1, m_numPartitions);

		for ( DataKeyDimension dim : m_keyspace.getDimensions() ) {
			if ( m_dependent.contains(dim) ) {
				continue; // skip dependent dimensions
			}
			KeyspaceDimension ksd = m_keyspace.getKeyspaceDimension(dim);
			int dimSize = ksd.getElements().size();
			Integer maxNum = m_canSplit.contains(dim) ? null : 1;
			m_partitioner.addDimension( dim, dimSize, 1, maxNum);
		}

		m_partitioner.computePartitions();
	}

}
