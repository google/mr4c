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

#ifndef __KEYSPACE_TEST_UTIL_H__
#define __KEYSPACE_TEST_UTIL_H__

#include "keys/keys_api.h"

namespace MR4C {

class KeyspaceTestUtil {


	private:

		DataKeyDimension m_dim1;
		DataKeyDimension m_dim2;
		DataKeyDimension m_dim3;

		DataKeyElement m_ele1a;
		DataKeyElement m_ele1b;
		DataKeyElement m_ele1c;
		DataKeyElement m_ele2a;
		DataKeyElement m_ele2b;
		DataKeyElement m_ele3;

	public:

		KeyspaceTestUtil();

		Keyspace* buildKeyspace1();

		Keyspace* buildKeyspace2();

};

}

#endif
