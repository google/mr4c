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

import com.google.mr4c.algorithm.Algorithm;
import com.google.mr4c.algorithm.AlgorithmContext;
import com.google.mr4c.algorithm.AlgorithmData;
import com.google.mr4c.algorithm.AlgorithmSchema;
import com.google.mr4c.algorithm.LogLevel;
import com.google.mr4c.config.MR4CRunnerConfig;
import com.google.mr4c.config.ConfigUtils;
import com.google.mr4c.config.algorithm.AlgorithmConfig;
import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.Category;
import com.google.mr4c.config.execution.ExecutionConfig;
import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.dataset.DatasetContext;
import com.google.mr4c.dataset.LogsDatasetBuilder;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyFilter;
import com.google.mr4c.keys.Keyspace;
import com.google.mr4c.message.Message;
import com.google.mr4c.message.Messages;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary;
import com.google.mr4c.sources.ConfiguredExecutionSource;
import com.google.mr4c.sources.DataFileSink;
import com.google.mr4c.sources.DatasetSource;
import com.google.mr4c.sources.DatasetSource.SourceType;
import com.google.mr4c.sources.DatasetSource.WriteMode;
import com.google.mr4c.sources.ExecutionSource;
import com.google.mr4c.stats.MR4CStats;
import com.google.mr4c.stats.StatsClient;
import com.google.mr4c.stats.StatsTimer;
import com.google.mr4c.util.MR4CLogging;
import com.google.mr4c.util.CollectionUtils;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlgoRunner {

	protected static final Logger s_log = MR4CLogging.getLogger(AlgoRunner.class);

	private ExecutionSource m_exeSrc;
	private MR4CConfig m_bbConf;
	private AlgorithmData m_algoData = new AlgorithmData();
	private AlgorithmData m_logData = new AlgorithmData();
	private boolean m_failed = false;
	private String m_failureMsg;

	public static void main(String argv[]) throws Exception {
		AlgoRunnerConfig config = new AlgoRunnerConfig(true);
		config.setCommandLineArguments(argv);
		config.configure();
		AlgoRunner runner = new AlgoRunner(config);
		runner.executeStandalone();
	}

	public AlgoRunner(AlgoRunnerConfig runnerConfig) throws IOException {
		this(
			runnerConfig.getSource(),
			runnerConfig.getMR4CConfig()
		);
	}

	public AlgoRunner(ExecutionSource exeSrc) throws IOException {
		this(
			exeSrc,
			MR4CConfig.getDefaultInstance()
		);
	}

	public AlgoRunner(ExecutionSource exeSrc, MR4CConfig bbConf) throws IOException {
		m_exeSrc = exeSrc;
		m_bbConf = bbConf;
		validateExecutionSource();
	}

	private void validateExecutionSource() throws IOException {
		AlgorithmSchema algoSchema = m_exeSrc.getAlgorithm().getAlgorithmSchema();
		
		validateDatasetNames(
			"Input",
			new HashSet<String>(algoSchema.getRequiredInputDatasets()),
			new HashSet<String>(algoSchema.getOptionalInputDatasets()),
			m_exeSrc.getInputDatasetNames()
		);
		validateDatasetNames(
			"Output",
			Collections.<String>emptySet(), // all outputs are optional
			new HashSet<String>(algoSchema.getOutputDatasets()),
			m_exeSrc.getOutputDatasetNames(SourceType.DATA) // don't want log datasets here
		);
		ensureOutputsExist();
	}

	private void validateDatasetNames(String type, Set<String> requiredNames, Set<String> optionalNames, Set<String> datasetNames) {
		Set<String> allNames = new HashSet<String>();
		allNames.addAll(requiredNames);
		allNames.addAll(optionalNames);
		if ( !datasetNames.containsAll(requiredNames) ) {
			throw new RuntimeException(String.format("%s dataset mismatch: missing dataset(s): algo requires [%s]; exe config provides [%s]", type, requiredNames, datasetNames));
		}
		if ( !allNames.containsAll(datasetNames) ) {
			throw new RuntimeException(String.format("%s dataset mismatch: extra dataset(s): algo expects [%s]; exe config provides [%s]", type, allNames, datasetNames));
		}
		
	}

	private void ensureOutputsExist() throws IOException {
		for ( String name : m_exeSrc.getOutputDatasetNames() ) {
			DatasetSource src = m_exeSrc.getOutputDatasetSource(name);
			src.ensureExists();
		}
	}

	public void executeStandalone() throws IOException {
		loadInputData();
		executeAlgorithm(buildContext());
		assertSuccess();
		saveOutputData(WriteMode.ALL);
		copyOutputToFinal();
		buildLogsDatasets();
		saveLogs(WriteMode.ALL);
		copyLogsToFinal();
		cleanupAlgorithm();
	}

	public ExecutionSource getExecutionSource() {
		return m_exeSrc;
	}

	public AlgorithmConfig getAlgorithmConfig() {
		return m_exeSrc.getAlgorithmConfig();
	}

	public Algorithm getAlgorithm() throws IOException {
		return m_exeSrc.getAlgorithm();
	}

	public AlgorithmData getAlgorithmData() {
		return m_algoData;
	}

	public AlgorithmData getLogsData() {
		return m_logData;
	}

	private AlgorithmContext buildContext() {
		return new AlgorithmContext() {
			public void log(LogLevel level, String msg) {
				s_log.debug("Logging at level {} : {}", level, msg);
			}

			public void progress(float percentDone, String msg) {
				s_log.info("Progress reported: {}% done : {}", percentDone, msg);
			}

			public void failure(String msg) {
				m_failed = true;
				m_failureMsg = msg;
				s_log.error("Algorithm failed with message [{}]", msg);
			}

			public boolean isFailed() {
				return m_failed;
			}

			public String getEnvironmentDescription() {
				return "Standalone";
			}

			public void sendMessage(Message msg) {
				Messages.handleMessage(msg);
			}

		};
	}



	public void loadInputData() throws IOException {
		loadInputDatasets();
		for ( String excluded : m_exeSrc.getAlgorithm().getAlgorithmSchema().getExcludedInputDatasets() ) {
			s_log.info("Excluding input dataset [{}] from keyspace", excluded);
			m_algoData.excludeInputDatasetFromKeyspace(excluded);
		}
		m_algoData.generateKeyspaceFromInputDatasets();
		validateKeyspace();
		addAlgorithmParameters();
		prepOutputDatasets();
	}

	private void loadInputDatasets() throws IOException {
		for ( String inputName : m_exeSrc.getInputDatasetNames() ) {
			s_log.info("Begin loading input dataset [{}]", inputName);
			DatasetSource datasetSource = m_exeSrc.getInputDatasetSource(inputName);
			Dataset dataset = datasetSource.readDataset();
			dataset.setContext(new InputDatasetContext(datasetSource));
			int fileKeyCount = dataset.getAllFileKeys().size();
			int metaKeyCount = dataset.getAllMetadataKeys().size();
			s_log.info("Input dataset [{}] has {} file keys and {} metadata keys", new Object[] {inputName, fileKeyCount, metaKeyCount});
			s_log.info("End loading input dataset [{}]", inputName);
			m_algoData.addInputDataset(inputName, dataset);
		}
	}

	private void prepOutputDatasets() throws IOException {
		for ( String outputName : m_exeSrc.getOutputDatasetNames() ) {
			s_log.info("Begin preparing output dataset [{}]", outputName);
			DatasetSource datasetSource = m_exeSrc.getOutputDatasetSource(outputName);
			Dataset dataset = new Dataset();
			dataset.setContext(new OutputDatasetContext(datasetSource));
			s_log.info("End preparing output dataset [{}]", outputName);
			m_algoData.addOutputDataset(outputName, dataset);
		}
	}

	private void validateKeyspace() throws IOException {
		Set<DataKeyDimension> expected = new HashSet<DataKeyDimension>(m_exeSrc.getAlgorithm().getAlgorithmSchema().getExpectedDimensions());
		if ( expected.isEmpty() ) {
			return; // no dimensions specified
		}
		Keyspace keyspace = new Keyspace();
		keyspace.addKeys(m_algoData.getAllInputKeys());
		Set<DataKeyDimension> actual = keyspace.getDimensions();
		if ( !expected.containsAll(actual) ) {
			throw new RuntimeException(String.format("Dimension mismatch: algo expects [%s]; datasets provides [%s]", expected, actual));
		}
	}

	private void addAlgorithmParameters() {
		Properties defaultParams = filterVariables("default", m_exeSrc.getConfigParams());
		Properties algoParams = m_bbConf.getCategory(Category.ALGO).getProperties(false);
		algoParams = ConfigUtils.resolveProperties(algoParams, false);
		algoParams = filterVariables("algorithm,", algoParams);
		m_algoData.getConfig().putAll(defaultParams);
		m_algoData.getConfig().putAll(algoParams);
	}

	private Properties filterVariables(String desc, Properties props) {
		Set<String> unresolved = ConfigUtils.findUnresolvedProperties(props);
		for ( String name : unresolved ) {
			s_log.warn("Ignoring {} parameter [{}]; contains unresolved variable: [{}]", desc, name, props.getProperty(name));
		}
		Properties result = new Properties();
		result.putAll(props);
		CollectionUtils.clearProperties(result, unresolved);
		return result;
	}

	public void slice(DataKeyFilter filter) {
		m_algoData = m_algoData.slice(filter);
	}

	public void executeAlgorithm(AlgorithmContext context) throws IOException {
		String algoName = m_exeSrc.getAlgorithm().getAlgorithmConfig().getName();
		String successName = String.format("mr4c.algorithm.%s.success", algoName);
		String failureName = String.format("mr4c.algorithm.%s.failure", algoName);
		StatsTimer timer = new StatsTimer(
			MR4CStats.getClient(),
			successName,
			failureName
		);
		boolean success = false;
		try {
			doExecuteAlgorithm(context);
			success = true;
		} finally {
			timer.done(success);
		}
	}

	private void doExecuteAlgorithm(AlgorithmContext context) throws IOException {
		s_log.info("Executing algorithm [{}]", m_exeSrc.getAlgorithmConfig().getName());
		m_exeSrc.getAlgorithm().execute(m_algoData, context);
		m_algoData.releaseInputs();
		assertSuccess();
	}

	private void assertSuccess() {
		if ( m_failed ) {
			throw new RuntimeException(String.format("Algorithm failed with message [%s]", m_failureMsg));
		}
	}

	public void saveOutputData(WriteMode writeMode) throws IOException {
		Set<String> fromData = m_algoData.getOutputDatasetNames();
		Set<String> fromSrc = m_exeSrc.getOutputDatasetNames(SourceType.DATA);
		// fromData should have all of fromSrc
		// cases:
		//	data has one we didn't source - OK
		//	we sourced one, not in the data - not OK!!!
		if ( !fromData.containsAll(fromSrc) ) {
			throw new RuntimeException(String.format("Output dataset mismatch: config expects [%s]; algo data provides [%s]", fromSrc, fromData));
		}
		for ( String outputName : fromSrc ) {
			saveOutputDataset(outputName, writeMode);
		}
	}

	public void saveOutputDataset(String name, WriteMode writeMode) throws IOException {
		saveOutputDataset(name, m_algoData.getOutputDataset(name), writeMode);
	}

	public void saveLogsDataset(String name, WriteMode writeMode) throws IOException {
		saveOutputDataset(name, m_logData.getOutputDataset(name), writeMode);
	}

	private void saveOutputDataset(String name, Dataset dataset, WriteMode writeMode) throws IOException {

		s_log.info("Begin saving output dataset [{}]", name);
		int fileKeyCount = dataset.getAllFileKeys().size();
		int metaKeyCount = dataset.getAllMetadataKeys().size();
		s_log.info("Output dataset [{}] has {} file keys and {} metadata keys", new Object[] {name, fileKeyCount, metaKeyCount});
		DatasetSource src = m_exeSrc.getOutputDatasetSource(name);

		boolean writeFiles = writeMode!=WriteMode.SERIALIZED_ONLY;
		boolean writeSerialized = writeMode!=WriteMode.FILES_ONLY;

		if ( writeFiles ) {
			handleRemainingFileWrites(name, src, dataset);
			dataset.release();
		}

		if ( writeSerialized ) {
			src.writeDataset(dataset,WriteMode.SERIALIZED_ONLY);
		}

		s_log.info("End saving output dataset [{}]", name);
	}

	private void handleRemainingFileWrites(String name, DatasetSource src, Dataset dataset) throws IOException {
		for ( DataKey key : dataset.getAllFileKeys() ) {
			DataFile file = dataset.getFile(key);
			if ( file.hasContent() ) {
				 doFileWrite(src, key, file);
			}
		}
	}

	public void copyOutputToFinal() throws IOException {
		for ( String outputName : m_exeSrc.getOutputDatasetNames(SourceType.DATA) ) {
			copyOutputToFinal(outputName);
		}
	}

	public void copyOutputToFinal(String name) throws IOException {
		s_log.info("Begin copying output dataset [{}] to final storage", name);
		DatasetSource src = m_exeSrc.getOutputDatasetSource(name);
		src.copyToFinal();
		s_log.info("End copying output dataset [{}] to final storage", name);
	}

	public void rebuildLogsDatasets() throws IOException {
		m_logData = new AlgorithmData();
		buildLogsDatasets();
	}

	public void buildLogsDatasets() throws IOException {
		for ( String name : m_exeSrc.getOutputDatasetNames(SourceType.LOGS) ) {
			Dataset dataset = buildLogsDataset(name);
			m_logData.addOutputDataset(name, dataset);
		}
	}

	public Dataset buildLogsDataset(String name) throws IOException {
		LogsDatasetBuilder builder = new LogsDatasetBuilder(m_bbConf);
		builder.init();
		builder.addFiles(MR4CLogging.instance().extractLogFiles());
		builder.addFiles(m_exeSrc.getAlgorithm().getGeneratedLogFiles());
		return builder.getDataset();
	}
		
	public void saveLogs(WriteMode writeMode) throws IOException {
		s_log.info("Begin writing logs datasets");
		for ( String name : m_exeSrc.getOutputDatasetNames(SourceType.LOGS) ) {
			saveLogsDataset(name, writeMode);
		}
		s_log.info("End writing logs datasets");
	}

	public void copyLogsToFinal() throws IOException {
		for ( String outputName : m_exeSrc.getOutputDatasetNames(SourceType.LOGS) ) {
			copyOutputToFinal(outputName);
		}
	}

	public void cleanupAlgorithm() throws IOException {
		m_exeSrc.getAlgorithm().cleanup();
	}

	private class InputDatasetContext implements DatasetContext {

		DatasetSource m_src;

		public InputDatasetContext(DatasetSource src) {
			m_src = src;
		}

		public DataFile findFile(DataKey key) throws IOException {
			return m_src.isQueryOnly() ?
				m_src.findDataFile(key) :
				null;
		}

		public boolean isOutput() {
			return false;
		}

		public boolean isQueryOnly() {
			return m_src.isQueryOnly();
		}

		public void addFile(DataKey key, DataFile file) throws IOException {
			throw new IllegalStateException("Not allowed to add file to InputDatasetContext");
		}

		public String getFileName(DataKey key) throws IOException {
			DataFileSink fileSink = m_src.getDataFileSink(key);
			return fileSink.getFileName();
		}

	}

	private class OutputDatasetContext implements DatasetContext {

		DatasetSource m_src;

		public OutputDatasetContext(DatasetSource src) {
			m_src = src;
		}

		public DataFile findFile(DataKey key) throws IOException {
			throw new UnsupportedOperationException("Find file not supported for output datasets");
		}

		public boolean isOutput() {
			return true;
		}

		public boolean isQueryOnly() {
			return false;
		}

		public void addFile(DataKey key, DataFile file) throws IOException {
			DataFileSink fileSink = m_src.getDataFileSink(key);
			if ( file.getFileSource()!=null ) {
				fileSink.writeFile(file.getFileSource().getFileInputStream());
				file.release();
			} else {
				file.setFileSink(fileSink);
			}
		}

		public String getFileName(DataKey key) throws IOException {
			DataFileSink fileSink = m_src.getDataFileSink(key);
			return fileSink.getFileName();
		}

	}

	private void doFileWrite(DatasetSource src, DataKey key, DataFile file) throws IOException {
		DataFileSink fileSink = src.getDataFileSink(key);
		fileSink.writeFile(file.getBytes());
	}

	public static class AlgoRunnerConfig extends MR4CRunnerConfig<ExecutionSource> {

		public AlgoRunnerConfig(boolean dumpToFiles) {
			super(s_log, dumpToFiles);
		}

		protected ExecutionSource loadSource(URI confFile) throws IOException {
			ConfiguredExecutionSource src = new ConfiguredExecutionSource(confFile);
			src.loadConfigs();
			return src;
		}

	}
}
