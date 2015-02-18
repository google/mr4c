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

#include <iostream>
#include <sstream>
#include <execinfo.h>
#include <cstdlib>
#include "util/util_api.h"

namespace MR4C {

std::string StackUtil::generateBackTrace() {
	return generateBackTrace(20);
}

std::string StackUtil::generateBackTrace(int maxFrames) {

	void** frames = new void*[maxFrames];
	int numFrames = backtrace(frames, maxFrames);
	char** symbols = backtrace_symbols(frames,numFrames);

	std::ostringstream ss;
	for ( int i=0; i<numFrames; i++ ) {
		if ( i!=0 ) {
			ss << std::endl;
		}
		ss << symbols[i];
	}
	free(symbols);
	delete[] frames;
	return ss.str();
}

std::string StackUtil::formatLocation(const char* file, const char* function, int line) {
	std::ostringstream ss;
	ss << file << "(" << line << "):" << function;
	return ss.str();
}
		
}
