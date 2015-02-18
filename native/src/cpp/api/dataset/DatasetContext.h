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

#ifndef __MR4C_DATASET_CONTEXT_H__
#define __MR4C_DATASET_CONTEXT_H__

#include "DataFile.h"
#include "RandomAccessFile.h"
#include "WritableRandomAccessFile.h"
#include "keys/keys_api.h"

namespace MR4C {


class DatasetContext {

	public:

		DatasetContext();

		/**
		  * Tries to find a file corresponding to the given key.
		  * Default behavior is no file found.
		  * @return null if there is no file with this key
		*/
		virtual DataFile* findDataFile(const DataKey& key) const;

		/**
		  * default is false
		*/
		virtual bool isOutput() const;

		/**
		  * Indicates that no file keys are pushed into the dataset
		  * Default is false
		*/
		virtual bool isQueryOnly() const;

		/**
		  * Create a new output file for writing.
		  * Default is to throw exception
		*/
		virtual void addDataFile(const DataKey& key, DataFile* file);

		/**
		  * Tries to get the name of the data file.
		  * Default is name not available.
		  * @return empty string if the name is not available
		*/
		virtual std::string getDataFileName(const DataKey& key) const;

		/**
		  * Access input file as a read-only random access file
		  * Default is to throw exception
		*/
		virtual RandomAccessFile* readFileAsRandomAccess(const DataKey& key) const;

		/**
		  * Access output file as a writable random access file
		  * Default is to throw exception
		*/
		virtual WritableRandomAccessFile* writeFileAsRandomAccess(const DataKey& key) const;

};

}
#endif


