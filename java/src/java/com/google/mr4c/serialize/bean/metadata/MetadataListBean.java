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
import com.google.mr4c.metadata.MetadataList;
import com.google.mr4c.metadata.PrimitiveFactory;
import com.google.mr4c.metadata.PrimitiveType;

import java.util.ArrayList;
import java.util.List;

public class MetadataListBean implements MetadataElementBean {

	private MetadataEntryBean[] entries;

	public static MetadataListBean instance(MetadataList list) {
		MetadataListBean bean = new MetadataListBean();
		List<MetadataEntryBean> entries = new ArrayList<MetadataEntryBean>();
		for ( MetadataElement element : list.getList()) {
			MetadataEntryBean entry = MetadataEntryBean.instance(element);
			entries.add(entry);
		}
		bean.entries = entries.toArray(new MetadataEntryBean[entries.size()]);
		return bean;
	}

	public MetadataListBean(){}

	public MetadataElementType getMetadataElementType() {
		return MetadataElementType.LIST;
	}
	
	public MetadataList toMetadataElement() {
		List<MetadataElement> elements = new ArrayList<MetadataElement>();
		for ( MetadataEntryBean  entry : entries ) {
			MetadataElement element = entry.toMetadataElement();
			elements.add(element);
		}
		return new MetadataList(elements);
	}

}

