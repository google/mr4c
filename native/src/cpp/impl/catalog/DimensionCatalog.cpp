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


class DimensionCatalogImpl {

	friend class DimensionCatalog;

	private:

		std::map<std::string,DimensionCatalog::Type> m_stringToEnum;
		std::map<DimensionCatalog::Type,std::string> m_enumToString;

		static DimensionCatalogImpl& instance() {
			static DimensionCatalogImpl s_instance;
			return s_instance;
		}

		DimensionCatalogImpl() {
			mapType(DimensionCatalog::FRAME, "FRAME");
			mapType(DimensionCatalog::SENSOR, "SENSOR");
			mapType(DimensionCatalog::IMAGE_TYPE, "IMAGE_TYPE");
			mapType(DimensionCatalog::ANCHOR, "ANCHOR");
			mapType(DimensionCatalog::DATA_SOURCE, "DATA_SOURCE");
		}

		// making sure these are private
		DimensionCatalogImpl(const DimensionCatalogImpl& types);
		DimensionCatalogImpl& operator=(const DimensionCatalogImpl& types);

		void mapType(DimensionCatalog::Type type, const std::string& strType) {
			m_stringToEnum[strType] = type;
			m_enumToString[type] = strType;
		}

		DimensionCatalog::Type enumFromString(std::string strType) {
			if ( m_stringToEnum.count(strType)==0 ) {
				MR4C_THROW(std::invalid_argument, "No catalog dimension named [" << strType << "]");
			}
			return m_stringToEnum[strType];
		}


		std::string enumToString(DimensionCatalog::Type type) {
			if ( m_enumToString.count(type)==0 ) {
				MR4C_THROW(std::invalid_argument, "No catalog dimension enum=" << type);
			}
			return m_enumToString[type];
		}


		bool hasDimension(Keyspace keyspace, DimensionCatalog::Type type) {
			DataKeyDimension dim = toDimension(type);
			return keyspace.hasDimension(dim);
		}

		KeyspaceDimension findDimension(Keyspace keyspace, DimensionCatalog::Type type) {
			DataKeyDimension dim = toDimension(type);
			if ( !keyspace.hasDimension(dim) ) {
				MR4C_THROW( std::invalid_argument, "Dimension [" << dim.getName() << "] not found in keyspace");
			}
			return keyspace.getKeyspaceDimension(dim);
		}
			

		DataKeyDimension toDimension(DimensionCatalog::Type type) {
			return DataKeyDimension(enumToString(type));
		}

	};

DimensionCatalog::Type DimensionCatalog::enumFromString(std::string strType) {
	return DimensionCatalogImpl::instance().enumFromString(strType);
}


std::string DimensionCatalog::enumToString(DimensionCatalog::Type type) {
	return DimensionCatalogImpl::instance().enumToString(type);
}

bool DimensionCatalog::hasDimension(Keyspace keyspace, Type type) { 
	return DimensionCatalogImpl::instance().hasDimension(keyspace, type);
}

KeyspaceDimension DimensionCatalog::findDimension(Keyspace keyspace, Type type) {
	return DimensionCatalogImpl::instance().findDimension(keyspace, type);
}

DataKeyDimension DimensionCatalog::toDimension(Type type) {
	return DimensionCatalogImpl::instance().toDimension(type);
}


}


