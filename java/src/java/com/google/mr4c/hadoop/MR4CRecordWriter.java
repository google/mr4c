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

import com.google.mr4c.AlgoRunner;
import com.google.mr4c.algorithm.AlgorithmData;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.sources.DatasetSource.SourceType;
import com.google.mr4c.sources.DatasetSource.WriteMode;
import com.google.mr4c.sources.ExecutionSource;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.io.StringReader;

import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.io.Text;

import org.slf4j.Logger;

public class MR4CRecordWriter implements RecordWriter<Text,Text> {

	protected Logger m_log = MR4CLogging.getLogger(MR4CRecordWriter.class);

	private AlgoRunner m_algoRunner;
	private DatasetSerializer m_serializer;

	public MR4CRecordWriter(ExecutionSource  exeSrc) throws IOException {
		m_algoRunner = new AlgoRunner(exeSrc);
		m_serializer = SerializerFactories.getSerializerFactory("application/json").createDatasetSerializer();
	}

	public void write(Text key, Text value) throws IOException {
		try {
			doWrite(key,value);
		} catch ( Exception e ) {
			m_log.error("Error in record writer", e);
			throw new IOException(e);
		}
	}

	private void doWrite(Text key, Text value) throws IOException {
		String name = key.toString();
		StringReader reader = new StringReader(value.toString());
		Dataset dataset = m_serializer.deserializeDataset(reader);
		if ( m_algoRunner.getExecutionSource().getOutputDatasetNames(SourceType.LOGS).contains(name) ) {
			writeLogs(name, dataset);
		} else {
			writeData(name, dataset);
		}
	}

	private void writeData(String name, Dataset dataset) throws IOException {
		AlgorithmData algoData = m_algoRunner.getAlgorithmData();
		algoData.addOutputDataset(name,dataset);
		m_algoRunner.saveOutputDataset(name, WriteMode.SERIALIZED_ONLY);
		m_algoRunner.copyOutputToFinal(name);
	}

	private void writeLogs(String name, Dataset dataset) throws IOException {
		Dataset slice = m_algoRunner.buildLogsDataset(name); // should be the reducer logs
		dataset.addSlice(slice);
		AlgorithmData logData = m_algoRunner.getLogsData();
		logData.addOutputDataset(name,dataset);
		m_algoRunner.saveLogsDataset(name, WriteMode.SERIALIZED_ONLY);
	}
	
	public void close(Reporter reporter) throws IOException {
		if ( m_algoRunner!=null ) {
			saveReducerLogFiles();
		}
	}

	private void saveReducerLogFiles() throws IOException {
		// rebuild logs datasets to be reducer only
		m_algoRunner.rebuildLogsDatasets();
		m_algoRunner.saveLogs(WriteMode.FILES_ONLY);
		m_algoRunner.copyLogsToFinal();
	}

}
