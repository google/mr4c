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

import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.Keyspace;
import com.google.mr4c.metadata.MetadataMap;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.sources.DatasetSource.WriteMode;
import com.google.mr4c.sources.ExecutionSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Iterator;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.io.Text;

// K2 = output dataset name
// V2 = serialized dataset slice
// K3 = output dataset name (same as K2)
// V3 = serialized output dataset (slices all combined!)
public class HadoopReducer implements Reducer<Text,Text,Text,Text> {

	private JobConf m_job;
	private MR4CMRJob m_bbJob;
	private MR4CReducer m_reducer;



	public void configure(JobConf job) {
		m_job = job;
		m_reducer = null; // clear in case we get reconfigured
	}

	public void reduce(Text key, Iterator<Text> values, OutputCollector<Text,Text> output, Reporter reporter) throws IOException {

		if ( m_reducer==null ) {
			ExecutionSource exeSrc = HadoopUtils.initFromJobAndCreateSource(m_job, true);
			m_reducer = new MR4CReducer(exeSrc);
		}

		m_reducer.reduce(key, values, output, reporter);

	}

	public void close() {
		// no-op
	}
}


