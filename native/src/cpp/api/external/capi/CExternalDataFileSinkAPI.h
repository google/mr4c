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

#ifndef __MR4C_C_EXTERNAL_DATA_FILE_SINK_API_H__
#define __MR4C_C_EXTERNAL_DATA_FILE_SINK_API_H__

#include <stddef.h>

typedef struct CExternalDataFileSinkStruct *CExternalDataFileSinkPtr;

#ifdef __cplusplus
extern "C" {
#endif

/**
  * Function type for callback to write the next chunk of a file.
  * Returns number of bytes actually written.
  * Returns true if success
  * @param buf array to write data from
  * @param number of bytes to write
*/
typedef bool (*MR4CDataSinkWritePtr)(
	char* buf,
	size_t num
);

/**
  * Function type for callback to close output file
*/
typedef void (*MR4CCloseDataSinkPtr)();


struct CExternalDataSinkCallbacksStruct {
	MR4CDataSinkWritePtr writeCallback;
	MR4CCloseDataSinkPtr closeCallback;
};

typedef struct CExternalDataSinkCallbacksStruct CExternalDataSinkCallbacks; 

CExternalDataFileSinkPtr CExternalDataFileSink_newDataFileSink(
	const CExternalDataSinkCallbacks& dataSinkCallbacks
);

#ifdef __cplusplus
}
#endif 

#endif 

