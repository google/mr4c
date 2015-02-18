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

#ifndef __MR4C_GEO_EAST_NORTH_COORD_H__
#define __MR4C_GEO_EAST_NORTH_COORD_H__

#include <string>
#include <ostream>

#include "LatLonCoord.h"

namespace MR4C {

class EastNorthCoordImpl;

/**
  * A coordinate given as "easting" and "northing"
*/
class EastNorthCoord {

	public:

		EastNorthCoord();

		EastNorthCoord(
			double east,
			double north
		);

		EastNorthCoord(const EastNorthCoord& coord);

		double getEast() const;

		double getNorth() const;

		std::string str() const;

		~EastNorthCoord();

		EastNorthCoord& operator=(const EastNorthCoord& coord);

		bool operator==(const EastNorthCoord& coord) const;
		bool operator!=(const EastNorthCoord& coord) const;

	private:

		EastNorthCoordImpl* m_impl;


};

std::ostream& operator<<(std::ostream& os, const EastNorthCoord& coord);

}

#endif

