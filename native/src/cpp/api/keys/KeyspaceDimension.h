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

#ifndef __MR4C_KEYSPACE_DIMENSION_H__
#define __MR4C_KEYSPACE_DIMENSION_H__

#include <vector>

#include "DataKeyDimension.h"
#include "DataKeyElement.h"

namespace MR4C {

class KeyspaceDimensionImpl;

/**
  * What elements are included in the keyspace for a given dimension.
  * Instances are immutable.
*/
class KeyspaceDimension {

	public:

		KeyspaceDimension();

		KeyspaceDimension(const KeyspaceDimension& ksd);

		KeyspaceDimension(const DataKeyDimension& dim, const std::vector<DataKeyElement>& elements);

		DataKeyDimension getDimension() const;

		/**
		  * returns the elements in their defined order, e.g. a series of frames in timestamp order
		*/
		std::vector<DataKeyElement> getElements() const;

		size_t getElementCount() const;

		DataKeyElement getElement(size_t index) const;

		std::string str() const;

		~KeyspaceDimension();

		KeyspaceDimension& operator=(const KeyspaceDimension& ksd);

		bool operator==(const KeyspaceDimension& ksd) const;
		bool operator!=(const KeyspaceDimension& ksd) const;
		bool operator<(const KeyspaceDimension& ksd) const;

	private:

		KeyspaceDimensionImpl* m_impl;


};

std::ostream& operator<<(std::ostream& os, const KeyspaceDimension& ksd);

}

#endif
