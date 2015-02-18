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

package com.google.mr4c.algorithm;

import com.google.mr4c.config.ConfigDescriptor;
import com.google.mr4c.config.algorithm.AlgorithmConfig;
import com.google.mr4c.algorithm.AlgorithmEnvironment;
import com.google.mr4c.content.ContentFactories;
import com.google.mr4c.nativec.ExternalAlgorithmDataSerializer;
import com.google.mr4c.nativec.ExternalAlgorithmSerializer;
import com.google.mr4c.nativec.ExternalFactory;
import com.google.mr4c.nativec.jna.JnaExternalFactory;
import com.google.mr4c.nativec.jna.JnaNativeAlgorithm;
import com.google.mr4c.serialize.ConfigSerializer;
import com.google.mr4c.serialize.PropertiesSerializer;
import com.google.mr4c.serialize.SerializerFactory;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.serialize.param.ParameterizedConfigSerializer;
import com.google.mr4c.util.MR4CLogging;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.Map;
import java.util.HashMap;

import org.apache.hadoop.fs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Algorithms {

	protected static final Logger s_log = MR4CLogging.getLogger(Algorithms.class);

	private static Map<AlgorithmType,Factory> s_factories = new HashMap<AlgorithmType,Factory>();
	// assume all serialization is json for now
	private static SerializerFactory s_serFact = SerializerFactories.getSerializerFactory("application/json");

	public static AlgorithmConfig getAlgorithmConfig(ConfigDescriptor descriptor) throws IOException {
		if ( descriptor.hasFile() ) {
			return getAlgorithmConfigFromFile(descriptor);
		} else if ( descriptor.hasInline() ) {
			return getAlgorithmConfigInline(descriptor);
		} else if ( descriptor.hasName() ) {
			throw new IllegalArgumentException("Naming known algorithms not supported yet");
		} else {
			throw new IllegalArgumentException("ConfigDescriptor is empty");
		}
	}

	private static AlgorithmConfig getAlgorithmConfigFromFile(ConfigDescriptor descriptor) throws IOException {
		s_log.info("Reading algorithm config from [{}]", descriptor.getConfigFile());
		return getAlgorithmConfigFromContent(descriptor);
	}

	private static AlgorithmConfig getAlgorithmConfigInline(ConfigDescriptor descriptor) throws IOException {
		s_log.info("Algorithm config is inline");
		return getAlgorithmConfigFromContent(descriptor);
	}

	private static AlgorithmConfig getAlgorithmConfigFromContent(ConfigDescriptor descriptor) throws IOException {
		Reader reader = descriptor.getContent();
		try {
			return getAlgorithmConfig(reader);
		} finally {
			reader.close();
		}
	}

	public static AlgorithmConfig getAlgorithmConfig(Reader reader) throws IOException {
		ConfigSerializer ser = s_serFact.createConfigSerializer();
		ser = new ParameterizedConfigSerializer(ser);
		return ser.deserializeAlgorithmConfig(reader);
	}

    public static Algorithm getAlgorithm(AlgorithmConfig config, 
                                         AlgorithmEnvironment env ) throws IOException {
		AlgorithmType type = config.getType();
		Factory factory = s_factories.get(type);
		if ( factory==null ) {
			throw new IllegalArgumentException("No algorithm factory for type=["+type+"]");
		}
		Algorithm algo = factory.create(config);
		algo.setAlgorithmConfig(config);
		algo.setAlgorithmEnvironment( env );
		algo.init();
		return algo;
    }

	public static Algorithm getAlgorithm(AlgorithmConfig config) throws IOException {
        return getAlgorithm( config, new AlgorithmEnvironment() );
		
	}

	private static interface Factory {
		
		Algorithm create(AlgorithmConfig config);

	}

	private static class JavaFactory implements Factory {

		public Algorithm create(AlgorithmConfig config) {
			try {
				String className = config.getArtifact();
				Class clazz = Class.forName(className);
				return (Algorithm) clazz.newInstance();
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
	}
	static { s_factories.put(AlgorithmType.JAVA, new JavaFactory()); }
		
	private static class NativeFactory implements Factory {

		public Algorithm create(AlgorithmConfig config) {
			ExternalFactory extFactory = new JnaExternalFactory(s_serFact);
			ExternalAlgorithmSerializer algoSerializer = new ExternalAlgorithmSerializer(s_serFact, extFactory);
			ExternalAlgorithmDataSerializer dataSerializer = new ExternalAlgorithmDataSerializer(s_serFact, extFactory);
			PropertiesSerializer propsSerializer = s_serFact.createPropertiesSerializer();
			return new JnaNativeAlgorithm(algoSerializer, dataSerializer, propsSerializer, extFactory);
		}
	}
	static { s_factories.put(AlgorithmType.NATIVEC, new NativeFactory()); }
		
	
	
}

