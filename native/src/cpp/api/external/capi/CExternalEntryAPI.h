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

#ifndef __MR4C_C_EXTERNAL_ENTRY_API_H__
#define __MR4C_C_EXTERNAL_ENTRY_API_H__

#include "CExternalAlgorithmDataAPI.h"
#include "CExternalContextAPI.h"
#include "CExternalDatasetAPI.h"

#ifdef __cplusplus
extern "C" {
#endif

void CExternalEntry_dumpDataset(CExternalDatasetPtr datasetHandle);

CExternalDatasetPtr CExternalEntry_cloneDataset(CExternalDatasetPtr datasetHandle);

CExternalAlgorithmDataPtr CExternalEntry_cloneAlgorithmData(CExternalAlgorithmDataPtr algoDataHandle);

CExternalAlgorithmPtr CExternalEntry_getAlgorithm(const char* algoName);

bool CExternalEntry_executeAlgorithm(const char* algoName, CExternalAlgorithmDataPtr inputDataHandle, CExternalContextPtr contextHandle);

void CExternalEntry_pushEnvironmentProperties(const char* setName, const char* serializedProps);

void CExternalEntry_testLogging(CExternalContextPtr contextHandle, const char* level, const char* msg);

void CExternalEntry_testProgressReporting(CExternalContextPtr contextHandle, float percentDone, const char* msg);

void CExternalEntry_testFailureReporting(CExternalContextPtr contextHandle, const char* msg);

void CExternalEntry_testSendMessage(CExternalContextPtr contextHandle, const CExternalContextMessage& msg);

void CExternalEntry_testAddDataFile(CExternalDatasetPtr datasetHandle, CExternalDataFilePtr fileHandle, bool stream);

const char* CExternalEntry_testGetDataFileName(CExternalDatasetPtr datasetHandle, const char* serializedKey);

CExternalDataFilePtr CExternalEntry_testFindDataFile(CExternalDatasetPtr datasetHandle, const char* serializedKey);

bool CExternalEntry_testIsQueryOnly(CExternalDatasetPtr datasetHandle);

CExternalDataFilePtr CExternalEntry_testReadFileAsRandomAccess(CExternalDatasetPtr datasetHandle, const char* serializedKey);

void CExternalEntry_testWriteFileAsRandomAccess(CExternalDatasetPtr datasetHandle, CExternalDataFilePtr fileHandle);

void CExternalEntry_deleteLocalTempFiles();

const char** CExternalEntry_getLogFilePaths();

#ifdef __cplusplus
}
#endif 

#endif 

