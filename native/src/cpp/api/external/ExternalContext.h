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

#ifndef __MR4C_EXTERNAL_CONTEXT_H__
#define __MR4C_EXTERNAL_CONTEXT_H__

#include "context/context_api.h"
#include "CExternalContext.h"

namespace MR4C {


class ExternalContextImpl;

class ExternalContext {

	public:

		ExternalContext(const CExternalContextCallbacks& callbacks);

		void reportFailure(const char* msg);

		void initContext(AlgorithmContext& context);

		~ExternalContext();

	private:

		ExternalContextImpl* m_impl;

		// prevent calling these
		ExternalContext(const ExternalContext& context);
		ExternalContext& operator=(const ExternalContext& context);

};

}
#endif



