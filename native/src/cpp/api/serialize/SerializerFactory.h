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

#ifndef __MR4C_SERIALIZER_FACTORY_H__
#define __MR4C_SERIALIZER_FACTORY_H__

#include "AlgorithmSerializer.h"
#include "AlgorithmConfigSerializer.h"
#include "DatasetSerializer.h"
#include "KeyspaceSerializer.h"

namespace MR4C {

class SerializerFactory {

	public :

		virtual AlgorithmSerializer* createAlgorithmSerializer() const =0;

		virtual AlgorithmConfigSerializer* createAlgorithmConfigSerializer() const =0;

		virtual DatasetSerializer* createDatasetSerializer() const =0;

		virtual KeyspaceSerializer* createKeyspaceSerializer() const =0;

		virtual PropertiesSerializer* createPropertiesSerializer() const =0;
};

}
#endif

