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

package com.google.mr4c.nativec.jna;

import com.google.mr4c.algorithm.AlgorithmContext;
import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.nativec.ExternalAlgorithm;
import com.google.mr4c.nativec.ExternalAlgorithmData;
import com.google.mr4c.nativec.ExternalContext;
import com.google.mr4c.nativec.ExternalDataFile;
import com.google.mr4c.nativec.ExternalDataFileSink;
import com.google.mr4c.nativec.ExternalDataFileSource;
import com.google.mr4c.nativec.ExternalDataset;
import com.google.mr4c.nativec.ExternalDatasetSerializer;
import com.google.mr4c.nativec.ExternalEntry;
import com.google.mr4c.nativec.ExternalFactory;
import com.google.mr4c.nativec.ExternalRandomAccessFileSink;
import com.google.mr4c.nativec.ExternalRandomAccessFileSource;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.SerializerFactory;
import com.google.mr4c.sources.DataFileSink;
import com.google.mr4c.sources.DataFileSource;

public class JnaExternalFactory implements ExternalFactory {

	private SerializerFactory m_serFact;

	public JnaExternalFactory(SerializerFactory factory) {
		m_serFact = factory;
	}

	public ExternalEntry newEntry() {
		return new JnaExternalEntry(m_serFact);
	}

	public ExternalAlgorithm newAlgorithm(String name) {
		return new JnaExternalAlgorithm(name);
	}

	public ExternalAlgorithmData newAlgorithmData() {
		return new JnaExternalAlgorithmData();
	}

	public ExternalDataset newDataset(String name, Dataset dataset) {
		DatasetSerializer ser = m_serFact.createDatasetSerializer();
		ExternalDatasetSerializer extSer = new ExternalDatasetSerializer( ser, this);
		return new JnaExternalDataset(name, dataset, ser, extSer);
	}

	public ExternalDataFile newDataFile(String serializedKey, DataFile file) {
		return new JnaExternalDataFile(serializedKey, file);
	}

	public ExternalContext newContext(AlgorithmContext context) {
		return new JnaExternalContext(context);
	}


	public ExternalDataFileSource newDataFileSource(DataFileSource src) {
		return new JnaDataFileSource(src);
	}

	public ExternalDataFileSink newDataFileSink(DataFileSink sink) {
		return new JnaDataFileSink(sink);
	}

	public ExternalRandomAccessFileSource newRandomAccessFileSource(DataFileSource src) {
		return new JnaRandomAccessFileSource(src);
	}

	public ExternalRandomAccessFileSink newRandomAccessFileSink(DataFileSink sink) {
		return new JnaRandomAccessFileSink(sink);
	}

}
