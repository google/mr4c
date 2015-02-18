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

package com.google.mr4c.sources;

import com.google.mr4c.content.ContentFactories;
import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import org.slf4j.Logger;

// For sources where the entire dataset is in a single file
public class SimpleDatasetSource extends AbstractDatasetSource {

	protected static final Logger s_log = MR4CLogging.getLogger(SimpleDatasetSource.class);

	private URI m_file;
	private DatasetSerializer m_serializer;

	public SimpleDatasetSource(URI file, DatasetSerializer serializer) {
		m_file = file;
		m_serializer = serializer;
	}

	public synchronized Dataset readDataset() throws IOException {
		s_log.info("Begin reading dataset");
		s_log.info("Looking for dataset at [{}]", m_file);
		Reader reader = ContentFactories.readContentAsReader(m_file);
		Dataset dataset = null;
		try {
			dataset = m_serializer.deserializeDataset(reader);
		} finally {
			reader.close();
		}
		if ( dataset.hasFiles() ) {
			throw new IllegalStateException("Trying to read dataset with files from simple dataset source");
		}
		s_log.info("Done reading dataset");
		return dataset;
	}

	public void writeDataset(Dataset dataset) throws IOException {
		writeDataset(dataset, WriteMode.ALL);
	}

	public synchronized void writeDataset(Dataset dataset, WriteMode writeMode) throws IOException {
		if ( writeMode==WriteMode.FILES_ONLY ) {
			return; // nothing to do yet
		}
		s_log.info("Begin writing dataset");
		if ( dataset.hasFiles() ) {
			throw new IllegalStateException("Trying to write dataset with files to simple dataset source");
		}
		s_log.info("Writing dataset to [{}]", m_file);
		ensureExists();
		Writer writer = ContentFactories.getWriterForContent(m_file);
		try {
			m_serializer.serializeDataset(dataset, writer);
		} finally {
			writer.close();
		}
		s_log.info("Done writing dataset");
	}

	public void ensureExists() throws IOException {
		ContentFactories.ensureParentExists(m_file);
	}

	public DataFileSink getDataFileSink(DataKey key) {
		throw new IllegalStateException("All data is in the single serialized file");
	}

	public DataFile findDataFile(DataKey key) {
		throw new IllegalStateException("All data is in the single serialized file");
	}

	public String getDescription() {
		return m_file.toString();
	}

}
