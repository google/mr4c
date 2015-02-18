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

#ifndef __ALGORITHM_DATA_TEST_UTIL_H__
#define __ALGORITHM_DATA_TEST_UTIL_H__

#include "algorithm/algorithm_api.h"
#include "dataset/DatasetTestUtil.h"
#include "keys/KeyspaceTestUtil.h"

namespace MR4C {

class AlgorithmDataTestUtil {


	public:

		AlgorithmDataTestUtil();

		AlgorithmData* buildAlgorithmData1();

		AlgorithmData* buildAlgorithmData2();

		AlgorithmConfig* buildAlgorithmConfig1();

		AlgorithmConfig* buildAlgorithmConfig2();


	private:

		DatasetTestUtil m_datasetUtil;
		KeyspaceTestUtil m_keyspaceUtil;

};

}

#endif
