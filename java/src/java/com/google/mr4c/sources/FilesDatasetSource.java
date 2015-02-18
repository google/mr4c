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

import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilesDatasetSource extends AbstractFilesDatasetSource implements DatasetSource {

	protected static final Logger s_log = MR4CLogging.getLogger(FilesDatasetSource.class);

	protected FileSource m_fileSrc;

	public FilesDatasetSource(FileSource fileSrc) {
		this(null, fileSrc);
	}

	public FilesDatasetSource(FilesDatasetSourceConfig config, FileSource fileSrc) {
		this(s_log, config, fileSrc);
	}
	
	protected FilesDatasetSource(Logger log, FilesDatasetSourceConfig config, FileSource fileSrc) {
		super(log, config);
		m_fileSrc = fileSrc;
	}

	public void writeDataset(Dataset dataset) throws IOException {
		writeDataset(dataset, WriteMode.ALL);
	}

	public synchronized void writeDataset(Dataset dataset, WriteMode writeMode) throws IOException {
		s_log.info("Begin writing dataset; writeMode={}", writeMode);
		if ( writeMode!=WriteMode.SERIALIZED_ONLY ) {
			writeDataFiles(dataset);
		}
		if ( writeMode!=WriteMode.FILES_ONLY ) {
			writeDatasetToFile(dataset);
			writeConfigToFile();
		}
		s_log.info("Done writing dataset");
	}

	public String getDescription() {
		return String.format("directory dataset source stored by [%s]", m_fileSrc.getDescription());
	}


	protected List<String> getAllMetadataFileNames() throws IOException {
		List<String> names = new ArrayList<String>();
		for ( String name : Arrays.asList(m_serFileName, m_confFileName)) {
			if ( m_fileSrc.fileExists(name) ) {
				names.add(name);
			}
		}
		return names;
	}

	protected List<String> getAllDataFileNames() throws IOException {
		List<String> names = new ArrayList<String>(m_fileSrc.getAllFileNames());
		names.remove(m_serFileName);
		names.remove(m_confFileName);
		return names;
	}

	protected DataFileSource getMetadataFileSource(String name) throws IOException {
		return getDataFileSource(name);
	}

	protected DataFileSource getDataFileSource(String name) throws IOException {
		DataFileSource src = m_fileSrc.getFileSource(name);
		s_log.debug("Creating data file source for [{}]", src.getDescription());
		return src;
	}

	protected DataFileSource getDataFileSourceIfExists(String name) throws IOException {
		DataFileSource src = m_fileSrc.getFileSourceOnlyIfExists(name);
		if ( src!=null ) {
			s_log.debug("Creating data file source for [{}]", src.getDescription());
		}
		return src;
	}

	protected DataFileSink getMetadataFileSink(String name) throws IOException {
		return getDataFileSink(name);
	}

	protected DataFileSink getDataFileSink(String name) throws IOException {
		DataFileSink sink = m_fileSrc.getFileSink(name);
		s_log.debug("Creating data file sink for [{}]", sink.getDescription());
		return sink;
	}

	public void ensureExists() throws IOException {
		m_fileSrc.ensureExists();
	}

	protected Collection<DataKey> getSortedFileKeys(Dataset dataset) {
		return dataset.getAllFileKeys();
	}

}
