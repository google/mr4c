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

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.*;
import static org.junit.Assert.*;

public class CollectionUtilsTest {

	@Test public void testConcatenate() {
		List<List<Integer>> input = Arrays.asList(
			Arrays.asList(1,2),
			Arrays.asList(3,4),
			Arrays.asList(5,6)
		);
		List<Integer> result = CollectionUtils.concatenate(input);
		List<Integer> expected = Arrays.asList(1,2,3,4,5,6);
		assertEquals(expected,result);
	}
	@Test public void testPartitionExactFit() {
		List<Integer> input = Arrays.asList(1,2,3,4,5,6);
		List<List<Integer>> result = CollectionUtils.partition(input,3);
		List<List<Integer>> expected = Arrays.asList(
			Arrays.asList(1,2),
			Arrays.asList(3,4),
			Arrays.asList(5,6)
		);
		assertEquals(expected,result);
	}

	@Test public void testPartitionNotExactFit() {
		List<Integer> input = Arrays.asList(1,2,3,4,5,6,7);
		List<List<Integer>> result = CollectionUtils.partition(input,3);
		List<List<Integer>> expected = Arrays.asList(
			Arrays.asList(1,2,3),
			Arrays.asList(4,5),
			Arrays.asList(6,7)
		);
		assertEquals(expected,result);
	}

	@Test public void testPartitionNotEnoughElements() {
		List<Integer> input = Arrays.asList(1,2,3,4);
		List<List<Integer>> result = CollectionUtils.partition(input,6);
		List<List<Integer>> expected = Arrays.asList(
			Arrays.asList(1),
			Arrays.asList(2),
			Arrays.asList(3),
			Arrays.asList(4)
		);
		assertEquals(expected,result);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testPartitionZeroSublists() {
		List<Integer> input = Arrays.asList(1,2,3,4,5,6,7);
		CollectionUtils.partition(input,0);
	}

	@Test public void testPartitionBySizeExactFit() {
		List<Integer> input = Arrays.asList(1,2,3,4,5,6);
		List<List<Integer>> result = CollectionUtils.partitionBySize(input,2);
		List<List<Integer>> expected = Arrays.asList(
			Arrays.asList(1,2),
			Arrays.asList(3,4),
			Arrays.asList(5,6)
		);
		assertEquals(expected,result);
	}

	@Test public void testPartitionBySizeNotExactFit() {
		List<Integer> input = Arrays.asList(1,2,3,4,5,6,7);
		List<List<Integer>> result = CollectionUtils.partitionBySize(input,3);
		List<List<Integer>> expected = Arrays.asList(
			Arrays.asList(1,2,3),
			Arrays.asList(4,5,6),
			Arrays.asList(7)
		);
		assertEquals(expected,result);
	}

	@Test public void testPartitionBySizeNotEnoughElements() {
		List<Integer> input = Arrays.asList(1,2,3,4);
		List<List<Integer>> result = CollectionUtils.partitionBySize(input,6);
		List<List<Integer>> expected = Arrays.asList(
			input
		);
		assertEquals(expected,result);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testPartitionBySizeZeroSize() {
		List<Integer> input = Arrays.asList(1,2,3,4,5,6,7);
		CollectionUtils.partition(input,0);
	}

	@Test public void testOverlapNoChange() {
		List<List<Integer>> input = Arrays.asList(
			Arrays.asList(1,2,3,4,5),
			Arrays.asList(6,7,8,9,10),
			Arrays.asList(11,12,13,14,15)
		);
		List<List<Integer>> result = CollectionUtils.overlap(input, 0, 0);
		assertEquals(input,result);
	}

	@Test public void testOverlapBeforeOnly() {
		List<List<Integer>> input = Arrays.asList(
			Arrays.asList(1,2,3,4,5),
			Arrays.asList(6,7,8,9,10),
			Arrays.asList(11,12,13,14,15)
		);
		List<List<Integer>> result = CollectionUtils.overlap(input, 2, 0);
		List<List<Integer>> expected = Arrays.asList(
			Arrays.asList(1,2,3,4,5),
			Arrays.asList(4,5,6,7,8,9,10),
			Arrays.asList(9,10,11,12,13,14,15)
		);
		assertEquals(expected,result);
	}

	@Test public void testOverlapAfterOnly() {
		List<List<Integer>> input = Arrays.asList(
			Arrays.asList(1,2,3,4,5),
			Arrays.asList(6,7,8,9,10),
			Arrays.asList(11,12,13,14,15)
		);
		List<List<Integer>> result = CollectionUtils.overlap(input, 0, 1);
		List<List<Integer>> expected = Arrays.asList(
			Arrays.asList(1,2,3,4,5,6),
			Arrays.asList(6,7,8,9,10,11),
			Arrays.asList(11,12,13,14,15)
		);
		assertEquals(expected,result);
	}

	@Test public void testOverlapBoth() {
		List<List<Integer>> input = Arrays.asList(
			Arrays.asList(1,2,3,4,5),
			Arrays.asList(6,7,8,9,10),
			Arrays.asList(11,12,13,14,15)
		);
		List<List<Integer>> result = CollectionUtils.overlap(input, 1, 2);
		List<List<Integer>> expected = Arrays.asList(
			Arrays.asList(1,2,3,4,5,6,7),
			Arrays.asList(5,6,7,8,9,10,11,12),
			Arrays.asList(10,11,12,13,14,15)
		);
		assertEquals(expected,result);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testOverlapBeforeNegative() {
		List<List<Integer>> input = Arrays.asList(
			Arrays.asList(1,2,3,4,5),
			Arrays.asList(6,7,8,9,10),
			Arrays.asList(11,12,13,14,15)
		);
		CollectionUtils.overlap(input, -1, 2);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOverlapAfterNegative() {
		List<List<Integer>> input = Arrays.asList(
			Arrays.asList(1,2,3,4,5),
			Arrays.asList(6,7,8,9,10),
			Arrays.asList(11,12,13,14,15)
		);
		CollectionUtils.overlap(input, 0, -1);
	}

	@Test public void testToTrimmedProperties() {
		Properties src = new Properties();
		src.setProperty("var1", "val1");
		src.setProperty("var2", " val2 ");
		Properties expected = new Properties();
		expected.setProperty("var1", "val1");
		expected.setProperty("var2", "val2");
		Properties actual = CollectionUtils.toTrimmedProperties(src);
		assertEquals(expected, actual);
	}

	@Test public void testPropertiesToFileContent() {
		Properties props1 = new Properties();
		props1.setProperty("var1", "val1");
		props1.setProperty("var2", " val2");
		String content = CollectionUtils.toFileContent(props1);
		Properties props2 = CollectionUtils.fromFileContent(content);
		assertEquals(props1, props2);
	}
		
}

