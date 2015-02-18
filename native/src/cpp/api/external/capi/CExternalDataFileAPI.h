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

#ifndef __MR4C_C_EXTERNAL_DATA_FILE_API_H__
#define __MR4C_C_EXTERNAL_DATA_FILE_API_H__

#include <stddef.h>
#include "CExternalDataFileSourceAPI.h"
#include "CExternalDataFileSinkAPI.h"

typedef struct CExternalDataFileStruct *CExternalDataFilePtr;

#ifdef __cplusplus
extern "C" {
#endif

CExternalDataFilePtr CExternalDataFile_newDataFile(const char* key, const char* name);

const char* CExternalDataFile_getSerializedFile(CExternalDataFilePtr fileHandle);

void  CExternalDataFile_setSerializedFile(
	CExternalDataFilePtr fileHandle,
	const char* serializedFile
);

void CExternalDataFile_setFileSource(
		CExternalDataFilePtr fileHandle,
		CExternalDataFileSourcePtr sourceHandle
);

CExternalDataFileSourcePtr CExternalDataFile_getFileSource(CExternalDataFilePtr fileHandle);

void CExternalDataFile_setFileSink(
		CExternalDataFilePtr fileHandle,
		CExternalDataFileSinkPtr sinkHandle
);

CExternalDataFileSinkPtr CExternalDataFile_getFileSink(CExternalDataFilePtr fileHandle);


const char* CExternalDataFile_getSerializedKey(CExternalDataFilePtr fileHandle);

const char* CExternalDataFile_getFileName(CExternalDataFilePtr fileHandle);

#ifdef __cplusplus
}
#endif 

#endif 

