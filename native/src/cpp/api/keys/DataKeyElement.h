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

#ifndef __MR4C_DATA_KEY_ELEMENT_H__
#define __MR4C_DATA_KEY_ELEMENT_H__

#include <vector>
#include <string>
#include <ostream>

#include "DataKeyDimension.h"

namespace MR4C {

class DataKeyElementImpl;

/**
  * Represents one element of a data key, such as a specific frame.
  * Instances are immutable.
*/

class DataKeyElement {

	public:

		DataKeyElement();

		DataKeyElement(const std::string& id, const DataKeyDimension& dim);

		DataKeyElement(const DataKeyElement& element);

		std::string getIdentifier() const;
		
		DataKeyDimension getDimension() const;

		std::string str() const;

		static std::vector<std::string> toElementIds(const std::vector<DataKeyElement>& elements);

		~DataKeyElement();

		DataKeyElement& operator=(const DataKeyElement& element);

		bool operator<(const DataKeyElement& element) const;
		bool operator==(const DataKeyElement& element) const;
		bool operator!=(const DataKeyElement& element) const;


	private:
		DataKeyElementImpl* m_impl;

};

std::ostream& operator<<(std::ostream& os, const DataKeyElement& element);


}
#endif
