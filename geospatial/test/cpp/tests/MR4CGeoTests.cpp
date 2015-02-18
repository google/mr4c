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

CPPUNIT_REGISTRY_ADD("CoordTests", "MR4CGeoTests");
CPPUNIT_REGISTRY_ADD("GDALTests", "MR4CGeoTests");
CPPUNIT_REGISTRY_ADD("MBTilesTests", "MR4CGeoTests");

namespace MR4C {

	std::string extractTestName(int argc, char* argv[]) {
		if ( argc==1 ) {
			return "MR4CGeoTests";
		} else {
			return argv[1];
		}
	}
}


