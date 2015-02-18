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

#include "dataset/dataset_api.h"
#include "external/external_api.h"

CExternalRandomAccessFileSinkPtr CExternalRandomAccessFile_newRandomAccessFileSink(const CExternalRandomAccessFileCallbacks& callbacks) {
	MR4C::ExternalRandomAccessFileSink* sink = new MR4C::ExternalRandomAccessFileSink(callbacks);
	return wrapExternalRandomAccessFileSink(sink);
}

CExternalRandomAccessFileSinkPtr wrapExternalRandomAccessFileSink(MR4C::ExternalRandomAccessFileSink* sink) {
	if ( sink==NULL ) {
		return NULL;
	}
	CExternalRandomAccessFileSinkPtr handle = new CExternalRandomAccessFileSink();
	handle->sink = sink;
	return handle;
}

