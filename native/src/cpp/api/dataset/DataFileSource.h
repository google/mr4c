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

#ifndef __MR4C_DATA_FILE_SOURCE_H__
#define __MR4C_DATA_FILE_SOURCE_H__

#include <string>

namespace MR4C {



class DataFileSource {

	public:

		/**
		  * NOTE: logical const, implementations may make changes behind
		  * the interface to lookup the data
		*/
		virtual char* getFileBytes() const =0;

		/**
		  * NOTE: logical const, implementations may make changes behind
		  * the interface to lookup the data
		*/
		virtual size_t getFileSize() const =0;

		/**
		  * Read up to the next num bytes into buf, returns the number of bytes read
		*/
		virtual size_t read(char* buf, size_t num);

		/**
		  * Skip up to the next num bytes, returns the number of bytes skipped
		*/
		virtual size_t skip(size_t num);

		virtual void release() =0;

		virtual bool isReleased() const =0;

		bool operator!=(const DataFileSource& src) const;

};


/**
  * Compare the content of two sources.  This is only a valid comparison if
  * both sources have not been released.  An exception will be thrown if either
  * has been released
*/
bool operator==(const DataFileSource& src1, const DataFileSource& src2);


}
#endif


