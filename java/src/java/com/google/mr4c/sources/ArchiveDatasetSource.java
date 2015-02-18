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
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchiveDatasetSource extends AbstractFilesDatasetSource {

	protected static final Logger s_log = MR4CLogging.getLogger(ArchiveDatasetSource.class);

	private ArchiveSource m_archSrc; 

	public ArchiveDatasetSource(ArchiveSource archSrc) {
		this(null, archSrc);
	}

	public ArchiveDatasetSource(FilesDatasetSourceConfig config, ArchiveSource archSrc) {
		super(s_log,config);
		m_archSrc = archSrc;
	}
	
	public void writeDataset(Dataset dataset, WriteMode writeMode) throws IOException {
		if ( writeMode!=WriteMode.ALL ) {
			throw new IllegalArgumentException("Only allowed to write entire archive at once");
		}
		writeDataset(dataset);
	}

	public void writeDataset(Dataset dataset) throws IOException {
		s_log.info("Begin writing dataset");
		m_archSrc.startWrite();
		try {
			writeDataFiles(dataset);
			writeDatasetToFile(dataset);
			writeConfigToFile();
		} finally {
			m_archSrc.finishWrite();
		}
		s_log.info("Done writing dataset");
	}

	public String getDescription() {
		return String.format("archive dataset source stored by [%s]", m_archSrc.getDescription());
	}


	protected List<String> getAllMetadataFileNames() throws IOException {
		return m_archSrc.getAllMetadataFileNames();
	}

	protected List<String> getAllDataFileNames() throws IOException {
		return m_archSrc.getAllFileNames();
	}

	protected DataFileSource getMetadataFileSource(String name) throws IOException {
		return m_archSrc.getMetadataFileSource(name);
	}

	protected DataFileSource getDataFileSource(String name) throws IOException {
		DataFileSource src = m_archSrc.getFileSource(name);
		s_log.debug("Creating data file source for [{}]", src.getDescription());
		return src;
	}

	protected DataFileSource getDataFileSourceIfExists(String name) throws IOException {
		DataFileSource src = m_archSrc.getFileSourceOnlyIfExists(name);
		if ( src!=null ) {
			s_log.debug("Creating data file source for [{}]", src.getDescription());
		}
		return src;
	}



	protected DataFileSink getMetadataFileSink(String name) throws IOException {
		return m_archSrc.getMetadataFileSink(name);
	}

	protected DataFileSink getDataFileSink(String name) throws IOException {
		DataFileSink sink = m_archSrc.getFileSink(name);
		s_log.debug("Creating data file sink for [{}]", sink.getDescription());
		return sink;
	}

	protected Collection<DataKey> getSortedFileKeys(Dataset dataset) {
		DataKeyFileMapper mapper = m_config.getKeyFileMapper();
		SortedMap<String,DataKey> map = new TreeMap<String,DataKey>();
		for ( DataKey key : dataset.getAllFileKeys() ) {
			String name = mapper.getFileName(key);
			map.put(name,key);
		}
		return map.values();
	}

}
