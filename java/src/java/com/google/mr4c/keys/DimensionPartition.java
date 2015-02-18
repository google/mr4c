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
import java.util.List;

public class DimensionPartition {

	private DataKeyDimension m_dim;
	private List<DataKeyElement> m_elements = Collections.synchronizedList( new ArrayList<DataKeyElement>() );
	private BasicElementFilter m_filter;

	public DimensionPartition(DataKeyDimension dim) {
		m_dim = dim;
		m_filter = new BasicElementFilter(dim);
	}
	

	public DataKeyDimension getDimension() {
		return m_dim;
	}

	public void addElements(List<DataKeyElement> elements) {
		m_elements.addAll(elements);
		m_filter.addElements(elements);
	}
	
	public void addElement(DataKeyElement element) {
		m_elements.add(element);
		m_filter.addElement(element);
	}

	public List<DataKeyElement> getElements() {
		return m_elements; 
	}

	public ElementFilter getFilter() {
		return m_filter;
	}


}


