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

#include <stdexcept>
#include <string>

#include "error/error_api.h"
#include "util/util_api.h"

namespace MR4C {

class ErrorSeverities {

	friend class Error;

	private:

		std::map<std::string,Error::Severity> m_stringToSev;
		std::map<Error::Severity,std::string> m_sevToString;

		static ErrorSeverities& instance() {
			static ErrorSeverities s_instance;
			return s_instance;
		}

		ErrorSeverities() {
			mapSeverity(Error::WARN, "WARN");
			mapSeverity(Error::ERROR, "ERROR");
		}

		// making sure these are private
		ErrorSeverities(const ErrorSeverities& sevs);
		ErrorSeverities& operator=(const ErrorSeverities& sevs);

		void mapSeverity(Error::Severity sev, const std::string& strSev) {
			m_stringToSev[strSev] = sev;
			m_sevToString[sev] = strSev;
		}

		Error::Severity severityFromString(std::string strSev) {
			if ( m_stringToSev.count(strSev)==0 ) {
				MR4C_THROW(std::invalid_argument, "No error severity named [" << strSev << "]");
			}
			return m_stringToSev[strSev];
		}


		std::string severityToString(Error::Severity sev) {
			if ( m_sevToString.count(sev)==0 ) {
				MR4C_THROW(std::invalid_argument, "No error severity enum = " << sev);
			}
			return m_sevToString[sev];
		}

};

class ErrorImpl {

	friend class Error;

	private:
		Error::Severity m_severity;
		std::string m_summary;
		std::string m_detail;
		std::string m_source;

		ErrorImpl(
			const std::string& summary, 
			const std::string& detail,
			const std::string& source,
			Error::Severity severity
		) {
			m_summary = summary;
			m_detail = detail;
			m_source = source;
			m_severity = severity;
		}

		ErrorImpl(const ErrorImpl& error) {
			initFrom(error);
		}

		ErrorImpl() {
		}

		void initFrom(const ErrorImpl&  error) {
			m_summary = error.m_summary;
			m_detail = error.m_detail;
			m_source = error.m_source;
			m_severity = error.m_severity;
		}

		Error::Severity getSeverity() const {
			return m_severity;
		}

		std::string getSummary() const {
			return m_summary;
		}

		std::string getDetail() const {
			return m_detail;
		}

		bool operator==(const ErrorImpl& error) const {
			return
				m_severity==error.m_severity && 
				m_source==error.m_source && 
				m_summary==error.m_summary && 
				m_detail==error.m_detail;
		}

		std::string str() const {
			MR4C_RETURN_STRING("severity=[" << Error::severityToString(m_severity) << "]; source=[" << m_source << "]; summary=[" << m_summary << "]; detail=[" << m_detail << "]");
		}

		~ErrorImpl() {}
};

Error::Severity Error::severityFromString(std::string strSev) {
	return ErrorSeverities::instance().severityFromString(strSev);
}

std::string Error::severityToString(Error::Severity sev) {
	return ErrorSeverities::instance().severityToString(sev);
}

Error::Error(
	const std::string& summary, 
	const std::string& detail,
	const std::string& source,
	Severity severity

) {
	m_impl = new ErrorImpl(summary, detail, source, severity);
}

Error::Error(const Error& error) {
	m_impl = new ErrorImpl(*error.m_impl);
}

Error::Error() {
	m_impl = new ErrorImpl();
}

Error::Severity Error::getSeverity() const {
	return m_impl->m_severity;
}
		
std::string Error::getSummary() const {
	return m_impl->m_summary;
}
		
std::string Error::getDetail() const {
	return m_impl->m_detail;
}

std::string Error::getSource() const {
	return m_impl->m_source;
}

Error& Error::operator=(const Error& error) {
	m_impl->initFrom(*error.m_impl);
	return *this;
}

bool Error::operator==(const Error& error) const {
	return *m_impl==*error.m_impl;
}

bool Error::operator!=(const Error&  error) const {
	return !operator==(error);
}

std::string Error::str() const {
	return m_impl->str();
}

Error::~Error() {
	delete m_impl;
}

std::ostream& operator<<(std::ostream& os, const Error& error) {
	os << error.str();
	return os;
}


}

