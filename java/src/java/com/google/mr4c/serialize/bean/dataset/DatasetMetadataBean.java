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

package com.google.mr4c.serialize.bean.dataset;

import com.google.mr4c.keys.DataKey;
import com.google.mr4c.metadata.MetadataElement;
import com.google.mr4c.serialize.bean.keys.DataKeyBean;
import com.google.mr4c.serialize.bean.metadata.MetadataEntryBean;

public class DatasetMetadataBean {

	private DataKeyBean key;
	private MetadataEntryBean metadata;

	public static DatasetMetadataBean instance(DataKey key, MetadataElement metadata) {
		DatasetMetadataBean bean = new DatasetMetadataBean();
		bean.key = DataKeyBean.instance(key);
		bean.metadata = MetadataEntryBean.instance(metadata);
		return bean;
	}

	public DatasetMetadataBean(){}

	public DataKey extractDataKey() {
		return key.toKey();
	}

	public MetadataElement extractMetadata() {
		return metadata.toMetadataElement();
	}

}
