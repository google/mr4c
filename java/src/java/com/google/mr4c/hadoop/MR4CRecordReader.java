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

import java.io.StringWriter;
import java.util.List;

import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.io.Text;

public class MR4CRecordReader implements RecordReader<Text,DataKeyList> {

	private MR4CInputSplit m_split;
	private boolean m_done=false;

	// NOTE: treating the entire split as a single hadoop "record"
	// would need additional splitting logic to generate multiple records

	public MR4CRecordReader(MR4CInputSplit split) {
		m_split=split;
	}

	public void close() {
		// no-op
	}

	public Text createKey() {
		return new Text();
	}

	public DataKeyList createValue() {
		return new DataKeyList();
	}

	// NOTE: Position and progress are of limited usefulness as long as we send all the keys as a single record
	// Doing the best we can for now

	public long getPos() {
		return m_done ? 1 : 0; // this could also be number of keys
	}

	public float getProgress() {
		return m_done ? 1.0f : 0.0f;
	}

	public boolean next(Text key, DataKeyList value) {
		if ( m_done ) {
			return false;
		}
		key.set(""+ m_split.getSequenceNumber());
		value.setKeys(m_split.getKeys().getKeys());
		m_done=true;
		return true;
	}
  
}


