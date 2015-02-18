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

import com.google.mr4c.algorithm.AlgorithmSchema;
import com.google.mr4c.serialize.AlgorithmSerializer;
import com.google.mr4c.serialize.bean.algorithm.AlgorithmSchemaBean;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class BeanBasedAlgorithmSerializer implements AlgorithmSerializer {

	private AlgorithmBeanSerializer m_ser;

	public BeanBasedAlgorithmSerializer(AlgorithmBeanSerializer beanSerializer) {
		m_ser = beanSerializer;
	}

	public void serializeAlgorithmSchema(AlgorithmSchema algoSchema, Writer writer) throws IOException {
		AlgorithmSchemaBean bean = AlgorithmSchemaBean.instance(algoSchema);
		m_ser.serializeAlgorithmSchemaBean(bean,writer);
	}

	public AlgorithmSchema deserializeAlgorithmSchema(Reader reader) throws IOException {
		AlgorithmSchemaBean bean = m_ser.deserializeAlgorithmSchemaBean(reader);
		return bean.toAlgorithmSchema();
	}

	public String getContentType() {
		return m_ser.getContentType();
	}
}

