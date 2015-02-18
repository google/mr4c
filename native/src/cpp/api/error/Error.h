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

#ifndef __MR4C_ERROR_H__
#define __MR4C_ERROR_H__

#include <string>

namespace MR4C {

class ErrorImpl;

class Error {

	public:

		enum Severity {
			WARN, 
			ERROR 
		};

		/**
		  * Parses the string equivalent of the enum.
		  * For example: "ERROR" --> ERROR
		*/
		static Severity severityFromString(std::string strSev);

		/**
		  * Returns the string equivalent of the enum.
		  * For example: ERROR --> "ERROR"
		*/
		static std::string severityToString(Severity sev);


		Error();

		Error(
			const std::string& summary, 
			const std::string& detail,
			const std::string& source,
			Severity severity = ERROR
		);

		Error(const Error& error);

		Severity getSeverity() const;

		std::string getSummary() const;

		std::string getDetail() const;

		std::string getSource() const;

		std::string str() const;
	
		~Error();

		Error& operator=(const Error& error);

		bool operator==(const Error& error) const;
		bool operator!=(const Error& error) const;

	private:
		ErrorImpl* m_impl;

};

std::ostream& operator<<(std::ostream& os, const Error& error);


}
#endif


