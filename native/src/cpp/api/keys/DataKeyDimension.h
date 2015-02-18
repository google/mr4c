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

#ifndef __MR4C_DATA_KEY_DIMENSION_H__
#define __MR4C_DATA_KEY_DIMENSION_H__

#include <string>
#include <ostream>

namespace MR4C {

class DataKeyDimensionImpl;

/**
  * Represents a dimension for data, such as sensors, frames, etc.
  * Instances are immutable.
*/

class DataKeyDimension {

	public:

		DataKeyDimension();

		DataKeyDimension(const DataKeyDimension& dim);

		DataKeyDimension(const std::string& name);

		std::string getName() const;

		std::string str() const;

		~DataKeyDimension();

		DataKeyDimension& operator=(const DataKeyDimension& dim);

		bool operator==(const DataKeyDimension& dim) const;
		bool operator!=(const DataKeyDimension& dim) const;
		bool operator<(const DataKeyDimension& dim) const;

	private:
		DataKeyDimensionImpl* m_impl;
};

std::ostream& operator<<(std::ostream& os, const DataKeyDimension& dim);

}

#endif
