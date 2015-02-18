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

#ifndef __MR4C_C_EXTERNAL_RANDOM_ACCESS_FILE_API_H__
#define __MR4C_C_EXTERNAL_RANDOM_ACCESS_FILE_API_H__

#include <stddef.h>

typedef struct CExternalRandomAccessFileSourceStruct *CExternalRandomAccessFileSourcePtr;

typedef struct CExternalRandomAccessFileSinkStruct *CExternalRandomAccessFileSinkPtr;

#ifdef __cplusplus
extern "C" {
#endif

/**
  * Function type for callback to read the next chunk of a file.
  * Returns true if success
  * @param buf array to read data into
  * @param num maximum number of bytes to read
  * @param read pointer to write number of bytes actually read
*/
typedef bool (*MR4CRandomAccessFileReadPtr)(
	char* buf,
	size_t num,
	size_t* read
);

/**
  * Function type for callback to write the next chunk of a file.
  * Returns number of bytes actually written.
  * Returns true if success
  * @param buf array to write data from
  * @param number of bytes to write
*/
typedef bool (*MR4CRandomAccessFileWritePtr)(
	char* buf,
	size_t num
);

/**
  * Function type for callback to get current file pointer location
  * Returns true if success
  * @param loc pointer to write file pointer location
*/
typedef bool (*MR4CRandomAccessFileGetLocationPtr)(
	size_t* loc
);

/**
  * Function type for callback to set current file pointer location
  * Returns true if success
  * @param loc file pointer location
*/
typedef bool (*MR4CRandomAccessFileSetLocationPtr)(
	size_t loc
);

/**
  * Function type for callback to get file size
  * Returns true if success
  * @param size pointer to write file size
*/
typedef bool (*MR4CRandomAccessFileGetSizePtr)(
	size_t* size
);

/**
  * Function type for callback to set file size
  * Returns true if success
  * @param size file size
*/
typedef bool (*MR4CRandomAccessFileSetSizePtr)(
	size_t size
);

/**
  * Function type for callback to close file
*/
typedef void (*MR4CRandomAccessFileClosePtr)();


struct CExternalRandomAccessFileCallbacksStruct {
	MR4CRandomAccessFileReadPtr readCallback;
	MR4CRandomAccessFileWritePtr writeCallback;
	MR4CRandomAccessFileGetLocationPtr getLocationCallback;
	MR4CRandomAccessFileSetLocationPtr setLocationCallback;
	MR4CRandomAccessFileSetLocationPtr setLocationFromEndCallback;
	MR4CRandomAccessFileSetLocationPtr skipForwardCallback;
	MR4CRandomAccessFileSetLocationPtr skipBackwardCallback;
	MR4CRandomAccessFileGetSizePtr getSizeCallback;
	MR4CRandomAccessFileSetSizePtr setSizeCallback;
	MR4CRandomAccessFileClosePtr closeCallback;
};

typedef struct CExternalRandomAccessFileCallbacksStruct CExternalRandomAccessFileCallbacks;

CExternalRandomAccessFileSourcePtr CExternalRandomAccessFile_newRandomAccessFileSource(
	const CExternalRandomAccessFileCallbacks& fileCallbacks
);

CExternalRandomAccessFileSinkPtr CExternalRandomAccessFile_newRandomAccessFileSink(
	const CExternalRandomAccessFileCallbacks& fileCallbacks
);

#ifdef __cplusplus
}
#endif 

#endif 

