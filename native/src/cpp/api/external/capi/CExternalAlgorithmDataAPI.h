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

#ifndef __MR4C_C_EXTERNAL_ALGORITHM_DATA_API_H__
#define __MR4C_C_EXTERNAL_ALGORITHM_DATA_API_H__

#include <stddef.h>
#include "CExternalDatasetAPI.h"

typedef struct CExternalAlgorithmDataStruct *CExternalAlgorithmDataPtr;

#ifdef __cplusplus
extern "C" {
#endif

CExternalAlgorithmDataPtr CExternalAlgorithmData_newAlgorithmData();

void CExternalAlgorithmData_setSerializedKeyspace(CExternalAlgorithmDataPtr algoDataHandle, const char* keyspace);

const char* CExternalAlgorithmData_getSerializedKeyspace(CExternalAlgorithmDataPtr algoDataHandle);

void CExternalAlgorithmData_setSerializedConfig(CExternalAlgorithmDataPtr algoDataHandle, const char* config);

const char* CExternalAlgorithmData_getSerializedConfig(CExternalAlgorithmDataPtr algoDataHandle);

void CExternalAlgorithmData_addInputDataset(CExternalAlgorithmDataPtr algoDataHandle, CExternalDatasetPtr datasetHandle);

CExternalDatasetPtr CExternalAlgorithmData_getInputDataset(CExternalAlgorithmDataPtr algoDataHandle, int index);

size_t CExternalAlgorithmData_getInputDatasetCount(CExternalAlgorithmDataPtr algoDataHandle);

void CExternalAlgorithmData_addOutputDataset(CExternalAlgorithmDataPtr algoDataHandle, CExternalDatasetPtr datasetHandle);

CExternalDatasetPtr CExternalAlgorithmData_getOutputDataset(CExternalAlgorithmDataPtr algoDataHandle, int index);

size_t CExternalAlgorithmData_getOutputDatasetCount(CExternalAlgorithmDataPtr algoDataHandle);

#ifdef __cplusplus
}
#endif 

#endif 

