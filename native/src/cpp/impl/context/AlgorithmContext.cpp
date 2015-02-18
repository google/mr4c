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

#include <cstdio>
#include <cstdarg>
#include <vector>
#include <mutex>
#include <log4cxx/logger.h>
#include <log4cxx/spi/location/locationinfo.h>

#include "context/context_api.h"
#include "util/util_api.h"

using log4cxx::LoggerPtr;
using log4cxx::spi::LocationInfo;

namespace MR4C {

class AlgorithmContextAlgoLogger : public Logger {

	friend class AlgorithmContextImpl;

	private: 

	LoggerPtr m_logger;

		AlgorithmContextAlgoLogger(const std::string& name) {
			m_logger = MR4CLogging::getAlgorithmLogger(name);
		}

	public :
	
		void log(LogLevel level, const std::string& msg) {
			log("", -1, level, msg);
		}
	
		virtual void log(const std::string& file, int line, LogLevel level, const std::string& msg) {
			LocationInfo loc(file.c_str(), "", line);
			switch(level) {
				case INFO :
					m_logger->info(msg,loc);
					break;
				case DEBUG :
					m_logger->debug(msg,loc);
					break;
				case WARN :
					m_logger->warn(msg,loc);
					break;
				case ERROR :
					m_logger->error(msg,loc);
					break;
			}
		}

};

class AlgorithmContextImpl {

	friend class AlgorithmContext;

	private:

		std::string m_name;
		LoggerPtr m_frameworkLogger;
		Logger* m_algoLogger;
		std::vector<Logger*> m_loggers;
		std::mutex m_loggerMutex;
		std::vector<ProgressReporter*> m_progs;
		std::mutex m_progsMutex;
		std::vector<MessageConsumer*> m_consumers;
		std::mutex m_consumersMutex;

		AlgorithmContextImpl(const std::string& name) {
			m_name = name;
			init();
		}

		void init() {
			m_frameworkLogger = MR4CLogging::getLogger("context.AlgorithmContext");
			m_algoLogger = new AlgorithmContextAlgoLogger(m_name);
			registerLogger(m_algoLogger);
		}


		void log(Logger::LogLevel level, const char* format, va_list args) {
			log("", -1, level, format, args);
		}

		void log(const std::string& file, int line, Logger::LogLevel level, const char* format, va_list args) {
			std::string msg = IOUtil::vprintfToString(format, args);
			log(file, line, level, msg);
		}

		void log(Logger::LogLevel level, const std::string& msg) {
			log("", -1, level, msg);
		}

		void log(const std::string& file, int line, Logger::LogLevel level, const std::string& msg) {
			LOG4CXX_DEBUG(m_frameworkLogger, "Logging to context at level [ " << Logger::enumToString(level) << "]; " << msg);
			std::unique_lock<std::mutex> lock(m_loggerMutex);
			std::vector<Logger*>::iterator iter = m_loggers.begin();
			for ( ; iter!=m_loggers.end(); iter++ ) {
				Logger* logger = *iter;
				logger->log(file, line, level, msg);
			}
			lock.unlock();
		}

		void progress(float percentDone, const char* format, va_list args) {
			std::string msg = IOUtil::vprintfToString(format, args);
			progress(percentDone,msg);
		}

		void progress(float percentDone, const std::string& msg) {
			LOG4CXX_INFO(m_frameworkLogger, "Progress reported: " << percentDone << " % done; " << msg);
			std::unique_lock<std::mutex> lock(m_progsMutex);
			std::vector<ProgressReporter*>::iterator iter = m_progs.begin();
			for ( ; iter!=m_progs.end(); iter++ ) {
				ProgressReporter* prog = *iter;
				prog->progress(percentDone,msg);
			}
			lock.unlock();
		}

		void sendMessage(const Message& msg) {
		    std::unique_lock<std::mutex> lock(m_consumersMutex);
			std::vector<MessageConsumer*>::iterator iter = m_consumers.begin();
			for ( ; iter!=m_consumers.end(); iter++ ) {
				MessageConsumer* consumer = *iter;
				consumer->receiveMessage(msg);
			}
			lock.unlock();
		}

		void registerLogger(Logger* logger) {
		    std::unique_lock<std::mutex> lock(m_loggerMutex);
			m_loggers.push_back(logger);
			lock.unlock();
			LOG4CXX_INFO(m_frameworkLogger, "Logger registered");
		}

		void registerProgressReporter(ProgressReporter* prog) {
		    std::unique_lock<std::mutex> lock(m_progsMutex);
			m_progs.push_back(prog);
			lock.unlock();
			LOG4CXX_INFO(m_frameworkLogger, "Progress reporter registered");
		}

		void registerMessageConsumer(MessageConsumer* consumer) {
		    std::unique_lock<std::mutex> lock(m_consumersMutex);
			m_consumers.push_back(consumer);
			lock.unlock();
			LOG4CXX_INFO(m_frameworkLogger, "Message consumer registered");
		}

		std::string createTempDirectory() {
			std::string dir = MR4CTempFiles::instance().createTempDirectory(".");
			LOG4CXX_INFO(m_frameworkLogger, "Temporary directory [" << dir << "] created");
			return dir;
		}


		~AlgorithmContextImpl() {
			delete m_algoLogger;
		} 

};

AlgorithmContext::AlgorithmContext(const std::string& name) {
	m_impl = new AlgorithmContextImpl(name);
}

AlgorithmContext::~AlgorithmContext() {
	delete m_impl;
} 

void AlgorithmContext::log( Logger::LogLevel level, const char* format, ...) {
	va_list args;
	va_start (args, format);
	m_impl->log(level, format, args);
	va_end (args);
}

void AlgorithmContext::log( const std::string& file, int line, Logger::LogLevel level, const char* format, ...) {
	va_list args;
	va_start (args, format);
	m_impl->log(file, line, level, format, args);
	va_end (args);
}

void AlgorithmContext::log(Logger::LogLevel level, const std::string& msg) {
	m_impl->log(level,msg);
}

void AlgorithmContext::log( const std::string& file, int line, Logger::LogLevel level, const std::string& msg) {
	m_impl->log(file, line, level, msg);
}

void AlgorithmContext::progress(float percentDone, const char* format, ...) {
	va_list args;
	va_start (args, format);
	m_impl->progress(percentDone, format, args);
	va_end (args);
}

void AlgorithmContext::progress(float percentDone, const std::string& msg) {
	m_impl->progress(percentDone, msg);
}

void AlgorithmContext::sendMessage(const Message& msg) {
	m_impl->sendMessage(msg);
}

void AlgorithmContext::registerLogger(Logger* logger) {
	m_impl->registerLogger(logger);
}

void AlgorithmContext::registerProgressReporter(ProgressReporter* prog) {
	m_impl->registerProgressReporter(prog);
}

void AlgorithmContext::registerMessageConsumer(MessageConsumer* consumer) {
	m_impl->registerMessageConsumer(consumer);
}

std::string AlgorithmContext::createTempDirectory() {
	return m_impl->createTempDirectory();
}

}
