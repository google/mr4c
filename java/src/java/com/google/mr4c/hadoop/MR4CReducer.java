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
import com.google.mr4c.util.MR4CLogging;

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

import org.slf4j.Logger;

// K2 = output dataset name
// V2 = serialized dataset slice
// K3 = output dataset name (same as K2)
// V3 = serialized output dataset (slices all combined!)
public class MR4CReducer {

	protected final Logger m_log = MR4CLogging.getLogger(MR4CReducer.class);

	private ExecutionSource m_exeSrc;
	private DatasetSerializer m_serializer = SerializerFactories.getSerializerFactory("application/json").createDatasetSerializer();

	public MR4CReducer(ExecutionSource exeSrc) {
		m_exeSrc = exeSrc;
	}


	public void reduce(Text key, Iterator<Text> values, OutputCollector<Text,Text> output, Reporter reporter) throws IOException {
		try {
			doReduce(key, values, output, reporter);
		} catch (Exception e) {
			m_log.error("Error in reducer", e);
			throw new IOException(e);
		}
	}

	private void doReduce(Text key, Iterator<Text> values, OutputCollector<Text,Text> output, Reporter reporter) throws IOException {

		Dataset dataset = new Dataset();

		while ( values.hasNext() ) {
			Text value = values.next();
			StringReader reader = new StringReader(value.toString());
			Dataset slice = m_serializer.deserializeDataset(reader);
			dataset.addSlice(slice);
		}

		StringWriter writer = new StringWriter();
		m_serializer.serializeDataset(dataset, writer);
		Text value = new Text(writer.toString());
		output.collect(key,value);
	}

}


