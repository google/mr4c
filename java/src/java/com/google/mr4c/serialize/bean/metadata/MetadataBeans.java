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

import com.google.mr4c.metadata.MetadataArray;
import com.google.mr4c.metadata.MetadataElement;
import com.google.mr4c.metadata.MetadataElementType;
import com.google.mr4c.metadata.MetadataField;
import com.google.mr4c.metadata.MetadataKey;
import com.google.mr4c.metadata.MetadataList;
import com.google.mr4c.metadata.MetadataMap;

import java.util.HashMap;
import java.util.Map;

public abstract class MetadataBeans {

	private static Map<MetadataElementType,Class<? extends MetadataElementBean>> s_classMap = new HashMap<MetadataElementType,Class<? extends MetadataElementBean>>();
	private static Map<MetadataElementType,BeanFactory> s_factories = new HashMap<MetadataElementType,BeanFactory>();

	static {
		s_classMap.put(MetadataElementType.FIELD, MetadataFieldBean.class);
		s_classMap.put(MetadataElementType.ARRAY, MetadataArrayBean.class);
		s_classMap.put(MetadataElementType.LIST, MetadataListBean.class);
		s_classMap.put(MetadataElementType.MAP, MetadataMapBean.class);
		s_classMap.put(MetadataElementType.KEY, MetadataKeyBean.class);
	}

	public static Class<? extends MetadataElementBean> getBeanClass(MetadataElementType type) {
		return s_classMap.get(type);
	}

	public static MetadataElementBean toMetadataElementBean(MetadataElement element) {
		BeanFactory factory = s_factories.get(element.getMetadataElementType());
		return factory.createBean(element);
	}

	private static interface BeanFactory<T extends MetadataElement> {
		MetadataElementBean createBean(T element);
	}

	private static class FieldBeanFactory implements BeanFactory<MetadataField> {
		public MetadataElementBean createBean(MetadataField field) {
			return MetadataFieldBean.instance(field);
		}
	}
	static { s_factories.put(MetadataElementType.FIELD, new FieldBeanFactory()); }
	
	private static class ArrayBeanFactory implements BeanFactory<MetadataArray> {
		public MetadataElementBean createBean(MetadataArray array) {
			return MetadataArrayBean.instance(array);
		}
	}
	static { s_factories.put(MetadataElementType.ARRAY, new ArrayBeanFactory()); }
	
	private static class ListBeanFactory implements BeanFactory<MetadataList> {
		public MetadataElementBean createBean(MetadataList list) {
			return MetadataListBean.instance(list);
		}
	}
	static { s_factories.put(MetadataElementType.LIST, new ListBeanFactory()); }
	
	private static class MapBeanFactory implements BeanFactory<MetadataMap> {
		public MetadataElementBean createBean(MetadataMap map) {
			return MetadataMapBean.instance(map);
		}
	}
	static { s_factories.put(MetadataElementType.MAP, new MapBeanFactory()); }
	
	private static class KeyBeanFactory implements BeanFactory<MetadataKey> {
		public MetadataElementBean createBean(MetadataKey key) {
			return MetadataKeyBean.instance(key);
		}
	}
	static { s_factories.put(MetadataElementType.KEY, new KeyBeanFactory()); }
	

}
	


