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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class PathUtils {

	/**
	  * Takes the elements of one path and prepends any elements that are missing in another path
	*/
	public static String prependMissingPathElements(String path, String otherPath, String separator) {

		List<String> pathElements = Arrays.asList(StringUtils.split(path, separator));
		List<String> otherElements = Arrays.asList(StringUtils.split(otherPath, separator));
		List<String> toAdd = new ArrayList<String>();
		for ( String element : otherElements ) {
			if ( !pathElements.contains(element) ) {
				toAdd.add(element);
			}
		}
		List<String> newElements = new ArrayList<String>();
		newElements.addAll(toAdd);
		newElements.addAll(pathElements);
		return StringUtils.join(newElements, separator);
	}

}
