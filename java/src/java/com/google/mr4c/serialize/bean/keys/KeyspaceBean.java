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
import com.google.mr4c.keys.Keyspace;
import com.google.mr4c.keys.KeyspaceDimension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeyspaceBean {

	private KeyspaceDimensionBean[] dimensions;

	public static KeyspaceBean instance(Keyspace keyspace) {
		KeyspaceBean bean = new KeyspaceBean();
		List<KeyspaceDimensionBean> dimensions = new ArrayList<KeyspaceDimensionBean>();
		List<DataKeyDimension> sorted = new ArrayList<DataKeyDimension>(keyspace.getDimensions());
		Collections.sort(sorted);
		for ( DataKeyDimension dim : sorted ) {
			KeyspaceDimension ksd = keyspace.getKeyspaceDimension(dim);
			KeyspaceDimensionBean ksdBean = KeyspaceDimensionBean.instance(ksd);
			dimensions.add(ksdBean);
		}
		bean.dimensions = dimensions.toArray(new KeyspaceDimensionBean[dimensions.size()]);
		return bean;
	}

	public KeyspaceBean(){}

	public Keyspace toKeyspace() {
		Keyspace keyspace = new Keyspace();
		for ( KeyspaceDimensionBean ksdBean : dimensions ) {
			KeyspaceDimension ksd = ksdBean.toKeyspaceDimension();
			keyspace.addKeyspaceDimension(ksd);
		}
		return keyspace;
	}

}
