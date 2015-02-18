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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class KeyspacePartitionerTest {

	private Set<Set<DataKey>> m_expected = new HashSet<Set<DataKey>>();
	private Set<DataKey> m_allKeys = new HashSet<DataKey>();
	private Keyspace m_keyspace = new Keyspace();
	private KeyspacePartitioner m_partitioner;

	private DataKeyDimension m_frame;
	private DataKeyDimension m_sensor;
	private DataKeyDimension m_type;

	private DataKeyElement m_f1;
	private DataKeyElement m_f2;
	private DataKeyElement m_f3;
	private DataKeyElement m_f4;
	private DataKeyElement m_f5;

	private DataKeyElement m_s1;
	private DataKeyElement m_s2;
	private DataKeyElement m_s3;

	
	private DataKeyElement m_pan;
	private DataKeyElement m_ms;

	private DataKey m_rootKey;

	private DataKey m_f1_key;
	private DataKey m_f1_s1_pan;
	private DataKey m_f1_s1_ms;
	private DataKey m_f1_s2_pan;
	private DataKey m_f1_s2_ms;
	private DataKey m_f1_s3_pan;
	private DataKey m_f1_s3_ms;

	private DataKey m_f2_key;
	private DataKey m_f2_s1_pan;
	private DataKey m_f2_s1_ms;
	private DataKey m_f2_s2_pan;
	private DataKey m_f2_s2_ms;
	private DataKey m_f2_s3_pan;
	private DataKey m_f2_s3_ms;

	private DataKey m_f3_key;
	private DataKey m_f3_s1_pan;
	private DataKey m_f3_s1_ms;
	private DataKey m_f3_s2_pan;
	private DataKey m_f3_s2_ms;
	private DataKey m_f3_s3_pan;
	private DataKey m_f3_s3_ms;

	private DataKey m_f4_key;
	private DataKey m_f4_s1_pan;
	private DataKey m_f4_s1_ms;
	private DataKey m_f4_s2_pan;
	private DataKey m_f4_s2_ms;
	private DataKey m_f4_s3_pan;
	private DataKey m_f4_s3_ms;

	private DataKey m_f5_key;
	private DataKey m_f5_s1_pan;
	private DataKey m_f5_s1_ms;
	private DataKey m_f5_s2_pan;
	private DataKey m_f5_s2_ms;
	private DataKey m_f5_s3_pan;
	private DataKey m_f5_s3_ms;


		

	@Before public void setUp() {
		buildDimensions();
		buildElements();
		buildKeys();
	}

	@Test public void testPartitioningNormal() {
		buildPartitionsNormal();
		Map<DataKeyDimension,Integer> counts = new HashMap<DataKeyDimension,Integer>();
		counts.put(m_frame,3);
		counts.put(m_sensor,2);
		counts.put(m_type,1);
		m_partitioner = new KeyspacePartitioner(m_keyspace, counts);
		testPartitioning();
	}

	@Test public void testPartitioningWithChunkSize() {
		buildPartitionsWithChunkSize();
		Map<DataKeyDimension,Integer> counts = new HashMap<DataKeyDimension,Integer>();
		counts.put(m_frame,3);
		counts.put(m_sensor,2);
		counts.put(m_type,1);
		m_partitioner = new KeyspacePartitioner(m_keyspace, counts);
		m_partitioner.specifyChunkSize(m_frame, 3);
		testPartitioning();
	}

	@Test public void testPartitioningOverlap() {
		buildPartitionsOverlap();
		Map<DataKeyDimension,Integer> counts = new HashMap<DataKeyDimension,Integer>();
		counts.put(m_frame,3);
		counts.put(m_sensor,2);
		counts.put(m_type,1);
		m_partitioner = new KeyspacePartitioner(m_keyspace, counts);
		m_partitioner.addOverlaps(m_frame, 1, 0);
		testPartitioning();
	}

	@Test public void testPartitioningRootOnly() {
		addPartition(m_rootKey);
		Map<DataKeyDimension,Integer> counts = new HashMap<DataKeyDimension,Integer>();
		m_partitioner = new KeyspacePartitioner(m_keyspace, counts);
		testPartitioning();
	}

	private void testPartitioning() {
		m_partitioner.partition();
		List<KeyspacePartition> partitions = m_partitioner.getKeyPartitions();	
		Set<Set<DataKey>> results = new HashSet<Set<DataKey>>();
		for ( KeyspacePartition partition : partitions ) {
			DataKeyFilter filter = partition.getIndependentFilter();
			results.add(DataKeyUtils.filter(filter,m_allKeys));
		}
		assertEquals(m_expected, results);
	}

	private void buildDimensions() {
		m_frame = new DataKeyDimension("frame");
		m_sensor = new DataKeyDimension("sensor");
		m_type = new DataKeyDimension("type");
	}

	private void buildElements() {

		m_f1 = new DataKeyElement("f1", m_frame);
		m_f2 = new DataKeyElement("f2", m_frame);
		m_f3 = new DataKeyElement("f3", m_frame);
		m_f4 = new DataKeyElement("f4", m_frame);
		m_f5 = new DataKeyElement("f5", m_frame);

		m_s1 = new DataKeyElement("s1", m_sensor);
		m_s2 = new DataKeyElement("s2", m_sensor);
		m_s3 = new DataKeyElement("s3", m_sensor);

		m_pan = new DataKeyElement("pan", m_type);
		m_ms = new DataKeyElement("ms", m_type);

	}

	private void buildKeys() {

		m_rootKey = DataKeyFactory.newKey();

		m_f1_key = DataKeyFactory.newKey(m_f1);
		m_f1_s1_pan = DataKeyFactory.newKey(m_f1, m_s1, m_pan);
		m_f1_s1_ms = DataKeyFactory.newKey(m_f1, m_s1, m_ms);
		m_f1_s2_pan = DataKeyFactory.newKey(m_f1, m_s2, m_pan);
		m_f1_s2_ms = DataKeyFactory.newKey(m_f1, m_s2, m_ms);
		m_f1_s3_pan = DataKeyFactory.newKey(m_f1, m_s3, m_pan);
		m_f1_s3_ms = DataKeyFactory.newKey(m_f1, m_s3, m_ms);

		m_f2_key = DataKeyFactory.newKey(m_f2);
		m_f2_s1_pan = DataKeyFactory.newKey(m_f2, m_s1, m_pan);
		m_f2_s1_ms = DataKeyFactory.newKey(m_f2, m_s1, m_ms);
		m_f2_s2_pan = DataKeyFactory.newKey(m_f2, m_s2, m_pan);
		m_f2_s2_ms = DataKeyFactory.newKey(m_f2, m_s2, m_ms);
		m_f2_s3_pan = DataKeyFactory.newKey(m_f2, m_s3, m_pan);
		m_f2_s3_ms = DataKeyFactory.newKey(m_f2, m_s3, m_ms);

		m_f3_key = DataKeyFactory.newKey(m_f3);
		m_f3_s1_pan = DataKeyFactory.newKey(m_f3, m_s1, m_pan);
		m_f3_s1_ms = DataKeyFactory.newKey(m_f3, m_s1, m_ms);
		m_f3_s2_pan = DataKeyFactory.newKey(m_f3, m_s2, m_pan);
		m_f3_s2_ms = DataKeyFactory.newKey(m_f3, m_s2, m_ms);
		m_f3_s3_pan = DataKeyFactory.newKey(m_f3, m_s3, m_pan);
		m_f3_s3_ms = DataKeyFactory.newKey(m_f3, m_s3, m_ms);

		m_f4_key = DataKeyFactory.newKey(m_f4);
		m_f4_s1_pan = DataKeyFactory.newKey(m_f4, m_s1, m_pan);
		m_f4_s1_ms = DataKeyFactory.newKey(m_f4, m_s1, m_ms);
		m_f4_s2_pan = DataKeyFactory.newKey(m_f4, m_s2, m_pan);
		m_f4_s2_ms = DataKeyFactory.newKey(m_f4, m_s2, m_ms);
		m_f4_s3_pan = DataKeyFactory.newKey(m_f4, m_s3, m_pan);
		m_f4_s3_ms = DataKeyFactory.newKey(m_f4, m_s3, m_ms);

		m_f5_key = DataKeyFactory.newKey(m_f5);
		m_f5_s1_pan = DataKeyFactory.newKey(m_f5, m_s1, m_pan);
		m_f5_s1_ms = DataKeyFactory.newKey(m_f5, m_s1, m_ms);
		m_f5_s2_pan = DataKeyFactory.newKey(m_f5, m_s2, m_pan);
		m_f5_s2_ms = DataKeyFactory.newKey(m_f5, m_s2, m_ms);
		m_f5_s3_pan = DataKeyFactory.newKey(m_f5, m_s3, m_pan);
		m_f5_s3_ms = DataKeyFactory.newKey(m_f5, m_s3, m_ms);

	}

	private void addPartition(DataKey ... keys) {
		addPartition(Arrays.asList(keys));
	}

	private void addPartition(Collection<DataKey> keys) {
		Set<DataKey> part = new HashSet<DataKey>(keys);
		m_expected.add(part);
		m_keyspace.addKeys(keys);
		m_allKeys.addAll(keys);
	}

	private void buildPartitionsNormal() {	

		addPartition(
			m_rootKey,
			m_f1_key,
			m_f1_s1_pan,
			m_f1_s1_ms,
			m_f1_s2_pan,
			m_f1_s2_ms,
			m_f2_key,
			m_f2_s1_pan,
			m_f2_s1_ms,
			m_f2_s2_pan,
			m_f2_s2_ms
		);
		
		addPartition(
			m_rootKey,
			m_f3_key,
			m_f3_s1_pan,
			m_f3_s1_ms,
			m_f3_s2_pan,
			m_f3_s2_ms,
			m_f4_key,
			m_f4_s1_pan,
			m_f4_s1_ms,
			m_f4_s2_pan,
			m_f4_s2_ms
		);
		
		
		
		addPartition(
			m_rootKey,
			m_f1_key,
			m_f1_s3_pan,
			m_f1_s3_ms,
			m_f2_key,
			m_f2_s3_pan,
			m_f2_s3_ms
		);
		
		addPartition(
			m_rootKey,
			m_f3_key,
			m_f3_s3_pan,
			m_f3_s3_ms,
			m_f4_key,
			m_f4_s3_pan,
			m_f4_s3_ms
		);
		
		addPartition(
			m_rootKey,
			m_f5_key,
			m_f5_s1_pan,
			m_f5_s1_ms,
			m_f5_s2_pan,
			m_f5_s2_ms
		);
		
		addPartition(
			m_rootKey,
			m_f5_key,
			m_f5_s3_pan,
			m_f5_s3_ms
		);

	}

	private void buildPartitionsWithChunkSize() {	

		addPartition(
			m_rootKey,
			m_f1_key,
			m_f1_s1_pan,
			m_f1_s1_ms,
			m_f1_s2_pan,
			m_f1_s2_ms,
			m_f2_key,
			m_f2_s1_pan,
			m_f2_s1_ms,
			m_f2_s2_pan,
			m_f2_s2_ms,
			m_f3_key,
			m_f3_s1_pan,
			m_f3_s1_ms,
			m_f3_s2_pan,
			m_f3_s2_ms
		);

		addPartition(
			m_rootKey,
			m_f1_key,
			m_f1_s3_pan,
			m_f1_s3_ms,
			m_f2_key,
			m_f2_s3_pan,
			m_f2_s3_ms,
			m_f3_key,
			m_f3_s3_pan,
			m_f3_s3_ms
		);

		addPartition(
			m_rootKey,
			m_f4_key,
			m_f4_s1_pan,
			m_f4_s1_ms,
			m_f4_s2_pan,
			m_f4_s2_ms,
			m_f5_key,
			m_f5_s1_pan,
			m_f5_s1_ms,
			m_f5_s2_pan,
			m_f5_s2_ms
		);

		addPartition(
			m_rootKey,
			m_f4_key,
			m_f4_s3_pan,
			m_f4_s3_ms,
			m_f5_key,
			m_f5_s3_pan,
			m_f5_s3_ms
		);

	}

	private void buildPartitionsOverlap() {	

		addPartition(
			m_rootKey,
			m_f1_key,
			m_f1_s1_pan,
			m_f1_s1_ms,
			m_f1_s2_pan,
			m_f1_s2_ms,
			m_f2_key,
			m_f2_s1_pan,
			m_f2_s1_ms,
			m_f2_s2_pan,
			m_f2_s2_ms
		);
		
		addPartition(
			m_rootKey,
			m_f2_key,
			m_f2_s1_pan,
			m_f2_s1_ms,
			m_f2_s2_pan,
			m_f2_s2_ms,
			m_f3_key,
			m_f3_s1_pan,
			m_f3_s1_ms,
			m_f3_s2_pan,
			m_f3_s2_ms,
			m_f4_key,
			m_f4_s1_pan,
			m_f4_s1_ms,
			m_f4_s2_pan,
			m_f4_s2_ms
		);
		
		
		
		addPartition(
			m_rootKey,
			m_f1_key,
			m_f1_s3_pan,
			m_f1_s3_ms,
			m_f2_key,
			m_f2_s3_pan,
			m_f2_s3_ms
		);
		
		addPartition(
			m_rootKey,
			m_f2_key,
			m_f2_s3_pan,
			m_f2_s3_ms,
			m_f3_key,
			m_f3_s3_pan,
			m_f3_s3_ms,
			m_f4_key,
			m_f4_s3_pan,
			m_f4_s3_ms
		);
		
		addPartition(
			m_rootKey,
			m_f4_key,
			m_f4_s1_pan,
			m_f4_s1_ms,
			m_f4_s2_pan,
			m_f4_s2_ms,
			m_f5_key,
			m_f5_s1_pan,
			m_f5_s1_ms,
			m_f5_s2_pan,
			m_f5_s2_ms
		);
		
		addPartition(
			m_rootKey,
			m_f4_key,
			m_f4_s3_pan,
			m_f4_s3_ms,
			m_f5_key,
			m_f5_s3_pan,
			m_f5_s3_ms
		);

	}

}
