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
import com.google.mr4c.config.ConfigLoader;
import com.google.mr4c.config.diff.DiffConfig;
import com.google.mr4c.config.execution.DatasetConfig;
import com.google.mr4c.config.test.AlgoTestConfig;
import com.google.mr4c.content.ContentFactories;
import com.google.mr4c.serialize.ConfigSerializer;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.serialize.param.ParameterizedConfigSerializer;
import com.google.mr4c.sources.DiffSource;
import com.google.mr4c.sources.DiffSource.DiffOutput;
import com.google.mr4c.sources.ExecutionSource;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;

public class ConfiguredAlgoTestSource implements AlgoTestSource {

	private ConfigDescriptor m_confFile;
	private AlgoTestConfig m_testConfig;
	private ExecutionSource m_exeSrc;
	private Map<String,DiffSource> m_outputSrcs = new HashMap<String,DiffSource>();

	protected static final Logger s_log = MR4CLogging.getLogger(ConfiguredAlgoTestSource.class);

	public ConfiguredAlgoTestSource(ConfigDescriptor confFile) {
		m_confFile = confFile;
	}

	public ConfiguredAlgoTestSource(URI confFile) {
		this(new ConfigDescriptor(confFile));
	}

	public void loadConfig() throws IOException {
		ConfigSerializer ser = SerializerFactories.getSerializerFactory("application/json").createConfigSerializer(); // assume json config for now
		ser = new ParameterizedConfigSerializer(ser);
		ConfigLoader loader = new ConfigLoader(m_confFile, "test config", s_log);
		Reader reader = loader.load();
		try {
			m_testConfig = ser.deserializeAlgoTestConfig(reader);
		} finally {
			reader.close();
		}
	}

	public synchronized ExecutionSource getExecutionSource() throws IOException {
		if ( m_exeSrc==null ) {
			ConfigDescriptor exeConf = m_testConfig.getExecutionConfig();
			if ( exeConf==null ) {
				throw new IllegalArgumentException("No execution config provided");
			}
			ConfiguredExecutionSource exeSrc = new ConfiguredExecutionSource(exeConf);
			exeSrc.loadConfigs();
			m_exeSrc = exeSrc;
		}
		return m_exeSrc;	
	}

	public synchronized DiffSource getOutputDiffSource(String name) throws IOException {
		DiffSource src = m_outputSrcs.get(name);
		if ( src==null ) {
			DiffConfig diffConfig = m_testConfig.getOutputDiff(name);
			if ( diffConfig==null ) {
				throw new IllegalArgumentException(String.format("No diff config for output dataset [%s]", name));
			}
			src = new ConfiguredDiffSource(diffConfig);
			src = new CombinedDiffSource(name, src);
			m_outputSrcs.put(name, src);
		}
		return src;
	}

	public Set<String> getOutputDiffNames() {
		return m_testConfig.getOutputDiffNames();
	}

	private class CombinedDiffSource implements DiffSource {

		private String m_name;
		private DiffSource m_diffSrc;

		private CombinedDiffSource(String name, DiffSource diffSrc) {
			m_name = name;
			m_diffSrc = diffSrc;
		}

		public DatasetSource getExpectedDatasetSource() throws IOException {
			return m_diffSrc.getExpectedDatasetSource();
		}

		public DatasetSource getActualDatasetSource() throws IOException {
			return getExecutionSource().getOutputDatasetSource(m_name);
		}

		public DatasetSource getOutputDatasetSource(DiffOutput output) throws IOException {
			return m_diffSrc.getOutputDatasetSource(output);
		}

	}
}
