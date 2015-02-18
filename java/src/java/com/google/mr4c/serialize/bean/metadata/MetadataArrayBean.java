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

package com.google.mr4c.serialize.bean.metadata;

import com.google.mr4c.metadata.MetadataElement;
import com.google.mr4c.metadata.MetadataElementType;
import com.google.mr4c.metadata.MetadataArray;
import com.google.mr4c.metadata.PrimitiveFactory;
import com.google.mr4c.metadata.PrimitiveType;

import java.util.ArrayList;
import java.util.List;

public class MetadataArrayBean implements MetadataElementBean {

	private String[] values;
	private PrimitiveType primitiveType;

	public static MetadataArrayBean instance(MetadataArray array) {
		MetadataArrayBean bean = new MetadataArrayBean();
		List<String> vals = new ArrayList<String>();
		for ( Object val : array.getValues()) {
			vals.add(val.toString());
		}
		bean.values = vals.toArray(new String[array.getValues().size()]);
		bean.primitiveType = array.getType();
		return bean;
	}

	public MetadataArrayBean(){}

	public MetadataElementType getMetadataElementType() {
		return MetadataElementType.ARRAY;
	}
	
	public MetadataArray toMetadataElement() {
		return PrimitiveFactory.parseArray(values, primitiveType);
	}

}

