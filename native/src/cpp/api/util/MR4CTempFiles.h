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

#ifndef __MR4C_MR4C_TEMP_FILES_H__
#define __MR4C_MR4C_TEMP_FILES_H__

#include <string>
#include "util/Properties.h"

namespace MR4C {

class MR4CTempFilesImpl;

/**
  * Singleton for managing environmental properties
*/

class MR4CTempFiles {

	public :

		static MR4CTempFiles& instance();

		std::string createTempDirectory(const std::string& parent);

		std::set<std::string> getAllocatedDirectories();

		void deleteAllocatedDirectories();

		~MR4CTempFiles();

	private:

		MR4CTempFilesImpl* m_impl;

		MR4CTempFiles();
		MR4CTempFiles(const MR4CTempFiles& temp);
		MR4CTempFiles& operator=(const MR4CTempFiles& temp);

};

}

#endif
