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

#include "gdal/gdal_api.h"
#include "mbtiles/mbtiles_api.h"

#include <iostream>

namespace MR4C {

class TileExtractorImpl {

	friend class TileExtractor;

	private :

		const GDALFile* m_mosaicGDAL;
		ImageBox m_mosaicImageBox;
		BoundingBox m_mosaicBound;
		std::shared_ptr<EastNorthTrans> m_trans;
		int m_bands;

		TileExtractorImpl(
			const GDALFile& mosaicGDAL,
			const ImageBox& mosaicImageBox
		) {
			m_mosaicGDAL = &mosaicGDAL;
			m_mosaicImageBox = mosaicImageBox;
			m_mosaicBound = m_mosaicImageBox.getBound();
			m_trans = m_mosaicBound.getEastNorthTransformer();
			m_bands = m_mosaicGDAL->getGDALDataset()->GetRasterCount();
		}


		GDALFile* extractTile(
			const TileKey& tile,
			const std::string& name,
			const std::string& format
		) const {

			// locate the tile
			std::shared_ptr<EastNorthTrans> trans = m_trans; // seem to need this to compile, not sure why
			BoundingBox tileBound = toBoundingBox(tile, trans);
			ImageBox tileImageBox(256, 256, tileBound);


			// work with a TIFF because we can't make JPEG or PNG without copying
			GDALMemoryFile gdalTiffTileFile("tile_tiff");
			GDALDataset* gdalTiffTile = newGDALDataset(gdalTiffTileFile.getPath(), "GTiff", 256, 256, m_bands, GDT_Byte);
			gdalTiffTileFile.setGDALDataset(gdalTiffTile);
			setNoDataValue(gdalTiffTile, 0.0);
		
			// copy intersection of tile and mosaic into tile	
			BoundingBox intersect = BoundingBox::intersect(tileBound, m_mosaicBound);
			ImageBox imgInMosaic = m_mosaicImageBox.window(intersect);
			ImageBox imgInTile = tileImageBox.window(intersect);
			copyGDALImage(m_mosaicGDAL->getGDALDataset(), imgInMosaic, gdalTiffTile, imgInTile);

			// Make a file in the format we want
			GDALFile* gdalTileFile = copyGDALFileToMemory(name, gdalTiffTileFile, format);
			gdalTiffTileFile.close();
			gdalTiffTileFile.deleteFile();

			return gdalTileFile;
		}

};


TileExtractor::TileExtractor(
	const GDALFile& mosaicGDAL,
	const ImageBox& mosaicImageBox
) {
	m_impl = new TileExtractorImpl(mosaicGDAL, mosaicImageBox);
}

GDALFile* TileExtractor::extractTile(
	const TileKey& tile,
	const std::string& name,
	const std::string& format
) const {
	return m_impl->extractTile(tile, name, format);
}

TileExtractor::~TileExtractor() {
	delete m_impl;
}

}
