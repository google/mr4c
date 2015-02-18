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

#ifndef __MR4C_GEO_GDAL_FILE_BASE_H__
#define __MR4C_GEO_GDAL_FILE_BASE_H__

#include <string>

#include "gdal_priv.h"

#include "dataset/dataset_api.h"


namespace MR4C {

class GDALFileBaseImpl;

class GDALFileBase : public GDALFile {

	friend class GDALFileBaseImpl;

	public:

		std::string getPath() const;

		GDALDataset* getGDALDataset() const;

		void setGDALDataset(GDALDataset* gdal);

		void close();

		DataFile* toDataFile(const std::string& contentType);

		~GDALFileBase();

	protected:


		GDALFileBase();

		/**
		  * Call from read-only constructors to initialize
		*/
		void initForRead(
			const std::string& path,
			std::shared_ptr<DataFileSource>& src
		);

		/**
		  * Call from writable constructors to initialize
		*/
		void initForWrite(
			const std::string& path
		);


		/**
		  * Subclasses must implement to store file content so it can be read by GDAL from path
		*/
		virtual void storeContent(
			const std::string& path,
			std::shared_ptr<DataFileSource>& src
		) =0;

		/**
		  * Subclasses must implement to retrieve file content written by GDAL to path
		*/
		virtual std::shared_ptr<DataFileSource> retrieveContent(
			const std::string& path
		) =0;

	private:

		GDALFileBaseImpl* m_impl;
};

}

#endif



