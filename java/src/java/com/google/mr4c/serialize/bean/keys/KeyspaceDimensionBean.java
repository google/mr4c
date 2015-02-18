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

import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.KeyspaceDimension;

import java.util.ArrayList;
import java.util.List;

public class KeyspaceDimensionBean {

	private String dimension;
	private DataKeyElementBean[] elements;

	public static KeyspaceDimensionBean instance(KeyspaceDimension ksd) {
		KeyspaceDimensionBean bean = new KeyspaceDimensionBean();
		bean.dimension = ksd.getDimension().getName();
		List<DataKeyElementBean> elements = new ArrayList<DataKeyElementBean>();
		for ( DataKeyElement keyElement : ksd.getElements() ) {
			DataKeyElementBean eleBean = DataKeyElementBean.instance(keyElement);
			elements.add(eleBean);
		}
		bean.elements = elements.toArray(new DataKeyElementBean[elements.size()]);
		return bean;
	}

	public KeyspaceDimensionBean(){}

	public KeyspaceDimension toKeyspaceDimension() {
		DataKeyDimension dim = new DataKeyDimension(dimension);
		KeyspaceDimension ksd = new KeyspaceDimension(dim);
		for ( DataKeyElementBean eleBean : elements ) {
			DataKeyElement keyElement = eleBean.toKeyElement();
			ksd.addElement(keyElement);
		}
		return ksd;
	}

}

