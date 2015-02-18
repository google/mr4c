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

#ifndef __MR4C_HELPERS_H__
#define __MR4C_HELPERS_H__

#include <sstream>
#include "StackUtil.h"

/**
  * Allows creating a formatted string in one line
*/
#define MR4C_FORMAT_STRING(targetString,message) { std::ostringstream _ss; _ss << message; targetString = _ss.str(); }

/**
  * Allows returning a formatted string in one line
*/
#define MR4C_RETURN_STRING(message) { std::ostringstream _ss; _ss << message; return _ss.str(); }

/**
  * Allows throwing an exception and formatting its message in one line.  This
  * macro also captures the location in the code and a stack trace, which are
  * added to the exception message.
*/
#define MR4C_THROW(name, message) { \
	std::ostringstream _ss; \
	_ss << message << std::endl << \
	StackUtil::formatLocation(__FILE__, __FUNCTION__, __LINE__) << std::endl \
	<< StackUtil::generateBackTrace(); \
	throw name(_ss.str()); \
}

#define MR4C_DEBUG(message) { \
	std::cout << message << " at " << \
	StackUtil::formatLocation(__FILE__, __FUNCTION__, __LINE__) << std::endl; \
}

#endif
