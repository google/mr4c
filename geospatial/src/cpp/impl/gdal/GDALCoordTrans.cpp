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

#include <stdexcept>

#include "coord/coord_api.h"
#include "gdal/gdal_api.h"
#include "util/util_api.h"

namespace MR4C {


class GDALCoordTransImpl {

	friend class GDALCoordTrans;

	private :

		std::shared_ptr<OGRCoordinateTransformation> m_ENtoLL;
		std::shared_ptr<OGRCoordinateTransformation> m_LLtoEN;

		GDALCoordTransImpl(std::shared_ptr<OGRCoordinateTransformation>& ogrTrans) {
			m_ENtoLL = ogrTrans;
			OGRCoordinateTransformation* reverse =
				OGRCreateCoordinateTransformation(
					m_ENtoLL.get()->GetTargetCS(),
					m_ENtoLL.get()->GetSourceCS()
			);
			if ( reverse==NULL ) {
				MR4C_THROW(std::logic_error, "Call to OGRCreateCoordinateTransformation for reverse transformation failed");
			}
			m_LLtoEN = std::shared_ptr<OGRCoordinateTransformation>(reverse);
		}

		std::shared_ptr<OGRCoordinateTransformation> getOGRTransformation() const {
			return m_ENtoLL;
		}

		std::shared_ptr<OGRCoordinateTransformation> getReverseOGRTransformation() const {
			return m_LLtoEN;
		}

		EastNorthCoord toEastNorth(const LatLonCoord& latLonCoord) const {
			double east = latLonCoord.getLonDegrees();
			double north = latLonCoord.getLatDegrees();
			if ( !m_LLtoEN.get()->Transform(1, &east, &north) ) {
				MR4C_THROW(std::logic_error, "Coordinate transform failed for coord " << latLonCoord);
			}
			return EastNorthCoord(east, north);
		}

		LatLonCoord toLatLon(const EastNorthCoord& eastNorthCoord) const {
			double lon = eastNorthCoord.getEast();
			double lat = eastNorthCoord.getNorth();
			if ( !m_ENtoLL.get()->Transform(1, &lon, &lat) ) {
				MR4C_THROW(std::logic_error, "Coordinate transform failed for coord " << eastNorthCoord);
			}
			return LatLonCoord::fromDegrees(lat, lon);
		}

		~GDALCoordTransImpl() {}
	
};

GDALCoordTrans::GDALCoordTrans(std::shared_ptr<OGRCoordinateTransformation>& ogrTrans) {
	m_impl = new GDALCoordTransImpl(ogrTrans);
}

std::shared_ptr<OGRCoordinateTransformation> GDALCoordTrans::getOGRTransformation() const {
	return m_impl->getOGRTransformation();
}

std::shared_ptr<OGRCoordinateTransformation> GDALCoordTrans::getReverseOGRTransformation() const {
	return m_impl->getReverseOGRTransformation();
}

EastNorthCoord GDALCoordTrans::toEastNorth(const LatLonCoord& latLonCoord) const {
		return m_impl->toEastNorth(latLonCoord);
}

LatLonCoord GDALCoordTrans::toLatLon(const EastNorthCoord& eastNorthCoord) const {
		return m_impl->toLatLon(eastNorthCoord);
}

GDALCoordTrans::~GDALCoordTrans() {
	delete m_impl;
}

}
