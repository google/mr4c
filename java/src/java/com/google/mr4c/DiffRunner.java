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

package com.google.mr4c;

import com.google.mr4c.config.MR4CRunnerConfig;
import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.diff.DiffConfig;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.dataset.DatasetDiff;
import com.google.mr4c.sources.ConfiguredDiffSource;
import com.google.mr4c.sources.DatasetSource;
import com.google.mr4c.sources.DiffSource;
import com.google.mr4c.sources.DiffSource.DiffOutput;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;

public class DiffRunner {

	protected static final Logger s_log = MR4CLogging.getLogger(DiffRunner.class);

	private DiffSource m_diffSrc;
	private MR4CConfig m_bbConf;
	private DatasetDiff m_diff;
	private Dataset m_expected;
	private Dataset m_actual;
	private boolean m_minOut;

	public static void main(String argv[]) throws Exception {
		DiffRunnerConfig config = new DiffRunnerConfig(true);
		config.setCommandLineArguments(argv);
		config.configure();
		DiffRunner runner = new DiffRunner(config);
		runner.execute();
	}

	public DiffRunner(DiffRunnerConfig runnerConfig) throws IOException {
		this(
			runnerConfig.getSource(),
			runnerConfig.getMR4CConfig()
		);
	}

	public DiffRunner(DiffSource diffSrc) throws IOException {
		this(
			diffSrc,
			MR4CConfig.getDefaultInstance()
		);
	}

	public DiffRunner(DiffSource diffSrc, MR4CConfig bbConf) throws IOException {
		m_diffSrc = diffSrc;
		m_bbConf = bbConf;
		validateDiffSource();
	}

	public void setMinimalOutput(boolean minOut) {
		m_minOut = minOut;
	}

	private void validateDiffSource() throws IOException {
		//ensureOutputsExist();
	}

	private void ensureOutputsExist() throws IOException {
		for ( DiffOutput output : DiffOutput.values() ) {
			DatasetSource src = m_diffSrc.getOutputDatasetSource(output);
			src.ensureExists();
		}
	}

	public void execute() throws IOException {
		loadInputData();
		m_diff = new DatasetDiff(m_expected, m_actual);
		m_diff.computeDiff();
		saveOutputData();
	}

	public DatasetDiff getDiff() {
		return m_diff;
	}

	public void loadInputData() throws IOException {
		m_expected = loadInputDataset("expected", m_diffSrc.getExpectedDatasetSource());
		m_actual = loadInputDataset("actual", m_diffSrc.getActualDatasetSource());

	}

	private Dataset loadInputDataset(String name, DatasetSource src) throws IOException {
		s_log.info("Begin loading {} dataset", name);
		Dataset dataset = src.readDataset();
		int fileKeyCount = dataset.getAllFileKeys().size();
		int metaKeyCount = dataset.getAllMetadataKeys().size();
		s_log.info("{} dataset has {} file keys and {} metadata keys", new Object[] {name, fileKeyCount, metaKeyCount});
		s_log.info("End loading {} dataset", name);
		return dataset;
	}

	public void saveOutputData() throws IOException {
		if ( m_minOut && !m_diff.different() ) {
			s_log.info("No differences found, no datasets will be written");
			return;
		}
		saveOutputDataset(m_diff.getSame(), DiffOutput.SAME);
		saveOutputDataset(m_diff.getOnlyInDataset1(), DiffOutput.EXPECTED_ONLY);
		saveOutputDataset(m_diff.getOnlyInDataset2(), DiffOutput.ACTUAL_ONLY);
		saveOutputDataset(m_diff.getDifferentInDataset1(), DiffOutput.DIFF_EXPECTED);
		saveOutputDataset(m_diff.getDifferentInDataset2(), DiffOutput.DIFF_ACTUAL);
	}

	public void saveOutputDataset(Dataset dataset, DiffOutput output) throws IOException {
		s_log.info("Begin saving output dataset [{}]", output);
		int fileKeyCount = dataset.getAllFileKeys().size();
		int metaKeyCount = dataset.getAllMetadataKeys().size();
		s_log.info("Output dataset [{}] has {} file keys and {} metadata keys", new Object[] {output, fileKeyCount, metaKeyCount});
		if ( dataset.isEmpty() ) {
			s_log.info("Output dataset [{}] has no keys, nothing will be written", output);
			return;
		}
		DatasetSource src = m_diffSrc.getOutputDatasetSource(output);
		src.writeDataset(dataset);
		dataset.release(); 
		s_log.info("End saving output dataset [{}]", output);
	}

	public static class DiffRunnerConfig extends MR4CRunnerConfig<DiffSource> {

		public DiffRunnerConfig(boolean dumpToFiles) {
			super(s_log, dumpToFiles);
		}

		protected DiffSource loadSource(URI confFile) throws IOException {
			ConfiguredDiffSource src = new ConfiguredDiffSource(confFile);
			src.loadConfig();
			return src;
		}

	}

}
