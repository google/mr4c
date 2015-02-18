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

#ifndef __MR4C_GEO_SIMPLE_EAST_NORTH_TRANS_H__
#define __MR4C_GEO_SIMPLE_EAST_NORTH_TRANS_H__

#include "EastNorthCoord.h"
#include "EastNorthTrans.h"
#include "LatLonCoord.h"

namespace MR4C {

class SimpleEastNorthTransImpl;

/**
  * Transformation that assumes the earth is a perfect sphere.
  * Useful for testing and rough calculations.
*/
class SimpleEastNorthTrans : public EastNorthTrans {

	public:

		SimpleEastNorthTrans(double radius);

		double getRadius() const;

		EastNorthCoord toEastNorth(const LatLonCoord& latLonCoord) const;

		LatLonCoord toLatLon(const EastNorthCoord& eastNorthCoord) const;

		std::string str() const;

		~SimpleEastNorthTrans();

	private:

		SimpleEastNorthTransImpl* m_impl;
};

std::ostream& operator<<(std::ostream& os, const SimpleEastNorthTrans& trans);

}

#endif

