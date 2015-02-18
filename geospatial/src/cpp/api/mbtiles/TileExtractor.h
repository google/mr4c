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

#ifndef __MR4C_GEO_TILE_EXTRACTOR_H__
#define __MR4C_GEO_TILE_EXTRACTOR_H__

#include <string>

#include "coord/coord_api.h"
#include "gdal/gdal_api.h"
#include "keys/keys_api.h"

namespace MR4C {

class TileExtractorImpl;


/**
  * Class to extract specified tiles from a geo-referenced image.
  * Input image is accessed via GDAL
*/
class TileExtractor {

	public:

		TileExtractor(
			const GDALFile& mosaicGDAL,
			const ImageBox& mosaicImageBox
		);

		/**
		  * Extract a specified tile.
		  * @param name name to use for the GDAL file
		  * @param format GDAL format for the tile
		*/
		GDALFile* extractTile(
			const TileKey& tile,
			const std::string& name,
			const std::string& format
		) const;


		~TileExtractor();

	private:

		TileExtractorImpl* m_impl;

		// prevent calling these
		TileExtractor();
		TileExtractor(const TileExtractor& ext);
		TileExtractor& operator=(const TileExtractor& ext);

};

}

#endif

