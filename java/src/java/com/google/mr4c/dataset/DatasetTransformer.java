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

package com.google.mr4c.dataset;

import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.KeyTransformer;
import com.google.mr4c.metadata.MetadataElement;
import com.google.mr4c.metadata.MetadataElementType;
import com.google.mr4c.metadata.MetadataKey;
import com.google.mr4c.metadata.MetadataList;
import com.google.mr4c.metadata.MetadataMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// transform a dataset from one set of keys to another via key mappings
public class DatasetTransformer {

	private KeyTransformer m_keyTrans;

	public DatasetTransformer(KeyTransformer keyTrans) {
		m_keyTrans = keyTrans;
		
	}

	public Dataset transformDataset(Dataset originalDataset, boolean readOnly) {

		Dataset newDataset = new Dataset();
		for ( DataKey origKey : originalDataset.getAllFileKeys() ) {
			DataFile file = originalDataset.getFile(origKey);
			DataKey newKey = m_keyTrans.transformKey(origKey);
			newDataset.addFile(newKey, file);
		}
		for ( DataKey origKey : originalDataset.getAllMetadataKeys() ) {
			MetadataMap origMeta = originalDataset.getMetadata(origKey);
			DataKey newKey = m_keyTrans.transformKey(origKey);
			MetadataMap newMeta = (MetadataMap) transformMetadata(origMeta, readOnly);
			newDataset.addMetadata(newKey, newMeta);
		}
		return newDataset;
	}

	private MetadataElement transformMetadata(MetadataElement meta, boolean readOnly) {
		switch ( meta.getMetadataElementType() ) {
			case FIELD :
			case ARRAY :
				return meta;
			case KEY :
				return transformMetaKey((MetadataKey) meta);
			case LIST:
				return transformMetaList((MetadataList) meta, readOnly);
			case MAP:
				return transformMetaMap((MetadataMap) meta, readOnly);
			default:
				throw new IllegalStateException("This is impossible");
		}
	}

	private MetadataKey transformMetaKey(MetadataKey metaKey) {
		DataKey origKey = metaKey.getKey();
		DataKey newKey = m_keyTrans.transformKey(origKey);
		return origKey.equals(newKey) ?
			metaKey :
			new MetadataKey(newKey);
	}

	private MetadataList transformMetaList(MetadataList metaList, boolean readOnly) {
		Map<Integer,MetadataElement> changes = new HashMap<Integer,MetadataElement>();
		for ( int index=0; index< metaList.getList().size(); index++ ) {
			MetadataElement element = metaList.getList().get(index);
			MetadataElement newElement = transformMetadata(element, readOnly);
			if ( !newElement.equals(element) ) {
				changes.put(index, newElement);
			}
		}

		if ( changes.isEmpty() ) {
			return metaList;
		}

		MetadataList newMetaList = readOnly ?
			// makes a new list with the same elements as the old, so we can replace them
			new MetadataList(new ArrayList<MetadataElement>(metaList.getList()) ) :
			// otherwise just replace directly
			metaList;

		for ( Integer index : changes.keySet() ) {
			MetadataElement element = changes.get(index);
			newMetaList.getList().set(index,element);
		}

		return newMetaList;
	}

	private MetadataMap transformMetaMap(MetadataMap metaMap, boolean readOnly) {
		Map<String,MetadataElement> changes = new HashMap<String,MetadataElement>();
		for ( String key : metaMap.getMap().keySet() ) {
			MetadataElement element = metaMap.getMap().get(key);
			MetadataElement newElement = transformMetadata(element, readOnly);
			if ( !newElement.equals(element) ) {
				changes.put(key, newElement);
			}
		}

		if ( changes.isEmpty() ) {
			return metaMap;
		}

		MetadataMap newMetaMap = readOnly ?
			// makes a new map with the same elements as the old, so we can replace them
			new MetadataMap(new HashMap<String,MetadataElement>(metaMap.getMap()) ) :
			// otherwise just replace directly
			metaMap;

		for ( String key : changes.keySet() ) {
			MetadataElement element = changes.get(key);
			newMetaMap.getMap().put(key,element);
		}

		return newMetaMap;
	}

}

