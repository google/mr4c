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

#include <map>
#include <stdexcept>

#include "serialize/serialize_api.h"
#include "util/util_api.h"

namespace MR4C {

class SerializerRegistryImpl {

	friend class SerializerRegistry;

	private:

		std::map<std::string,SerializerFactory*> m_factories;

		SerializerRegistryImpl() {}

		void registerSerializerFactory(const std::string& contentType, SerializerFactory* factory) {
			m_factories[contentType] = factory;
		}

		SerializerFactory* getSerializerFactory(const std::string& contentType) const {
			if ( m_factories.count(contentType)==0 ) {
				MR4C_THROW( std::invalid_argument, "No serializer factory registered for content type [" << contentType << "]");

			}
			return m_factories.find(contentType)->second;
		}

		~SerializerRegistryImpl() {} 

};


SerializerRegistry& SerializerRegistry::instance() {
	static SerializerRegistry s_instance;
	return s_instance;
}

SerializerRegistry::SerializerRegistry() {
	m_impl = new SerializerRegistryImpl();
}

SerializerRegistry::~SerializerRegistry() {
	delete m_impl;
} 

void SerializerRegistry::registerSerializerFactory(const std::string& contentType, SerializerFactory* factory) {
	m_impl->registerSerializerFactory(contentType, factory);
}

SerializerFactory* SerializerRegistry::getSerializerFactory(const std::string& contentType) const {
	return m_impl->getSerializerFactory(contentType);
}


}
