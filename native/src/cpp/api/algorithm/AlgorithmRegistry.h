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

#ifndef __MR4C_ALGORITHM_REGISTRY_H__
#define __MR4C_ALGORITHM_REGISTRY_H__

#include <string>
#include "Algorithm.h"

namespace MR4C {

class AlgorithmRegistryImpl;

/**
  * Singleton that holds a mapping of names to algorithms
*/

class AlgorithmRegistry {

	public:

		static AlgorithmRegistry& instance();

		void registerAlgorithm(const std::string& name, Algorithm* algorithm);

		bool hasAlgorithm(const std::string& name) const;

		Algorithm* getAlgorithm(const std::string& name) const;

		~AlgorithmRegistry();

	private:

		AlgorithmRegistryImpl* m_impl;

		AlgorithmRegistry();
		AlgorithmRegistry(const AlgorithmRegistry& reg);
		AlgorithmRegistry& operator=(const AlgorithmRegistry& reg);

};

}

#endif
