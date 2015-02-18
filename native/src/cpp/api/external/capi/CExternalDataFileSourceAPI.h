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

#ifndef __MR4C_C_EXTERNAL_DATA_FILE_SOURCE_API_H__
#define __MR4C_C_EXTERNAL_DATA_FILE_SOURCE_API_H__

#include <stddef.h>

typedef struct CExternalDataFileSourceStruct *CExternalDataFileSourcePtr;

#ifdef __cplusplus
extern "C" {
#endif

/**
  * Function type for callback to get bytes of a file.
  * Returns NULL on error
*/
typedef char* (*MR4CGetDataSourceBytesPtr)();

/**
  * Function type for callback to get size of a file.
  * Returns true if success
  * @param size pointer to write file size
*/
// FIXED
typedef bool (*MR4CGetDataSourceSizePtr)(
	size_t* size
);

/**
  * Function type for callback to release file resources
*/
typedef void (*MR4CReleaseDataSourcePtr)();

/**
  * Function type for callback to read the next chunk of a file.
  * Returns true if success
  * @param buf array to read data into
  * @param num maximum number of bytes to read
  * @param read pointer to write number of bytes actually read
*/
// FIXED
typedef bool (*MR4CDataSourceReadPtr)(
	char* buf,
	size_t num,
	size_t *read
);

/**
  * Function type for callback to skip forward in a file
  * Returns true if success
  * @param num number of bytes to skip
  * @param skipped pointer to write number of bytes actually skipped
*/
// FIXED
typedef bool (*MR4CDataSourceSkipPtr)(
	size_t num,
	size_t *skipped
);

struct CExternalDataSourceCallbacksStruct {
	MR4CGetDataSourceBytesPtr getBytesCallback;
	MR4CGetDataSourceSizePtr getSizeCallback;
	MR4CReleaseDataSourcePtr releaseCallback;
	MR4CDataSourceReadPtr readCallback;
	MR4CDataSourceSkipPtr skipCallback;
};

typedef struct CExternalDataSourceCallbacksStruct CExternalDataSourceCallbacks; 

CExternalDataFileSourcePtr CExternalDataFileSource_newDataFileSource(
	const CExternalDataSourceCallbacks& dataSourceCallbacks
);

char* CExternalDataFileSource_getBytes(CExternalDataFileSourcePtr srcHandle);

size_t CExternalDataFileSource_getSize(CExternalDataFileSourcePtr srcHandle);

#ifdef __cplusplus
}
#endif 

#endif 

