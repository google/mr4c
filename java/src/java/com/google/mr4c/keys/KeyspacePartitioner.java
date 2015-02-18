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

import com.google.mr4c.util.CollectionUtils;
import com.google.mr4c.util.CombinatoricUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyspacePartitioner {

	private static class DimensionInfo {
		private DataKeyDimension m_dim;
		private int m_overlapBefore=0;
		private int m_overlapAfter=0;
		private int m_chunkSize=1;
		private int m_partCount;
		private List<DimensionPartition> m_partitions = new ArrayList<DimensionPartition>();
	}

	private Keyspace m_keyspace;

	private Map<DataKeyDimension,DimensionInfo> m_dimInfo = new HashMap<DataKeyDimension,DimensionInfo>();

	private List<KeyspacePartition> m_keyPartitions = new ArrayList<KeyspacePartition>();


	public KeyspacePartitioner(Keyspace keyspace, Map<DataKeyDimension,Integer> dimPartCounts) {
		m_keyspace = keyspace;
		for ( DataKeyDimension dim : m_keyspace.getDimensions() ) {
			// Only do the ones we have a count for (skips dependent dimensions)
			if ( !dimPartCounts.containsKey(dim) ) {
				continue;
			}
			DimensionInfo dimInfo  = new DimensionInfo();
			dimInfo.m_dim = dim;
			dimInfo.m_partCount = dimPartCounts.get(dim);
			m_dimInfo.put(dim, dimInfo);
		}

	}

	public synchronized void addOverlaps(DataKeyDimension dim, int before, int after) {
		DimensionInfo dimInfo = m_dimInfo.get(dim);
		if ( dimInfo==null ) {
			throw new IllegalArgumentException(String.format("Dimension [%s] is not in the keyspace", dim));
		}
		dimInfo.m_overlapBefore = before;
		dimInfo.m_overlapAfter = after;
	}

	public synchronized void specifyChunkSize(DataKeyDimension dim, int chunkSize) {
		DimensionInfo dimInfo = m_dimInfo.get(dim);
		if ( dimInfo==null ) {
			throw new IllegalArgumentException(String.format("Dimension [%s] is not in the keyspace", dim));
		}
		dimInfo.m_chunkSize = chunkSize;
	}
		

	public synchronized List<KeyspacePartition> getKeyPartitions() {
		return m_keyPartitions;
	}

	public synchronized void partition() {
		generateDimensionPartitions();
		generateKeyPartitions();
	}

	private void generateDimensionPartitions() {
		for ( DataKeyDimension dim : m_dimInfo.keySet() ) {
			partitionDimension(dim);
		}
	}
		
	private void partitionDimension(DataKeyDimension dim) {
		KeyspaceDimension ksd = m_keyspace.getKeyspaceDimension(dim);
		DimensionInfo dimInfo = m_dimInfo.get(dim);
		List<DataKeyElement> elements = ksd.getElements();

		// Want to do the partition on chunks of the desired split size mutliple
		List<List<DataKeyElement>> chunks = CollectionUtils.partitionBySize(elements, dimInfo.m_chunkSize);

		List<List<List<DataKeyElement>>> chunkedPartitions = CollectionUtils.partition(chunks, dimInfo.m_partCount);

		// Now return it to partition by individual elements
		List<List<DataKeyElement>> partitions = new ArrayList<List<DataKeyElement>>();
		for (List<List<DataKeyElement>> chunkedPartition : chunkedPartitions) {
			partitions.add(CollectionUtils.concatenate(chunkedPartition));
		}

		partitions = CollectionUtils.overlap(partitions, dimInfo.m_overlapBefore, dimInfo.m_overlapAfter);
		for ( List<DataKeyElement> partition : partitions ) { 
			DimensionPartition dimPart = new DimensionPartition(dim);
			dimPart.addElements(partition);
			dimInfo.m_partitions.add(dimPart);
		}
	}


	private void generateKeyPartitions() {
		List<Collection<DimensionPartition>> dimParts = new ArrayList<Collection<DimensionPartition>>();
		for ( DataKeyDimension dim : m_dimInfo.keySet() )  {
			dimParts.add(m_dimInfo.get(dim).m_partitions);
		}
		List<List<DimensionPartition>> allCombos = CombinatoricUtils.everyCombination(dimParts);
		for ( List<DimensionPartition> combo : allCombos ) {
			KeyspacePartition partition = new KeyspacePartition();
			for ( DimensionPartition dimPart : combo ) {
				partition.addIndependentDimension(dimPart);
			}
			m_keyPartitions.add(partition);
		}
		// make sure we always generate at least one partition
		if ( m_keyPartitions.isEmpty() ) {
			m_keyPartitions.add(new KeyspacePartition());
		}
	}

}
