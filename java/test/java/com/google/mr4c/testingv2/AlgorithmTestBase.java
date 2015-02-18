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

package com.google.mr4c.testingv2;

import com.google.mr4c.AlgoTestRunner;
import com.google.mr4c.AlgoTestRunner.AlgoTestRunnerConfig;
import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.Category;
import com.google.mr4c.config.category.CategoryConfig;
import com.google.mr4c.config.category.CategoryBuilder;
import com.google.mr4c.hadoop.MR4CMRJob;
import com.google.mr4c.hadoop.HadoopTestUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class AlgorithmTestBase {


	protected void runTest(URI confFile, Properties runtimeProps, URI ... runtimeFiles) throws IOException {

		AlgoTestRunnerConfig runnerConfig = configure(
			confFile,
			toFileList(runtimeFiles),
			runtimeProps
		);
		
		AlgoTestRunner testRunner = new AlgoTestRunner(runnerConfig);
		testRunner.executeStandalone();
		handleResults(testRunner);

	}

	protected void runMiniMRTest(String name, URI testConfFile, URI exeConfFile, String runtimeFiles, Properties runtimeProps, int numTasks) throws IOException {
		AlgoTestRunnerConfig runnerConfig = configure(
			testConfFile,
			runtimeFiles,
			runtimeProps
		);
		
		MR4CMRJob bbJob = new MR4CMRJob(runnerConfig.getMR4CConfig(), false);
		bbJob.setExecutionConfig(exeConfFile);
		bbJob.setNumTasks(numTasks);

		HadoopTestUtils.runMiniMRJob(name, bbJob);

		AlgoTestRunner testRunner = new AlgoTestRunner(runnerConfig);
		testRunner.checkResults();
		handleResults(testRunner);
	}

	private AlgoTestRunnerConfig configure(URI confFile, String runtimeFiles, Properties runtimeProps) throws IOException {

		if ( runtimeProps==null ) runtimeProps = new Properties();

		// Need to use default instance because that's what json deserializers will use to resolve runtime properties
		MR4CConfig bbConf = MR4CConfig.getDefaultInstance();
		CategoryConfig runtimeConf = bbConf.getCategory(Category.RUNTIME);
		CategoryBuilder builder = new CategoryBuilder(Category.RUNTIME);
		builder.buildStandardCategory(
			runtimeFiles,
			runtimeProps,
			runtimeConf
		);
		runtimeConf.setProperties(builder.getProperties());

		AlgoTestRunnerConfig runnerConfig = new AlgoTestRunnerConfig(false);
		runnerConfig.setMR4CConfig(bbConf);
		runnerConfig.setConfFile(confFile);
		runnerConfig.configure();

		return runnerConfig;
 
	}

	private void handleResults(AlgoTestRunner testRunner) {
		if ( !testRunner.isPassed() ) {
			fail();
		}
	}

	protected void useTestDFS(String rootPropName, Properties runtimeProps) throws IOException {
		String outputRoot = HadoopTestUtils.getTestDFS().getUri().toString();
		runtimeProps.setProperty(rootPropName, outputRoot);
	}

	private String toFileList(URI ... uris) {
		List<String> files = new ArrayList<String>();
		for ( URI uri : uris ) {
			files.add(uri.toString());
		}
		return StringUtils.join(files, ",");
	}
}
