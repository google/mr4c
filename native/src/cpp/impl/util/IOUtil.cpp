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
#include <vector>
#include <sys/stat.h>
#include "util/util_api.h"

namespace MR4C {

bool IOUtil::directoryExists(const std::string& path) {
	struct stat sb;
	return stat(path.c_str(), &sb)==0 && S_ISDIR(sb.st_mode);
}

std::string IOUtil::printfToString(const char* format, ...) {
	va_list args;
	va_start (args, format);
	std::string str = IOUtil::vprintfToString(format, args);
	va_end (args);
	return str;
}

std::string IOUtil::vprintfToString(const char* format, va_list args) {
	va_list args2;
	va_copy(args2, args);
	int len = vsnprintf(NULL, 0, format, args2);
	va_end(args2);
	if (len < 0 ) {
		MR4C_THROW(std::logic_error, "Printf conversion failed; returned code " << len << " for format string [" << format << "]");
	}
	std::vector<char> buf(len+1); // add 1 for null termination
	int len2 = vsnprintf(buf.data(), len+1, format, args);
	if (len2 < 0 ) {
		MR4C_THROW(std::logic_error, "Printf conversion failed; returned code " << len2 << " for format string [" << format << "]");
	}
	return std::string(buf.data());
}
	

}
