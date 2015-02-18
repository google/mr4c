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


class EastNorthCoordImpl {

	friend class EastNorthCoord;

	private :

		double m_east;
		double m_north;

		EastNorthCoordImpl() {
			m_east = 0.0;
			m_north = 0.0;
		}

		EastNorthCoordImpl(
			double east,
			double north
		) {
			m_east = east;
			m_north = north;
		}

		EastNorthCoordImpl(const EastNorthCoord& coord) {
			initFrom(coord);
		}

		void initFrom(const EastNorthCoord& coord) {
			m_east = coord.getEast();
			m_north = coord.getNorth();
		}

		double getEast() const {
			return m_east;
		}

		double getNorth() const {
			return m_north;
		}

		std::string str() const {
			MR4C_RETURN_STRING("east=" << m_east << "; north=" << m_north);
		}

		~EastNorthCoordImpl() {}
	
		static constexpr double EPS=1e-6;
	
		bool operator==(const EastNorthCoordImpl& coord) const {
			if ( fabs(m_east - coord.m_east) > EPS ) return false;
			if ( fabs(m_north - coord.m_north) > EPS ) return false;
			return true;
		}
	
};

EastNorthCoord::EastNorthCoord() {
	m_impl = new EastNorthCoordImpl();
}

EastNorthCoord::EastNorthCoord(
	double east,
	double north
) {
	m_impl = new EastNorthCoordImpl(east, north);
}

EastNorthCoord::EastNorthCoord(const EastNorthCoord& coord) {
	m_impl = new EastNorthCoordImpl(coord);
}

double EastNorthCoord::getEast() const {
	return m_impl->getEast();
}

double EastNorthCoord::getNorth() const {
	return m_impl->getNorth();
}

std::string EastNorthCoord::str() const {
	return m_impl->str();
}

EastNorthCoord::~EastNorthCoord() {
	delete m_impl;
}

EastNorthCoord& EastNorthCoord::operator=(const EastNorthCoord& coord) {
	m_impl->initFrom(coord);
	return *this;
}

bool EastNorthCoord::operator==(const EastNorthCoord& coord) const {
	return *m_impl==*coord.m_impl;
}

bool EastNorthCoord::operator!=(const EastNorthCoord& coord) const {
	return !operator==(coord);
}

std::ostream& operator<<(std::ostream& os, const EastNorthCoord& coord) {
	os << coord.str();
	return os;
}


}
