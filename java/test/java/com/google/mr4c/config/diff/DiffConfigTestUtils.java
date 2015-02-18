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

package com.google.mr4c.config.diff;

import com.google.mr4c.config.execution.ConfigTestUtils;

import java.net.URISyntaxException;

public abstract class DiffConfigTestUtils {

	public static DiffConfig buildDiffConfig1() throws URISyntaxException {
		DiffConfig diffConfig = new DiffConfig();
		diffConfig.setExpectedDataset(ConfigTestUtils.buildDatasetConfig1());
		diffConfig.setActualDataset(ConfigTestUtils.buildDatasetConfig2());
		diffConfig.setDiffDataset(ConfigTestUtils.buildDatasetConfig3());
		diffConfig.setDiffParam("param1");
		return diffConfig;
	}

	public static DiffConfig buildDiffConfig2() throws URISyntaxException {
		DiffConfig diffConfig = new DiffConfig();
		diffConfig.setExpectedDataset(ConfigTestUtils.buildDatasetConfig1());
		diffConfig.setActualDataset(ConfigTestUtils.buildDatasetConfig2());
		diffConfig.setDiffDataset(ConfigTestUtils.buildDatasetConfig3());
		diffConfig.setDiffParam("param2");
		return diffConfig;
	}

}
