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

#include <ostream>
#include <cmath>

#include "coord/coord_api.h"
#include "util/util_api.h"

namespace MR4C {


class NormMercCoordImpl {

	friend class NormMercCoord;

	private :

		double m_x;
		double m_y;

		NormMercCoordImpl() {
			m_x = 0.0;
			m_y = 0.0;
		}

		NormMercCoordImpl(
			double x,
			double y
		) {
			m_x = x;
			m_y = y;
		}

		NormMercCoordImpl(const NormMercCoord& coord) {
			initFrom(coord);
		}

		NormMercCoordImpl(const LatLonCoord& latLonCoord) {
			m_x = toX(latLonCoord.getLonRadians());
			m_y = toY(latLonCoord.getLatRadians());
		}

		void initFrom(const NormMercCoord& coord) {
			m_x = coord.getX();
			m_y = coord.getY();
		}

		double getX() const {
			return m_x;
		}

		double getY() const {
			return m_y;
		}

		LatLonCoord toLatLon() const {
			double lat = toLat(m_y);
			double lon = toLon(m_x);
			return LatLonCoord::fromRadians(lat, lon);
		}

		std::string str() const {
			MR4C_RETURN_STRING("x=" << m_x << "; y=" << m_y);
		}

		~NormMercCoordImpl() {}
		
		static constexpr double EPS=1e-10;

		bool operator==(const NormMercCoordImpl& coord) const {
			if ( fabs(m_x - coord.m_x) > EPS ) return false;
			if ( fabs(m_y - coord.m_y) > EPS ) return false;
			return true;
		}

		static double toX(double lon) {
			return (1.0 + (lon /M_PI)) / 2.0;
		}

		static double toY(double lat) {
			double yRad = log(
				tan(lat) +
				1.0/cos(lat)
			);
			return (1.0 - (yRad / M_PI)) / 2.0;
		}

		static double toLon(double x) {
			return M_PI * ( 2.0 * x - 1.0);
		}

		static double toLat(double y) {
			double yRad = M_PI * (1.0 - 2.0 * y);
			return atan(sinh(yRad));
		}

	
};

NormMercCoord::NormMercCoord() {
	m_impl = new NormMercCoordImpl();
}

NormMercCoord::NormMercCoord(
	double x,
	double y
) {
	m_impl = new NormMercCoordImpl(x,y);
}

NormMercCoord::NormMercCoord(const NormMercCoord& coord) {
	m_impl = new NormMercCoordImpl(coord);
}

NormMercCoord::NormMercCoord(const LatLonCoord& latLonCoord) {
	m_impl = new NormMercCoordImpl(latLonCoord);
}

double NormMercCoord::getX() const {
	return m_impl->getX();
}

double NormMercCoord::getY() const {
	return m_impl->getY();
}

LatLonCoord NormMercCoord::toLatLon() const {
	return m_impl->toLatLon();
}

std::string NormMercCoord::str() const {
	return m_impl->str();
}

NormMercCoord::~NormMercCoord() {
	delete m_impl;
}

NormMercCoord& NormMercCoord::operator=(const NormMercCoord& coord) {
	m_impl->initFrom(coord);
	return *this;
}

bool NormMercCoord::operator==(const NormMercCoord& coord) const {
	return *m_impl==*coord.m_impl;
}

bool NormMercCoord::operator!=(const NormMercCoord& coord) const {
	return !operator==(coord);
}

std::ostream& operator<<(std::ostream& os, const NormMercCoord& coord) {
	os << coord.str();
	return os;
}


}
