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


class LatLonCoordImpl {

	friend class LatLonCoord;

	private :

		double m_latRad;
		double m_lonRad;
		double m_latDeg;
		double m_lonDeg;

		LatLonCoordImpl() {
			m_latRad = 0.0;
			m_lonRad = 0.0;
			m_latDeg = 0.0;
			m_lonDeg = 0.0;
		}

		void initFromRadians(double lat, double lon) {
			m_latRad = lat;
			m_lonRad = lon;
			m_latDeg = toDegrees(lat);
			m_lonDeg = toDegrees(lon);
		}

		void initFromDegrees(double lat, double lon) {
			m_latDeg = lat;
			m_lonDeg = lon;
			m_latRad = toRadians(lat);
			m_lonRad = toRadians(lon);
		}

		LatLonCoordImpl(const LatLonCoord& coord) {
			initFrom(coord);
		}

		void initFrom(const LatLonCoord& coord) {
			m_latRad = coord.getLatRadians();
			m_lonRad = coord.getLonRadians();
			m_latDeg = coord.getLatDegrees();
			m_lonDeg = coord.getLonDegrees();
		}

		double getLatRadians() const {
			return m_latRad;
		}

		double getLonRadians() const {
			return m_lonRad;
		}

		double getLatDegrees() const {
			return m_latDeg;
		}

		double getLonDegrees() const {
			return m_lonDeg;
		}

		std::string str() const {
			MR4C_RETURN_STRING("lat_rads=" << m_latRad << "; lon_rads=" << m_lonRad << "; lat_degs=" << m_latDeg << "; lon_degs=" << m_lonDeg );
		}

		~LatLonCoordImpl() {}
	
		static constexpr double EPS=1e-10;
	
		bool operator==(const LatLonCoordImpl& coord) const {
			if ( fabs(m_latRad - coord.m_latRad) > EPS ) return false;
			if ( fabs(m_lonRad - coord.m_lonRad) > EPS ) return false;
			if ( fabs(m_latDeg - coord.m_latDeg) > EPS ) return false;
			if ( fabs(m_lonDeg - coord.m_lonDeg) > EPS ) return false;
			return true;
		}

		static double toRadians(double deg) {
			return M_PI * deg / 180.0;
		}
	
		static double toDegrees(double rad) {
			return 180.0 * rad / M_PI;
		}
	
};

LatLonCoord::LatLonCoord() {
	m_impl = new LatLonCoordImpl();
}


LatLonCoord LatLonCoord::fromRadians(double lat, double lon) {
	LatLonCoord coord;
	coord.m_impl->initFromRadians(lat, lon);
	return coord;
}

LatLonCoord LatLonCoord::fromDegrees(double lat, double lon) {
	LatLonCoord coord;
	coord.m_impl->initFromDegrees(lat, lon);
	return coord;
}

LatLonCoord* LatLonCoord::newFromRadians(double lat, double lon) {
	LatLonCoord* coord = new LatLonCoord();
	coord->m_impl->initFromRadians(lat, lon);
	return coord;
}

LatLonCoord* LatLonCoord::newFromDegrees(double lat, double lon) {
	LatLonCoord* coord = new LatLonCoord();
	coord->m_impl->initFromDegrees(lat, lon);
	return coord;
}

LatLonCoord::LatLonCoord(const LatLonCoord& coord) {
	m_impl = new LatLonCoordImpl(coord);
}

double LatLonCoord::getLatRadians() const {
	return m_impl->getLatRadians();
}

double LatLonCoord::getLonRadians() const {
	return m_impl->getLonRadians();
}

double LatLonCoord::getLatDegrees() const {
	return m_impl->getLatDegrees();
}

double LatLonCoord::getLonDegrees() const {
	return m_impl->getLonDegrees();
}

std::string LatLonCoord::str() const {
	return m_impl->str();
}

LatLonCoord::~LatLonCoord() {
	delete m_impl;
}

LatLonCoord& LatLonCoord::operator=(const LatLonCoord& coord) {
	m_impl->initFrom(coord);
	return *this;
}

bool LatLonCoord::operator==(const LatLonCoord& coord) const {
	return *m_impl==*coord.m_impl;
}

bool LatLonCoord::operator!=(const LatLonCoord& coord) const {
	return !operator==(coord);
}

std::ostream& operator<<(std::ostream& os, const LatLonCoord& coord) {
	os << coord.str();
	return os;
}


}
