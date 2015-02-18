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

#ifndef __MR4C_SERIALIZER_REGISTRY_H__
#define __MR4C_SERIALIZER_REGISTRY_H__

#include <string>
#include "SerializerFactory.h"

namespace MR4C {

class SerializerRegistryImpl;

/**
  * Singleton that holds a mapping of content types to serializers that can
  * handle those content types
*/
class SerializerRegistry {

	public:

		static SerializerRegistry& instance();

		void registerSerializerFactory(const std::string& contentType, SerializerFactory* factory);

		SerializerFactory* getSerializerFactory(const std::string& contentType) const;

		~SerializerRegistry();

	private:

		SerializerRegistryImpl* m_impl;

		SerializerRegistry();
		SerializerRegistry(const SerializerRegistry& reg);
		SerializerRegistry& operator=(const SerializerRegistry& reg);

};

}

#endif
