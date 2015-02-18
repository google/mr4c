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

#ifndef __MR4C_LOGGER_H__
#define __MR4C_LOGGER_H__

#include <string>

namespace MR4C {

/**
  * Class to extend to receive log messages
*/

class Logger {

	public:

		enum LogLevel {
			INFO,
			ERROR,
			DEBUG,
			WARN 
		};

		/**
		  * Parses the string equivalent of the enum.
		  * For example: "KEY" --> KEY
		*/
		static LogLevel enumFromString(std::string strLevel);

		/**
		  * Returns the string equivalent of the enum.
		  * For example: KEY --> "KEY"
		*/
		static std::string enumToString(LogLevel level);

		virtual void log(LogLevel level, const std::string& msg) =0;

		/**
		  * Default implementation ignores file and line
		*/
		virtual void log(const std::string& file, int line, LogLevel level, const std::string& msg);

};

}

#endif
