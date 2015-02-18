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

#include "external/external_api.h"

CExternalDatasetPtr CExternalDataset_newDataset(
	const char* name,
	const CExternalDatasetCallbacks& callbacks
) {
	MR4C::ExternalDataset* dataset = new MR4C::ExternalDataset();
	dataset->init(name, callbacks);
	return wrapExternalDataset(dataset);
}

const char* CExternalDataset_getName(CExternalDatasetPtr datasetHandle) {
	return datasetHandle->dataset->getName();
}

const char* CExternalDataset_getSerializedDataset(CExternalDatasetPtr datasetHandle) {
	return datasetHandle->dataset->getSerializedDataset();
}

void  CExternalDataset_setSerializedDataset(
	CExternalDatasetPtr datasetHandle,
	const char* serializedDataset
) {
	datasetHandle->dataset->setSerializedDataset(serializedDataset);
}

void CExternalDataset_addDataFile(
	CExternalDatasetPtr datasetHandle,
	CExternalDataFilePtr fileHandle
) {
	datasetHandle->dataset->addDataFile(fileHandle->file);
}

CExternalDataFilePtr CExternalDataset_getDataFile(
	CExternalDatasetPtr datasetHandle,
	int index
) {
	MR4C::ExternalDataFile* file = datasetHandle->dataset->getDataFile(index);
	return wrapExternalDataFile(file);
}

size_t CExternalDataset_getFileCount(CExternalDatasetPtr datasetHandle) {
	return datasetHandle->dataset->getFileCount();
}

CExternalDatasetPtr wrapExternalDataset(MR4C::ExternalDataset* dataset) {
	if ( dataset==NULL ) {
		return NULL;
	}
	CExternalDatasetPtr handle = new CExternalDataset();
	handle->dataset = dataset;
	return handle;
}


