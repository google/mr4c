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

public class KeyDimensionPartitionerTest {

	private Keyspace m_keyspace = new Keyspace();

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


	@Before public void setUp() {
		buildDimensions();
		buildElements();
		buildKeyspace();
	}

	@Test public void testPartitioningWith3() {
		Map<DataKeyDimension,Integer> expected = new HashMap<DataKeyDimension,Integer>();
		expected.put(m_frame,3);
		expected.put(m_sensor,1);
		expected.put(m_type,1);
		testPartitioningAllSplits(3, expected);
	}

	@Test public void testPartitioningWith10() {
		Map<DataKeyDimension,Integer> expected = new HashMap<DataKeyDimension,Integer>();
		expected.put(m_frame,5);
		expected.put(m_sensor,2);
		expected.put(m_type,1);
		testPartitioningAllSplits(10, expected);
	}

	@Test public void testPartitioningWith40() {
		Map<DataKeyDimension,Integer> expected = new HashMap<DataKeyDimension,Integer>();
		expected.put(m_frame,5);
		expected.put(m_sensor,3);
		expected.put(m_type,2);
		testPartitioningAllSplits(40, expected);
	}

	@Test public void testPartitioningWithNoSplits() {
		Map<DataKeyDimension,Integer> expected = new HashMap<DataKeyDimension,Integer>();
		expected.put(m_frame,1);
		expected.put(m_sensor,1);
		expected.put(m_type,1);
		testPartitioning(10, expected);
	}

	@Test public void testPartitioningWith40NoTypeSplit() {
		Map<DataKeyDimension,Integer> expected = new HashMap<DataKeyDimension,Integer>();
		expected.put(m_frame,5);
		expected.put(m_sensor,3);
		expected.put(m_type,1);
		testPartitioning(40, expected, m_frame, m_sensor);
	}

	private void testPartitioningAllSplits(int numPartitions, Map<DataKeyDimension,Integer> expected) {
		testPartitioning(numPartitions, expected, m_frame, m_sensor, m_type);
	}

	private void testPartitioning(int numPartitions, Map<DataKeyDimension,Integer> expected, DataKeyDimension ... canSplit) {
		KeyDimensionPartitioner partitioner = new KeyDimensionPartitioner(m_keyspace, numPartitions);
		for ( DataKeyDimension dim : canSplit ) {
			partitioner.canSplit(dim);
		}
		partitioner.partition();
		Map<DataKeyDimension,Integer> counts = partitioner.getComputedPartitions();
		assertEquals(expected, counts);
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

	private void buildKeyspace() {

		addElement(m_f1);
		addElement(m_f2);
		addElement(m_f3);
		addElement(m_f4);
		addElement(m_f5);

		addElement(m_s1);
		addElement(m_s2);
		addElement(m_s3);

		addElement(m_pan);
		addElement(m_ms);


	}

	private void addElement(DataKeyElement element) {
		DataKey key = DataKeyFactory.newKey(element);
		m_keyspace.addKey(key);
	}

}
