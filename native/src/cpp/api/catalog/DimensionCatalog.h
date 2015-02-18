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

#ifndef __MR4C_DIMENSION_CATALOG_H__
#define __MR4C_DIMENSION_CATALOG_H__

#include <string>
#include "keys/keys_api.h"

namespace MR4C {


/**
  * Catalog of standard dimensions
*/

class DimensionCatalog {

	public :
		enum Type {
			ANCHOR, 
			FRAME, 
			SENSOR, 
			IMAGE_TYPE,
			DATA_SOURCE 
		};


		/**
		  * Parses the string equivalent of the enum.
		  * For example: "FRAME" --> FRAME
		*/
		static Type enumFromString(std::string strType);

		/**
		  * Returns the string equivalent of the enum.
		  * For example: FRAME --> "FRAME"
		*/
		static std::string enumToString(Type type);

		/**
		  * Returns true if the specified dimension type exists in
		  * the keyspace
		*/
		static bool hasDimension(Keyspace keyspace, Type type);

		/**
		  * Finds the KeyspaceDimension for the dimension type.
		  * Will throw an exception if it is not found
		*/
		static KeyspaceDimension findDimension(Keyspace keyspace, Type type);

		/**
		  * Create KeyDimension object for the dimension type
		*/
		static DataKeyDimension toDimension(Type type);

};

}
#endif


