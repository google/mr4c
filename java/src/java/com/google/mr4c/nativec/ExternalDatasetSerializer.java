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

package com.google.mr4c.nativec;

import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.serialize.DatasetSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class ExternalDatasetSerializer {

	private DatasetSerializer m_serializer;
	private ExternalFactory m_factory;

	public ExternalDatasetSerializer(DatasetSerializer serializer, ExternalFactory factory) {
		m_serializer = serializer;
		m_factory = factory;
	}

	public ExternalDataset serializeDataset(String name, Dataset dataset) throws IOException {
		ExternalDataset extDataset = m_factory.newDataset(name, dataset);
		StringWriter writer = new StringWriter();
		m_serializer.serializeDataset(dataset,writer);
		extDataset.setSerializedDataset(writer.toString());
		for ( DataKey key : dataset.getAllFileKeys() ) {
			DataFile file = dataset.getFile(key);
			extDataset.addDataFile(serializeDataFile(key, file));
		}
		return extDataset;
	}

	public ExternalDataFile serializeDataFile(DataKey key, DataFile file) {
		return serializeDataFile(key, file, true);
	}

	private ExternalDataFile serializeDataFile(DataKey key, DataFile file, boolean includeSerialized) {
		String serKey = m_serializer.serializeDataKey(key);
		ExternalDataFile extFile = m_factory.newDataFile(serKey, file);
		if ( file.hasContent() ) {
			ExternalDataFileSource extSrc = m_factory.newDataFileSource(file.getFileSource());
			extFile.setDataFileSource(extSrc);
		}
		if ( includeSerialized ) {
			String serFile = m_serializer.serializeDataFile(file);
			extFile.setSerializedFile(serFile);
		}
		if ( file.getFileSink()!=null ) {
			ExternalDataFileSink extSink = m_factory.newDataFileSink(file.getFileSink());
			extFile.setDataFileSink(extSink);
		}
		return extFile;
	}

	public Dataset deserializeDataset(ExternalDataset extDataset) throws IOException {
		Dataset dataset = new Dataset();
		deserializeDataset(dataset, extDataset);
		return dataset;
	}

	public void deserializeDataset(Dataset dataset, ExternalDataset extDataset) throws IOException {
		String serializedDataset = extDataset.getSerializedDataset();
		StringReader reader = new StringReader(serializedDataset);
		// separate files and metadata,
		// metadata will be added en masse
		// files were probably added one at a time already, but we'll check
		Dataset deserDataset = m_serializer.deserializeDataset(reader);
		Dataset metadata = deserDataset.toMetadataOnly();
		Dataset files = deserDataset.toFilesOnly();
		dataset.addSlice(metadata);
		for ( int i=0; i<extDataset.getFileCount(); i++ ) {
			ExternalDataFile extFile = extDataset.getDataFile(i);
			String serKey = extFile.getSerializedKey();
			DataKey key = m_serializer.deserializeDataKey(serKey);
			if ( dataset.getFile(key)==null ) {
				DataFile file = files.getFile(key);
				populateFile(extFile, file);
				dataset.addFile(key, file);
			}
		}
	}

	public DataFile deserializeDataFile(ExternalDataFile extFile) {
		String serFile = extFile.getSerializedFile();
		DataFile file = m_serializer.deserializeDataFile(serFile);
		populateFile(extFile, file);
		return file;
	}

	private void populateFile(ExternalDataFile extFile, DataFile file) {
		ExternalDataFileSource extSrc = extFile.getDataFileSource();
		if ( extSrc!=null ) {
			file.setFileSource(extSrc.getSource());
		}
	}

}
