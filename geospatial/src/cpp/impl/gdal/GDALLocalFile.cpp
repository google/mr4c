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

#include <cstdio>
#include <string>

#include "dataset/dataset_api.h"
#include "gdal/gdal_api.h"

namespace MR4C {

class GDALLocalFileImpl : public GDALFileBase {

	friend class GDALLocalFile;

	private :

		GDALLocalFileImpl() {}

		void init(
			const std::string& dir,
			const std::string& name,
			DataFile& file
		) {
			std::string path = toLocalPath(dir, name);
			std::shared_ptr<DataFileSource> src  = file.getFileSource();
			initForRead(path,src);
		}

		void init(
			const std::string& dir,
			const std::string& name
		) {
			std::string path = toLocalPath(dir, name);
			initForWrite(path);
		}

		static std::string toLocalPath(const std::string& dir, const std::string& name) {
			return dir + "/" + name;
		}

		void deleteFile() {
			remove(getPath().c_str());
		}

	protected: 

		void storeContent(
			const std::string& path,
			std::shared_ptr<DataFileSource>& src
		) {
			LocalDataFileSink sink(path);
			Dataset::copySourceToSink(src, &sink);
		}


		std::shared_ptr<DataFileSource> retrieveContent(
			const std::string& path
		) {
			return std::shared_ptr<DataFileSource>(
				new LocalDataFileSource(path)
			);
		}

};

GDALLocalFile::GDALLocalFile(
	const std::string& dir,
	const std::string& name,
	DataFile& file
) {
	m_impl = new GDALLocalFileImpl();
	m_impl->init(dir, name, file);
}

GDALLocalFile::GDALLocalFile(
	const std::string& dir,
	const std::string& name
) {
	m_impl = new GDALLocalFileImpl();
	m_impl->init(dir, name);
}

std::string GDALLocalFile::getPath() const {
	return m_impl->getPath();
}

GDALDataset* GDALLocalFile::getGDALDataset() const {
	return m_impl->getGDALDataset();
}

void GDALLocalFile::setGDALDataset(GDALDataset* gdal) {
	m_impl->setGDALDataset(gdal);
}

void GDALLocalFile::close() {
	m_impl->close();
}

void GDALLocalFile::deleteFile() {
	m_impl->deleteFile();
}

DataFile* GDALLocalFile::toDataFile(const std::string& contentType) {
	return m_impl->toDataFile(contentType);
}

GDALLocalFile::~GDALLocalFile() {
	delete m_impl;
}

}
