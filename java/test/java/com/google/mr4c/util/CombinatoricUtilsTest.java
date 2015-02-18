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
import java.util.Collection;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class CombinatoricUtilsTest {

	@Test public void testEveryCombination() {

		List<Collection<Character>> inputs = Arrays.asList(
			(Collection<Character>) Arrays.asList('1','2','3','4'),
			(Collection<Character>) Arrays.asList('a','e','i'),
			(Collection<Character>) Arrays.asList('s','t')
		);

		List<List<Character>> expected = Arrays.asList(
			Arrays.asList('1','a','s'),
			Arrays.asList('1','a','t'),
			Arrays.asList('1','e','s'),
			Arrays.asList('1','e','t'),
			Arrays.asList('1','i','s'),
			Arrays.asList('1','i','t'),
			Arrays.asList('2','a','s'),
			Arrays.asList('2','a','t'),
			Arrays.asList('2','e','s'),
			Arrays.asList('2','e','t'),
			Arrays.asList('2','i','s'),
			Arrays.asList('2','i','t'),
			Arrays.asList('3','a','s'),
			Arrays.asList('3','a','t'),
			Arrays.asList('3','e','s'),
			Arrays.asList('3','e','t'),
			Arrays.asList('3','i','s'),
			Arrays.asList('3','i','t'),
			Arrays.asList('4','a','s'),
			Arrays.asList('4','a','t'),
			Arrays.asList('4','e','s'),
			Arrays.asList('4','e','t'),
			Arrays.asList('4','i','s'),
			Arrays.asList('4','i','t')
		);

		List<List<Character>> result = CombinatoricUtils.everyCombination(inputs);
		assertEquals(expected,result);
	}


}

