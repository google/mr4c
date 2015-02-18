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

#ifndef __MR4C_GEO_NORM_MERC_COORD_H__
#define __MR4C_GEO_NORM_MERC_COORD_H__

#include <string>
#include <ostream>

#include "LatLonCoord.h"

namespace MR4C {

class NormMercCoordImpl;

/**
  * A coordinate for a "normalized" Mercator projection.
  * The values of X and Y both are in the range 0.0-1.0.
  * Origin is top left.
*/
class NormMercCoord {

	public:

		NormMercCoord();

		NormMercCoord(
			double x,
			double y
		);

		NormMercCoord(const NormMercCoord& coord);

		NormMercCoord(const LatLonCoord& latLonCoord);

		double getX() const;

		double getY() const;

		LatLonCoord toLatLon() const;

		std::string str() const;

		~NormMercCoord();

		NormMercCoord& operator=(const NormMercCoord& coord);

		bool operator==(const NormMercCoord& coord) const;
		bool operator!=(const NormMercCoord& coord) const;

	private:

		NormMercCoordImpl* m_impl;


};

std::ostream& operator<<(std::ostream& os, const NormMercCoord& coord);

}

#endif

