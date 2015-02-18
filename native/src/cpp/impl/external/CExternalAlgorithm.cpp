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

CExternalAlgorithmPtr CExternalAlgorithm_newAlgorithm(const char* name) {
	MR4C::ExternalAlgorithm* algo = new MR4C::ExternalAlgorithm(name);
	return wrapExternalAlgorithm(algo);
}

const char* CExternalAlgorithm_getSerializedAlgorithm(CExternalAlgorithmPtr algoHandle) {
	return algoHandle->algo->getSerializedAlgorithm();
}

void  CExternalAlgorithm_setSerializedAlgorithm(
	CExternalAlgorithmPtr algoHandle,
	const char* serializedAlgo
) {
	algoHandle->algo->setSerializedAlgorithm(serializedAlgo);
}

CExternalAlgorithmPtr wrapExternalAlgorithm(MR4C::ExternalAlgorithm* algo) {
	if ( algo==NULL ) {
		return NULL;
	}
	CExternalAlgorithmPtr handle = new CExternalAlgorithm();
	handle->algo = algo;
	return handle;
}


