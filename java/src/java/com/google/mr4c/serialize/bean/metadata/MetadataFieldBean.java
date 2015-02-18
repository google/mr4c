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
import com.google.mr4c.metadata.MetadataField;
import com.google.mr4c.metadata.PrimitiveFactory;
import com.google.mr4c.metadata.PrimitiveType;

public class MetadataFieldBean implements MetadataElementBean {

	private String value;
	private PrimitiveType primitiveType;

	public static MetadataFieldBean instance(MetadataField field) {
		MetadataFieldBean bean = new MetadataFieldBean();
		bean.value = field.getValue().toString();
		bean.primitiveType = field.getType();
		return bean;
	}

	public MetadataFieldBean(){}

	public MetadataElementType getMetadataElementType() {
		return MetadataElementType.FIELD;
	}
	
	public MetadataField toMetadataElement() {
		return PrimitiveFactory.parseField(value, primitiveType);
	}

}

