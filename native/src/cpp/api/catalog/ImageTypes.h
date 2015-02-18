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

#ifndef __MR4C_IMAGE_TYPES_H__
#define __MR4C_IMAGE_TYPES_H__

#include <string>

namespace MR4C {


/**
  * Catalog of standard dimensions
*/

class ImageTypes {

	public :
		enum Type {
			/**
			  * Panchromatic
			*/
			PAN, 

			/**
			  * Multispectral
			*/
			MS, 

			/**
			  * Multispectral Mask
			*/
			MS_MASK,

			/**
			  * RGB PAN Sharpened
			*/
			RGB_PS,

			/**
			  * Near-infra-red PAN Sharpened
			*/
			NIR_PS,

			/**
			  * RGB Thumbnail
			*/
			RGB_THUMB,

			/**
			  * Red
			*/
			RED, 

			/**
			  * Green
			*/
			GRN, 

			/**
			  * Blue
			*/
			BLU, 

			/**
			  * NIR
			*/
			NIR
		};


		/** Parses the string equivalent of the enum.
		  * For example: "PAN" --> PAN.
		  * Note that an underscore in the enum name will be replaced
		  * by a dash in the string equivalent.  This is to avoid
		  * ambiguity when underscores are used as delimiters between
		  * parts of a file name.
		*/
		static Type enumFromString(std::string strType);

		/**
		  * Returns the string equivalent of the enum.
		  * For example: PAN --> "PAN"
		  * Note that an underscore in the enum name will be replaced
		  * by a dash in the string equivalent.  This is to avoid
		  * ambiguity when underscores are used as delimiters between
		  * parts of a file name.
		*/
		static std::string enumToString(Type type);

		/**
		  * Create KeyElement object for the image type
		*/
		static DataKeyElement toImageType(Type type);

};

}
#endif



