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

package com.google.mr4c.config.algorithm;

import com.google.mr4c.algorithm.AlgorithmType;

public abstract class AlgoConfigTestUtils {

	public static AlgorithmConfig buildAlgorithmConfig1() {
		AlgorithmConfig config = new AlgorithmConfig("algo1", "desc 1", AlgorithmType.NATIVEC, "lib1");
		config.addInputDataset("input1", true);
		config.addInputDataset("input2", true);
		config.addOutputDataset("output");
		config.addDimension(buildDimensionConfig1());
		return config;
	}

	public static AlgorithmConfig buildAlgorithmConfig2() {
		AlgorithmConfig config = new AlgorithmConfig("algo2", "desc 2", AlgorithmType.NATIVEC, "lib2");
		config.addInputDataset("input1", true);
		config.addInputDataset("input2", true);
		config.addOutputDataset("output1");
		config.addOutputDataset("output2");
		config.addDimension(buildDimensionConfig1());
		return config;
	}

	public static DimensionConfig buildDimensionConfig1() {
		DimensionConfig config = new DimensionConfig("dim1", true, 1, 2, null, null, false);
		return config;
	}

	public static DimensionConfig buildDimensionConfig2() {
		DimensionConfig config = new DimensionConfig("dim2", false, 1, 2, 10, 5, false);
		return config;
	}

}
