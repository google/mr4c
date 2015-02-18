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

#ifndef __MR4C_MR4C_ENVIRONMENT_H__
#define __MR4C_MR4C_ENVIRONMENT_H__

#include <string>
#include "util/Properties.h"

namespace MR4C {

class MR4CEnvironmentImpl;

/**
  * Singleton for managing environmental properties
*/

class MR4CEnvironment {

	public :

		enum EnvSet {

			/** MR4C runtime properties: mr4c.runtime.XXX */
			RUNTIME,

			/** Java system properties: mr4c.java.XXX */
			JAVA,

			/** Custom properties: mr4c.custom.XXX */
			CUSTOM,

			/** Raw properties: XXX */
			RAW
		};

		static MR4CEnvironment& instance();

		/**
		  * Parses the string equivalent of the enum.
		  * For example:  "JAVA" --> JAVA
		*/
		static EnvSet enumFromString(const std::string& strEnvSet);

		/**
		  * Returns the string equivalent of the enum.
		  * For example:  JAVA --> "JAVA"
		*/
		static std::string enumToString(EnvSet envSet);

		void addPropertySet(EnvSet envSet, const Properties& props);

		Properties getProperties(EnvSet envSet) const;

		static std::string getVariablePrefix(EnvSet envSet);

		static std::string getVariableName(EnvSet envSet, const std::string& name);

		~MR4CEnvironment();

	private:

		MR4CEnvironmentImpl* m_impl;

		MR4CEnvironment();
		MR4CEnvironment(const MR4CEnvironment& reg);
		MR4CEnvironment& operator=(const MR4CEnvironment& reg);

};

}

#endif
