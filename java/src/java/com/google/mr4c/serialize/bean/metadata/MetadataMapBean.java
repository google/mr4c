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
import com.google.mr4c.metadata.MetadataMap;
import com.google.mr4c.metadata.PrimitiveFactory;
import com.google.mr4c.metadata.PrimitiveType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetadataMapBean implements MetadataElementBean {

	private Map<String,MetadataEntryBean> entries;

	public static MetadataMapBean instance(MetadataMap map) {
		MetadataMapBean bean = new MetadataMapBean();
		bean.entries = new HashMap<String,MetadataEntryBean>();
		List<String> sorted = new ArrayList<String>(map.getMap().keySet());
		Collections.sort(sorted);
		for ( String key : sorted ) {
			MetadataElement element = map.getMap().get(key);
			MetadataEntryBean entry = MetadataEntryBean.instance(element);
			bean.entries.put(key,entry);
		}
		return bean;
	}

	public MetadataMapBean(){}

	public MetadataElementType getMetadataElementType() {
		return MetadataElementType.MAP;
	}
	
	public MetadataMap toMetadataElement() {
		Map<String,MetadataElement> elements = new HashMap<String,MetadataElement>();
		for ( String key : entries.keySet() ) {
			MetadataEntryBean entry = entries.get(key);
			MetadataElement element = entry.toMetadataElement();
			elements.put(key,element);
		}
		return new MetadataMap(elements);
	}

}

