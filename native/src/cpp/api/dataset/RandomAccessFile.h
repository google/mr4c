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

#ifndef __MR4C_RANDOM_ACCESS_FILE_H__
#define __MR4C_RANDOM_ACCESS_FILE_H__

namespace MR4C {

/**
  * File that allows arbitrary movement of the file pointer.  Implementations will most likely be local disk files
*/
class RandomAccessFile {

	public:

		/**
		  * Read up to the next num bytes into buf, returns the number of bytes read
		*/
		virtual size_t read(char* buf, size_t num) =0;

		/**
		  * Returns the absolute location of the file pointer, in bytes
		*/
		virtual size_t getLocation() =0;

		/**
		  * Set absolute location from file start
		*/
		virtual void setLocation(size_t loc) =0;

		/**
		  * Set location in bytes back from end of file
		*/
		virtual void setLocationFromEnd(size_t loc) =0;

		/**
		  * Skip num bytes forward from current location
		*/
		virtual void skipForward(size_t num) =0;

		/**
		  * Skip num bytes backward from current location
		*/
		virtual void skipBackward(size_t num) =0;

		virtual size_t getFileSize() =0;

		virtual void close() =0;

		virtual bool isClosed() const =0;

};

}

#endif


