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

#ifndef __MR4C_GEO_LAT_LON_COORD_H__
#define __MR4C_GEO_LAT_LON_COORD_H__

#include <string>
#include <ostream>

namespace MR4C {

class LatLonCoordImpl;

/**
  * Latitude-Longitude coordinate
*/
class LatLonCoord {

	public:

		LatLonCoord();

		static LatLonCoord fromRadians(double lat, double lon);

		static LatLonCoord fromDegrees(double lat, double lon);

		static LatLonCoord* newFromRadians(double lat, double lon);

		static LatLonCoord* newFromDegrees(double lat, double lon);

		LatLonCoord(const LatLonCoord& coord);

		double getLatRadians() const;

		double getLonRadians() const;

		double getLatDegrees() const;

		double getLonDegrees() const;

		std::string str() const;

		~LatLonCoord();

		LatLonCoord& operator=(const LatLonCoord& coord);

		bool operator==(const LatLonCoord& coord) const;
		bool operator!=(const LatLonCoord& coord) const;

	private:

		LatLonCoordImpl* m_impl;


};

std::ostream& operator<<(std::ostream& os, const LatLonCoord& coord);

}

#endif

