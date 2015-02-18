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

CExternalAlgorithmDataPtr CExternalAlgorithmData_newAlgorithmData() {
	MR4C::ExternalAlgorithmData* algoData = new MR4C::ExternalAlgorithmData();
	return wrapExternalAlgorithmData(algoData);
}

void CExternalAlgorithmData_setSerializedKeyspace(CExternalAlgorithmDataPtr algoDataHandle, const char* keyspace) {
	algoDataHandle->algoData->setSerializedKeyspace(keyspace);
}

const char* CExternalAlgorithmData_getSerializedKeyspace(CExternalAlgorithmDataPtr algoDataHandle) {
	return algoDataHandle->algoData->getSerializedKeyspace();
}

void CExternalAlgorithmData_setSerializedConfig(CExternalAlgorithmDataPtr algoDataHandle, const char* config) {
	algoDataHandle->algoData->setSerializedConfig(config);
}

const char* CExternalAlgorithmData_getSerializedConfig(CExternalAlgorithmDataPtr algoDataHandle) {
	return algoDataHandle->algoData->getSerializedConfig();
}

void CExternalAlgorithmData_addInputDataset(CExternalAlgorithmDataPtr algoDataHandle, CExternalDatasetPtr datasetHandle) {
	algoDataHandle->algoData->addInputDataset(datasetHandle->dataset);
}

CExternalDatasetPtr CExternalAlgorithmData_getInputDataset(CExternalAlgorithmDataPtr algoDataHandle, int index) {
	MR4C::ExternalDataset* dataset = algoDataHandle->algoData->getInputDataset(index);
	return wrapExternalDataset(dataset);
}

size_t CExternalAlgorithmData_getInputDatasetCount(CExternalAlgorithmDataPtr algoDataHandle) {
	return algoDataHandle->algoData->getInputDatasetCount();
}

void CExternalAlgorithmData_addOutputDataset(CExternalAlgorithmDataPtr algoDataHandle, CExternalDatasetPtr datasetHandle) {
	algoDataHandle->algoData->addOutputDataset(datasetHandle->dataset);
}

CExternalDatasetPtr CExternalAlgorithmData_getOutputDataset(CExternalAlgorithmDataPtr algoDataHandle, int index) {
	MR4C::ExternalDataset* dataset = algoDataHandle->algoData->getOutputDataset(index);
	return wrapExternalDataset(dataset);
}

size_t CExternalAlgorithmData_getOutputDatasetCount(CExternalAlgorithmDataPtr algoDataHandle) {
	return algoDataHandle->algoData->getOutputDatasetCount();
}

CExternalAlgorithmDataPtr wrapExternalAlgorithmData(MR4C::ExternalAlgorithmData* algoData) {
	if ( algoData==NULL ) {
		return NULL;
	}
	CExternalAlgorithmDataPtr handle = new CExternalAlgorithmData();
	handle->algoData = algoData;
	return handle;
}


