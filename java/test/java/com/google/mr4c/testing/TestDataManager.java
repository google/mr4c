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

package com.google.mr4c.testing;

import com.google.mr4c.algorithm.Algorithm;
import com.google.mr4c.algorithm.AlgorithmBase;
import com.google.mr4c.algorithm.AlgorithmContext;
import com.google.mr4c.algorithm.AlgorithmData;
import com.google.mr4c.algorithm.AlgorithmSchema;
import com.google.mr4c.algorithm.AlgorithmType;
import com.google.mr4c.config.algorithm.AlgorithmConfig;
import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;
import com.google.mr4c.keys.Keyspace;
import com.google.mr4c.keys.KeyspaceDimension;
import com.google.mr4c.metadata.MetadataArray;
import com.google.mr4c.metadata.MetadataField;
import com.google.mr4c.metadata.MetadataMap;
import com.google.mr4c.metadata.PrimitiveType;
import com.google.mr4c.sources.AbstractDatasetSource;
import com.google.mr4c.sources.BytesDataFileSink;
import com.google.mr4c.sources.CustomExecutionSource;
import com.google.mr4c.sources.DataFileSink;
import com.google.mr4c.sources.DatasetSource;
import com.google.mr4c.sources.DatasetSource.WriteMode;
import com.google.mr4c.sources.ExecutionSource;
import com.google.mr4c.util.CombinatoricUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;

import static org.junit.Assert.*;

/**
  * Class to manage the creation of a complete set of data for an algorithm run.
  * It uses simple methods to derive data from keys and dataset names.
*/
public class TestDataManager {

	private Map<String,InputDatasetSource> m_inputs = new HashMap<String,InputDatasetSource>();
	private Map<String,OutputDatasetSource> m_outputs = new HashMap<String,OutputDatasetSource>();
	private Keyspace m_keyspace = new Keyspace();
	private Set<DataKey> m_keys;
	private CustomExecutionSource m_exeSrc;

	// for each key, can derive ALL associated data as follows:
	// 1.  Make a label: dataset_name + key.toString()
	// 2.  label.getBytes() becomes the file data
	// 3.  metadata for the key could be the string value, hash, length, array of bytes, etc.


	public void addInputDataset(String name) {
		m_inputs.put(name, new InputDatasetSource(name));
	}

	public void addOutputDataset(String name) {
		m_outputs.put(name, new OutputDatasetSource(name));
	}

	public void addDimension(String dimName, Collection<String> elementNames) {
		m_keyspace.addKeyspaceDimension(generateKeyspaceDimension(dimName, elementNames));
	}
	

	/**
	  * Call this after adding all dimensions and datasets to generate the keys and keyspaces.
	*/
	public void readyToTest() {
		m_keys = generateKeys(m_keyspace);
		Algorithm algo = new TestAlgorithm();
		AlgorithmConfig algoConfig = new AlgorithmConfig("test", "testing", AlgorithmType.JAVA, TestAlgorithm.class.getName());
		algo.setAlgorithmConfig(algoConfig);
		algo.init();
		m_exeSrc = new CustomExecutionSource(algo);
		for ( String name : m_inputs.keySet() ) {
			m_exeSrc.addInputSource(name, m_inputs.get(name));
		}
		for ( String name : m_outputs.keySet() ) {
			OutputDatasetSource src = m_outputs.get(name);
			src.init();
			m_exeSrc.addOutputSource(name, src);
		}
	}

	public ExecutionSource getExecutionSource() {
		return m_exeSrc;
	}

	public Set<DataKey> getKeys() {
		return m_keys;
	}

	public Keyspace getKeyspace() {
		return m_keyspace;
	}

	public DatasetSource getInputDatasetSource(String name) {
		return m_inputs.get(name);
	}

	public DatasetSource getOutputDatasetSource(String name) {
		return m_outputs.get(name);
	}

	/**
		Check that these modes, and only these modes, were called
	*/
	public void assertWriteCalled(String name, WriteMode ... modes) {
		m_outputs.get(name).assertWriteCalled(modes);
	}

	public void assertSerializedContentCorrect(String name) {
		m_outputs.get(name).assertSerializedContentCorrect();
	}

	public void assertFileContentCorrect(String name) throws IOException {
		m_outputs.get(name).assertFileContentCorrect();
	}

	public boolean copyToFinalCalled(String name) {
		return m_outputs.get(name).m_copy;
	}

	public static Dataset buildDataset(String name, Set<DataKey> keys) {
		Dataset dataset = new Dataset();
		populateDataset(dataset, name, keys);
		return dataset;
	}

	public static void populateDataset(Dataset dataset, String name, Set<DataKey> keys) {
		for ( DataKey key : keys ) {
			dataset.addFile(key, buildFile(name,key));
			dataset.addMetadata(key, buildMetadata(name,key));
		}
	}

	public static DataFile buildFile(String name, DataKey key) {
		String label = toLabel(name,key);
		return new DataFile(label.getBytes(), "text/plain");
	}

	public static MetadataMap buildMetadata(String name, DataKey key) {
		String label = toLabel(name,key);
		MetadataMap metamap = new MetadataMap();
		metamap.getMap().put("label", new MetadataField(label, PrimitiveType.STRING));
		metamap.getMap().put("length", new MetadataField(label.length(), PrimitiveType.INTEGER));
		metamap.getMap().put("bytes", new MetadataArray(ArrayUtils.toObject(label.getBytes()), PrimitiveType.BYTE));
		return metamap;
	}

	public static String toLabel(String name, DataKey key) {
		return String.format("%s_%s", name, key.toString());
	}


	// dim name + list of values ---> Keyspace Dimension
	public static KeyspaceDimension generateKeyspaceDimension(String dimName, Collection<String> elementNames) {
		DataKeyDimension dim = new DataKeyDimension(dimName);
		KeyspaceDimension ksd = new KeyspaceDimension(dim);
		for ( String eleName : elementNames ) {
			ksd.addElement(new DataKeyElement(eleName, dim));
		}
		return ksd;
	}

	// Keyspace --> all combos of keys
	public static Set<DataKey> generateKeys(Keyspace keyspace) {
		Set<DataKey> keys = new HashSet<DataKey>();
		List<Collection<DataKeyElement>> inputs = new ArrayList<Collection<DataKeyElement>>();
		for ( DataKeyDimension dim : keyspace.getDimensions() ) {
			KeyspaceDimension ksd = keyspace.getKeyspaceDimension(dim);
			inputs.add(ksd.getElements());
		}
		List<List<DataKeyElement>> outputs = CombinatoricUtils.everyCombination(inputs);
		for ( List<DataKeyElement> keyElements : outputs ) {
			keys.add(DataKeyFactory.newKey(keyElements));
		}
		return keys;
	}



	private class InputDatasetSource extends AbstractDatasetSource {

		private String m_name;

		private InputDatasetSource(String name) {
			m_name = name;
		}

		public Dataset readDataset() throws IOException {
			return buildDataset(m_name, m_keys);
		}

		public void writeDataset(Dataset dataset) throws IOException {
			throw new IOException("Writing to input dataset source");
		}

		public void writeDataset(Dataset dataset, WriteMode writeMode) throws IOException {
			throw new IOException("Writing to input dataset source");
		}

		public DataFileSink getDataFileSink(DataKey key) throws IOException {
			throw new IOException("Writing to input dataset source");
		}

		public DataFile findDataFile(DataKey key) throws IOException {
			return buildFile(m_name, key);
		}

		public void copyToFinal() throws IOException {
			throw new IOException("Writing to input dataset source");
		}

		public String getDescription() {
			return String.format("Test input dataset source [%s]", m_name);
		}

	}

	private class OutputDatasetSource extends AbstractDatasetSource {

		private String m_name;
		private Dataset m_expected;
		private Dataset m_written;
		private Map<DataKey,BytesDataFileSink> m_sinks = new HashMap<DataKey,BytesDataFileSink>();
		private boolean m_copy=false;
		private Set<WriteMode> m_writeCalls = new HashSet<WriteMode>();

		private OutputDatasetSource(String name) {
			m_name = name;
		}

		private void init() {
			m_expected = buildDataset(m_name, m_keys);
		}

		public Dataset readDataset() throws IOException {
			throw new IOException("Reading from output dataset source");
		}

		public void writeDataset(Dataset dataset) throws IOException {
			writeDataset(dataset, WriteMode.ALL);
		}

		public void writeDataset(Dataset dataset, WriteMode writeMode) throws IOException {
			validateMode(writeMode);
			m_writeCalls.add(writeMode);
		
			boolean writeFiles = writeMode!=WriteMode.SERIALIZED_ONLY;
			boolean writeSerialized = writeMode!=WriteMode.FILES_ONLY;

			if ( writeSerialized ) {
				m_written = dataset;
			}

			if ( writeFiles ) {
				for ( DataKey key : dataset.getAllFileKeys() ) {
					DataFile file = dataset.getFile(key);
					DataFileSink sink = getDataFileSink(key);
					sink.writeFile(file.getBytes());
				}
			}
	
		}

		private void validateMode(WriteMode writeMode) {
			boolean twice = writeMode==WriteMode.ALL && !m_writeCalls.isEmpty() || m_writeCalls.contains(writeMode);
			String msg = "Tried to write twice to dataset " + m_name;
			assertFalse(msg, twice);
		}

		public DataFileSink getDataFileSink(DataKey key) throws IOException {
			BytesDataFileSink sink = m_sinks.get(key);
			if ( sink==null ) {
				sink = new BytesDataFileSink();
				m_sinks.put(key, sink);
			}
			return sink;
		}

		public DataFile findDataFile(DataKey key) throws IOException {
			throw new IOException("Reading from output dataset source");
		}

		public void copyToFinal() throws IOException {
			if ( m_copy ) {
				throw new IllegalStateException("Tried to write the same dataset twice");
			}
			m_copy=true;
		}

		public String getDescription() {
			return String.format("Test output dataset source [%s]", m_name);
		}

		void assertWriteCalled(WriteMode ... modes) {
			List<WriteMode> modeList = Arrays.asList(modes);
			assertTrue(String.format("Called modes %s on dataset %s", modeList, m_name), m_writeCalls.containsAll(modeList));
		}

		void assertSerializedContentCorrect() {
			assertTrue( String.format("Serialized content for dataset %s", m_name), m_expected.equalsIgnoreFileContent(m_written));
		}

		void assertFileContentCorrect() throws IOException {
			for ( DataKey key : m_expected.getAllFileKeys() ) {
				DataFile file  = m_expected.getFile(key);
				BytesDataFileSink sink = m_sinks.get(key);
				String msg = String.format("File content for key %s in dataset %s", key, m_name);
				assertNotNull(msg, sink);
				assertTrue(msg, Arrays.equals(file.getBytes(), sink.getWrittenBytes()));
			}
		}
		
	}

	private class TestAlgorithm extends AlgorithmBase {

		public void init() {
			System.out.println("Called init!!!!!");
			AlgorithmSchema schema = new AlgorithmSchema();
			for ( String input : m_inputs.keySet() ) {
				schema.addInputDataset(input);
			}
			for ( String output : m_outputs.keySet() ) {
				schema.addOutputDataset(output);
			}
			for ( DataKeyDimension dim : m_keyspace.getDimensions() ) {
				schema.addExpectedDimension(dim);
			}
			setAlgorithmSchema(schema);
		}

		public void cleanup() {}

		public void execute(AlgorithmData data, AlgorithmContext context) throws IOException {
			for ( String name : m_outputs.keySet() ) {
				Dataset dataset = data.getOutputDataset(name);
				populateDataset(dataset, name, m_keys);
			}
		}

		public Collection<File> getRequiredFiles() {
			return Collections.emptyList();
		}

		public Collection<File> getGeneratedLogFiles() {
			return Collections.<File>emptySet();
		}

	}
}
