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

import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.dataset.DatasetTestUtils;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyFilter;
import com.google.mr4c.keys.IdentityDataKeyFilter;

import java.net.URISyntaxException;

public abstract class AlgorithmDataTestUtils {


	public static AlgorithmData buildAlgorithmData1() {
		return buildAlgorithmData1Slice(IdentityDataKeyFilter.INSTANCE);
	}

	public static AlgorithmData buildAlgorithmData1Slice(DataKeyFilter filter) {
		Dataset input1 = DatasetTestUtils.buildDataset1();
		Dataset input2 = DatasetTestUtils.buildDataset2();
		Dataset output = DatasetTestUtils.buildDataset2();
		AlgorithmData algoData = new AlgorithmData();
		algoData.addInputDataset("input1", input1.slice(filter));
		algoData.addInputDataset("input2", input2.slice(filter));
		algoData.addOutputDataset("output", output.slice(filter));
		algoData.generateKeyspaceFromInputDatasets();
		algoData.getConfig().setProperty("param1", "val1");
		algoData.getConfig().setProperty("param2", "val2");
		return algoData;
	}

	public static AlgorithmData buildAlgorithmData2() {
		Dataset input = DatasetTestUtils.buildDataset2();
		Dataset output1 = DatasetTestUtils.buildDataset2();
		Dataset output2 = DatasetTestUtils.buildDataset1();
		AlgorithmData algoData = new AlgorithmData();
		algoData.addInputDataset("input", input);
		algoData.addOutputDataset("output1", output1);
		algoData.addOutputDataset("output2", output2);
		algoData.generateKeyspaceFromInputDatasets();
		algoData.getConfig().setProperty("param3", "val3");
		return algoData;
	}

	public static AlgorithmSchema buildAlgorithmSchema() {
		AlgorithmSchema schema = new AlgorithmSchema();
		schema.addInputDataset("input1");
		schema.addInputDataset("input2");
		schema.addInputDataset("input3", true);
		schema.addInputDataset("input4", false, true);
		schema.addInputDataset("input5", true, true);
		schema.addOutputDataset("output1");
		schema.addOutputDataset("output2");
		schema.addExpectedDimension(new DataKeyDimension("dim1"));
		schema.addExpectedDimension(new DataKeyDimension("dim2"));
		return schema;
	}

}
