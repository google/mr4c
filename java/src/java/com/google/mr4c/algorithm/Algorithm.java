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

import com.google.mr4c.config.algorithm.AlgorithmConfig;
import com.google.mr4c.keys.DataKeyDimension;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public interface Algorithm {

	AlgorithmConfig getAlgorithmConfig();

	void setAlgorithmConfig(AlgorithmConfig config);

	void setAlgorithmEnvironment(AlgorithmEnvironment env);

	AlgorithmEnvironment getAlgorithmEnvironment();

	AlgorithmSchema getAlgorithmSchema();

	void setAlgorithmSchema(AlgorithmSchema schema);

	void init();

	void execute(AlgorithmData data, AlgorithmContext context) throws IOException;

	void cleanup();

	Collection<File> getRequiredFiles();

	Collection<File> getGeneratedLogFiles();	

}

