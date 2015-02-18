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

#ifndef __MR4C_KEYSPACE_BUILDER_H__
#define __MR4C_KEYSPACE_BUILDER_H__

#include <set>

#include "DataKey.h"
#include "Keyspace.h"

namespace MR4C {

class KeyspaceBuilderImpl;

/**
  * Use this class to build up a keyspace from a bunch of keys
*/

class KeyspaceBuilder {

	public:

		KeyspaceBuilder();


		void addKey(const DataKey& key);

		void addKeys(const std::set<DataKey>& keys);

		Keyspace toKeyspace() const;

		~KeyspaceBuilder();


	private:

		KeyspaceBuilderImpl* m_impl;

		// prevent calling this
		KeyspaceBuilder(const KeyspaceBuilder& builder);
		KeyspaceBuilder& operator=(const KeyspaceBuilder& builder);

};

}

#endif

