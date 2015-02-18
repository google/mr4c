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


CExternalContextPtr CExternalContext_newContext(
	const CExternalContextCallbacks& contextCallbacks
) {
	MR4C::ExternalContext* context = new MR4C::ExternalContext(contextCallbacks);
	return wrapExternalContext(context);
}

CExternalContextPtr wrapExternalContext(MR4C::ExternalContext* context) {
	if ( context==NULL ) {
		return NULL;
	}
	CExternalContextPtr handle = new CExternalContext();
	handle->context = context;
	return handle;
}

