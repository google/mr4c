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

import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyFactory;
import com.google.mr4c.metadata.MetadataElement;
import com.google.mr4c.metadata.MetadataElementType;
import com.google.mr4c.metadata.MetadataField;
import com.google.mr4c.metadata.MetadataMap;
import com.google.mr4c.metadata.PrimitiveType;
import com.google.mr4c.util.MR4CLogging;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;

/**
  * Dataset source where each named file becomes a metadata string
*/
public class MetafilesDatasetSource extends AbstractDatasetSource {

	// NOTE: possible expansions of this class:
	// 1.  Exploding file content into full metadata structure (by config or file type)
	// 2.  Another version for ArchiveSource
	
	protected static final Logger s_log = MR4CLogging.getLogger(MetafilesDatasetSource.class);

	private FileSource m_fileSrc;

	public MetafilesDatasetSource(FileSource fileSrc) {
		m_fileSrc = fileSrc;
	}

	public synchronized Dataset readDataset() throws IOException {
		s_log.info("Begin reading dataset");
		Dataset dataset = new Dataset();
		MetadataMap map = new MetadataMap();
		DataKey key = DataKeyFactory.newKey();
		for ( String name : m_fileSrc.getAllFileNames() ) {
			s_log.info("Begin reading metadata item {}", name);
			DataFileSource src = m_fileSrc.getFileSource(name);
			InputStream input = src.getFileInputStream();
			try {
				String content = IOUtils.toString(input);
				MetadataField field = new MetadataField(content,PrimitiveType.STRING);
				map.getMap().put(name, field);
			} finally {
				input.close(); 
			}
			s_log.info("Done reading metadata item {}", name);
		}
		dataset.addMetadata(key,map);
		s_log.info("Done reading dataset");
		return dataset;

	}


	public void writeDataset(Dataset dataset) throws IOException {
		writeDataset(dataset, WriteMode.ALL);
	}

	public synchronized void writeDataset(Dataset dataset, WriteMode writeMode) throws IOException {
		if ( writeMode==WriteMode.FILES_ONLY) {
			return; // nothing to do yet

		}
		s_log.info("Begin writing dataset");
		DataKey key = DataKeyFactory.newKey();
		MetadataMap map = dataset.getMetadata(key);
		if ( map==null ) {
			throw new IOException("Missing metadata map");
		}
		for ( String name : map.getMap().keySet() ) {
			s_log.info("Begin writing metadata item {}", name);
			MetadataElement element = map.getMap().get(name);
			MetadataElementType type = element.getMetadataElementType();
			if ( type!=MetadataElementType.FIELD ) {
				throw new IOException(String.format("Element [%s] is type %s in metafiles dataset; all elements must be FIELD with String content", name, type));
			}
			MetadataField field = (MetadataField) map.getMap().get(name);
			String content = field.getValue().toString();
			DataFileSink sink = m_fileSrc.getFileSink(name);
			OutputStream output = sink.getFileOutputStream();
			try {
				IOUtils.write(content,output);
			} finally {
				output.close(); 
			}
			s_log.info("Done writing metadata item {}", name);
		}
		s_log.info("Done writing dataset");
	}

	public DataFile findDataFile(DataKey key) throws IOException {
		throw new IllegalStateException("This source is metadata only");
	}

	public DataFileSink getDataFileSink(DataKey key) throws IOException {
		throw new IllegalStateException("This source is metadata only");
	}

	public void ensureExists() throws IOException {
		m_fileSrc.ensureExists();
	}

	public String getDescription() {
		return String.format("metadata files dataset source stored by [%s]", m_fileSrc.getDescription());
	}

}

