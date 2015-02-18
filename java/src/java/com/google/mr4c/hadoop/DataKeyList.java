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

package com.google.mr4c.hadoop;

import com.google.mr4c.keys.DataKey;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.SerializerFactories;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataKeyList implements Writable {

	private List<DataKey> m_keys = new ArrayList<DataKey>();
	private DatasetSerializer m_serializer = SerializerFactories.getSerializerFactory("application/json").createDatasetSerializer();

	public DataKeyList() {}

	public DataKeyList(Collection<DataKey> keys) {
		m_keys.addAll(keys);
	}

	public List<DataKey> getKeys() {
		return m_keys;
	}

	public void setKeys(List<DataKey> keys) {
		m_keys = new ArrayList<DataKey>(keys);
	}

	public void readFields(DataInput in) throws IOException {
		m_keys.clear();
		int num = in.readInt();
		for ( int i=0; i<num; i++ ){
			String serializedKey = in.readUTF();
			DataKey key = m_serializer.deserializeDataKey(serializedKey);
			m_keys.add(key);
		}
	}

	public void write(DataOutput out) throws IOException {
		out.writeInt(m_keys.size());
		for ( DataKey key : m_keys ) {
			String serializedKey = m_serializer.serializeDataKey(key);
			out.writeUTF(serializedKey);
		}
	}

}


