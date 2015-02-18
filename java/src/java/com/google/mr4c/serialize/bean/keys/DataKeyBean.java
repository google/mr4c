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

package com.google.mr4c.serialize.bean.keys;

import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataKeyBean {

	private DataKeyElementBean[] elements;

	public static DataKeyBean instance(DataKey key) {
		DataKeyBean bean = new DataKeyBean();
		List<DataKeyElementBean> elements = new ArrayList<DataKeyElementBean>();
		List<DataKeyDimension> sorted = new ArrayList<DataKeyDimension>(key.getDimensions());
		Collections.sort(sorted);
		for ( DataKeyDimension dim : sorted ) {
			DataKeyElement keyElement  = key.getElement(dim);
			DataKeyElementBean eleBean = DataKeyElementBean.instance(keyElement);
			elements.add(eleBean);
		}
		bean.elements = elements.toArray(new DataKeyElementBean[elements.size()]);
		return bean;
	}

	public DataKeyBean(){}

	public DataKey toKey() {
		Set<DataKeyElement> keyElements = new HashSet<DataKeyElement>();
		for ( DataKeyElementBean eleBean : elements ) {
			DataKeyElement keyElement = eleBean.toKeyElement();
			keyElements.add(keyElement);
		}
		return DataKeyFactory.newKey(keyElements);
	}

}

