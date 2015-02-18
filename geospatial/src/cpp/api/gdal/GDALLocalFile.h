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

#ifndef __MR4C_GEO_GDAL_LOCAL_FILE_H__
#define __MR4C_GEO_GDAL_LOCAL_FILE_H__

#include <string>

#include "gdal_priv.h"

#include "dataset/dataset_api.h"


namespace MR4C {

class GDALLocalFileImpl;

/**
  * GDALDataset stored as a temp file on local disk.
  * Call AlgorithmContext::createTempDirectory() to get a directory for files
*/
class GDALLocalFile : public GDALFile {

	public:

		/**
		  * Create a read-only GDALDataset from an existing file
		*/
		GDALLocalFile(
			const std::string& dir,
			const std::string& name,
			DataFile& file
		);

		/**
		  * Create a writable GDALDataset.  Caller is responsible for creating a GDALDataset and calling setGDALDataset.
		*/
		GDALLocalFile(
			const std::string& dir,
			const std::string& name
		);

		std::string getPath() const;

		GDALDataset* getGDALDataset() const;

		void setGDALDataset(GDALDataset* gdal);

		void close();

		void deleteFile();

		DataFile* toDataFile(const std::string& contentType);

		~GDALLocalFile();

	private:

		GDALLocalFileImpl* m_impl;

		// prevent calling these
		GDALLocalFile();
		GDALLocalFile(const GDALLocalFile& file);
		GDALLocalFile& operator=(const GDALLocalFile& file);

};

}

#endif



