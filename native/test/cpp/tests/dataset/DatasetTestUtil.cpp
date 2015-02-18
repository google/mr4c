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

#include <map>
#include "dataset/DatasetTestUtil.h"
#include "dataset/dataset_api.h"
#include "keys/keys_api.h"
#include "metadata/metadata_api.h"
#include "util/util_api.h"

namespace MR4C {

DatasetTestUtil::DatasetTestUtil() {
	m_dim1 = DataKeyDimension("dim1");
	m_dim2 = DataKeyDimension("dim2");
}

DataFile* DatasetTestUtil::buildDataFile1() {
	char data[4] = { 5, 6, 7, 8 };
	return new DataFile(
		copyArray(data,4), 4,
		"image/png"
	);
}

DataFile* DatasetTestUtil::buildDataFile2() {
	char data[3] = { 32,-6, 0};
	return new DataFile(
		copyArray(data,3), 3,
		"image/png"
	);
}

DataFile* DatasetTestUtil::buildDataFile3() {
	char data[3] = { 0, 66, 127};
	return new DataFile(
		copyArray(data,3), 3,
		"image/png"
	);
}

Dataset* DatasetTestUtil::buildDataset1() {

		Dataset* dataset = new Dataset();

		DataKey key1(DataKeyElement("val1", m_dim1));
		dataset->addDataFile(key1, buildDataFile1());

		DataKey key11(DataKeyElement("val11", m_dim1));
		dataset->addDataFile(key11, buildDataFile2());

		DataKey key2(DataKeyElement("val2", m_dim2));
		MetadataField field = MetadataField::createString("some_data");
		int vals[5] = {1,3,5,7,9};
		MetadataArray array = MetadataArray::createInteger(vals,5);
		MetadataKey metaKey(key11);
		MetadataMap* map = new MetadataMap();
		map->putElement("some_name", field);
		map->putElement("an_array", array);
		map->putElement("a_key", metaKey);
		dataset->addMetadata(key2, map);
		

		DataKey key22(DataKeyElement("val22", m_dim2));
		MetadataField field2 = MetadataField::createDouble(3.14159);
		MetadataList* list = new MetadataList();
		list->addElement(field2);
		MetadataMap* map2 = new MetadataMap();
		map2->putElement("pi", list);
		dataset->addMetadata(key22, map2);

		DataKeyBuilder builder;
		builder.addAllElements(key1);
		builder.addAllElements(key2);
		DataKey key3 = builder.toKey();
		dataset->addDataFile(key3, buildDataFile3());
	
		return dataset;	

}


Dataset* DatasetTestUtil::buildDataset2() {

		Dataset* dataset = new Dataset();

		DataKey key1(DataKeyElement("val1", m_dim1));
		dataset->addDataFile(key1, buildDataFile1());

		DataKey key2(DataKeyElement("val2", m_dim2));
		MetadataField field = MetadataField::createString("whatever");
		MetadataMap* map = new MetadataMap();
		map->putElement("name", field);
		dataset->addMetadata(key2, map);

		return dataset;	

}

std::map<DataKey,MetadataMap*> DatasetTestUtil::buildMetadata() {

	std::map<DataKey,MetadataMap*> metas;

	DataKey key11(DataKeyElement("val11", m_dim1));
	DataKey key2(DataKeyElement("val2", m_dim2));

	MetadataField field = MetadataField::createString("some_data");
	int vals[5] = {1,3,5,7,9};
	MetadataArray array = MetadataArray::createInteger(vals,5);
	MetadataKey metaKey(key11);
	MetadataMap* map = new MetadataMap();
	map->putElement("some_name", field);
	map->putElement("an_array", array);
	map->putElement("a_key", metaKey);
	metas[key2] = map;

	DataKey key22(DataKeyElement("val22", m_dim2));
	MetadataField field2 = MetadataField::createDouble(3.14159);
	MetadataList* list = new MetadataList();
	list->addElement(field2);
	MetadataMap* map2 = new MetadataMap();
	map2->putElement("pi", list);
	metas[key22] = map2;

	return metas;

}

}

