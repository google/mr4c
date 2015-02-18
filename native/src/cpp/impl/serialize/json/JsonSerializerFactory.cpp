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

#include "serialize/json/json_api.h"

namespace MR4C {


JsonSerializerFactory::JsonSerializerFactory() {} 

JsonSerializerFactory::~JsonSerializerFactory() {}

AlgorithmSerializer* JsonSerializerFactory::createAlgorithmSerializer() const {
	return new JsonAlgorithmSerializer();
}

AlgorithmConfigSerializer* JsonSerializerFactory::createAlgorithmConfigSerializer() const {
	return new JsonAlgorithmConfigSerializer();
}

DatasetSerializer* JsonSerializerFactory::createDatasetSerializer() const {
	return new JsonDatasetSerializer();
}

KeyspaceSerializer* JsonSerializerFactory::createKeyspaceSerializer() const {
	return new JsonKeyspaceSerializer();
}

PropertiesSerializer* JsonSerializerFactory::createPropertiesSerializer() const {
	return new JsonPropertiesSerializer();
}

class JsonSerializerFactoryRegister {
	public :
	JsonSerializerFactoryRegister() {
		SerializerRegistry::instance().registerSerializerFactory(
			"application/json", new JsonSerializerFactory());
	}
};

JsonSerializerFactoryRegister jsonSerializerFactoryRegister;

}
