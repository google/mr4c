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

#ifndef __MR4C_DATA_KEY_H__
#define __MR4C_DATA_KEY_H__

#include <set>
#include <string>
#include <ostream>

#include "DataKeyDimension.h"
#include "DataKeyElement.h"

namespace MR4C {

class DataKeyImpl;

/**
  * A key includes zero or one elements in each dimension.
  * Instances are immutable.
*/

class DataKey {

	public:

		/**
		  * Create empty key.  This is useful for keying global data,
		  * such as the single end result of an algorithm.
		*/
		DataKey();

		/**
		  * Create single element key
		*/
		DataKey(const DataKeyElement& element);

		/**
		  * Create two element key
		*/
		DataKey(
			const DataKeyElement& element1,
			const DataKeyElement& element2
		);

		/**
		  * Create three element key
		*/
		DataKey(
			const DataKeyElement& element1,
			const DataKeyElement& element2,
			const DataKeyElement& element3
		);

		/**
		  * Create multiple element key
		*/
		DataKey(const std::set<DataKeyElement>& elements);

		DataKey(const DataKey& key);

		std::set<DataKeyDimension> getDimensions() const;

		size_t getElementCount() const;

		bool hasDimension(const DataKeyDimension& dim) const;

		bool hasElement(const DataKeyElement& element) const;

		std::set<DataKeyElement> getElements() const;

		DataKeyElement getElement(const DataKeyDimension& dim) const;

		std::string str() const;

		/**
		  * Generate a unique name from this key
		*/
		std::string toName(const std::string& delim) const;

		~DataKey();

		DataKey& operator=(const DataKey& key);

		bool operator==(const DataKey& key) const;
		bool operator!=(const DataKey& key) const;
		bool operator<(const DataKey& key) const;

	private:

		DataKeyImpl* m_impl;


};

std::ostream& operator<<(std::ostream& os, const DataKey& key);

}

#endif
