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

#include "algorithm/AlgorithmDataTestUtil.h"
#include "algorithm/algorithm_api.h"
#include "dataset/dataset_api.h"

namespace MR4C {

AlgorithmDataTestUtil::AlgorithmDataTestUtil() {}

AlgorithmData* AlgorithmDataTestUtil::buildAlgorithmData1() {
	Dataset* input1 = m_datasetUtil.buildDataset1();
	Dataset* input2 = m_datasetUtil.buildDataset2();
	Dataset* output = m_datasetUtil.buildDataset2();
	Keyspace* keyspace = m_keyspaceUtil.buildKeyspace1();
	AlgorithmConfig* config = buildAlgorithmConfig1();
	AlgorithmData* algoData = new AlgorithmData();
	algoData->setKeyspace(*keyspace);
	algoData->setConfig(*config);
	algoData->addInputDataset("input1", input1);
	algoData->addInputDataset("input2", input2);
	algoData->addOutputDataset("output", output);
	return algoData;
}
	
AlgorithmData* AlgorithmDataTestUtil::buildAlgorithmData2() {
	Dataset* input = m_datasetUtil.buildDataset2();
	Dataset* output1 = m_datasetUtil.buildDataset2();
	Dataset* output2 = m_datasetUtil.buildDataset1();
	Keyspace* keyspace = m_keyspaceUtil.buildKeyspace2();
	AlgorithmConfig* config = buildAlgorithmConfig2();
	AlgorithmData* algoData = new AlgorithmData();
	algoData->setKeyspace(*keyspace);
	algoData->setConfig(*config);
	algoData->addInputDataset("input", input);
	algoData->addOutputDataset("output1", output1);
	algoData->addOutputDataset("output2", output2);
	return algoData;
}

AlgorithmConfig* AlgorithmDataTestUtil::buildAlgorithmConfig1() {
	std::map<std::string,std::string> map;
	map["string"]="whatever";
	map["boolean"]="true";
	map["int"]="123";
	map["double"]="4546.789";
	return new AlgorithmConfig(map);
}

AlgorithmConfig* AlgorithmDataTestUtil::buildAlgorithmConfig2() {
	std::map<std::string,std::string> map;
	map["string"]="whatever";
	map["boolean"]="true";
	map["int"]="123";
	map["double"]="4546.789";
	map["yoyo"]="yoyoma";
	return new AlgorithmConfig(map);
}

}

