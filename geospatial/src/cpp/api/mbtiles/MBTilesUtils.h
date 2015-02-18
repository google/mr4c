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

#ifndef __MR4C_GEO_MBTILES_UTILS_H__
#define __MR4C_GEO_MBTILES_UTILS_H__

#include <set>

#include "coord/coord_api.h"

namespace MR4C {

	/**
	  * count of tiles at a zoom level (2^zoom)
	*/
	int zoomToTileCount(int zoom);

	/**
	  * zoom level for at least this many tiles
	*/
	int tileCountToZoom(int tiles);

	/**
	  * tile size at zoom level
	*/
	double zoomToTileSize(int zoom);

	/**
	  * zoom level for tiles this size or less
	*/
	int tileSizeToZoom(double size); 

	/**
	  * Return the Y coordinate "flipped" due to MBTiles spec having origin at SW corner instead of NW corner
	*/
	int flipTileY(int zoom, int y);

	/**
	  * what is the tile index at a given zoom level
	*/
	int tileIndex(double location, int zoom);


	/**
	  * bounding box for a tile
	*/
	BoundingBox toBoundingBox(const TileKey& tile, std::shared_ptr<EastNorthTrans>& trans);

	/**
	  * bounding box for a set of tiles
	*/
	BoundingBox toBoundingBox(const std::set<TileKey>& tiles, std::shared_ptr<EastNorthTrans>& trans);


	/**
	  * Finds all tiles that are at least partially in the bounding box for the zoom level
	*/
	std::set<TileKey> findTilesInBoundingBox(const BoundingBox& bound, int zoom);

}

#endif

