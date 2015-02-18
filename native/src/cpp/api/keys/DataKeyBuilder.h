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

#ifndef __MR4C_DATA_KEY_BUILDER_H__
#define __MR4C_DATA_KEY_BUILDER_H__

#include <set>

#include "DataKeyDimension.h"
#include "DataKeyElement.h"
#include "DataKey.h"

namespace MR4C {

class DataKeyBuilderImpl;

/**
  * Use this class to build up a key step-by-step.
*/

class DataKeyBuilder {

	public:

		DataKeyBuilder();

		/**
		  * Copy from an existing builder.  New builder starts with the
		  * same set of elements as its source.
		*/
		DataKeyBuilder(const DataKeyBuilder& builder);

		/**
		  * Add a single element
		*/
		void addElement(const DataKeyElement& element);

		/**
		  * Add multiple elements
		*/
		void addElements(const std::set<DataKeyElement>& elements);

		/**
		  * Add all the elements in the key
		*/
		void addAllElements(const DataKey& key);

		/**
		  * Create a key from the elements that have been added
		*/
		DataKey toKey() const;

		~DataKeyBuilder();

		/**
		  * Copy from an existing builder.  This builder starts with the
		  * same set of elements as its source.  Any elements previously
		  * added to this builder are removed
		*/
		DataKeyBuilder& operator=(const DataKeyBuilder& builder);


	private:

		DataKeyBuilderImpl* m_impl;

		// prevent calling this
		bool operator==(const DataKey& key) const;

};

}

#endif

