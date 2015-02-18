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

#include <iostream>
#include <map>
#include <vector>
#include <sstream>
#include <stdexcept>
#include <jansson.h>

#include "dataset/dataset_api.h"
#include "keys/keys_api.h"
#include "metadata/metadata_api.h"
#include "serialize/json/json_api.h"
#include "util/util_api.h"

namespace MR4C {

class JsonDatasetSerializerImpl {

	friend class JsonDatasetSerializer;

	private:

		std::string serializeDataset(const Dataset& dataset) const {
			json_t* jsonFiles = allFilesToJson(dataset);
			json_t* jsonMeta = allMetadataToJson(dataset);
			json_t* jsonDataset = json_object();
			json_object_set_new(jsonDataset, "files", jsonFiles);
			json_object_set_new(jsonDataset, "metadata", jsonMeta);

			return toStringAndFree(jsonDataset);
		}

		Dataset* deserializeDataset(const std::string& json) const {
			
			json_t* jsonDataset = fromString(json);
			json_t* jsonFiles = json_object_get(jsonDataset, "files");
			json_t* jsonMetas = json_object_get(jsonDataset, "metadata");
			Dataset* dataset = new Dataset();
			addAllFilesFromJson(dataset,jsonFiles);
			addAllMetadataFromJson(dataset,jsonMetas);
			json_decref(jsonDataset);
			return dataset;
		}

		std::string serializeDataFile(const DataFile& file) const {
			json_t* jsonFile = fileToJson(file);
			return toStringAndFree(jsonFile);
		}

		DataFile* deserializeDataFile(const std::string& data) const {
			json_t* jsonFile = fromString(data);
			DataFile* file = jsonToFile(jsonFile);
			json_decref(jsonFile);
			return file;
		}

		std::string serializeDataKey(const DataKey& key) const {
			json_t* jsonKey = keyToJson(key);
			return toStringAndFree(jsonKey);
		}

		DataKey deserializeDataKey(const std::string& data) const {
			json_t* jsonKey = fromString(data);
			DataKey key = jsonToKey(jsonKey);
			json_decref(jsonKey);
			return key;
		}

		std::string serializeMetadata(const std::map<DataKey,MetadataMap*>& metas) const {
			json_t* jsonMeta = metadataToJson(metas);
			return toStringAndFree(jsonMeta, JSON_INDENT(2)); // pretty print this one
		}

		std::map<DataKey,MetadataMap*> deserializeMetadata(const std::string& data) const {
			json_t* jsonMeta = fromString(data);
			std::map<DataKey,MetadataMap*>  meta = metadataFromJson(jsonMeta);
			json_decref(jsonMeta);
			return meta;
		}

		std::string toStringAndFree(json_t* jsonObject) const {
			return JanssonUtil::toStringAndFree(jsonObject);
		}

		std::string toStringAndFree(json_t* jsonObject, size_t flags) const {
			return JanssonUtil::toStringAndFree(jsonObject, flags);
		}

		json_t* fromString(const std::string& json) const {
			return JanssonUtil::fromString(json);
		}

		json_t* toJsonString(const std::string& str) const {
			return JanssonUtil::toJsonString(str);
		}

		std::string fromJsonString(const json_t* jsonString) const {
			return JanssonUtil::fromJsonString(jsonString);
		}

		json_t* allFilesToJson(const Dataset& dataset) const {
			std::set<DataKey> keys = dataset.getAllFileKeys();
			json_t* jsonFiles = json_array();
			for ( std::set<DataKey>::iterator iter = keys.begin(); iter!=keys.end(); iter++ ) {
				DataFile* file = dataset.getDataFile(*iter);
				json_array_append_new(jsonFiles, keyAndFileToJson(*iter,*file));
			}
			return jsonFiles;
		}

		void addAllFilesFromJson(Dataset* dataset, const json_t* jsonFiles) const {
			JanssonUtil::assertJsonType(jsonFiles, JSON_ARRAY);
			size_t size = json_array_size(jsonFiles);
			for (size_t i=0; i<size; i++ ) {
				json_t* jsonKeyAndFile = json_array_get(jsonFiles,i);
				JanssonUtil::assertJsonType(jsonKeyAndFile, JSON_OBJECT);
				json_t* jsonKey = json_object_get(jsonKeyAndFile, "key");
				json_t* jsonFile = json_object_get(jsonKeyAndFile, "file");
				DataKey key = jsonToKey(jsonKey);
				DataFile* file = jsonToFile(jsonFile);
				dataset->addDataFile(key,file);
			}
		}

		json_t* allMetadataToJson(const Dataset& dataset) const {
			std::set<DataKey> keys = dataset.getAllMetadataKeys();
			json_t* jsonMetas = json_array();
			for ( std::set<DataKey>::iterator iter = keys.begin(); iter!=keys.end(); iter++ ) {
				MetadataMap* meta = dataset.getMetadata(*iter);
				json_array_append_new(jsonMetas, keyAndMetadataToJson(*iter,*meta));
			}
			return jsonMetas;

		}

		json_t* metadataToJson(const std::map<DataKey,MetadataMap*>& metas) const {
			json_t* jsonMetas = json_array();
			for ( std::map<DataKey,MetadataMap*>::const_iterator iter = metas.begin(); iter!=metas.end(); iter++ ) {
				DataKey key = iter->first;
				MetadataMap* meta = iter->second;
				json_array_append_new(jsonMetas, keyAndMetadataToJson(key,*meta));
			}
			return jsonMetas;
		}

		void addAllMetadataFromJson(Dataset* dataset, const json_t* jsonMetas) const {
			JanssonUtil::assertJsonType(jsonMetas, JSON_ARRAY);
			size_t size = json_array_size(jsonMetas);
			for (size_t i=0; i<size; i++ ) {
				json_t* jsonKeyAndMeta = json_array_get(jsonMetas,i);
				std::pair<DataKey,MetadataMap*> pair = jsonToKeyAndMetadata(jsonKeyAndMeta);
				dataset->addMetadata(pair.first,pair.second);
			}
		}

		std::map<DataKey,MetadataMap*> metadataFromJson(const json_t* jsonMetas) const {
			JanssonUtil::assertJsonType(jsonMetas, JSON_ARRAY);
			size_t size = json_array_size(jsonMetas);
			std::map<DataKey,MetadataMap*> metas;
			for (size_t i=0; i<size; i++ ) {
				json_t* jsonKeyAndMeta = json_array_get(jsonMetas,i);
				std::pair<DataKey,MetadataMap*> pair = jsonToKeyAndMetadata(jsonKeyAndMeta);
				metas[pair.first] = pair.second;
			}
			return metas;
		}

		json_t* keyToJson(const DataKey& key) const {
			json_t* jsonKey = json_object();
			json_t* jsonElements = json_array();
			std::set<DataKeyElement> elements = key.getElements();
			for ( std::set<DataKeyElement>::iterator iter = elements.begin(); iter!=elements.end(); iter++ ) {
				json_t* jsonElement = JsonCommonSerializer::keyElementToJson(*iter);
				json_array_append_new(jsonElements, jsonElement);
			}
			json_object_set_new(jsonKey, "elements", jsonElements);
			return jsonKey;
		}

		DataKey jsonToKey(const json_t* jsonKey) const {
			JanssonUtil::assertJsonType(jsonKey, JSON_OBJECT);
			json_t* jsonElements = json_object_get(jsonKey, "elements");
			JanssonUtil::assertJsonType(jsonElements, JSON_ARRAY);
			size_t size = json_array_size(jsonElements);
			std::set<DataKeyElement> elements;
			for ( size_t i=0; i<size; i++ ) {
				json_t* jsonElement = json_array_get(jsonElements, i);
				elements.insert(JsonCommonSerializer::jsonToKeyElement(jsonElement));
			}
			return DataKey(elements);
		}


		json_t* fileToJson(const DataFile& file) const {
			json_t* jsonFile = json_object();
			json_object_set_new(jsonFile, "contentType", toJsonString(file.getContentType()));
			return jsonFile;
		}
			
		DataFile* jsonToFile(const json_t* jsonFile) const {
			JanssonUtil::assertJsonType(jsonFile, JSON_OBJECT);
			json_t* jsonContentType = json_object_get(jsonFile, "contentType");
			std::string contentType = fromJsonString(jsonContentType);
			return new DataFile(contentType);
		}

		json_t* keyAndFileToJson(const DataKey& key, const DataFile& file) const {
			json_t* jsonKeyAndFile = json_object();
			json_t* jsonKey = keyToJson(key);
			json_t* jsonFile = fileToJson(file);
			json_object_set_new(jsonKeyAndFile, "key", jsonKey);
			json_object_set_new(jsonKeyAndFile, "file", jsonFile);
			return jsonKeyAndFile;
		}

		json_t* metadataElementToJson(const MetadataElement& element) const {
			MetadataElement::Type type = element.getMetadataElementType();
			json_t* jsonElement;
			switch ( type ) {
				case MetadataElement::FIELD:
					jsonElement = metaFieldToJson(MetadataField::castToField(element));
					break;
				case MetadataElement::ARRAY:
					jsonElement = metaArrayToJson(MetadataArray::castToArray(element));

					break;
				case MetadataElement::LIST:
					jsonElement = metaListToJson(MetadataList::castToList(element));
					break;
				case MetadataElement::MAP:
					jsonElement = metaMapToJson(MetadataMap::castToMap(element));
					break;
				case MetadataElement::KEY:
					jsonElement = metaKeyToJson(MetadataKey::castToKey(element));
					break;
			}
			std::string strType = MetadataElement::enumToString(type);
			json_t* jsonType = toJsonString(strType);
			json_t* jsonEntry = json_object();
			json_object_set_new(jsonEntry, "element", jsonElement);
			json_object_set_new(jsonEntry, "elementType", jsonType);
			return jsonEntry;
		}

		MetadataElement* jsonToMetadataElement(const json_t* jsonMeta) const {
			JanssonUtil::assertJsonType(jsonMeta, JSON_OBJECT);
			json_t* jsonElement = json_object_get(jsonMeta, "element");
			json_t* jsonType = json_object_get(jsonMeta, "elementType");
			std::string strType = fromJsonString(jsonType);
			MetadataElement::Type type = MetadataElement::enumFromString(strType);
			MetadataElement* element;
			switch ( type ) {
				case MetadataElement::FIELD:
					element = new MetadataField(jsonToMetaField(jsonElement));
					break;
				case MetadataElement::ARRAY:
					element = new MetadataArray(jsonToMetaArray(jsonElement));
					break;
				case MetadataElement::LIST:
					element = jsonToMetaList(jsonElement);
					break;
				case MetadataElement::MAP:
					element = jsonToMetaMap(jsonElement);
					break;
				case MetadataElement::KEY:
					element = jsonToMetaKey(jsonElement);
					break;
				default:
					MR4C_THROW(std::runtime_error, "unknown metadata type [" << type << "]");
			}
			return element;
		}

		json_t* metaFieldToJson(const MetadataField& field) const {
			json_t* jsonField = json_object();
			json_t* jsonValue = toJsonString(field.toString());
			json_t* jsonPrimitiveType = toJsonString(Primitive::enumToString(field.getPrimitiveType()));
			json_object_set_new(jsonField, "value", jsonValue);
			json_object_set_new(jsonField, "primitiveType", jsonPrimitiveType);
			return jsonField;
		}

		MetadataField jsonToMetaField(const json_t* jsonField) const {
			JanssonUtil::assertJsonType(jsonField, JSON_OBJECT);
			json_t* jsonValue = json_object_get(jsonField, "value");
			json_t* jsonPrimitiveType = json_object_get(jsonField, "primitiveType");
			std::string strVal = fromJsonString(jsonValue);
			std::string strType = fromJsonString(jsonPrimitiveType);
			Primitive::Type primitiveType = Primitive::enumFromString(strType);
			return MetadataField::parseField(strVal, primitiveType);
		}

		json_t* metaArrayToJson(const MetadataArray& array) const {
			json_t* jsonArray = json_object();
			json_t* jsonValues = json_array();
			json_t* jsonPrimitiveType = toJsonString(Primitive::enumToString(array.getPrimitiveType()));
			std::vector<std::string> strVals(array.getSize());
			array.toString(strVals.data());
			for ( size_t i=0; i<array.getSize(); i++ ) {
				json_array_append_new(jsonValues, toJsonString(strVals[i]));
			}
			json_object_set_new(jsonArray, "values", jsonValues);
			json_object_set_new(jsonArray, "primitiveType", jsonPrimitiveType);
			return jsonArray;
		}

		MetadataArray jsonToMetaArray(const json_t* jsonArray) const {
			JanssonUtil::assertJsonType(jsonArray, JSON_OBJECT);
			json_t* jsonValues = json_object_get(jsonArray, "values");
			json_t* jsonPrimitiveType = json_object_get(jsonArray, "primitiveType");
			JanssonUtil::assertJsonType(jsonValues, JSON_ARRAY);
			size_t size = json_array_size(jsonValues);
			std::vector<std::string> strVals(size);
			for ( size_t i=0; i<size; i++ ) {
				json_t* jsonValue = json_array_get(jsonValues, i);
				strVals[i] = fromJsonString(jsonValue);
			}
			std::string strType = fromJsonString(jsonPrimitiveType);
			Primitive::Type primitiveType = Primitive::enumFromString(strType);
			MetadataArray array = MetadataArray::parseArray(strVals.data(), primitiveType, size);
			return array;
		}

		json_t* metaListToJson(const MetadataList& list) const {
			json_t* jsonList = json_object();
			json_t* jsonEntries = json_array();
			for ( size_t i=0; i<list.getSize(); i++ ) {
				json_array_append_new(jsonEntries, metadataElementToJson(*(list.getElement(i))));
			}
			json_object_set_new(jsonList, "entries", jsonEntries);
			return jsonList;
		}

		MetadataList* jsonToMetaList(const json_t* jsonList) const {
			JanssonUtil::assertJsonType(jsonList, JSON_OBJECT);
			json_t* jsonEntries = json_object_get(jsonList, "entries");
			JanssonUtil::assertJsonType(jsonEntries, JSON_ARRAY);
			size_t size = json_array_size(jsonEntries);
			MetadataList* list = new MetadataList();
			for ( size_t i=0; i<size; i++ ) {
				json_t* jsonEntry = json_array_get(jsonEntries, i);
				MetadataElement* element = jsonToMetadataElement(jsonEntry);
				list->addElement(element);
			}
			return list;
		}

		json_t* metaMapToJson(const MetadataMap& map) const {
			std::set<std::string> names = map.getAllNames();
			json_t* jsonMap = json_object();
			json_t* jsonEntries = json_object();
			for ( std::set<std::string>::iterator iter = names.begin(); iter!=names.end(); iter++ ) {
				MetadataElement* element = map.getElement(*iter);
				json_object_set_new(jsonEntries, iter->c_str(), metadataElementToJson(*element));
			}
			json_object_set_new(jsonMap, "entries", jsonEntries);
			return jsonMap;
		}

		MetadataMap* jsonToMetaMap(const json_t* jsonMap) const {
			JanssonUtil::assertJsonType(jsonMap, JSON_OBJECT);
			MetadataMap* map = new MetadataMap();
			json_t* jsonEntries = json_object_get(jsonMap, "entries");
			JanssonUtil::assertJsonType(jsonEntries, JSON_OBJECT);
			void* iter = json_object_iter(jsonEntries);
			while (iter) {
				std::string key = std::string(json_object_iter_key(iter));
				json_t* jsonElement = json_object_iter_value(iter);
				MetadataElement* element = jsonToMetadataElement(jsonElement);
				map->putElement(key, element);
				iter = json_object_iter_next(jsonEntries,iter);
			}
			return map;
		}

		json_t* metaKeyToJson(const MetadataKey& key) const {
			json_t* jsonMetaKey = json_object();
			json_t* jsonKey = keyToJson(key.getKey());
			json_object_set_new(jsonMetaKey, "key", jsonKey);
			return jsonMetaKey;
		}

		MetadataKey* jsonToMetaKey(const json_t* jsonMetaKey) const {
			JanssonUtil::assertJsonType(jsonMetaKey, JSON_OBJECT);
			json_t* jsonKey = json_object_get(jsonMetaKey, "key");
			DataKey key = jsonToKey(jsonKey);
			return new MetadataKey(key);
		}

		json_t* keyAndMetadataToJson(const DataKey& key, const MetadataMap& element)const {
			json_t* jsonKeyAndMeta = json_object();
			json_t* jsonKey = keyToJson(key);
			json_t* jsonMeta = metadataElementToJson(element);
			json_object_set_new(jsonKeyAndMeta, "key", jsonKey);
			json_object_set_new(jsonKeyAndMeta, "metadata", jsonMeta);
			return jsonKeyAndMeta;

		}

		std::pair<DataKey,MetadataMap*> jsonToKeyAndMetadata(const json_t* jsonKeyAndMeta) const {
			JanssonUtil::assertJsonType(jsonKeyAndMeta, JSON_OBJECT);
			json_t* jsonKey = json_object_get(jsonKeyAndMeta, "key");
			json_t* jsonMeta = json_object_get(jsonKeyAndMeta, "metadata");
			DataKey key = jsonToKey(jsonKey);
			MetadataElement* meta = jsonToMetadataElement(jsonMeta);
			MetadataMap* map = MetadataMap::castToMap(meta);
			return std::make_pair(key, map);
		}


};


JsonDatasetSerializer::JsonDatasetSerializer() {
	m_impl = new JsonDatasetSerializerImpl();
}

JsonDatasetSerializer::~JsonDatasetSerializer() {
	delete m_impl;
}

std::string JsonDatasetSerializer::serializeDataset(const Dataset& dataset) const {
	return m_impl->serializeDataset(dataset);
}

Dataset* JsonDatasetSerializer::deserializeDataset(const std::string& json) const {
	return m_impl->deserializeDataset(json);
}

std::string JsonDatasetSerializer::serializeDataFile(const DataFile& file) const {
	return m_impl->serializeDataFile(file);
}

DataFile* JsonDatasetSerializer::deserializeDataFile(const std::string& data) const {
	return m_impl->deserializeDataFile(data);
}

std::string JsonDatasetSerializer::serializeDataKey(const DataKey& key) const {
	return m_impl->serializeDataKey(key);
}

DataKey JsonDatasetSerializer::deserializeDataKey(const std::string& data) const {
	return m_impl->deserializeDataKey(data);
}

std::string JsonDatasetSerializer::serializeMetadata(const std::map<DataKey,MetadataMap*>& meta) const {
	return m_impl->serializeMetadata(meta);
}

std::map<DataKey,MetadataMap*> JsonDatasetSerializer::deserializeMetadata(const std::string& data) const {
	return m_impl->deserializeMetadata(data);
}

std::string JsonDatasetSerializer::getContentType() const {
	return "application/json";
}

}


