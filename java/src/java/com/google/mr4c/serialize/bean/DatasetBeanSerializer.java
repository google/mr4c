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

package com.google.mr4c.serialize.bean;

import com.google.mr4c.serialize.bean.dataset.DataFileBean;
import com.google.mr4c.serialize.bean.dataset.DatasetBean;
import com.google.mr4c.serialize.bean.keys.DataKeyBean;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface DatasetBeanSerializer {

	void serializeDatasetBean(DatasetBean dataset, Writer writer) throws IOException;

	DatasetBean deserializeDatasetBean(Reader reader) throws IOException;

	String serializeDataFileBean(DataFileBean file);

	DataFileBean deserializeDataFileBean(String serializedFile);

	String serializeDataKeyBean(DataKeyBean key);

	DataKeyBean deserializeDataKeyBean(String serializedKey);

	/**
	  * Returns the content type generated during serialization, and expected during deserialization
	*/
	String getContentType();
}
