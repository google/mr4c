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
#include <stdexcept>

#include "coord/coord_api.h"
#include "util/util_api.h"

namespace MR4C {


class BoundingBoxImpl {

	friend class BoundingBox;

	private :

		EastNorthCoord m_nwEN;
		EastNorthCoord m_seEN;
		LatLonCoord m_nwLL;
		LatLonCoord m_seLL;
		NormMercCoord m_nwNM;
		NormMercCoord m_seNM;
		std::shared_ptr<EastNorthTrans> m_trans;


		BoundingBoxImpl() {}

		BoundingBoxImpl(
			const EastNorthCoord& nw,
			const EastNorthCoord& se,
			std::shared_ptr<EastNorthTrans>& trans

		) {
			m_nwEN = nw;
			m_seEN = se;
			m_trans = trans;
			computeLatLonFromEastNorth();
			computeNormMerc();
			validateBox();
		}
			
		BoundingBoxImpl(
			const LatLonCoord& nw,
			const LatLonCoord& se,
			std::shared_ptr<EastNorthTrans>& trans
		) {
			
			m_nwLL = nw;
			m_seLL = se;
			m_trans = trans;
			computeEastNorth();
			computeNormMerc();
			validateBox();
		}

		BoundingBoxImpl(
			const NormMercCoord& nw,
			const NormMercCoord& se,
			std::shared_ptr<EastNorthTrans>& trans
		) {
			m_nwNM = nw;
			m_seNM = se;
			m_trans = trans;
			computeLatLonFromNormMerc();
			computeEastNorth();
			validateBox();
		}

		BoundingBoxImpl(const BoundingBox& box) {
			initFrom(box);
		}

		void initFrom(const BoundingBox& box) {
			m_nwEN = box.getNWCoordAsEastNorth();
			m_seEN = box.getSECoordAsEastNorth();
			m_nwLL = box.getNWCoordAsLatLon();
			m_seLL = box.getSECoordAsLatLon();
			m_nwNM = box.getNWCoordAsNormMerc();
			m_seNM = box.getSECoordAsNormMerc();
			m_trans = box.getEastNorthTransformer();
		}

		void computeLatLonFromEastNorth() {
			m_nwLL = m_trans.get()->toLatLon(m_nwEN);
			m_seLL = m_trans.get()->toLatLon(m_seEN);
		}

		void computeLatLonFromNormMerc() {
			m_nwLL = m_nwNM.toLatLon();
			m_seLL = m_seNM.toLatLon();
		}

		void computeNormMerc() {
			m_nwNM = NormMercCoord(m_nwLL);
			m_seNM = NormMercCoord(m_seLL);
		}

		void computeEastNorth() {
			m_nwEN = m_trans.get()->toEastNorth(m_nwLL);
			m_seEN = m_trans.get()->toEastNorth(m_seLL);
		}

		void validateBox() {
			validateOrientation();
			validateSize();
		}

		void validateOrientation() {
			if ( m_nwNM.getX() > m_seNM.getX() ) {
				MR4C_THROW(std::invalid_argument, "NW coord [" << m_nwNM << "] is east of SE coord [" << m_seNM << "]");
			}
			if ( m_nwNM.getY() > m_seNM.getY() ) {
				MR4C_THROW(std::invalid_argument, "NW coord [" << m_nwNM << "] is south of SE coord [" << m_seNM << "]");
			}
		}

		void validateSize() {
			if ( dx() < EPS ) {
				MR4C_THROW(std::invalid_argument, "BoundingBox width is zero");
			}
			if ( dy() < EPS ) {
				MR4C_THROW(std::invalid_argument, "BoundingBox height is zero");
			}
		}

		EastNorthCoord getNWCoordAsEastNorth() const {
			return m_nwEN;
		}

		EastNorthCoord getSECoordAsEastNorth() const {
			return m_seEN;
		}

		LatLonCoord getNWCoordAsLatLon() const {
			return m_nwLL;
		}

		LatLonCoord getSECoordAsLatLon() const {
			return m_seLL;
		}

		NormMercCoord getNWCoordAsNormMerc() const {
			return m_nwNM;
		}

		NormMercCoord getSECoordAsNormMerc() const {
			return m_seNM;
		}

		std::shared_ptr<EastNorthTrans> getEastNorthTransformer() const {
			return m_trans;
		}

		double dx() const {
			return m_seNM.getX() - m_nwNM.getX();
		}
	
		double dy() const {
			return m_seNM.getY() - m_nwNM.getY();
		}
	
		double dE() const {
			return m_seEN.getEast() - m_nwEN.getEast();
		}
	
		double dN() const {
			return m_nwEN.getNorth() - m_seEN.getNorth();
		}

		static bool intersecting(const BoundingBox& box1, const BoundingBox& box2) {
			EastNorthCoord nw1 = box1.getNWCoordAsEastNorth();
			EastNorthCoord se1 = box1.getSECoordAsEastNorth();
			EastNorthCoord nw2 = box2.getNWCoordAsEastNorth();
			EastNorthCoord se2 = box2.getSECoordAsEastNorth();

			if ( nw1.getEast() > se2.getEast() ) return false;
			if ( nw1.getNorth() < se2.getNorth() ) return false;
			if ( nw2.getEast() > se1.getEast() ) return false;
			if ( nw2.getNorth() < se1.getNorth() ) return false;

			return true;
		}

		static BoundingBox intersect(const BoundingBox& box1, const BoundingBox box2) {
			assertIntersecting(box1, box2);

			EastNorthCoord nw1 = box1.getNWCoordAsEastNorth();
			EastNorthCoord se1 = box1.getSECoordAsEastNorth();
			EastNorthCoord nw2 = box2.getNWCoordAsEastNorth();
			EastNorthCoord se2 = box2.getSECoordAsEastNorth();

			double nw_e = fmax(nw1.getEast(), nw2.getEast());
			double nw_n = fmin(nw1.getNorth(), nw2.getNorth());
			double se_e = fmin(se1.getEast(), se2.getEast());
			double se_n = fmax(se1.getNorth(), se2.getNorth());

			EastNorthCoord nw(nw_e, nw_n);
			EastNorthCoord se(se_e, se_n);
			std::shared_ptr<EastNorthTrans> trans = box1.getEastNorthTransformer();
			return BoundingBox(nw, se, trans);
		}

		static void assertIntersecting(const BoundingBox& box1, const BoundingBox box2) {
			if ( !intersecting(box1, box2) ) {
				MR4C_THROW(std::logic_error, "Bounding boxes [" << box1 << "] and [" << box2 << "] do not intersect");
			}
		}

		std::string str() const {
			MR4C_RETURN_STRING(
				"nwEN = " << m_nwEN << "; " <<
				"seEN = " << m_seEN << "; " <<
				"nwLL = " << m_nwLL << "; " <<
				"seLL = " << m_seLL << "; " <<
				"nwNM = " << m_nwNM << "; " <<
				"seNM = " << m_seNM
			);
		}

		~BoundingBoxImpl() {}

		bool operator==(const BoundingBoxImpl& box) const {
			if ( m_nwEN!=box.m_nwEN ) return false;
			if ( m_seEN!=box.m_seEN ) return false;
			if ( m_nwLL!=box.m_nwLL ) return false;
			if ( m_nwLL!=box.m_nwLL ) return false;
			if ( m_seNM!=box.m_seNM ) return false;
			if ( m_seNM!=box.m_seNM ) return false;
			return true;
		}

		static constexpr double EPS=1e-10;

};

BoundingBox::BoundingBox() {
	m_impl = new BoundingBoxImpl();
}

BoundingBox::BoundingBox(
	const EastNorthCoord& nw,
	const EastNorthCoord& se,
	std::shared_ptr<EastNorthTrans>& trans
) {
	m_impl = new BoundingBoxImpl(nw, se, trans);
}

BoundingBox::BoundingBox(
	const LatLonCoord& nw,
	const LatLonCoord& se,
	std::shared_ptr<EastNorthTrans>& trans
) {
	m_impl = new BoundingBoxImpl(nw, se, trans);
}

BoundingBox::BoundingBox(
	const NormMercCoord& nw,
	const NormMercCoord& se,
	std::shared_ptr<EastNorthTrans>& trans
) {
	m_impl = new BoundingBoxImpl(nw, se, trans);
}

BoundingBox::BoundingBox(const BoundingBox& box) {
	m_impl = new BoundingBoxImpl(box);
}

EastNorthCoord BoundingBox::getNWCoordAsEastNorth() const {
	return m_impl->getNWCoordAsEastNorth();
}

EastNorthCoord BoundingBox::getSECoordAsEastNorth() const {
	return m_impl->getSECoordAsEastNorth();
}

LatLonCoord BoundingBox::getNWCoordAsLatLon() const {
	return m_impl->getNWCoordAsLatLon();
}

LatLonCoord BoundingBox::getSECoordAsLatLon() const {
	return m_impl->getSECoordAsLatLon();
}

NormMercCoord BoundingBox::getNWCoordAsNormMerc() const {
	return m_impl->getNWCoordAsNormMerc();
}

NormMercCoord BoundingBox::getSECoordAsNormMerc() const {
	return m_impl->getSECoordAsNormMerc();
}

std::shared_ptr<EastNorthTrans> BoundingBox::getEastNorthTransformer() const {
	return m_impl->getEastNorthTransformer();
}

double BoundingBox::dx() const {
	return m_impl->dx();
}

double BoundingBox::dy() const {
	return m_impl->dy();
}

double BoundingBox::dE() const {
	return m_impl->dE();
}

double BoundingBox::dN() const {
	return m_impl->dN();
}

bool BoundingBox::intersecting(const BoundingBox& box1, const BoundingBox& box2) {
	return BoundingBoxImpl::intersecting(box1, box2);
}

BoundingBox BoundingBox::intersect(const BoundingBox& box1, const BoundingBox box2) {
	return BoundingBoxImpl::intersect(box1, box2);
}

std::string BoundingBox::str() const {
	return m_impl->str();
}

BoundingBox::~BoundingBox() {
	delete m_impl;
}

BoundingBox& BoundingBox::operator=(const BoundingBox& box) {
	m_impl->initFrom(box);
	return *this;
}

bool BoundingBox::operator==(const BoundingBox& box) const {
	return *m_impl==*box.m_impl;
}

bool BoundingBox::operator!=(const BoundingBox& box) const {
	return !operator==(box);
}

std::ostream& operator<<(std::ostream& os, const BoundingBox& box) {
	os << box.str();
	return os;
}


}
