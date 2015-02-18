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

package com.google.mr4c.util;

import java.util.Map;

import org.junit.*;
import static org.junit.Assert.*;

public class PartitionerTest {


	@Test public void testOneDimension() {
		Partitioner<String> part = new Partitioner<String>(1, 5);
		part.addDimension("dim1", 32, 1);
		part.computePartitions();
		Map<String,Integer> result = part.getComputedPartitions();
		assertEquals(5, (int)result.get("dim1"));
	}
	
	@Test public void testOneDimensionHitMax() {
		Partitioner<String> part = new Partitioner<String>(1, 5);
		part.addDimension("dim1", 6, 1, 3);
		part.computePartitions();
		Map<String,Integer> result = part.getComputedPartitions();
		assertEquals(3, (int)result.get("dim1"));
	}

	@Test public void testOneDimensionHitMin() {
		Partitioner<String> part = new Partitioner<String>(1, 5);
		part.addDimension("dim1", 20, 5, 10);
		part.computePartitions();
		Map<String,Integer> result = part.getComputedPartitions();
		assertEquals(5, (int)result.get("dim1"));
	}

	@Test(expected=IllegalStateException.class)
	public void testOneDimensionMinTooBig() {
		Partitioner<String> part = new Partitioner<String>(1, 5);
		part.addDimension("dim1", 20, 6, 10);
		part.computePartitions();
		Map<String,Integer> result = part.getComputedPartitions();
	}

	@Test(expected=IllegalStateException.class)
	public void testOneDimensionMaxTooSmall() {
		Partitioner<String> part = new Partitioner<String>(5, 10);
		part.addDimension("dim1", 20, 1, 4);
		part.computePartitions();
		Map<String,Integer> result = part.getComputedPartitions();
	}

	@Test public void testOneDimensionPlusFixed() {
		Partitioner<String> part = new Partitioner<String>(1, 7);
		part.addDimension("dim1", 32, 1);
		part.addDimension("dim2", 10, 2, 2);
		part.computePartitions();
		Map<String,Integer> result = part.getComputedPartitions();
		assertEquals(3, (int)result.get("dim1"));
		assertEquals(2, (int)result.get("dim2"));
	}

	@Test public void testMultipleDimensions() {
		Partitioner<String> part = new Partitioner<String>(1, 20);
		part.addDimension("dim1", 32, 1);
		part.addDimension("dim2", 10, 1);
		part.addDimension("dim3", 5, 1);
		part.computePartitions();
		Map<String,Integer> result = part.getComputedPartitions();
		assertEquals(10, (int)result.get("dim1"));
		assertEquals(2, (int)result.get("dim2"));
		assertEquals(1, (int)result.get("dim3"));
	}

	@Test public void testMultipleDimensionsPlusFixed() {
		Partitioner<String> part = new Partitioner<String>(1, 20);
		part.addDimension("dim1", 32, 1);
		part.addDimension("dim2", 10, 1);
		part.addDimension("dim3", 5, 1);
		part.addDimension("dim4", 1, 1, 1);
		part.computePartitions();
		Map<String,Integer> result = part.getComputedPartitions();
		assertEquals(10, (int)result.get("dim1"));
		assertEquals(2, (int)result.get("dim2"));
		assertEquals(1, (int)result.get("dim3"));
		assertEquals(1, (int)result.get("dim4"));
	}

	@Test public void testMultipleDimensionsExtraCapacity() {
		Partitioner<String> part = new Partitioner<String>(1, 10);
		part.addDimension("dim1", 5, 1);
		part.addDimension("dim2", 3, 1, 1);
		part.addDimension("dim3", 2, 1, 1);
		part.computePartitions();
		Map<String,Integer> result = part.getComputedPartitions();
		assertEquals(5, (int)result.get("dim1"));
		assertEquals(1, (int)result.get("dim2"));
		assertEquals(1, (int)result.get("dim3"));
	}


	@Test(expected=IllegalStateException.class)
	public void testMinTooSmall() {
		Partitioner<String> part = new Partitioner<String>(0, 5);
	}
		
	@Test(expected=IllegalStateException.class)
	public void testMaxTooSmall() {
		Partitioner<String> part = new Partitioner<String>(6, 5);
	}
		
	@Test(expected=IllegalStateException.class)
	public void testDimSizeTooSmall() {
		Partitioner<String> part = new Partitioner<String>(1, 100);
		part.addDimension("dim1", 0, 1 );
	}
		
	@Test(expected=IllegalStateException.class)
	public void testDimMinTooSmall() {
		Partitioner<String> part = new Partitioner<String>(1, 100);
		part.addDimension("dim1", 10, 0 );
	}
		
	@Test(expected=IllegalStateException.class)
	public void testDimMinTooLarge() {
		Partitioner<String> part = new Partitioner<String>(1, 100);
		part.addDimension("dim1", 10, 11 );
	}
		
	@Test(expected=IllegalStateException.class)
	public void testDimMaxTooSmall() {
		Partitioner<String> part = new Partitioner<String>(1, 100);
		part.addDimension("dim1", 10, 3, 2 );
	}
		
	@Test(expected=IllegalStateException.class)
	public void testDimMinTooLarge2() {
		Partitioner<String> part = new Partitioner<String>(1, 10);
		part.addDimension("dim1", 11, 20 );
	}
		

}

