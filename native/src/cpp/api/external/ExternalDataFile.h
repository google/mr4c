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

#ifndef __MR4C_EXTERNAL_DATA_FILE_H__
#define __MR4C_EXTERNAL_DATA_FILE_H__

#include "ExternalDataFileSink.h"
#include "ExternalDataFileSource.h"

namespace MR4C {


class ExternalDataFileImpl;

class ExternalDataFile {

	public:

		ExternalDataFile();

		void init(const char* key, const char* name);

		const char* getSerializedFile() const;

		void setSerializedFile(const char* serializedFile);

		ExternalDataFileSource* getFileSource() const;

		void setFileSource(ExternalDataFileSource* src);

		ExternalDataFileSink* getFileSink() const;

		void setFileSink(ExternalDataFileSink* sink);

		const char* getSerializedKey() const;

		const char* getFileName() const;

		~ExternalDataFile();


	private:

		ExternalDataFileImpl* m_impl;

		// prevent calling these
		ExternalDataFile(const ExternalDataFile& file);
		ExternalDataFile& operator=(const ExternalDataFile& file);

};

}
#endif


