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

import com.google.mr4c.config.diff.DiffConfig;
import com.google.mr4c.config.execution.DatasetConfig;
import com.google.mr4c.content.ContentFactories;
import com.google.mr4c.serialize.ConfigSerializer;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.serialize.param.ParameterizedConfigSerializer;
import com.google.mr4c.sources.DiffSource.DiffOutput;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

import org.slf4j.Logger;

public class ConfiguredDiffSource implements DiffSource {

	private URI m_confFile;
	private DiffConfig m_diffConfig;
	private DatasetSource m_expected;
	private DatasetSource m_actual;
	private Map<DiffOutput,DatasetSource> m_outputSrcs = new HashMap<DiffOutput,DatasetSource>();

	protected static final Logger s_log = MR4CLogging.getLogger(ConfiguredDiffSource.class);

	public ConfiguredDiffSource(URI confFile) {
		m_confFile = confFile;
	}

	public ConfiguredDiffSource(DiffConfig diffConfig) {
		m_diffConfig = diffConfig;
	}

	public void loadConfig() throws IOException {
		s_log.info("Reading diff config from [{}]", m_confFile);
		ConfigSerializer ser = SerializerFactories.getSerializerFactory("application/json").createConfigSerializer(); // assume json config for now
		ser = new ParameterizedConfigSerializer(ser);

		Reader reader = ContentFactories.readContentAsReader(m_confFile);
		try {
			m_diffConfig = ser.deserializeDiffConfig(reader);
		} finally {
			reader.close();
		}
	}

	public synchronized DatasetSource getExpectedDatasetSource() throws IOException {
		if ( m_expected==null ) {
			DatasetConfig datasetConfig = m_diffConfig.getExpectedDataset();
			if ( datasetConfig==null ) {
				throw new IllegalArgumentException("No source config for expected dataset");
			}
			m_expected = DatasetSources.getDatasetSource(datasetConfig);
		}
		return m_expected;
	}
	
	public synchronized DatasetSource getActualDatasetSource() throws IOException {
		if ( m_actual==null ) {
			DatasetConfig datasetConfig = m_diffConfig.getActualDataset();
			if ( datasetConfig==null ) {
				throw new IllegalArgumentException("No source config for actual dataset");
			}
			m_actual = DatasetSources.getDatasetSource(datasetConfig);
		}
		return m_actual;
	}

	public synchronized DatasetSource getOutputDatasetSource(DiffOutput output) throws IOException {
		DatasetSource src = m_outputSrcs.get(output);
		if ( src==null ) {
			DatasetConfig datasetConfig = m_diffConfig.getDiffDataset();
			if ( datasetConfig==null ) {
				throw new IllegalArgumentException("No source config for diff datasets");
			}
			src = createOutputSource(datasetConfig, output);
			m_outputSrcs.put(output, src);
		}
		return src;
	}

	private DatasetSource createOutputSource(DatasetConfig config, DiffOutput output) throws IOException {
		Map<String,String> props = new HashMap<String,String>();
		props.put(m_diffConfig.getDiffParam(), output.toString());
		ConfigSerializer ser = SerializerFactories.getSerializerFactory("application/json").createConfigSerializer(); // assume json config for now
		ser = new ParameterizedConfigSerializer(ser);
		StringWriter sw = new StringWriter();
		ser.serializeDatasetConfig(config, sw);
		String json = StrSubstitutor.replace(sw.toString(), props, "!(", ")");
		Reader reader = new StringReader(json);
		config = ser.deserializeDatasetConfig(reader);
		return DatasetSources.getDatasetSource(config);
	}

}

