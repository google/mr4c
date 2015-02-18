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

import com.google.mr4c.algorithm.Algorithm;
import com.google.mr4c.algorithm.AlgorithmEnvironment;
import com.google.mr4c.sources.ExecutionSource;
import com.google.mr4c.hadoop.HadoopUtils;

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

// K1 = identifier for split
// V1 = list of keys for the split
// K2 = output dataset name
// V2 = serialized output dataset
public class HadoopMapper implements Mapper<Text,DataKeyList,Text,Text> {

	private JobConf m_job;
	private MR4CMapper m_mapper;

	public void configure(JobConf job) {
		m_job = job;
		try {
			ExecutionSource exeSrc = HadoopUtils.initFromJobAndCreateSource(m_job, true);
			AlgorithmEnvironment env = new AlgorithmEnvironment();
			// loading algo to push environment for log config
			Algorithm algo = exeSrc.getAlgorithm( env );
			m_mapper = new MR4CMapper(exeSrc);
		} catch ( IOException e ) {
			// if an IO exception is thrown here, another chance in
			// the map method, which is allowed to rethrow
		}
		
	}

	public void map(Text key, DataKeyList value, OutputCollector<Text,Text> output, Reporter reporter) throws IOException {
		if ( m_mapper==null ) {
			ExecutionSource exeSrc = HadoopUtils.initFromJobAndCreateSource(m_job, true);
			m_mapper = new MR4CMapper(exeSrc);
		}
		m_mapper.map(key, value, output, reporter);
	}

	public void close() {
		// no-op
	}
}


