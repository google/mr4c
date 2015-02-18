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

#include <cmath>
#include <ostream>

#include "coord/coord_api.h"
#include "util/util_api.h"

namespace MR4C {


class SimpleEastNorthTransImpl {

	friend class SimpleEastNorthTrans;

	private :

		double m_radius;

		SimpleEastNorthTransImpl(double radius) {
			m_radius = radius;
		}

		double getRadius() const {
			return m_radius;
		}

		EastNorthCoord toEastNorth(const LatLonCoord& latLonCoord) const {
			double east = latLonCoord.getLonRadians() * m_radius;
			double north = latLonCoord.getLatRadians() * m_radius;
			return EastNorthCoord(east, north);
		}

		LatLonCoord toLatLon(const EastNorthCoord& eastNorthCoord) const {
			double lat = eastNorthCoord.getNorth() / m_radius;
			double lon = eastNorthCoord.getEast() / m_radius;
			return LatLonCoord::fromRadians(lat, lon);
		}

		std::string str() const {
			MR4C_RETURN_STRING("Simple transform with radius=" << m_radius);
		}

		~SimpleEastNorthTransImpl() {}
	
};

SimpleEastNorthTrans::SimpleEastNorthTrans( double radius) {
	m_impl = new SimpleEastNorthTransImpl(radius);
}

double SimpleEastNorthTrans::getRadius() const {
	return m_impl->getRadius();
}

EastNorthCoord SimpleEastNorthTrans::toEastNorth(const LatLonCoord& latLonCoord) const {
		return m_impl->toEastNorth(latLonCoord);
}

LatLonCoord SimpleEastNorthTrans::toLatLon(const EastNorthCoord& eastNorthCoord) const {
		return m_impl->toLatLon(eastNorthCoord);
}

std::string SimpleEastNorthTrans::str() const {
	return m_impl->str();
}

SimpleEastNorthTrans::~SimpleEastNorthTrans() {
	delete m_impl;
}

std::ostream& operator<<(std::ostream& os, const SimpleEastNorthTrans& trans) {
	os << trans.str();
	return os;
}

}
