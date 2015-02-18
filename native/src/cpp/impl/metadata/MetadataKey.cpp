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

#include "metadata/metadata_api.h"

namespace MR4C {

class MetadataKeyImpl {

	friend class MetadataKey;

	private:

		DataKey m_key;

		MetadataKeyImpl() {}

		MetadataKeyImpl(const DataKey& key) {
			m_key = key;
		}

		MetadataKeyImpl(const MetadataKeyImpl& key) {
			initFrom(key);
		}

		DataKey getKey() const {
			return m_key;
		}

		bool operator==(const MetadataKeyImpl& key) const {
			return m_key==key.m_key;
		}

		~MetadataKeyImpl() {} 

		void initFrom(const MetadataKeyImpl& key) {
			m_key = key.m_key;
		}

};



MetadataKey::MetadataKey() {
	m_impl = new MetadataKeyImpl();
}

MetadataKey::MetadataKey(const DataKey& key) {
	m_impl = new MetadataKeyImpl(key);
}

MetadataKey::MetadataKey(const MetadataKey& key) {
	m_impl = new MetadataKeyImpl(*key.m_impl);
}

DataKey MetadataKey::getKey() const {
	return m_impl->getKey();
}

MetadataElement::Type MetadataKey::getMetadataElementType() const {
	return MetadataElement::KEY;
}

CloneableMetadataElement* MetadataKey::clone() const {
	return new MetadataKey(*this);
}

MetadataKey& MetadataKey::operator=(const MetadataKey& key) {
	m_impl->initFrom(*key.m_impl);
	return *this;
}

bool MetadataKey::operator==(const MetadataKey& key) const {
	return *m_impl==*key.m_impl;
}


bool MetadataKey::operator==(const MetadataElement& element) const {
	if ( element.getMetadataElementType()!=MetadataElement::KEY) {
		return false;
	}
	const MetadataKey& key = castToKey(element);
	return operator==(key);
}

bool MetadataKey::operator!=(const MetadataKey& key) const {
	return !operator==(key);
}

bool MetadataKey::operator!=(const MetadataElement& element) const {
	return !operator==(element);
}

MetadataKey::~MetadataKey() {
	delete m_impl;
}

MetadataKey* MetadataKey::castToKey(MetadataElement* element) {
	return MetadataElement::castElement<MetadataKey>(element, MetadataElement::KEY);
}

const MetadataKey* MetadataKey::castToKey(const MetadataElement* element) {
	return MetadataElement::castElement<MetadataKey>(element, MetadataElement::KEY);
}

MetadataKey& MetadataKey::castToKey(MetadataElement& element) {
	return MetadataElement::castElement<MetadataKey>(element, MetadataElement::KEY);
}

const MetadataKey& MetadataKey::castToKey(const MetadataElement& element) {
	return MetadataElement::castElement<MetadataKey>(element, MetadataElement::KEY);
}

}
