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

#ifndef __MR4C_WRITABLE_RANDOM_ACCESS_FILE_H__
#define __MR4C_WRITABLE_RANDOM_ACCESS_FILE_H__

namespace MR4C {

class WritableRandomAccessFile : public virtual RandomAccessFile {

	public:

		/**
		  * Write num bytes from buf
		*/
		virtual void write(char* buf, size_t num) =0;

		/**
		  * Change size to truncate or extend the file
		*/
		virtual void setFileSize(size_t size) =0;

};

}
#endif


