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

#ifndef __MR4C_GEO_GDAL_FILE_H__
#define __MR4C_GEO_GDAL_FILE_H__

#include <string>

#include "gdal_priv.h"

#include "dataset/dataset_api.h"


namespace MR4C {

/**
  * Manages the storage of a file that is to be exposed as a GDALDataset.
*/
class GDALFile {

	public:

		/**
		  * Return the path that should be passed for GDAL file operations
		*/
		virtual std::string getPath() const =0;

		virtual GDALDataset* getGDALDataset() const =0;

		/**
		  * Assign a GDALDataset to this file.  This must be called to write a GDAL Dataset.
		*/
		virtual void setGDALDataset(GDALDataset* gdal) =0;

		/**
		  * Closes the underlying GDAL dataset
		*/
		virtual void close() =0;

		/**
		  * Exposes file content as a mr4c DataFile.  The file must be closed BEFORE calling this
		*/
		virtual DataFile* toDataFile(const std::string& contentType) =0;

		/**
		  * Deletes the file from storage
		*/
		virtual void deleteFile() =0;


};

}

#endif



