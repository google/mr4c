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

import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.bean.dataset.DatasetBean;
import com.google.mr4c.serialize.bean.dataset.DataFileBean;
import com.google.mr4c.serialize.bean.keys.DataKeyBean;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class BeanBasedDatasetSerializer implements DatasetSerializer {

	private DatasetBeanSerializer m_ser;

	public BeanBasedDatasetSerializer(DatasetBeanSerializer beanSerializer) {
		m_ser = beanSerializer;
	}

	public void serializeDataset(Dataset dataset, Writer writer) throws IOException {
		DatasetBean bean = DatasetBean.instance(dataset);
		m_ser.serializeDatasetBean(bean,writer);
	}

	public Dataset deserializeDataset(Reader reader) throws IOException {
		DatasetBean bean = m_ser.deserializeDatasetBean(reader);
		return bean.toDataset();
	}

	public String serializeDataFile(DataFile file) {
		DataFileBean bean = DataFileBean.instance(file);
		return m_ser.serializeDataFileBean(bean);
	}

	public DataFile deserializeDataFile(String serializedFile) {
		DataFileBean bean = m_ser.deserializeDataFileBean(serializedFile);
		return bean.toDataFile();
	}

	public String serializeDataKey(DataKey key) {
		DataKeyBean bean = DataKeyBean.instance(key);
		return m_ser.serializeDataKeyBean(bean);
	}

	public DataKey deserializeDataKey(String serializedKey) {
		DataKeyBean bean = m_ser.deserializeDataKeyBean(serializedKey);
		return bean.toKey();
	}

	public String getContentType() {
		return m_ser.getContentType();
	}
}

