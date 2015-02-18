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

#include <string>
#include <map>
#include <stdexcept>
#include "context/context_api.h"
#include "util/util_api.h"

namespace MR4C {


class LogLevels {

	friend class Logger;

	private:

		std::map<std::string,Logger::LogLevel> m_stringToEnum;
		std::map<Logger::LogLevel,std::string> m_enumToString;

		static LogLevels& instance() {
			static LogLevels s_instance;
			return s_instance;
		}

		LogLevels() {
			mapLevel(Logger::INFO, "INFO");
			mapLevel(Logger::ERROR, "ERROR");
			mapLevel(Logger::DEBUG, "DEBUG");
			mapLevel(Logger::WARN, "WARN");
		}

		// making sure these are private
		LogLevels(const LogLevels& levels);
		LogLevels& operator=(const LogLevels& levels);

		void mapLevel(Logger::LogLevel level, const std::string& strLevel) {
			m_stringToEnum[strLevel] = level;
			m_enumToString[level] = strLevel;
		}

		Logger::LogLevel enumFromString(std::string strLevel) {
			if ( m_stringToEnum.count(strLevel)==0 ) {
				MR4C_THROW( std::invalid_argument, "No log level named [" << strLevel << "]");
			}
			return m_stringToEnum[strLevel];
		}


		std::string enumToString(Logger::LogLevel level) {
			if ( m_enumToString.count(level)==0 ) {
				MR4C_THROW( std::invalid_argument, "No log level numbered [" << level << "]");
			}
			return m_enumToString[level];
		}


	};

Logger::LogLevel Logger::enumFromString(std::string strLevel) {
	return LogLevels::instance().enumFromString(strLevel);
}


std::string Logger::enumToString(Logger::LogLevel level) {
	return LogLevels::instance().enumToString(level);
}

void Logger::log(const std::string& file, int line, LogLevel level, const std::string& msg) {
	log(level,msg);
}

}


