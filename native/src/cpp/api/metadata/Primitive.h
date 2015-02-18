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

#ifndef __MR4C_PRIMITIVE_H__
#define __MR4C_PRIMITIVE_H__

#include <string>

namespace MR4C {

class PrimitiveImpl;

/**
  * Enumeration and utilities for the primitive types in fields and arrays.
*/

class Primitive {

	public :
		enum Type {
			/** C++ type is bool */
			BOOLEAN, 

			/** C++ type is char */
			BYTE, // char

			/** C++ type is int */
			INTEGER, 

			/** C++ type is float */
			FLOAT, 

			/** C++ type is double */
			DOUBLE, 

			/** C++ type is std::string */
			STRING,

			/** C++ type is size_t */
			SIZE_T,

			/** C++ type is long double */
			LONG_DOUBLE

		};

		/**
		  * Parses the string equivalent of the enum.
		  * For example:  "BYTE" --> BYTE
		*/
		static Type enumFromString(std::string strType);

		/**
		  * Returns the string equivalent of the enum.
		  * For example:  BYTE --> "BYTE"
		*/
		static std::string enumToString(Type type);


		/** For use by the framework **/
		template<typename T> static std::string toString(const T& val);

		/** For use by the framework **/
		template<typename T> static T fromString(const std::string& str);

		/** For use by the framework **/
		template<typename T> static void toString(const T* vals, std::string* strs, size_t size);

		/** For use by the framework **/
		template<typename T> static void fromString(const std::string* strs, T* vals, size_t size );

	private:

		Primitive();
		Primitive(const Primitive& prim);
		Primitive& operator=(const Primitive& prim);

};


}
#endif

