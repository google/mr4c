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

#ifndef __MR4C_ERROR_REPORTER_H__
#define __MR4C_ERROR_REPORTER_H__

#include <string>

#include "context/context_api.h"
#include "Error.h"

namespace MR4C {

class ErrorReporterImpl;

class ErrorReporter {

	public:

		ErrorReporter(AlgorithmContext& context, const std::string& topic = "errors");

		std::string getTopic() const;

		void reportError(const Error& error) const;

		~ErrorReporter();


	private:

		ErrorReporterImpl* m_impl;

		// prevent calling these
		ErrorReporter(const ErrorReporter& reporter);
		ErrorReporter& operator=(const ErrorReporter& reporter);

};

}
#endif


