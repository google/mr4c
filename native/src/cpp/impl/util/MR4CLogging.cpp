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


#include <log4cxx/basicconfigurator.h>
#include <log4cxx/file.h>
#include <log4cxx/logger.h>
#include <log4cxx/logmanager.h>
#include <log4cxx/propertyconfigurator.h>
#include <log4cxx/fileappender.h>
#include <log4cxx/helpers/fileinputstream.h>
#include <log4cxx/helpers/properties.h>
#include <cstdlib>
#include <cstdio>
#include <iostream>
#include <map>
#include <set>
#include <sstream>
#include <stdexcept>
#include <mutex>
#include "util/util_api.h"

using log4cxx::AppenderPtr;
using log4cxx::BasicConfigurator;
using log4cxx::File;
using log4cxx::FileAppender;
using log4cxx::Logger;
using log4cxx::LoggerPtr;
using log4cxx::LogManager;
using log4cxx::PropertyConfigurator;
using log4cxx::helpers::FileInputStream;
using log4cxx::helpers::FileInputStreamPtr;

namespace MR4C {

class MR4CLoggingImpl {

	friend class MR4CLogging;

	private:

		std::string FRAMEWORK_ROOT;
		std::string ALGO_ROOT;
		std::string CONF;
		std::string PROP;
		bool m_init;
		bool m_defaultInit;
		bool m_defaultInitMR4C;
		bool m_initMR4C;
		std::string m_file;
		std::mutex m_mutex;


		static MR4CLoggingImpl& instance() {
			static MR4CLoggingImpl s_instance;
			return s_instance;
		}

		MR4CLoggingImpl() {
			// C++ makes declaring string constants really painful!
			FRAMEWORK_ROOT = "mr4c.native";
			ALGO_ROOT = "mr4c.algo";
			CONF = "/etc/mr4c";
			PROP = "mr4c.logger.config";
			m_init=false;
			m_defaultInit=false;
			m_defaultInitMR4C=false;
			m_initMR4C=false;
		}

		LoggerPtr getLogger(const std::string& name) {
			initLogging();
			return getLoggerHelper(name);
		}

		LoggerPtr getLoggerHelper(const std::string& name) {
			std::ostringstream ss;
			ss << FRAMEWORK_ROOT << '.' << name;
			return Logger::getLogger(ss.str());
		}

		LoggerPtr getAlgorithmLogger(const std::string& name) {
			initLogging();
			std::ostringstream ss;
			ss << ALGO_ROOT << '.' << name;
			return Logger::getLogger(ss.str());
		}

		void initLogging() {
		    std::unique_lock<std::mutex> lock(m_mutex);
			if ( m_init ) {
				return;
			}
			checkLog4cxxInitialized();
			checkMR4CInitialized();
			loadMR4CIfNecessary();
			loadBasicConfIfNecessary();
			logResult();
			m_init=true;
			lock.unlock();
	}


		void checkLog4cxxInitialized() {
			// If log4cxx has been initialized correctly, there will be at least one root appender
			// Note that init won't happen until the first attempt to log is made
			m_defaultInit = Logger::getRootLogger()->getAllAppenders().size()>0;
		}
	
		void checkMR4CInitialized() {
			m_defaultInitMR4C= LogManager::exists(FRAMEWORK_ROOT);
			m_initMR4C = m_defaultInitMR4C;
		}

		void loadMR4CIfNecessary() {
			if ( m_initMR4C ) {
				return;
			}
			if (tryLoadFromEnv()) {
				m_initMR4C=true;
				return;
			}
			if (tryLoadFromLocalFile()) {
				m_initMR4C=true;
				return;
			}
			if (tryLoadFromInstalled()) {
				m_initMR4C=true;
				return;
			}
		}

		void loadBasicConfIfNecessary() {
			if ( !m_initMR4C && !m_defaultInit ) {
				BasicConfigurator::configure();
			}
		}

		void logResult() {

			LoggerPtr m_logger = getLoggerHelper("util.MR4CLogging");
			if ( m_defaultInit ) {
				if ( m_defaultInitMR4C ) {
					LOG4CXX_INFO(m_logger, "Log4cxx initialization loaded MR4C logging config");
				} else {
					if ( m_initMR4C ) {
						LOG4CXX_INFO(m_logger, "Added MR4C logging config from file " << m_file);
					} else {
						LOG4CXX_WARN(m_logger, "Log4cxx initialized, but MR4C logging config not found");
					}
				}
			} else {
				if ( m_initMR4C ) {
					LOG4CXX_INFO(m_logger, "Loaded MR4C logging config only from file " << m_file);
				} else {
					LOG4CXX_WARN(m_logger, "No logging config found, defaulted to BasicConfigurator");
				}
			}
			logAppenders();
		}


		void logAppenders() {
			std::map<std::string,std::set<std::string> > fileMap = extractAppenderMap();
			std::map<std::string,std::set<std::string> >::const_iterator iter=fileMap.begin();
			for ( ; iter!=fileMap.end(); iter++ ) {
				logFileAppenders(iter->first, iter->second);
			}
		}

		void logFileAppenders(const std::string& name, const std::set<std::string> files) {
			std::set<std::string>::const_iterator iter = files.begin();
			LoggerPtr logger = getLoggerHelper("util.MR4CLogging");
			for ( ; iter!=files.end(); iter++ ) {
				LOG4CXX_INFO(logger, "Logger " << name << " is going to " << *iter);
			}
			if ( files.empty() ) {
				LOG4CXX_WARN(logger, "No file appenders found for logger " << name);
			}
		}

		std::set<std::string> extractLogFiles() {
			std::set<std::string> result;
			std::map<std::string,std::set<std::string> > fileMap = extractAppenderMap();
			std::map<std::string,std::set<std::string> >::const_iterator iter=fileMap.begin();
			for ( ; iter!=fileMap.end(); iter++ ) {
				std::set<std::string> files = iter->second;
				result.insert(files.begin(), files.end());
			}
			return result;
		}

		std::map<std::string,std::set<std::string> > extractAppenderMap() {
			std::map<std::string,std::set<std::string> > result;

			std::vector<LoggerPtr> list = LogManager::getCurrentLoggers();
			std::vector<LoggerPtr>::iterator iter = list.begin();
			for ( ; iter!=list.end(); iter++ ) {
				LoggerPtr logger = *iter;
				result[logger->getName()] = extractAppenderFiles(logger);
			}
			return result;
		}

		std::set<std::string> extractAppenderFiles(LoggerPtr logger) {
			std::set<std::string> result;
			std::vector<AppenderPtr> list = logger->getAllAppenders();
			std::vector<AppenderPtr>::iterator iter = list.begin();
			for ( ; iter!=list.end(); iter++ ) {
				AppenderPtr app = *iter;
				std::string file = extractAppenderFile(app);
				if ( !file.empty() ) {
					result.insert(file);
				}
			}
			return result;
		}

		std::string extractAppenderFile(AppenderPtr app) {
			FileAppender* fileAppPtr = dynamic_cast<FileAppender*>(&(*app)); // extracting from a smart pointer here
			return fileAppPtr!=NULL ?
				fileAppPtr->getFile() :
				"";
		}


		bool tryLoadFromEnv() {
			char* path = getenv("MR4C_LOG4CXX_CONFIG");
			if ( path==NULL ) {
				return false;
			}
			return tryLoadFromFile(path);
		}

		bool tryLoadFromLocalFile() {
			if (tryLoadFromFile("log4cxx.properties") ) {
				return true;
			}
			return false;
		}

		bool tryLoadFromInstalled() {
			if (tryLoadFromFile("/etc/mr4c/log4cxx.properties") ) {
				return true;
			}
			return false;
		}

		bool tryLoadFromFile(const char* file) {
			if ( !fileExists(file) ) {
				return false;
			}
			FileInputStreamPtr fis = FileInputStreamPtr(new FileInputStream(file));
			log4cxx::helpers::Properties props;
			props.load(fis);
			fis->close();
			if ( props.get(PROP)!="true" ) {
				return false;
			}
			if ( m_defaultInit ) {
				// get rid of root, don't want it twice
				props.setProperty("log4j.rootLogger", "");
			}
			PropertyConfigurator::configure(props);
			m_file = file;
			return true;
		}

		bool fileExists(const std::string& name) {
			FILE* file = fopen(name.c_str(), "r");
			if ( file==NULL ) {
				return false;
			} else {
				fclose(file);
				return true;
			}
		}

};

LoggerPtr MR4CLogging::getLogger(const std::string& name) {
	return MR4CLoggingImpl::instance().getLogger(name);
}

LoggerPtr MR4CLogging::getAlgorithmLogger(const std::string& name) {
	return MR4CLoggingImpl::instance().getAlgorithmLogger(name);
}

std::set<std::string> MR4CLogging::extractLogFiles() {
	return MR4CLoggingImpl::instance().extractLogFiles();
}

}
