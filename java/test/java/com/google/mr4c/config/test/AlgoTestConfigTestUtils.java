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

package com.google.mr4c.config.test;

import com.google.mr4c.config.ConfigDescriptor;
import com.google.mr4c.config.diff.DiffConfigTestUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public abstract class AlgoTestConfigTestUtils {


	
	public static AlgoTestConfig buildAlgoTestConfig1() throws URISyntaxException {
		ConfigDescriptor exeSrcConfig = new ConfigDescriptor("exeConfig1");
		AlgoTestConfig config =  new AlgoTestConfig(exeSrcConfig);

		config.addOutputDiff("output1", DiffConfigTestUtils.buildDiffConfig1());
		config.addOutputDiff("output2", DiffConfigTestUtils.buildDiffConfig2());
		return config;
	}

	public static AlgoTestConfig buildAlgoTestConfig2() throws URISyntaxException {
		ConfigDescriptor exeSrcConfig = new ConfigDescriptor("exeConfig2");
		AlgoTestConfig config =  new AlgoTestConfig(exeSrcConfig);

		config.addOutputDiff("output1", DiffConfigTestUtils.buildDiffConfig2());
		config.addOutputDiff("output2", DiffConfigTestUtils.buildDiffConfig1());
		return config;
	}

}
