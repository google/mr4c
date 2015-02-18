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

import com.google.mr4c.algorithm.Algorithm;
import com.google.mr4c.algorithm.AlgorithmBase;
import com.google.mr4c.algorithm.AlgorithmContext;
import com.google.mr4c.algorithm.AlgorithmData;
import com.google.mr4c.algorithm.AlgorithmSchema;
import com.google.mr4c.algorithm.EnvironmentSet;
import com.google.mr4c.config.algorithm.AlgorithmConfig;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.serialize.PropertiesSerializer;
import com.google.mr4c.util.PathUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

public abstract class NativeAlgorithm extends AlgorithmBase implements Algorithm {

	protected ExternalAlgorithmSerializer m_algoSerializer;
	protected ExternalAlgorithmDataSerializer m_dataSerializer;
	protected PropertiesSerializer m_propsSerializer;
	protected ExternalEntry m_entry;
	protected ExternalFactory m_factory;

	public NativeAlgorithm(
		ExternalAlgorithmSerializer algoSerializer,
		ExternalAlgorithmDataSerializer dataSerializer,
		PropertiesSerializer propsSerializer,
		ExternalFactory factory
	) {
		m_algoSerializer = algoSerializer;
		m_dataSerializer = dataSerializer;
		m_propsSerializer = propsSerializer;
		m_factory = factory;
		m_entry = m_factory.newEntry();
	}

	public void init() {
		pushEnvironment();
		loadNativeLibraries();
		loadAlgorithm();
	}

	public void cleanup() {
		m_entry.deleteLocalTempFiles();
	}

	protected void pushEnvironment() {
		// for each env set, serialize, pass via entry
		for ( EnvironmentSet envSet : EnvironmentSet.values() ) {
			Properties props = getAlgorithmEnvironment().getPropertySet(envSet);
			if ( props==null ) continue;
			StringWriter sw = new StringWriter();
			try {
				m_propsSerializer.serializeProperties(props, sw);
			} catch ( IOException ioe ) {
				throw new IllegalStateException(ioe);
			}
			m_entry.pushEnvironmentProperties(envSet, sw.toString());
		}
	}

	protected abstract void loadNativeLibraries();

	protected void loadAlgorithm() {
		try {
			ExternalAlgorithm extAlgo = m_entry.getAlgorithm(getAlgorithmConfig().getName());
			AlgorithmSchema algoSchema = m_algoSerializer.deserializeAlgorithm(extAlgo);
			setAlgorithmSchema(algoSchema);
		} catch (IOException ioe) {
			throw new IllegalStateException(ioe);
		}
	}

	public void execute(AlgorithmData data, AlgorithmContext context) throws IOException {
		ExternalAlgorithmData extData = m_factory.newAlgorithmData();
		m_dataSerializer.serializeInputData(data, extData);
		m_dataSerializer.serializeOutputData(data, extData);
		ExternalContext extContext = m_factory.newContext(context);
		m_entry.executeAlgorithm(getAlgorithmConfig().getName(), extData, extContext);
		if ( !context.isFailed() ) {
			m_dataSerializer.deserializeOutputData(data,extData);
		}
	}

	public Collection<File> getGeneratedLogFiles() {
		Collection<String> paths = m_entry.getLogFiles();
		List<File> files = new ArrayList<File>();
		for ( String path : paths ) {
			files.add(new File(path));
		}
		return files;
	}
	
	/**
	  * ensures a sufficiently populated LD_LIBRARY_PATH will be pushed into the native environment
	*/
	public static String generateLibraryPath() {
		String jnaPath = System.getProperty("jna.library.path");
		if ( jnaPath==null ) jnaPath="";
		String ldPath = System.getenv("LD_LIBRARY_PATH");
		if ( ldPath==null ) ldPath="";
		String sep = System.getProperty("path.separator");
		return PathUtils.prependMissingPathElements(ldPath, jnaPath, sep);
	}

}

