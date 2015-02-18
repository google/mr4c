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

#ifndef __MR4C_GEO_MBTILES_DATASET_H__
#define __MR4C_GEO_MBTILES_DATASET_H__

#include <map>
#include <set>

#include "TileKey.h"
#include "dataset/dataset_api.h"
#include "keys/keys_api.h"
#include "metadata/metadata_api.h"

namespace MR4C {


class MBTilesDatasetImpl;

/**
  * Wrapper around a dataset to convert between mbtiles data and its representation in a mr4c dataset.
  *  Each DataKey in the dataset should map to a TileKey.
  *  Each DataFile contains the contents of a tile.
  *  Metadata should all be string fields in a map assigned to the empty key.
*/
class MBTilesDataset {

	public:

		MBTilesDataset(Dataset* dataset); 

		Dataset* getDataset() const;

		DataFile* getTile(const TileKey& key) const;

		void addTile(const TileKey& key, DataFile* file);

		bool hasTile(const TileKey& key) const;

		std::set<TileKey> getAllTileKeys() const;

		/**
		  * returns a copy of all the mbtiles metadata
		*/
		std::map<std::string,std::string> getMetadata() const;

		void addMetadata(const std::string& name, const std::string& value);

		/**
		  * Returns all the zoom levels in this dataset.
		  * This is populated at dataset creation time, so this is only valid for input datasets
		*/
		std::set<int> getZoomLevels() const;

		/**
		  * Returns all the X values of tiles for the given zoom level
		  * This is populated at dataset creation time, so this is only valid for input datasets
		*/
		std::set<int> getXValues(int zoom) const;

		/**
		  * Returns all the Y values of tiles for the given zoom level
		  * This is populated at dataset creation time, so this is only valid for input datasets
		*/
		std::set<int> getYValues(int zoom) const;


		~MBTilesDataset();

	private:

		MBTilesDatasetImpl* m_impl;

		// prevent calling these
		MBTilesDataset(const MBTilesDataset& dataset);
		MBTilesDataset& operator=(const MBTilesDataset& dataset);

};

}
#endif



