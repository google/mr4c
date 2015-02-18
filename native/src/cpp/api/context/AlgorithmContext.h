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

#ifndef __MR4C_ALGORITHM_CONTEXT_H__
#define __MR4C_ALGORITHM_CONTEXT_H__

#include <string>
#include <sstream>
#include "context/Logger.h"
#include "context/MessageConsumer.h"
#include "context/ProgressReporter.h"

/**
  * Allows algorithms to log a formatted message C++ style in one line with the code location included.
  * @param context type is AlgorithmContext&
  * @param level type is Logger::Level
  * @param msg any expression that can be on the right hand side of "<<" operator
*/
#define MR4C_ALGO_LOG_CPP(context, level, msg) { \
	std::ostringstream _ss; \
	 _ss << msg; \
	(context).log( __FILE__, __LINE__, level, _ss.str()); \
}

/**
  * Allows algorithms to log a formatted message C style (printf) with the code location included
  * @param context type is AlgorithmContext&
  * @param level type is Logger::Level
  * @param format type is const char*
*/
#define MR4C_ALGO_LOG_C(context, level, format, args...) { \
	(context).log( __FILE__, __LINE__, level, format, ##args); \
}

namespace MR4C {

class AlgorithmContextImpl;

/**
  * Provides algorithms with access to various services provided by the
  * framework.  A context instance is valid for the duration of a single
  * algorithm execution.
*/

class AlgorithmContext {

	public:

		AlgorithmContext(const std::string& name);

		/**
		  * Printf style logging method for algorithms
		*/
		void log(Logger::LogLevel level, const char* format, ...);

		/**
		  * Printf style logging method for algorithms with code location
		*/
		void log(const std::string& file, int line, Logger::LogLevel level, const char* format, ...);

		/**
		  * Logging method for algorithms
		*/
		void log(Logger::LogLevel level, const std::string& msg);

		/**
		  * Logging method for algorithms with code location
		*/
		void log( const std::string& file, int line, Logger::LogLevel level, const std::string& msg);

		/**
		  * Printf style progress reporting method for algorithms
		*/
		void progress(float percentDone, const char* format, ...);

		/**
		  * Progress reporting method for algorithms
		*/
		void progress(float percentDone, const std::string& msg);

		/**
		  * Register an object to receive all logging messages
		*/
		void registerLogger(Logger* logger);

		/**
		  * Register an object to receive all progress messages
		*/
		void registerProgressReporter(ProgressReporter* prog);

		/**
		  * Create a temporary directory for use by the algorithm.
		  *  MR4C is responsible for cleaning up the directory after the algorithm exits. 
		*/
		std::string createTempDirectory();

		/**
		  * Send message to a topic.  If a handler has not been
		  * configured for the topic, the message will just be logged
		*/
		void sendMessage(const Message& msg);

		/**
		  * Register an object to receive all messages sent to topics
		*/
		void registerMessageConsumer(MessageConsumer* consumer);

		~AlgorithmContext();

	private:

		AlgorithmContextImpl* m_impl;

		// prevent calling these
		AlgorithmContext(const AlgorithmContext& context);
		AlgorithmContext& operator=(const AlgorithmContext& context);

};

}

#endif
