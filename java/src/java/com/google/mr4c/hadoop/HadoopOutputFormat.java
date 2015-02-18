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

import com.google.mr4c.sources.ExecutionSource;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.io.Text;

public class HadoopOutputFormat implements OutputFormat<Text,Text> {

	public RecordWriter<Text,Text> getRecordWriter(FileSystem ignored, JobConf job, String name, Progressable progress) throws IOException {
		ExecutionSource exeSrc = HadoopUtils.initFromJobAndCreateSource(job, true);
		return MR4COutputFormat.getRecordWriter(exeSrc);
	}

	public void checkOutputSpecs(FileSystem ignored, JobConf job) throws IOException {
	}

}


