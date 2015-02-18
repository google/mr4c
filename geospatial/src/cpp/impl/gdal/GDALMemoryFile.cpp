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

#include <string>
#include <stdexcept>

#include "dataset/dataset_api.h"
#include "gdal/gdal_api.h"
#include "util/util_api.h"

namespace MR4C {

class GDALMemoryFileImpl : public GDALFileBase {

	friend class GDALMemoryFile;

	private :

		GDALMemoryFileImpl() {}

		void init(
			const std::string& name,
			DataFile& file
		) {
			std::string path = toInMemoryPath(name);
			std::shared_ptr<DataFileSource> src  = file.getFileSource();
			initForRead(path, src);
		}

		void init (
			const std::string& name
		) {
			std::string path = toInMemoryPath(name);
			initForWrite(path);
		}

		static std::string toInMemoryPath(const std::string& name) {
			return "/vsimem/" + name;
		}

		void deleteFile() {
			VSIUnlink(getPath().c_str()); // the magic way to delete a file from the GDAL memory file system
		}

		~GDALMemoryFileImpl() {
			deleteFile();
		}

	protected :

		void storeContent(
			const std::string& path,
			std::shared_ptr<DataFileSource>& src
		) {

			VSILFILE* fileHandle = VSIFileFromMemBuffer(
				path.c_str(),
				(GByte*)src->getFileBytes(),
				src->getFileSize(),
				false // retain control of the buffer
			);
			if ( fileHandle==NULL ) {
				MR4C_THROW(std::logic_error, "Failed to create memory file with path [" << path << "]");
			}
			VSIFCloseL(fileHandle); // close the file handle, don't need it, will be opened by GDAL
		}

		std::shared_ptr<DataFileSource> retrieveContent(
			const std::string& path
		) {
			vsi_l_offset size=0;
			char* bytes = (char*) VSIGetMemFileBuffer(
				path.c_str(),
				&size,
				true // take control of the buffer
			);
			if ( bytes==NULL ) {
				MR4C_THROW(std::logic_error, "Failed to obtain memory buffer at path [" << path << "]");
			}
			return std::shared_ptr<DataFileSource>(
				new SimpleDataFileSource(
					bytes,
					size,
					DataFile::Allocation::MALLOC
				)
			);
		}

};

GDALMemoryFile::GDALMemoryFile(
	const std::string& name,
	DataFile& file
) {
	m_impl = new GDALMemoryFileImpl();
	m_impl->init(name, file);
}

GDALMemoryFile::GDALMemoryFile(
	const std::string& name
) {
	m_impl = new GDALMemoryFileImpl();
	m_impl->init(name);
}

std::string GDALMemoryFile::getPath() const {
	return m_impl->getPath();
}

GDALDataset* GDALMemoryFile::getGDALDataset() const {
	return m_impl->getGDALDataset();
}

void GDALMemoryFile::setGDALDataset(GDALDataset* gdal) {
	m_impl->setGDALDataset(gdal);
}

void GDALMemoryFile::close() {
	m_impl->close();
}

void GDALMemoryFile::deleteFile() {
	m_impl->deleteFile();
}

DataFile* GDALMemoryFile::toDataFile(const std::string& contentType) {
	return m_impl->toDataFile(contentType);
}

GDALMemoryFile::~GDALMemoryFile() {
	delete m_impl;
}

}
