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

#ifndef __MR4C_GEO_BOUNDING_BOX_H__
#define __MR4C_GEO_BOUNDING_BOX_H__

#include <memory>
#include <string>
#include <ostream>

#include "EastNorthCoord.h"
#include "EastNorthTrans.h"
#include "LatLonCoord.h"
#include "NormMercCoord.h"

namespace MR4C {

class BoundingBoxImpl;

/**
  * Absolute location of a bounding box defined by its NW and SE corners.
  * An EastNorthTrans implementation is required to allow translating between Lat-Long and East-North representations of the bound.
*/
class BoundingBox {

	public:

		BoundingBox();

		BoundingBox(
			const EastNorthCoord& nw,
			const EastNorthCoord& se,
			std::shared_ptr<EastNorthTrans>& trans
		);

		BoundingBox(
			const LatLonCoord& nw,
			const LatLonCoord& se,
			std::shared_ptr<EastNorthTrans>& trans
		);

		BoundingBox(
			const NormMercCoord& nw,
			const NormMercCoord& se,
			std::shared_ptr<EastNorthTrans>& trans
		);

		BoundingBox(const BoundingBox& box);

		EastNorthCoord getNWCoordAsEastNorth() const;

		EastNorthCoord getSECoordAsEastNorth() const;

		LatLonCoord getNWCoordAsLatLon() const;

		LatLonCoord getSECoordAsLatLon() const;

		NormMercCoord getNWCoordAsNormMerc() const;

		NormMercCoord getSECoordAsNormMerc() const;

		std::shared_ptr<EastNorthTrans> getEastNorthTransformer() const;

		/**
		  * delta-X in NormMerc coordinate system
		*/	
		double dx() const;
	
		/**
		  * delta-Y in NormMerc coordinate system
		*/	
		double dy() const;
	
		/**
		  * delta-East in EastNorth coordinate system
		*/	
		double dE() const;
	
		/**
		  * delta-North in EastNorth coordinate system
		*/	
		double dN() const;

		/**
		  * returns true if the two bounding boxes intersect
		*/
		static bool intersecting(const BoundingBox& box1, const BoundingBox& box2);

		/**
		  * returns the box containing the intersection of the two boxes.
		  * If the boxes do not intersect, an exception is thrown.
		*/
		static BoundingBox intersect(const BoundingBox& box1, const BoundingBox box2);

		std::string str() const;

		~BoundingBox();

		BoundingBox& operator=(const BoundingBox& box);

		bool operator==(const BoundingBox& box) const;
		bool operator!=(const BoundingBox& box) const;

	private:

		BoundingBoxImpl* m_impl;


};

std::ostream& operator<<(std::ostream& os, const BoundingBox& box);

}

#endif


