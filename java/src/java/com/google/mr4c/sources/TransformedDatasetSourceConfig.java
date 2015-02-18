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

import com.google.mr4c.config.ConfigDescriptor;
import com.google.mr4c.config.execution.MapConfig;
import com.google.mr4c.content.ContentFactories;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.ConfigSerializer;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.serialize.param.ParameterizedConfigSerializer;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformedDatasetSourceConfig {

	protected static final Logger s_log = MR4CLogging.getLogger(TransformedDatasetSourceConfig.class);

	public static MapConfig load(ConfigDescriptor descriptor) throws IOException {
		if ( descriptor.hasFile() ) {
			return loadFromFile(descriptor);
		} else if ( descriptor.hasInline() ) {
			return loadInline(descriptor);
		} else if ( descriptor.hasName() ) {
			throw new IllegalArgumentException("Named configs not supported yet");
		} else {
			throw new IllegalArgumentException("ConfigDescriptor is empty");
		}
	}
	
	private static MapConfig loadFromFile(ConfigDescriptor descriptor) throws IOException {
		s_log.info("Loading dataset mapping config from [{}]", descriptor.getConfigFile());
		return loadFromContent(descriptor);
	}

	private static MapConfig loadInline(ConfigDescriptor descriptor) throws IOException {
		s_log.info("Dataset mapping config is inline");
		return loadFromContent(descriptor);
	}

	private static MapConfig loadFromContent(ConfigDescriptor descriptor) throws IOException {
		Reader reader = descriptor.getContent();
		try {
			return load(reader);
		} finally {
			reader.close();
		}
	}

	public static MapConfig load(Reader reader) throws IOException {
		ConfigSerializer ser = SerializerFactories.getSerializerFactory("application/json").createConfigSerializer(); // assume json config for now
		ser = new ParameterizedConfigSerializer(ser);
		return ser.deserializeMapConfig(reader);
	}

}

