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

#include <algorithm>
#include <iterator>
#include <map>
#include <set>
#include <stdexcept>

#include "dataset/dataset_api.h"
#include "mbtiles/mbtiles_api.h"
#include "util/util_api.h"

namespace MR4C {

class MBTilesDatasetImpl {

	friend class MBTilesDataset;

	private:

		Dataset* m_dataset;

		std::set<int> m_zooms;
		std::map<int,std::set<int>*> m_xByZoom;
		std::map<int,std::set<int>*> m_yByZoom;


		MBTilesDatasetImpl(Dataset* dataset) {
			m_dataset = dataset;
			init();
		}

		void init() {
			std::set<DataKey> keys = m_dataset->getAllFileKeys();
			for ( std::set<DataKey>::iterator iter = keys.begin(); iter!=keys.end(); iter++ ) {
				TileKey tileKey(*iter);
				int zoom = tileKey.getZoom();
				m_zooms.insert(zoom);
				addTileCoord(m_xByZoom, zoom, tileKey.getX());
				addTileCoord(m_yByZoom, zoom, tileKey.getY());
			}
		}

		void addTileCoord(std::map<int,std::set<int>*>& map, int zoom, int val) {
			std::set<int>* set = map[zoom];
			if ( set==NULL ) {
				set = new std::set<int>();
				map[zoom] = set;
			}
			set->insert(val);
		}

		Dataset* getDataset() const {
			return m_dataset;
		}

		bool hasTile(const TileKey& key) const {
			return m_dataset->hasDataFile(key.toDataKey());
		}

		DataFile* getTile(const TileKey& key) const {
			return m_dataset->getDataFile(key.toDataKey());
		}

		void addTile(const TileKey& key, DataFile* file) {
			m_dataset->addDataFile(key.toDataKey(), file);
		}

		std::set<TileKey> getAllTileKeys() const {
			std::set<DataKey> fileKeys = m_dataset->getAllFileKeys();
			std::set<TileKey> tileKeys;
			std::transform(fileKeys.begin(), fileKeys.end(), std::inserter(tileKeys, tileKeys.begin()), toTileKey);
			return tileKeys;
		}

		// this is the passed in function for transform call above
		static TileKey toTileKey(const DataKey& key) {
			return TileKey(key);
		}

		std::map<std::string,std::string> getMetadata() const {
			std::map<std::string,std::string> map;
			DataKey key;
			if ( !m_dataset->hasMetadata(key) ) {
				return map;
			}
			MetadataMap* metaMap = m_dataset->getMetadata(key);
			std::set<std::string> names = metaMap->getAllNames();
			for ( std::set<std::string>::iterator iter = names.begin(); iter!=names.end(); iter++ ) {
				std::string name = *iter;
				MetadataField field = *MetadataField::castToField(metaMap->getElement(name));
				map[name] = field.getStringValue();
			}
			return map;
		}
			
		void addMetadata(const std::string& name, const std::string& value) {
			DataKey key;
			MetadataMap* metaMap = NULL;
			if ( !m_dataset->hasMetadata(key) ) {
				metaMap = new MetadataMap();
				m_dataset->addMetadata(DataKey(), metaMap);
			} else {
				metaMap = m_dataset->getMetadata(key);
			}
			MetadataField field = MetadataField::createString(value);
			metaMap->putElement(name, field);
		}

		std::set<int> getZoomLevels() const {
			return m_zooms;
		}

		std::set<int> getXValues(int zoom) const {
			return m_xByZoom.count(zoom) > 0 ?
				*m_xByZoom.at(zoom) :
				std::set<int>();
		}

		std::set<int> getYValues(int zoom) const {
			return m_yByZoom.count(zoom) > 0 ?
				*m_yByZoom.at(zoom) :
				std::set<int>();
		}

		~MBTilesDatasetImpl() {
			deleteMapOfPointers<int,std::set<int>>(m_xByZoom);
			deleteMapOfPointers<int,std::set<int>>(m_yByZoom);
		} 

};

MBTilesDataset::MBTilesDataset(Dataset* dataset) {
	m_impl = new MBTilesDatasetImpl(dataset);
}

Dataset* MBTilesDataset::getDataset() const {
	return m_impl->getDataset();
}

DataFile* MBTilesDataset::getTile(const TileKey& key) const {
	return m_impl->getTile(key);
}

void MBTilesDataset::addTile(const TileKey& key, DataFile* file) {
	return m_impl->addTile(key, file);
}

bool MBTilesDataset::hasTile(const TileKey& key) const {
	return m_impl->hasTile(key);
}

std::set<TileKey> MBTilesDataset::getAllTileKeys() const {
	return m_impl->getAllTileKeys();
}

std::map<std::string,std::string> MBTilesDataset::getMetadata() const {
	return m_impl->getMetadata();
}

void MBTilesDataset::addMetadata(const std::string& name, const std::string& value) {
	return m_impl->addMetadata(name, value);
}

std::set<int> MBTilesDataset::getZoomLevels() const {
	return m_impl->getZoomLevels();
}

std::set<int> MBTilesDataset::getXValues(int zoom) const {
	return m_impl->getXValues(zoom);
}

std::set<int> MBTilesDataset::getYValues(int zoom) const {
	return m_impl->getYValues(zoom);
}

MBTilesDataset::~MBTilesDataset() {
	delete m_impl;
} 

}
