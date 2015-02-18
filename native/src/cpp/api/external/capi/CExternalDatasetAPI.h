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

#ifndef __MR4C_C_EXTERNAL_DATASET_API_H__
#define __MR4C_C_EXTERNAL_DATASET_API_H__

#include <stddef.h>
#include "CExternalDataFileAPI.h"
#include "CExternalRandomAccessFileAPI.h"

typedef struct CExternalDatasetStruct *CExternalDatasetPtr;

#ifdef __cplusplus
extern "C" {
#endif

/**
  * Function type for callback to find a file by key.
  * File will be added to dataset if found.
  * Returns null if no such file
*/
typedef CExternalDataFilePtr (*CExternalFindFunctionPtr) (
	const char* serializedKey
);

/**
  * Function type for callback to add a file.
  * If the file has a source, data will be written from the source.
  * Otherwise, a sink will be added to the file.
  * File will be added to dataset.
  * Returns false if the add failed
*/
typedef bool (*CExternalAddFunctionPtr) (
	CExternalDataFilePtr file
);

/**
  * Function type for callback to get an output file name
*/
typedef const char* (*CExternalFileNameFunctionPtr) (
	const char* serializedKey
);

/**
  * Function type for callback to ask if a dataset is query-only
*/
typedef bool (*CExternalQueryOnlyFunctionPtr) ();


/**
  * Function type for callback to provide read-only random access to a file
  * Returns null if no such file
*/
typedef CExternalRandomAccessFileSourcePtr (*CExternalRandomAccessSourceFunctionPtr) (
	const char* serializedKey
);

/**
  * Function type for callback to provide writable random access to a file
  * Returns null if no such file
*/
typedef CExternalRandomAccessFileSinkPtr (*CExternalRandomAccessSinkFunctionPtr) (
	const char* serializedKey
);


struct CExternalDatasetCallbacksStruct {
	CExternalFindFunctionPtr findFileCallback;
	CExternalAddFunctionPtr addFileCallback;
	CExternalFileNameFunctionPtr getFileNameCallback;
	CExternalQueryOnlyFunctionPtr isQueryOnlyCallback;
	CExternalRandomAccessSourceFunctionPtr randomAccessSourceCallback;
	CExternalRandomAccessSinkFunctionPtr randomAccessSinkCallback;
};

typedef struct CExternalDatasetCallbacksStruct CExternalDatasetCallbacks; 


CExternalDatasetPtr CExternalDataset_newDataset(
	const char* name,
	const CExternalDatasetCallbacks &callbacks
);

const char* CExternalDataset_getName(CExternalDatasetPtr datasetHandle);

const char* CExternalDataset_getSerializedDataset(CExternalDatasetPtr datasetHandle);

void  CExternalDataset_setSerializedDataset(
	CExternalDatasetPtr datasetHandle,
	const char* serializedDataset
);

void CExternalDataset_addDataFile(
	CExternalDatasetPtr datasetHandle,
	CExternalDataFilePtr fileHandle
);

CExternalDataFilePtr CExternalDataset_getDataFile(
	CExternalDatasetPtr datasetHandle,
	int index
);

size_t CExternalDataset_getFileCount(CExternalDatasetPtr datasetHandle);

#ifdef __cplusplus
}
#endif 

#endif 

