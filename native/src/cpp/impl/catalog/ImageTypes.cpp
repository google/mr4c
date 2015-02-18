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

#include <string>
#include <stdexcept>
#include <map>
#include "catalog/catalog_api.h"
#include "keys/keys_api.h"
#include "util/util_api.h"

namespace MR4C {


class ImageTypesImpl {

	friend class ImageTypes;

	private:

		std::map<std::string,ImageTypes::Type> m_stringToEnum;
		std::map<ImageTypes::Type,std::string> m_enumToString;

		static ImageTypesImpl& instance() {
			static ImageTypesImpl s_instance;
			return s_instance;
		}

		ImageTypesImpl() {
			mapType(ImageTypes::PAN, "PAN");
			mapType(ImageTypes::MS, "MS");
			mapType(ImageTypes::MS_MASK, "MS-MASK");
			mapType(ImageTypes::RGB_PS, "RGB-PS");
			mapType(ImageTypes::NIR_PS, "NIR-PS");
			mapType(ImageTypes::RGB_THUMB, "RGB-THUMB");
			mapType(ImageTypes::RED, "RED");
			mapType(ImageTypes::GRN, "GRN");
			mapType(ImageTypes::BLU, "BLU");
			mapType(ImageTypes::NIR, "NIR");
		}

		// making sure these are private
		ImageTypesImpl(const ImageTypesImpl& types);
		ImageTypesImpl& operator=(const ImageTypesImpl& types);

		void mapType(ImageTypes::Type type, const std::string& strType) {
			m_stringToEnum[strType] = type;
			m_enumToString[type] = strType;
		}

		ImageTypes::Type enumFromString(std::string strType) {
			if ( m_stringToEnum.count(strType)==0 ) {
				MR4C_THROW(std::invalid_argument, "No image type named [" << strType << "]");
			}
			return m_stringToEnum[strType];
		}


		std::string enumToString(ImageTypes::Type type) {
			if ( m_enumToString.count(type)==0 ) {
				MR4C_THROW(std::invalid_argument, "No image type enum=" << type);
			}
			return m_enumToString[type];
		}


		DataKeyElement toImageType(ImageTypes::Type type) {
			DataKeyDimension dim = DimensionCatalog::toDimension(DimensionCatalog::IMAGE_TYPE);
			return DataKeyElement(enumToString(type), dim);
		}

	};

ImageTypes::Type ImageTypes::enumFromString(std::string strType) {
	return ImageTypesImpl::instance().enumFromString(strType);
}

std::string ImageTypes::enumToString(ImageTypes::Type type) {
	return ImageTypesImpl::instance().enumToString(type);
}

DataKeyElement ImageTypes::toImageType(ImageTypes::Type type) {
	return ImageTypesImpl::instance().toImageType(type);
}


}


