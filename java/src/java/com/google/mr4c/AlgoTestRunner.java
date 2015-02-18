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
import com.google.mr4c.dataset.DatasetDiff;
import com.google.mr4c.sources.AlgoTestSource;
import com.google.mr4c.sources.ConfiguredAlgoTestSource;
import com.google.mr4c.sources.DiffSource;
import com.google.mr4c.sources.ExecutionSource;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

public class AlgoTestRunner {

	protected static final Logger s_log = MR4CLogging.getLogger(AlgoTestRunner.class);

	private AlgoTestSource m_testSrc;
	private MR4CConfig m_bbConf;
	private Map<String,DatasetDiff> m_diffs = new HashMap<String,DatasetDiff>();
	private boolean m_passed=true;

	public static void main(String argv[]) throws Exception {
		AlgoTestRunnerConfig config = new AlgoTestRunnerConfig(true);
		config.setCommandLineArguments(argv);
		config.configure();
		AlgoTestRunner runner = new AlgoTestRunner(config);
		runner.executeStandalone();
	}

	public AlgoTestRunner(AlgoTestRunnerConfig runnerConfig) throws IOException {
		this(
			runnerConfig.getSource(),
			runnerConfig.getMR4CConfig()
		);
	}

	public AlgoTestRunner(AlgoTestSource testSrc) throws IOException {
		this(
			testSrc,
			MR4CConfig.getDefaultInstance()
		);
	}

	public AlgoTestRunner(AlgoTestSource testSrc, MR4CConfig bbConf) throws IOException {
		m_testSrc = testSrc;
		m_bbConf = bbConf;
	}

	public void executeStandalone() throws IOException {
		execute();
		checkResults();
	}

	public void execute() throws IOException {
		ExecutionSource exeSrc = m_testSrc.getExecutionSource();
		AlgoRunner algoRunner = new AlgoRunner(exeSrc, m_bbConf);
		algoRunner.executeStandalone();
	}

	public void checkResults() throws IOException {
		for ( String name : m_testSrc.getOutputDiffNames() ) {
			DiffSource diffSrc = m_testSrc.getOutputDiffSource(name);
			DiffRunner diffRunner = new DiffRunner(diffSrc);
			diffRunner.setMinimalOutput(true);
			diffRunner.execute();
			DatasetDiff diff = diffRunner.getDiff();
			m_diffs.put(name, diff);
			if ( diff.different() ) {
				m_passed = false;
			}
		}
	}

	public boolean isPassed() {
		return m_passed;
	}

	public Map<String,DatasetDiff> getDiffs() {
		return m_diffs;
	}

	public static class AlgoTestRunnerConfig extends MR4CRunnerConfig<AlgoTestSource> {

		public AlgoTestRunnerConfig(boolean dumpToFiles) {
			super(s_log, dumpToFiles);
		}

		protected AlgoTestSource loadSource(URI confFile) throws IOException {
			ConfiguredAlgoTestSource src = new ConfiguredAlgoTestSource(confFile);
			src.loadConfig();
			return src;
		}

	}

}
