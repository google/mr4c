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

#ifndef __MR4C_C_EXTERNAL_CONTEXT_API_H__
#define __MR4C_C_EXTERNAL_CONTEXT_API_H__

typedef struct CExternalContextStruct *CExternalContextPtr;

#ifdef __cplusplus
extern "C" {
#endif

struct CExternalContextMessageStruct {
	const char* topic;
	const char* contentType;
	const char* content;
};

typedef struct CExternalContextMessageStruct CExternalContextMessage; 

/**
  * Function type for callback to receive logging
  * @param level One of INFO, ERROR, DEBUG, WARN
*/
typedef void (*CExternalLogFunctionPtr) (
	const char* level, 
	const char* message
);

/**
  * Function type for callback to receive progress reporting
*/
typedef void (*CExternalProgressFunctionPtr) (
	float percentDone,
	const char* message
);

/**
  * Function type for callback to receive messages to topics
*/
typedef void (*CExternalMessageFunctionPtr) (
	const CExternalContextMessage& message
);


/**
  * Function type for callback to report failure
*/
typedef void (*CExternalFailureFunctionPtr) (
	const char* message
);

struct CExternalContextCallbacksStruct {
	CExternalLogFunctionPtr logCallback;
	CExternalProgressFunctionPtr progressCallback;
	CExternalMessageFunctionPtr messageCallback;
	CExternalFailureFunctionPtr failureCallback;
};

typedef struct CExternalContextCallbacksStruct CExternalContextCallbacks; 

CExternalContextPtr CExternalContext_newContext(
	const CExternalContextCallbacks& contextCallbacks
);

#ifdef __cplusplus
}
#endif 

#endif 

