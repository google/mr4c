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

#ifndef __MR4C_MR4C_LOGGING_H__
#define __MR4C_MR4C_LOGGING_H__

#include <log4cxx/logger.h>

namespace MR4C {

class MR4CLogging {

	public :

		/**
		  * All MR4C framework classes should call this method to get their logger
		*/
		static log4cxx::LoggerPtr getLogger(const std::string& name);

		/**
		  * Returns the logger to send algorithm log messages
		*/
		static log4cxx::LoggerPtr getAlgorithmLogger(const std::string& name);

		/**
		  * Returns the paths for all log files in the log4cxx configuration
		*/
		static std::set<std::string> extractLogFiles();
};

}

#endif
