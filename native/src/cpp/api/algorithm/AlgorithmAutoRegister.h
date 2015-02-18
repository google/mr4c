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

#ifndef __MR4C_ALGORITHM_AUTO_REGISTER_H__
#define __MR4C_ALGORITHM_AUTO_REGISTER_H__

#include <string>
#include "Algorithm.h"
#include "AlgorithmRegistry.h"

/**
  * Macro to simplfy auto-registration.  For example:
  *     - MR4C_REGISTER_ALGORITHM(myexample, ExampleAlgorithm::create());
  *     .
  * @param name  The name to register the algorithm with.  This will also be
  * used to generate a uniquely named global instance of AlgorithmAutoRegister.
  * @param algoPtr Can be any expression that evaluates to Algorithm*.  A static
  * "create()" method is suggested.
*/

#define MR4C_REGISTER_ALGORITHM(name,algoPtr) \
AlgorithmAutoRegister mr4c_algo_auto_register_##name(#name,algoPtr)

namespace MR4C {

/**
  * Registers an algorithm upon instantiation.
  * Declaring an instance as a global variable will register the algorithm
  * when its library loads
*/

class AlgorithmAutoRegister {

	public:

		AlgorithmAutoRegister(const std::string& name, Algorithm* algorithm);

		~AlgorithmAutoRegister();

	private:

		AlgorithmAutoRegister();
		AlgorithmAutoRegister(const AlgorithmAutoRegister& reg);
		AlgorithmAutoRegister& operator=(const AlgorithmAutoRegister& reg);

};

}

#endif
