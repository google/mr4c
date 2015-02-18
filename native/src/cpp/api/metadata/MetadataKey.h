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

#ifndef __MR4C_METADATA_KEY_H__
#define __MR4C_METADATA_KEY_H__

#include "MetadataElement.h"
#include "keys/keys_api.h"

namespace MR4C {

class MetadataKeyImpl;

/**
  * Thin wrapper to allow DataKey to be a piece of metadata.
  * This class is immutable.
*/

class MetadataKey : public CloneableMetadataElement {


	public:

		MetadataKey();

		MetadataKey(const DataKey& key);

		MetadataKey(const MetadataKey& key);

		DataKey getKey() const;

		MetadataElement::Type getMetadataElementType() const;

		virtual CloneableMetadataElement* clone() const;
		
		MetadataKey& operator=(const MetadataKey& key);

		bool operator==(const MetadataKey& key) const;

		bool operator==(const MetadataElement& element) const;

		bool operator!=(const MetadataKey& key) const;

		bool operator!=(const MetadataElement& element) const;

		~MetadataKey();

		static const MetadataKey* castToKey(const MetadataElement* element);

		static MetadataKey* castToKey(MetadataElement* element);

		static const MetadataKey& castToKey(const MetadataElement& element);

		static MetadataKey& castToKey(MetadataElement& element);

	private:

		MetadataKeyImpl* m_impl;

};

}
#endif

