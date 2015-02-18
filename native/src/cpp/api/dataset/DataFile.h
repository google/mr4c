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

#ifndef __MR4C_DATA_FILE_H__
#define __MR4C_DATA_FILE_H__

#include <string>
#include <memory>
#include <mutex>
#include "DataFileSource.h"
#include "DataFileSink.h"

namespace MR4C {

class DataFileImpl; 

/**
  * Content of a file.  Consists of bytes and a MIME content type.
*/

class DataFile {

	public:

		enum Allocation {
			MALLOC,
			NEW
		};

		/**
		  * Create a new data file. The byte array must be allocated on
		  * the heap.  This class takes ownership of the byte array.
		  * @param alloc Indicate if malloc or new was used to allocate
		  * the array.
		*/
		DataFile(char* bytes, size_t size, const std::string& contentType, Allocation alloc = NEW);

		/**
		  * Create a new data file.  The DataFileSource is responsible
		  * for managing the underlying data
		*/
		DataFile(std::shared_ptr<DataFileSource>& src, const std::string& contentType);


		/**
		  * Create a new data file from a file on local disk.  All
		  * content should be in the file BEFORE the DataFile object
		  * is instantiated.
		  * @param path Path to the local file.
		*/
		DataFile(const std::string& path, const std::string& contentType);

		/**
		  * Create a new data file for chunked writing.
		*/
		DataFile(const std::string& contentType);

		/**
		  * For use by the framework after deserialization
		*/
		void setFileSource(std::shared_ptr<DataFileSource>& src);

		/**
		  * For use by the framework during serialization
		*/
		std::shared_ptr<DataFileSource> getFileSource();

		/**
		  * For use by the framework 
		*/
		void setFileSink(DataFileSink* sink);

		/**
		  * For use by the framework 
		*/
		DataFileSink* getFileSink();

		/**
		  * For input datasets only, returns the name of actual file on disk.
		  * @return will return an empty string if the file name is not available
		*/
		std::string getFileName();

		/**
		  * For use by the framework after deserialization
		*/
		void setFileName(const std::string& name);

		/**
		  * Returns true if a file source was assigned
		*/
		bool hasFileSource() const;

		/**
		  * Returns true if a file sink was assigned
		*/
		bool hasFileSink() const;

		std::string getContentType() const;

		/**
		  * Will throw exception if hasContent()==false
		*/
		char* getBytes() const;

		/** 
		  * Will throw exception if hasContent()==false
		*/
		size_t getSize() const;

		/** 
		  * Reads the next chunk of the file.
		  * Returns the number of bytes actually read.
		  * Returns 0 for EOF reached.
		  * Call release() to close the underlying resource if a series of calls will not go to EOF.
		  * Will throw exception if hasContent()==false.
		*/
		size_t read(char* buf, size_t num);

		/** 
		  * Skips forward in the file
		  * Returns the number of bytes actually skipped.
		  * Will throw exception if hasContent()==false.
		*/
		size_t skip(size_t num);

		/**
		  * Writes the next chunk of the file
		*/
		void write(char* buf, size_t num);

		/**
		  * Free the memory containing the file bytes.  Subsequent calls
		  * to read(), getBytes() and getSize() will fail.
		*/
		void release();

		/**
		  * Returns true if the DataFile is in a state that allows accessing content 
		*/
		bool hasContent() const;

		/**
		 * Framework only: Returns a unique_lock with ownership over the DataFile's
		 * mutex object. The lock is released either on destruction or by explicitly
		 * calling the .unlock() method. Call with doAcquire=false to return a lock
		 * without attempting to acquire it.
		 */
		std::unique_lock<std::recursive_mutex> getLock(bool doAcquire = true);


		bool operator==(const DataFile& file) const;

		bool operator!=(const DataFile& file) const;

		~DataFile();

	private:

		DataFileImpl* m_impl;

		// prevent calling these
		DataFile();
		DataFile(const DataFile& file);
		DataFile& operator=(const DataFile& file);

};

}
#endif


