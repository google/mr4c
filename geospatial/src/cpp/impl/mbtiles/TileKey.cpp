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
#include <ostream>
#include <stdexcept>
#include <string>

#include "mbtiles/mbtiles_api.h"
#include "util/util_api.h"

namespace MR4C {


class TileKeyDimensions {

	friend class TileKey;

	private:

		std::map<std::string,TileKey::Dimension> m_stringToDim;
		std::map<TileKey::Dimension,std::string> m_dimToString;

		static TileKeyDimensions& instance() {
			static TileKeyDimensions s_instance;
			return s_instance;
		}

		TileKeyDimensions() {
			mapDimension(TileKey::ZOOM, "ZOOM");
			mapDimension(TileKey::X, "X");
			mapDimension(TileKey::Y, "Y");
		}

		// making sure these are private
		TileKeyDimensions(const TileKeyDimensions& dims);
		TileKeyDimensions& operator=(const TileKeyDimensions& dims);

		void mapDimension(TileKey::Dimension dim, const std::string& strDim) {
			m_stringToDim[strDim] = dim;
			m_dimToString[dim] = strDim;
		}

		TileKey::Dimension dimFromString(std::string strDim) {
			if ( m_stringToDim.count(strDim)==0 ) {
				MR4C_THROW(std::invalid_argument, "No tile key dimesion named [" << strDim << "]");
			}
			return m_stringToDim[strDim];
		}


		std::string dimToString(TileKey::Dimension dim) {
			if ( m_dimToString.count(dim)==0 ) {
				MR4C_THROW(std::invalid_argument, "No tile key dimension enum = " << dim);
			}
			return m_dimToString[dim];
		}

};

class TileKeyImpl {

	friend class TileKey;

	private :

		int m_zoom;
		int m_x;
		int m_y;

		TileKeyImpl() {
			m_zoom = -1;
			m_x = -1;
			m_y = -1;
		}

		TileKeyImpl(
			int zoom,
			int x,
			int y
		) {
			m_zoom = zoom;
			m_x = x;
			m_y = y;
		}

		TileKeyImpl(const TileKey& tileKey) {
			initFrom(tileKey);
		}

		void initFrom(const TileKey& tileKey) {
			m_zoom = tileKey.getZoom();
			m_x = tileKey.getX();
			m_y = tileKey.getY();
		}

		TileKeyImpl(const DataKey& dataKey) {
			m_zoom = extractInt(dataKey, TileKey::Dimension::ZOOM);
			m_x = extractInt(dataKey, TileKey::Dimension::X);
			m_y = extractInt(dataKey, TileKey::Dimension::Y);
		}

		int extractInt(const DataKey& dataKey, TileKey::Dimension dimEnum) const {
			DataKeyDimension dim(TileKey::dimToString(dimEnum));
			DataKeyElement ele = dataKey.getElement(dim);
			std::string strVal = ele.getIdentifier();
			return stoi(strVal);
		}

		int getZoom() const {
			return m_zoom;
		}

		int getX() const {
			return m_x;
		}

		int getY() const {
			return m_y;
		}

		DataKey toDataKey() const {
			return DataKey(
				toElement(m_zoom, TileKey::Dimension::ZOOM),
				toElement(m_x, TileKey::Dimension::X),
				toElement(m_y, TileKey::Dimension::Y)
			);
		}

		DataKeyElement toElement(int val, TileKey::Dimension dimEnum) const {
			std::string strVal = std::to_string(val);
			DataKeyDimension dim(TileKey::dimToString(dimEnum));
			return DataKeyElement(strVal, dim);
		}	

		std::string str() const {
			MR4C_RETURN_STRING("zoom=" << m_zoom << "; x=" << m_x << "; y=" << m_y);
		}

		~TileKeyImpl() {}
		
		bool operator==(const TileKeyImpl& key) const {
			if ( m_zoom!=key.m_zoom ) return false;
			if ( m_x!=key.m_x ) return false;
			if ( m_y!=key.m_y ) return false;
			return true;
		}
		
		bool operator<(const TileKeyImpl& key) const {
			if ( m_zoom < key.m_zoom ) {
				return true;
			}
			if ( m_zoom > key.m_zoom ) {
				return false;
			}
			if ( m_x < key.m_x ) {
				return true;
			}
			if ( m_x > key.m_x ) {
				return false;
			}
			return m_y < key.m_y;
		}
		
};

TileKey::Dimension TileKey::dimFromString(std::string strDim) {
	return TileKeyDimensions::instance().dimFromString(strDim);
}

std::string TileKey::dimToString(TileKey::Dimension dim) {
	return TileKeyDimensions::instance().dimToString(dim);
}

TileKey::TileKey() {
	m_impl = new TileKeyImpl();
}

TileKey::TileKey(
	int zoom,
	int x,
	int y
) {
	m_impl = new TileKeyImpl(zoom, x, y);
}

TileKey::TileKey(const TileKey& key) {
	m_impl = new TileKeyImpl(key);
}

TileKey::TileKey(const DataKey& dataKey) {
	m_impl = new TileKeyImpl(dataKey);
}

int TileKey::getZoom() const {
	return m_impl->getZoom();
}

int TileKey::getX() const {
	return m_impl->getX();
}

int TileKey::getY() const {
	return m_impl->getY();
}

DataKey TileKey::toDataKey() const {
	return m_impl->toDataKey();
}

std::string TileKey::str() const {
	return m_impl->str();
}

TileKey::~TileKey() {
	delete m_impl;
}

TileKey& TileKey::operator=(const TileKey& key) {
	m_impl->initFrom(key);
	return *this;
}

bool TileKey::operator==(const TileKey& key) const {
	return *m_impl==*key.m_impl;
}

bool TileKey::operator!=(const TileKey& key) const {
	return !operator==(key);
}

bool TileKey::operator<(const TileKey& key) const {
	return *m_impl<*key.m_impl;
}

std::ostream& operator<<(std::ostream& os, const TileKey& key) {
	os << key.str();
	return os;
}


}
