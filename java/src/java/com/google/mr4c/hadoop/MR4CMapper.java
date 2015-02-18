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
import com.google.mr4c.keys.BasicDataKeyFilter;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.Keyspace;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.sources.DatasetSource.WriteMode;
import com.google.mr4c.sources.ExecutionSource;
import com.google.mr4c.util.MR4CLogging;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.io.Text;

import org.slf4j.Logger;

// K1 = identifier for split
// V1 = list of keys for the split
// K2 = output dataset name
// V2 = serialized output dataset
public class MR4CMapper {

	protected final Logger m_log = MR4CLogging.getLogger(MR4CMapper.class);

	private ExecutionSource m_exeSrc;
	private DatasetSerializer m_serializer = SerializerFactories.getSerializerFactory("application/json").createDatasetSerializer();

	public MR4CMapper(ExecutionSource exeSrc) {
		m_exeSrc = exeSrc;
	}

	public void map(Text key, DataKeyList value, OutputCollector<Text,Text> output, Reporter reporter) throws IOException {
		AlgoRunner runner = new AlgoRunner(m_exeSrc);
		boolean success = false;
		try {
			doMap(runner, key, value, output, reporter);
			success = true;
		} catch ( Exception e ) {
			m_log.error("Error in mapper", e);
			throw new IOException(e);
		} finally {
			try {
				handleLogs(runner, key, value, output, reporter);
			} catch ( Exception e ) {
				m_log.error("Error saving logs", e);
				// Only throw log failure if it won't mask another exception
				if ( success ) {
					throw new IOException(e);
				}
			}
		}
	}

	private void doMap(AlgoRunner runner, Text key, DataKeyList value, OutputCollector<Text,Text> output, Reporter reporter) throws IOException {

		runner.loadInputData();

		// slice down to only the part for this split
		BasicDataKeyFilter filter = new BasicDataKeyFilter();
		filter.addKeys(value.getKeys());
		runner.slice(filter);
		
		HadoopContext context = new HadoopContext(reporter);

		runner.executeAlgorithm(context);

		if ( context.isFailed() ) {
			throw new RuntimeException(String.format("Algorithm failed with message [%s]", context.getFailureMessage()));
		}

		// Files should have been written as they were added, get any that weren't
		runner.saveOutputData(WriteMode.FILES_ONLY);
	
		AlgorithmData algoData = runner.getAlgorithmData();
		for ( String name : algoData.getOutputDatasetNames() ) {
			Dataset dataset = algoData.getOutputDataset(name);
			collectOutput(output, name, dataset);
		}

		runner.cleanupAlgorithm();
			
	}

	private void handleLogs(AlgoRunner runner, Text key, DataKeyList value, OutputCollector<Text,Text> output, Reporter reporter) throws IOException {
		runner.buildLogsDatasets();
		runner.saveLogs(WriteMode.FILES_ONLY);
		AlgorithmData logData = runner.getLogsData();
		for ( String name : logData.getOutputDatasetNames() ) {
			Dataset dataset = logData.getOutputDataset(name);
			collectOutput(output, name, dataset);
		}
	}

	private void collectOutput(OutputCollector<Text,Text> output, String name, Dataset dataset) throws IOException {
		StringWriter writer = new StringWriter();
		m_serializer.serializeDataset(dataset,writer);
		Text outKey = new Text(name);
		Text outValue = new Text(writer.toString());
		output.collect(outKey,outValue);
	}

}


