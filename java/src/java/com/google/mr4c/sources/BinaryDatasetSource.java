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
import com.google.mr4c.content.ContentTypes;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyFactory;
import com.google.mr4c.util.MR4CLogging;

import java.io.InputStream;
import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// dataset is just a single file
public class BinaryDatasetSource extends AbstractDatasetSource {

	protected static final Logger s_log = MR4CLogging.getLogger(BinaryDatasetSource.class);

	private static final DataKey s_key = DataKeyFactory.newKey();
	private URI m_file;

	public BinaryDatasetSource(URI file) {
		m_file = file;
	}
	
	public Dataset readDataset() throws IOException {
		s_log.info("Begin reading dataset");
		s_log.info("Checking for single binary file at [{}]", m_file);
		if ( !ContentFactories.exists(m_file) ) {
			throw new IOException(String.format("No file found at [%s]", m_file));
		}
		Dataset dataset = new Dataset();
		DataFile file = createFile();
		dataset.addFile(s_key,file);
		s_log.info("Done reading dataset");
		return dataset;
	}

	private DataFile createFile() throws IOException {
		String contentType = ContentTypes.getContentTypeForName(m_file.toString());
		DataFile file = new DataFile(contentType);
		DataFileSource src = new URIDataFileSource(m_file);
		file.setFileSource(src);
		return file;
	}

	public void writeDataset(Dataset dataset) throws IOException {
		writeDataset(dataset, WriteMode.ALL);
	}

	public void writeDataset(Dataset dataset, WriteMode writeMode) throws IOException {
		s_log.info("Begin writing dataset; writeMode={}", writeMode);
		if ( writeMode!=WriteMode.SERIALIZED_ONLY ) {
			writeDataFile(dataset);
		}
		s_log.info("Done writing dataset");
	}

	private void writeDataFile(Dataset dataset) throws IOException {
		DataFile file = dataset.getFile(s_key);
		if ( file==null ) {
			throw new IllegalStateException("No file associated to the empty key");
		}
		ensureExists();
		URIDataFileSink sink = new URIDataFileSink(m_file);
		s_log.info("Writing single binary file to [{}]", m_file);
		InputStream input = file.getInputStream();
		try {
			sink.writeFile(input);
		} finally {
			input.close(); // quiet close via commons instead?
		}
	}

	public void ensureExists() throws IOException {
		ContentFactories.ensureParentExists(m_file);
	}

	public DataFileSink getDataFileSink(DataKey key) throws IOException {
		if ( !s_key.equals(key) ) {
			throw new IllegalStateException("Dataset should only have empty key");
		}
		return new URIDataFileSink(m_file);
	}

	public DataFile findDataFile(DataKey key) throws IOException {
		return s_key.equals(key) ?
			createFile() :
			null;
	}

	public String getDescription() {
		return m_file.toString();
	}


}
