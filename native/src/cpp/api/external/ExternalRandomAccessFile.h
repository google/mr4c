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

#ifndef __MR4C_EXTERNAL_RANDOM_ACCESS_FILE_H__
#define __MR4C_EXTERNAL_RANDOM_ACCESS_FILE_H__

#include <mutex>
#include "dataset/dataset_api.h"

namespace MR4C {

class ExternalRandomAccessFileImpl;

class ExternalRandomAccessFile : public virtual RandomAccessFile {

	public:

		ExternalRandomAccessFile(const CExternalRandomAccessFileCallbacks& callbacks);

		size_t read(char* buf, size_t num);

		size_t getLocation();

		void setLocation(size_t loc);

		void setLocationFromEnd(size_t loc);

		void skipForward(size_t num);

		void skipBackward(size_t num);

		size_t getFileSize();

		void close();

		bool isClosed() const;

		~ExternalRandomAccessFile();

	protected:

		void assertNotClosed() const;

		std::unique_lock<std::mutex> getLock(); // So we can lock the same mutex in ExternalRandomAccessFileSink

		const CExternalRandomAccessFileCallbacks& getCallbacks() const;

	private:

		ExternalRandomAccessFileImpl* m_impl;

		// prevent calling these
		ExternalRandomAccessFile(const ExternalRandomAccessFile& context);
		ExternalRandomAccessFile& operator=(const ExternalRandomAccessFile& context);




};

}

#endif



