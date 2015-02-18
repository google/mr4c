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
#include <set>

#include "coord/coord_api.h"
#include "mbtiles/mbtiles_api.h"

namespace MR4C {

	int zoomToTileCount(int zoom) {
		return (int) pow(2, zoom);
	}

	int tileCountToZoom(int tiles) {
		double zoom = log(tiles) / log(2.0);
		return (int) ceil(zoom);
	}

	double zoomToTileSize(int zoom) {
		return 1.0 / zoomToTileCount(zoom);
	}

	int tileSizeToZoom(double size) {
		int tiles = (int) ceil(1.0 / size);
		return tileCountToZoom(tiles);
	}

	int flipTileY(int zoom, int y) {
		return zoomToTileCount(zoom) - y - 1;
	}

	int tileIndex(double location, int zoom) {
		int count = zoomToTileCount(zoom);
		return (int) floor(location * count);
	}

	BoundingBox toBoundingBox(const TileKey& tile, std::shared_ptr<EastNorthTrans>& trans) {
		int zoom = tile.getZoom();
		int count = zoomToTileCount(zoom);
		int x = tile.getX();
		int y = tile.getY();
		int flipY = flipTileY(zoom, y);
		NormMercCoord nw(
			(x+0.0)/count,
			(flipY+0.0)/count
		);
		NormMercCoord se(
			(x+1.0)/count,
			(flipY+1.0)/count
		);
		return BoundingBox(nw, se, trans);
	}

	BoundingBox toBoundingBox(const std::set<TileKey>& tiles, std::shared_ptr<EastNorthTrans>& trans) {
		double xmin = 1.0;
		double xmax = 0.0;
		double ymin = 1.0;
		double ymax = 0.0;
		for ( std::set<TileKey>::iterator iter = tiles.begin(); iter!=tiles.end(); iter++ ) {
			TileKey tile = *iter;
			BoundingBox bound = toBoundingBox(tile, trans);
			NormMercCoord nw = bound.getNWCoordAsNormMerc();
			NormMercCoord se = bound.getSECoordAsNormMerc();
			xmin = fmin(nw.getX(), xmin);
			xmax = fmax(se.getX(), xmax);
			ymin = fmin(nw.getY(), ymin);
			ymax = fmax(se.getY(), ymax);
		}
		NormMercCoord nw(xmin, ymin);
		NormMercCoord se(xmax, ymax);
		return BoundingBox(nw, se, trans);
	}

	std::set<TileKey> findTilesInBoundingBox(const BoundingBox& bound, int zoom) {
		std::set<TileKey> tiles;

		NormMercCoord nw = bound.getNWCoordAsNormMerc();
		NormMercCoord se = bound.getSECoordAsNormMerc();

		int xmin = tileIndex(nw.getX(), zoom);
		int xmax = tileIndex(se.getX(), zoom);
		int ymin = flipTileY(zoom, tileIndex(se.getY(), zoom));
		int ymax = flipTileY(zoom, tileIndex(nw.getY(), zoom));

		for ( int x = xmin ; x<=xmax; x++ ) {
			for ( int y = ymin ; y<=ymax; y++ ) {
				tiles.insert(TileKey(zoom, x, y));
			}
		}

		return tiles;
	}


}

