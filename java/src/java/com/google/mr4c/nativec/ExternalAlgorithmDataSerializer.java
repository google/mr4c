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

import com.google.mr4c.algorithm.AlgorithmData;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.Keyspace;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.KeyspaceSerializer;
import com.google.mr4c.serialize.PropertiesSerializer;
import com.google.mr4c.serialize.SerializerFactory;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import org.slf4j.Logger;

public class ExternalAlgorithmDataSerializer {

	protected static final Logger s_log = MR4CLogging.getLogger(ExternalAlgorithmDataSerializer.class);

	private ExternalDatasetSerializer m_datasetSerializer;
	private KeyspaceSerializer m_keyspaceSerializer;
	private PropertiesSerializer m_propSerializer;

	public ExternalAlgorithmDataSerializer(
		SerializerFactory serializerFactory,
		ExternalFactory factory
	) {
		DatasetSerializer datasetSerializer = serializerFactory.createDatasetSerializer();
		m_datasetSerializer = new ExternalDatasetSerializer(datasetSerializer, factory);
		m_keyspaceSerializer = serializerFactory.createKeyspaceSerializer();
		m_propSerializer = serializerFactory.createPropertiesSerializer();
	}

	public void serializeInputData(AlgorithmData algoData, ExternalAlgorithmData extAlgoData) throws IOException {
		s_log.info("Begin serializing input data");
		serializeKeyspace(algoData, extAlgoData);
		serializeConfig(algoData, extAlgoData);
		serializeInputDatasets(algoData, extAlgoData);
		s_log.info("End serializing input data");
	}

	private void serializeInputDatasets(AlgorithmData algoData, ExternalAlgorithmData extAlgoData) throws IOException {
		for ( String name : algoData.getInputDatasetNames() ) {
			s_log.info("Serializing input dataset [{}]", name);
			Dataset dataset = algoData.getInputDataset(name);
			ExternalDataset extDataset = m_datasetSerializer.serializeDataset(name,dataset);
			extAlgoData.addInputDataset(extDataset);
		}
	}

	public void serializeOutputData(AlgorithmData algoData, ExternalAlgorithmData extAlgoData) throws IOException {
		s_log.info("Begin serializing output data");
		for ( String name : algoData.getOutputDatasetNames() ) {
			s_log.info("Serializing output dataset [{}]", name);
			Dataset dataset = algoData.getOutputDataset(name);
			ExternalDataset extDataset = m_datasetSerializer.serializeDataset(name,dataset);
			extAlgoData.addOutputDataset(extDataset);
		}
		s_log.info("End serializing output data");
	}

	private void serializeKeyspace(AlgorithmData algoData, ExternalAlgorithmData extAlgoData) throws IOException {
		s_log.info("Serializing keyspace");
		StringWriter writer = new StringWriter();
		m_keyspaceSerializer.serializeKeyspace(algoData.getKeyspace(), writer);
		extAlgoData.setSerializedKeyspace(writer.toString());
	}

	private void serializeConfig(AlgorithmData algoData, ExternalAlgorithmData extAlgoData) throws IOException {
		s_log.info("Serializing config params");
		StringWriter writer = new StringWriter();
		m_propSerializer.serializeProperties(algoData.getConfig(), writer);
		extAlgoData.setSerializedConfig(writer.toString());
	}
			
	public void deserializeInputData(AlgorithmData algoData, ExternalAlgorithmData extAlgoData) throws IOException {
		s_log.info("Begin deserializing input data");
		deserializeKeyspace(algoData, extAlgoData);
		deserializeConfig(algoData, extAlgoData);
		deserializeInputDatasets(algoData, extAlgoData);
		s_log.info("End deserializing input data");
	}

	private void deserializeInputDatasets(AlgorithmData algoData, ExternalAlgorithmData extAlgoData) throws IOException {
		for ( int i=0; i<extAlgoData.getInputDatasetCount(); i++ ) {
			ExternalDataset extDataset = extAlgoData.getInputDataset(i);
			String name = extDataset.getName();
			s_log.info("Deserializing input dataset [{}]", name);
			Dataset dataset = null;
			if ( algoData.hasInputDataset(name) ) {
				dataset = algoData.getInputDataset(name);
			} else {
				dataset = new Dataset();
				algoData.addInputDataset(name, dataset);
			}
			m_datasetSerializer.deserializeDataset(dataset, extDataset);
		}
	}

	public void deserializeOutputData(AlgorithmData algoData, ExternalAlgorithmData extAlgoData) throws IOException {
		s_log.info("Begin deserializing output data");
		for ( int i=0; i<extAlgoData.getOutputDatasetCount(); i++ ) {
			ExternalDataset extDataset = extAlgoData.getOutputDataset(i);
			String name = extDataset.getName();
			s_log.info("Deserializing output dataset [{}]", name);
			Dataset dataset = null;
			if ( algoData.hasOutputDataset(name) ) {
				dataset = algoData.getOutputDataset(name);
			} else {
				dataset = new Dataset();
				algoData.addOutputDataset(name, dataset);
			}
			m_datasetSerializer.deserializeDataset(dataset, extDataset);
		}
		s_log.info("End deserializing output data");
	}

	private void deserializeKeyspace(AlgorithmData algoData, ExternalAlgorithmData extAlgoData) throws IOException {
		s_log.info("Deserializing keyspace");
		Reader reader = new StringReader(extAlgoData.getSerializedKeyspace());
		Keyspace keyspace = m_keyspaceSerializer.deserializeKeyspace(reader);
		algoData.setKeyspace(keyspace);
	}

	private void deserializeConfig(AlgorithmData algoData, ExternalAlgorithmData extAlgoData) throws IOException {
		s_log.info("Deserializing config params");
		Reader reader = new StringReader(extAlgoData.getSerializedConfig());
		Properties config = m_propSerializer.deserializeProperties(reader);
		algoData.setConfig(config);
	}

}


