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

#ifndef __MR4C_ALGORITHM_SERIALIZER_H__
#define __MR4C_ALGORITHM_SERIALIZER_H__

#include "algorithm/algorithm_api.h"
#include "Serializer.h"

namespace MR4C {

class AlgorithmSerializer : public Serializer {

	public :

		virtual std::string serializeAlgorithm(const Algorithm& algo) const =0;

		virtual void deserializeAlgorithm(const std::string& serializedAlgo, Algorithm& algo) const =0;

};

}
#endif

