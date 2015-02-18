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

#ifndef __MR4C_GEO_TILE_KEY_H__
#define __MR4C_GEO_TILE_KEY_H__

#include <string>
#include <ostream>

#include "keys/keys_api.h"

namespace MR4C {

class TileKeyImpl;


/**
  * Type-safe key for tiles that maps to a DataKey with dimensions ZOOM, X, and Y
*/
class TileKey {

	public:

		enum Dimension {
			ZOOM, 
			X, 
			Y
		};

		/**
		  * Parses the string equivalent of the enum.
		  * For example: "ZOOM" --> ZOOM
		*/
		static Dimension dimFromString(std::string strDim);

		/**
		  * Returns the string equivalent of the enum.
		  * For example: ZOOM --> "ZOOM"
		*/
		static std::string dimToString(Dimension dim);


		TileKey();

		TileKey(
			int zoom,
			int x,
			int y
		);

		TileKey(const TileKey& tileKey);

		/**
		  * Create from a data key with dimensions ZOOM, X, and Y.
		  * Element values must be integers.
		*/
		TileKey(const DataKey& dataKey);

		int getZoom() const;

		int getX() const;

		int getY() const;

		/**
		  * Conversion to a data key with dimensions ZOOM, X, and Y
		*/
		DataKey toDataKey() const;

		std::string str() const;

		~TileKey();

		TileKey& operator=(const TileKey& key);

		bool operator==(const TileKey& key) const;
		bool operator!=(const TileKey& key) const;
		bool operator<(const TileKey& key) const;

	private:

		TileKeyImpl* m_impl;


};

std::ostream& operator<<(std::ostream& os, const TileKey& key);

}

#endif

