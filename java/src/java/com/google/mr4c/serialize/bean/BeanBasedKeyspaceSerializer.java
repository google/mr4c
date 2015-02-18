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

import com.google.mr4c.keys.Keyspace;
import com.google.mr4c.serialize.KeyspaceSerializer;
import com.google.mr4c.serialize.bean.keys.KeyspaceBean;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class BeanBasedKeyspaceSerializer implements KeyspaceSerializer {

	private KeyspaceBeanSerializer m_ser;

	public BeanBasedKeyspaceSerializer(KeyspaceBeanSerializer beanSerializer) {
		m_ser = beanSerializer;
	}

	public void serializeKeyspace(Keyspace keyspace, Writer writer) throws IOException {
		KeyspaceBean bean = KeyspaceBean.instance(keyspace);
		m_ser.serializeKeyspaceBean(bean,writer);
	}

	public Keyspace deserializeKeyspace(Reader reader) throws IOException {
		KeyspaceBean bean = m_ser.deserializeKeyspaceBean(reader);
		return bean.toKeyspace();
	}

	public String getContentType() {
		return m_ser.getContentType();
	}
}

