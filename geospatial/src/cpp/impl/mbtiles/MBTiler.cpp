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

#include <string>
#include <stdexcept>

#include "algo_dev_api.h"
#include "mr4c_geo_api.h"
#include "util/util_api.h"

namespace MR4C {

class MBTilerImpl {

		friend class MBTiler;

		private:

			AlgorithmData* m_data;
			AlgorithmContext* m_context;
			const AlgorithmConfig* m_config;
			Dataset* m_input;
			Dataset* m_output;
			int m_minZoom;
			int m_maxZoom;
			int m_minZoomAuto;
			int m_maxZoomAuto;
			std::string m_name;
			std::string m_desc;
			std::string m_type;
			std::string m_format;
			std::string m_gdalFormat;
			std::string m_metaBound;
			int m_version;
			DataFile* m_mosaic;
			GDALFile* m_mosaicGDAL;
			ImageBox m_mosaicImgBox;
			BoundingBox m_mosaicBound;
			BoundingBox m_tilesBound;
			MBTilesDataset* m_mbtiles;
			TileExtractor* m_extractor;


		MBTilerImpl(AlgorithmData& data, AlgorithmContext& context) {
			m_data = &data;
			m_context = &context;
		}

		void execute() {
			init();
			loadMosaic();
			computeZooms();
			buildTiles();
			addMetadata();
			m_mosaicGDAL->close();
		}

		void init() {
			m_input = m_data->getInputDataset("input");
			m_output = m_data->getOutputDataset("output");
			m_config = &m_data->getConfig();
			extractParameters();
			m_mbtiles = new MBTilesDataset(m_output);
		}

		void extractParameters() {
			extractName();
			extractDescription();
			extractType();
			extractVersion();
			extractFormat();
			extractMinZoom();
			extractMaxZoom();
		}

		void extractName() {
			m_name = m_config->getConfigParam("name");
			MR4C_ALGO_LOG_CPP(*m_context, Logger::INFO, "Name = " << m_name);
		}

		void extractDescription() {
			m_desc = m_config->hasConfigParam("description") ?
				m_config->getConfigParam("description") :
				"No description provided";
			MR4C_ALGO_LOG_CPP(*m_context, Logger::INFO, "Description = " << m_desc);
		}

		void extractType() {
			m_type = m_config->hasConfigParam("type") ?
				m_config->getConfigParam("type") :
				"overlay";
			MR4C_ALGO_LOG_CPP(*m_context, Logger::INFO, "Type = " << m_type);
			if ( m_type.compare("overlay")!=0 && m_type.compare("baselayer")!=0 ) {
				MR4C_THROW(std::invalid_argument, "Invalid type [" << m_type << "]; must be 'baselayer' or 'overlay'");
			}
		}

		void extractVersion() {
			m_version = m_config->hasConfigParam("version") ?
				m_config->getConfigParamAsInt("version") :
				1;
			MR4C_ALGO_LOG_C(*m_context, Logger::INFO, "Version = %i ", m_version);
		}

		void extractFormat() {
			m_format = m_config->hasConfigParam("format") ?
				m_config->getConfigParam("format") :
				"jpg";
			MR4C_ALGO_LOG_CPP(*m_context, Logger::INFO, "Format = " << m_format);
			if ( m_format.compare("jpg")!=0 && m_format.compare("png")!=0 ) {
				MR4C_THROW(std::invalid_argument, "Invalid format [" << m_format << "]; must be 'png' or 'jpg'");
			}
			if ( m_format.compare("jpg")==0 ) {
				m_gdalFormat = "JPEG";
			} else {
				m_gdalFormat = "PNG";
			}
		}
			
		void extractMinZoom() {
			m_minZoom=-1;
			if ( m_config->hasConfigParam("minZoom") ) {
				m_minZoom = m_config->getConfigParamAsInt("minZoom");
				MR4C_ALGO_LOG_C(*m_context, Logger::INFO, "Specified min zoom = %i ", m_minZoom);
			}
		}

		void extractMaxZoom() {
			m_maxZoom=-1;
			if ( m_config->hasConfigParam("maxZoom") ) {
				m_maxZoom = m_config->getConfigParamAsInt("maxZoom");
				MR4C_ALGO_LOG_C(*m_context, Logger::INFO, "Specified max zoom = %i ", m_maxZoom);
			}
		}

		void computeZooms() {
			computeMinZoom();
			computeMaxZoom();
		}

		void computeMinZoom() {
			int zoomX = tileSizeToZoom(m_mosaicBound.dx());
			int zoomY = tileSizeToZoom(m_mosaicBound.dy());
			m_minZoomAuto = std::min(zoomX, zoomY);
			MR4C_ALGO_LOG_C(*m_context, Logger::INFO, "Computed min zoom = %i ", m_minZoomAuto);
			if ( m_minZoom==-1 ) {
				m_minZoom = m_minZoomAuto;
				MR4C_ALGO_LOG_C(*m_context, Logger::INFO, "No min zoom specified; using computed value");
			}
		}

		void computeMaxZoom() {
			int zoomX = computeZoomForNoResize(m_mosaicBound.dx(), m_mosaicImgBox.getWidth());
			int zoomY = computeZoomForNoResize(m_mosaicBound.dy(), m_mosaicImgBox.getHeight());
			m_maxZoomAuto = std::max(zoomX, zoomY);
			MR4C_ALGO_LOG_C(*m_context, Logger::INFO, "Computed max zoom = %i ", m_maxZoomAuto);
			if ( m_maxZoom==-1 ) {
				m_maxZoom = m_maxZoomAuto;
				MR4C_ALGO_LOG_C(*m_context, Logger::INFO, "No max zoom specified; using computed value");
			}
		}

		int computeZoomForNoResize(double boundSize, int imagePixels) {
			double tileSize = boundSize  * 256.0 / imagePixels;
			return tileSizeToZoom(tileSize);
		}

		void loadMosaic() {
			DataKey key;
			m_mosaic = m_input->getDataFile(key);
			std::string tempDir = m_context->createTempDirectory();
			m_mosaicGDAL = new GDALLocalFile(tempDir, "mosaic.tif", *m_mosaic);
			m_mosaic->release(); // we have it in a local file now
			m_mosaicImgBox = locateGDALDataset(m_mosaicGDAL->getGDALDataset());
			m_mosaicBound = m_mosaicImgBox.getBound();
			
			MR4C_ALGO_LOG_CPP(*m_context, Logger::INFO, "Mosaic Image Bounds : " << m_mosaicImgBox);
			m_extractor = new TileExtractor(*m_mosaicGDAL, m_mosaicImgBox);
		}

		void buildTiles() {
			for ( int zoom=m_minZoom; zoom<=m_maxZoom; zoom++ ) {
				std::set<TileKey> tiles = findTilesInBoundingBox(m_mosaicBound, zoom); 
				logTiles(zoom, tiles);
				if ( zoom==m_minZoom ) {
					computeTilesBound(tiles);
				}
				for ( std::set<TileKey>::iterator iter = tiles.begin(); iter!=tiles.end(); iter++ ) {
					TileKey tile = *iter;
					generateTile(tile);
				}
			}
		}

		void computeTilesBound(const std::set<TileKey>& tiles) {
			std::shared_ptr<EastNorthTrans> trans = m_mosaicBound.getEastNorthTransformer();
			m_tilesBound = toBoundingBox(tiles, trans);
			LatLonCoord nw = m_tilesBound.getNWCoordAsLatLon();
			LatLonCoord se = m_tilesBound.getSECoordAsLatLon();
			double left = nw.getLonDegrees();
			double bottom = se.getLatDegrees();
			double right = se.getLonDegrees();
			double top = nw.getLatDegrees();
			m_metaBound =
				std::to_string(left) + "," + 
				std::to_string(bottom) + "," + 
				std::to_string(right) + "," + 
				std::to_string(top);
			MR4C_ALGO_LOG_CPP(*m_context, Logger::INFO, "Bound for metadata : " << m_metaBound);
		}

		void addMetadata() {
			m_mbtiles->addMetadata("name", m_name);
			m_mbtiles->addMetadata("description", m_desc);
			m_mbtiles->addMetadata("type", m_type);
			m_mbtiles->addMetadata("format", "png");
			m_mbtiles->addMetadata("version", std::to_string(m_version));
			m_mbtiles->addMetadata("bounds", m_metaBound);
		}

		void logTiles(int zoom, const std::set<TileKey>& tiles) {
			MR4C_ALGO_LOG_CPP(*m_context, Logger::INFO, "Begin tiles for zoom level " << zoom);
			for ( std::set<TileKey>::iterator iter = tiles.begin(); iter!=tiles.end(); iter++ ) {
				MR4C_ALGO_LOG_CPP(*m_context, Logger::INFO, *iter);
			}
			MR4C_ALGO_LOG_CPP(*m_context, Logger::INFO, "End tiles for zoom level " << zoom);
		}
			
		void generateTile(TileKey tile) {
			MR4C_ALGO_LOG_CPP(*m_context, Logger::INFO, "Generating tile " << tile);
			GDALFile* gdalTileFile = m_extractor->extractTile(tile, "tile", m_gdalFormat);
			gdalTileFile->close();
			DataFile* file = gdalTileFile->toDataFile("image/png");
			m_mbtiles->addTile(tile, file);
			gdalTileFile->deleteFile();
			delete gdalTileFile;
		}

		~MBTilerImpl() {
			delete m_extractor;
			delete m_mosaicGDAL;
			delete m_mbtiles;
		}
};

class MBTiler : public Algorithm {

	public:
		MBTiler() {
		}

		static Algorithm* create() {
			MBTiler* algo = new MBTiler();
			algo->addInputDataset("input");
			algo->addOutputDataset("output");
			return algo;
		}

		void executeAlgorithm(AlgorithmData& data, AlgorithmContext& context) {
			MBTilerImpl* impl = new MBTilerImpl(data,context);
			impl->execute();
			delete impl;
		}
			
};

MR4C_REGISTER_ALGORITHM(mbtiler, MBTiler::create()); 

}
