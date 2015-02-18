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
import com.google.mr4c.serialize.ConfigSerializer;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.serialize.SerializerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

public abstract class AbstractFilesDatasetSource extends AbstractDatasetSource {

	public static final String DEFAULT_SERIALIZED_FILE_NAME_BASE = "dataset";
	public static final String DEFAULT_CONFIG_FILE_NAME_BASE = "directory";


	protected Logger m_log;

	protected DatasetSerializer m_serializer; // default to use the JSON ser
	protected ConfigSerializer m_confSerializer; // default to use the JSON ser
	protected FilesDatasetSourceConfig m_config;
	protected String m_serFileName; 
	protected String m_confFileName; 

	protected AbstractFilesDatasetSource(Logger log) {
		this(log, null);
	}

	protected AbstractFilesDatasetSource(Logger log, FilesDatasetSourceConfig config) {
		m_log = log;
		m_config = config;

		SerializerFactory factory = SerializerFactories.getSerializerFactory("application/json");
		m_serializer = factory.createDatasetSerializer();
		m_confSerializer = factory.createConfigSerializer();

		m_serFileName = ContentTypes.appendSuffix(DEFAULT_SERIALIZED_FILE_NAME_BASE, m_serializer.getContentType() ); // default, setter could override
		m_confFileName = ContentTypes.appendSuffix(DEFAULT_CONFIG_FILE_NAME_BASE, m_confSerializer.getContentType() ); // default, setter could override


	}
	
	public Dataset readDataset() throws IOException {
		m_log.info("Begin reading dataset");
		List<String> names = getAllMetadataFileNames();

		Dataset dataset = tryReadingDatasetFromFile(names).toMetadataOnly(); // we are going to ignore any DataFiles in the dataset file

		if ( isQueryOnly() ) {
			m_log.info("Dataset is configured as query-only; not going to find all files");
		} else {
			FilesDatasetSourceConfig config = selectConfig(names);
			findFiles(dataset, config);
			addFileSources(dataset, config);
		}

		m_log.info("Done reading dataset");
		return dataset;
	}

	private Dataset tryReadingDatasetFromFile(List<String> names) throws IOException {
		m_log.info("Looking for dataset file named [{}]", m_serFileName);
		if ( names.contains(m_serFileName) ) {
			return readDatasetFromFile();
		} else {
			m_log.warn("No dataset file named [{}] found", m_serFileName);
			return new Dataset();
		}
	}

	private Dataset readDatasetFromFile() throws IOException {

		DataFileSource src = getMetadataFileSource(m_serFileName);
		m_log.info("Reading dataset file from [{}]", src.getDescription());
		Reader reader = new InputStreamReader(src.getFileInputStream());
		try {
			return m_serializer.deserializeDataset(reader);
		} finally {
			reader.close();
		}
	}

	private FilesDatasetSourceConfig selectConfig() throws IOException {
		return selectConfig(getAllMetadataFileNames());
	}

	private FilesDatasetSourceConfig selectConfig(List<String> names) throws IOException {

		boolean haveSelfConfig = names.contains(m_confFileName);
	
		if ( m_config!=null ) {
			if ( !m_config.isSelfConfig() ) {
				m_log.info("Configuration specifies don't use self config");
				return m_config;
			} else if ( !haveSelfConfig ) {
				m_log.info("No self-config file named [{}] found; reverting to provided source config", m_confFileName);
				return m_config;
			}
		}

		if ( !haveSelfConfig ) {
			throw new IllegalStateException(String.format("No self-config file named [%s] found, and no source config provided", m_confFileName));
		}
	
		m_log.info("Looking for self-config file named [{}]", m_confFileName);
		return readConfigFromFile();
		
	}

	private FilesDatasetSourceConfig readConfigFromFile() throws IOException {
		DataFileSource src = getMetadataFileSource(m_confFileName);
		m_log.info("Reading self-config file from [{}]", src.getDescription());
		Reader reader = new InputStreamReader(src.getFileInputStream());
		try {
			return FilesDatasetSourceConfig.load(reader);
		} finally {
			reader.close();
		}
	}

	private Map<DataKey,String> makeFileNameMap(FilesDatasetSourceConfig config) throws IOException {
		DataKeyFileMapper mapper = config.getKeyFileMapper();
		Map<DataKey,String> nameMap = new HashMap<DataKey,String>();
		for ( String name : getAllDataFileNames() ) {
			if ( mapper.canMapName(name) ) {
				DataKey key = mapper.getKey(name);
				nameMap.put(key,name);
			}
		}
		return nameMap;
	}
			
	private void findFiles(Dataset dataset, FilesDatasetSourceConfig config) throws IOException {
		List<String> names = new ArrayList<String>(getAllDataFileNames());
		DataKeyFileMapper mapper = config.getKeyFileMapper();
		for ( String name : names ) {
			if ( config.ignoreExtraFiles() && !mapper.canMapName(name) ) {
				m_log.warn("Ignoring file [{}] (no match)", name);
				continue;
			}
			DataKey key = mapper.getKey(name);
			String contentType = ContentTypes.getContentTypeForName(name);
			DataFile file = new DataFile(contentType);
			dataset.addFile(key,file);
		}
	}

	private void addFileSources(Dataset dataset, FilesDatasetSourceConfig config) throws IOException {
		Map<DataKey,String> nameMap = makeFileNameMap(config);
		for ( DataKey key : dataset.getAllFileKeys() ) {
			DataFile file = dataset.getFile(key);
			String name = nameMap.get(key);
			DataFileSource src = getDataFileSource(name);
			file.setFileSource(src);
		}
	}

	protected void writeDataFiles(Dataset dataset) throws IOException {
		assertHaveConfig();
		ensureExists();
		for ( DataKey key : getSortedFileKeys(dataset) ) {
			DataFile file = dataset.getFile(key);
			writeFile(key,file);
		}
	}

	protected void writeFile(DataKey key, DataFile file) throws IOException {
		assertHaveConfig();
		DataFileSink sink = getDataFileSink(key);
		InputStream input = file.getInputStream();
		try {
			sink.writeFile(input);
		} finally {
			input.close();
		}
	}

	protected void writeDatasetToFile(Dataset dataset) throws IOException {
		Dataset metadata = dataset.toMetadataOnly(); // no longer putting file info into the dataset file
		if ( !metadata.hasMetadata() ) {
			m_log.info("Will not write a dataset file because there is no metadata");
			return;
		}
		assertHaveConfig();
		DataFileSink sink = getMetadataFileSink(m_serFileName);
		m_log.info("Writing dataset file to [{}]", sink.getDescription());
		Writer writer  = new OutputStreamWriter(sink.getFileOutputStream());
		try {
			m_serializer.serializeDataset(metadata, writer);
		} finally {
			writer.close();
		}
	}

	protected void writeConfigToFile() throws IOException {
		assertHaveConfig();
		if ( m_config.isSelfConfig() && m_config.getDirectoryConfig()!=null ) {
			writeConfigToFileHelper();
		}
	}

	private void writeConfigToFileHelper() throws IOException {
		DataFileSink sink = getMetadataFileSink(m_confFileName);
		m_log.info("Writing config file to [{}]", sink.getDescription());
		Writer writer  = new OutputStreamWriter(sink.getFileOutputStream());
		try {
			m_confSerializer.serializeDirectoryConfig(m_config.getDirectoryConfig(), writer);
		} finally {
			writer.close();
		}
	}

	public DataFileSink getDataFileSink(DataKey key) throws IOException {
		String name = m_config.getKeyFileMapper().getFileName(key);
		return getDataFileSink(name);
	}

	public DataFile findDataFile(DataKey key) throws IOException {
		String name = selectConfig().getKeyFileMapper().getFileName(key);
		DataFileSource src = getDataFileSourceIfExists(name);
		if ( src==null ) {
			m_log.warn("No file named [{}] found ", name);
			return null;
		}
		String contentType = ContentTypes.getContentTypeForName(name);
		return new DataFile(src,contentType);
	}

	protected abstract List<String> getAllMetadataFileNames() throws IOException; 

	protected abstract List<String> getAllDataFileNames() throws IOException; 

	protected abstract DataFileSource getMetadataFileSource(String name) throws IOException;

	protected abstract DataFileSource getDataFileSource(String name) throws IOException;

	protected abstract DataFileSource getDataFileSourceIfExists(String name) throws IOException;

	protected abstract DataFileSink getMetadataFileSink(String name) throws IOException;

	protected abstract DataFileSink getDataFileSink(String name) throws IOException;

	protected abstract Collection<DataKey> getSortedFileKeys(Dataset dataset);

	protected void assertHaveConfig() {
		if ( m_config==null ) {
			throw new IllegalStateException(String.format("[%s] is missing source config", getDescription()));
		}
	}

}
