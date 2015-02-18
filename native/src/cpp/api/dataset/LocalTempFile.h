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

#ifndef __MR4C_LOCAL_TEMP_FILE_H__
#define __MR4C_LOCAL_TEMP_FILE_H__

#include <string>
//#include <memory>
//#include "DataFileSource.h"
//#include "DataFileSink.h"

namespace MR4C {

class LocalTempFileImpl; 

/**
  * Manages storage of a temp file on local disk
*/
class LocalTempFile {

	public:

		LocalTempFile(
			const std::string& dir,
			const std::string& name
		);

		std::string getPath() const;

		void deleteFile();

		void copyFrom(DataFile& dataFile);

		DataFile* toDataFile(const std::string& contentType);

		~LocalTempFile();

	private:

		LocalTempFileImpl* m_impl;

		// prevent calling these
		LocalTempFile();
		LocalTempFile(const LocalTempFile& file);
		LocalTempFile& operator=(const LocalTempFile& file);

};

}
#endif


