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

#include <cstdlib>
#include <iostream>
#include <stdexcept>
#include "dataset/dataset_api.h"
#include "util/util_api.h"

namespace MR4C {

size_t DataFileSource::read(char* buf, size_t num) {
	throw std::logic_error("No implementation of 'read' provided");
}

size_t DataFileSource::skip(size_t num) {
	throw std::logic_error("No implementation of 'skip' provided");
}

bool DataFileSource::operator!=(const DataFileSource& src) const {
	return !(*this==src);
}

bool operator==(const DataFileSource& src1, const DataFileSource& src2) {
	if ( src1.isReleased() || src2.isReleased() ) {
		throw std::invalid_argument("Not allowed to compare released data sources");
	}
	size_t size1 = src1.getFileSize();
	size_t size2 = src2.getFileSize();
	if ( size1!=size2 ) {
		return false;
	}
	char* bytes1 = src1.getFileBytes();
	char* bytes2 = src2.getFileBytes();
	return compareArray(bytes1, bytes2, size1);
}


}

