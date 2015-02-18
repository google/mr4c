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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

class DataKeyComparator implements Comparator<DataKey> {

	public static final DataKeyComparator INSTANCE = new DataKeyComparator();

	public int compare(DataKey key1, DataKey key2 ) {

		List<DataKeyDimension> dims1 = sortDimensions(key1);
		List<DataKeyDimension> dims2 = sortDimensions(key2);

		Iterator<DataKeyDimension> iter1 = dims1.iterator();
		Iterator<DataKeyDimension> iter2 = dims2.iterator();

		for ( ;; ) {
			if ( !iter1.hasNext() && !iter2.hasNext() ) {
				return 0;
			}
			if ( !iter1.hasNext() ) {
				return -1;
			}
			if ( !iter2.hasNext() ) {
				return 1;
			}
			DataKeyDimension dim1 = iter1.next();
			DataKeyDimension dim2 = iter2.next();
			if ( !dim1.equals(dim2) ) {
				return dim1.compareTo(dim2);
			}
			DataKeyElement ele1 = key1.getElement(dim1);
			DataKeyElement ele2 = key2.getElement(dim2);
			if ( !ele1.equals(ele2) ) {
				return ele1.compareTo(ele2);
			}
		}
	
	}

	private List<DataKeyDimension> sortDimensions(DataKey key) {
		List<DataKeyDimension> dims = new ArrayList<DataKeyDimension>(key.getDimensions());
		Collections.sort(dims);
		return dims;
	}

}

