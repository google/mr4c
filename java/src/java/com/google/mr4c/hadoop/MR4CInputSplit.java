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

import org.apache.hadoop.mapred.InputSplit;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

public class MR4CInputSplit implements InputSplit {

	private int m_seqNum;
	private DataKeyList m_keys = new DataKeyList();
	private String[] m_locs;

	public MR4CInputSplit() {}

	public MR4CInputSplit(int seqNum, Collection<DataKey> keys, Collection<String> locations) {
		m_seqNum = seqNum;
		m_keys = new DataKeyList(keys);
		m_locs = locations.toArray(new String[locations.size()]);
	}

	public long getLength() {
		return m_keys.getKeys().size();
	}

	public int getSequenceNumber() {
		return m_seqNum;
	}

	public DataKeyList getKeys() {
		return m_keys;
	}

	public String[] getLocations() {
		return m_locs;
	}

	public void readFields(DataInput in) throws IOException {
		m_seqNum = in.readInt();
		m_keys.readFields(in);
		int size = in.readInt();
		m_locs = new String[size];
		for ( int i=0; i<size; i++ ) {
			m_locs[i] = in.readUTF();
		}
	}

	public void write(DataOutput out) throws IOException {
		out.writeInt(m_seqNum);
		m_keys.write(out);
		out.writeInt(m_locs.length);
		for ( String loc : m_locs ) {
			out.writeUTF(loc);
		}
	}

}


