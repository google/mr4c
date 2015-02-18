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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CombinatoricUtils {

	// input: list of collections of elements
	// output: lists of lists, all possible lists containing one element from each input, in order of original collections
	public static <T> List<List<T>> everyCombination(List<Collection<T>> inputs) {

		if ( inputs.isEmpty() ) {
			return Collections.emptyList();
		}

		List<List<T>> result = new ArrayList<List<T>>();
		Collection<T> first = inputs.get(0);
		List<Collection<T>> rest = inputs.subList(1,inputs.size());
		List<List<T>> partial = everyCombination(rest);
	
		for ( T element : first ) {
			if ( partial.isEmpty() ) { 
				result.add(Collections.singletonList(element));
			} else {
				for ( List<T> partList : partial ) {
					List<T> full = new ArrayList<T>();
					full.add(element);
					full.addAll(partList);
					result.add(full);
				}
			}
		}
		return result;
	}

}
