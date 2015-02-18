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

#ifndef __MR4C_EXTERNAL_ALGORITHM_H__
#define __MR4C_EXTERNAL_ALGORITHM_H__

#include <cstring>

#include "algorithm/algorithm_api.h"

namespace MR4C {


class ExternalAlgorithmImpl;

class ExternalAlgorithm {

	public:

		ExternalAlgorithm(const char* name);
	
		const char* getName() const;

		const char* getSerializedAlgorithm() const;

		void setSerializedAlgorithm(const char* serializedAlgo);

		~ExternalAlgorithm();

	private:

		ExternalAlgorithmImpl* m_impl;

		// prevent calling these
		ExternalAlgorithm(const ExternalAlgorithm& file);
		ExternalAlgorithm& operator=(const ExternalAlgorithm& file);

};

}
#endif



