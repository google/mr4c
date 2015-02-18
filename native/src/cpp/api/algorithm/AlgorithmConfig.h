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

#ifndef __MR4C_ALGORITHM_CONFIG_H__
#define __MR4C_ALGORITHM_CONFIG_H__

#include <string>
#include <set>
#include <map>

namespace MR4C {

class AlgorithmConfigImpl;

/**
  * A map of configuration properties passed to an algorithm. Immutable.
  * These are generally parameters that control how an algorithm executes.
  * Examples are scaling factor and jpeg quality. 
*/

class AlgorithmConfig {

	public:

		AlgorithmConfig();

		AlgorithmConfig(const std::map<std::string,std::string>& params);
		AlgorithmConfig(const AlgorithmConfig& config);

		std::set<std::string> getAllParamNames() const;

		bool hasConfigParam(const std::string& name) const;

		std::string getConfigParam(const std::string& name) const;

		bool getConfigParamAsBoolean(const std::string& name) const;

		int getConfigParamAsInt(const std::string& name) const;

		double getConfigParamAsDouble(const std::string& name) const;

		bool operator==(const AlgorithmConfig& config) const;

		bool operator!=(const AlgorithmConfig& config) const;

		AlgorithmConfig& operator=(const AlgorithmConfig& config);

		~AlgorithmConfig();

	private:

		AlgorithmConfigImpl* m_impl;

};

}

#endif
