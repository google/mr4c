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
#include <log4cxx/logger.h>

#include "dataset/dataset_api.h"
#include "util/util_api.h"

namespace MR4C {

DatasetContext::DatasetContext() {}

DataFile* DatasetContext::findDataFile(const DataKey& key) const {
	return NULL;
}

bool DatasetContext::isOutput() const {
	return false;
}

bool DatasetContext::isQueryOnly() const {
	return false;
}

void DatasetContext::addDataFile(const DataKey& key, DataFile* file) {
	throw std::logic_error("Not allowed to add output files to this dataset");
}

std::string DatasetContext::getDataFileName(const DataKey& key) const {
	return std::string();
}

RandomAccessFile* DatasetContext::readFileAsRandomAccess(const DataKey& key) const {
	throw std::logic_error("Random file access not available for this dataset");
}

WritableRandomAccessFile* DatasetContext::writeFileAsRandomAccess(const DataKey& key) const {
	throw std::logic_error("Random file access not available for this dataset");
}


}
