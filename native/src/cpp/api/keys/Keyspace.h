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

#ifndef __MR4C_KEYSPACE_H__
#define __MR4C_KEYSPACE_H__

#include <set>

#include "DataKeyDimension.h"
#include "KeyspaceDimension.h"

namespace MR4C {

class KeyspaceImpl;

/**
  * A Keyspace captures all the elements that are present in the datasets passed
  * to an algorithm.
  * Instances are immutable.
*/
class Keyspace {

	public:

		Keyspace();

		Keyspace(const Keyspace& keyspace);

		Keyspace(const std::set<KeyspaceDimension>& dims);

		std::set<DataKeyDimension> getDimensions() const;

		size_t getDimensionCount() const;

		bool hasDimension(const DataKeyDimension& dim) const;

		/**
		  * Returns true if all the dimensions in the key are part of
		  * this keyspace.  Note that this is indepdent of whether this
		  * was one of the keys the keyspace was derived from.
		*/
		bool includesKey(const DataKey& key) const;

		/**
		  * Throws exception if includesKey() is false
		*/
		void validateKey(const DataKey& key) const;

		KeyspaceDimension getKeyspaceDimension(const DataKeyDimension& dim) const;

		std::string str() const;

		~Keyspace();

		Keyspace& operator=(const Keyspace& keyspace);

		bool operator==(const Keyspace& keyspace) const;
		bool operator!=(const Keyspace& keyspace) const;
		bool operator<(const Keyspace& keyspace) const;

	private:

		KeyspaceImpl* m_impl;


};

std::ostream& operator<<(std::ostream& os, const Keyspace& keyspace);

}

#endif
