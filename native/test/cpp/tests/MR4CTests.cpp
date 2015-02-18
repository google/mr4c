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

#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>

CPPUNIT_REGISTRY_ADD("AlgorithmTests", "MR4CTests");
CPPUNIT_REGISTRY_ADD("ContextTests", "MR4CTests");
CPPUNIT_REGISTRY_ADD("DatasetTests", "MR4CTests");
CPPUNIT_REGISTRY_ADD("ErrorTests", "MR4CTests");
CPPUNIT_REGISTRY_ADD("ExternalTests", "MR4CTests");
CPPUNIT_REGISTRY_ADD("KeysTests", "MR4CTests");
CPPUNIT_REGISTRY_ADD("MetadataTests", "MR4CTests");
CPPUNIT_REGISTRY_ADD("JsonTests", "MR4CTests");
CPPUNIT_REGISTRY_ADD("UtilTests", "MR4CTests");

namespace MR4C {

	std::string extractTestName(int argc, char* argv[]) {
		if ( argc==1 ) {
			return "MR4CTests";
		} else {
			return argv[1];
		}
	}
}


