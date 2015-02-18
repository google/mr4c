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

#include <stdexcept>
#include <string>

#include "gdal/gdal_api.h"
#include "util/util_api.h"

namespace MR4C {

class GDALFileBaseImpl {

	friend class GDALFileBase;

	private :

		std::string m_path;
		GDALDataset* m_gdal;
		std::shared_ptr<DataFileSource> m_src;
		GDALFileBase* m_self;

		GDALFileBaseImpl(
			GDALFileBase* self
		) {
			m_self = self;
		}

		void initForRead(
			const std::string& path,
			std::shared_ptr<DataFileSource>& src
		) {
			m_path = path;
			m_src = src;
			m_self->storeContent(m_path, m_src);
			GDALAllRegister(); // make sure drivers are available
			m_gdal = (GDALDataset *) GDALOpen( m_path.c_str(), GA_ReadOnly );
			if ( m_gdal==NULL ) {
				MR4C_THROW(std::logic_error, "Failed to open GDALDataset at path [" << m_path << "]");
			}
		}

		void initForWrite(
			const std::string& path
		) {
			m_path = path;
		}

		std::string getPath() const {
			return m_path;
		}

		GDALDataset* getGDALDataset() const {
			return m_gdal;
		}

		void setGDALDataset(GDALDataset* gdal) {
			m_gdal = gdal;
		}

		void close() {
			GDALClose(m_gdal);
		}

		DataFile* toDataFile(const std::string& contentType) {
			if ( m_src.get()==NULL ) {
				m_src = m_self->retrieveContent(m_path);
			}
			return new DataFile(m_src, contentType);
		}

};

GDALFileBase::GDALFileBase() { 
	m_impl = new GDALFileBaseImpl(this);
}

void GDALFileBase::initForRead(
	const std::string& path,
	std::shared_ptr<DataFileSource>& src
) {
	m_impl->initForRead(path,src);
}

void GDALFileBase::initForWrite(
	const std::string& path
) {
	m_impl->initForWrite(path);
}

std::string GDALFileBase::getPath() const {
	return m_impl->getPath();
}

GDALDataset* GDALFileBase::getGDALDataset() const {
	return m_impl->getGDALDataset();
}

void GDALFileBase::setGDALDataset(GDALDataset* gdal) {
	m_impl->setGDALDataset(gdal);
}

void GDALFileBase::close() {
	m_impl->close();
}

DataFile* GDALFileBase::toDataFile(const std::string& contentType) {
	return m_impl->toDataFile(contentType);
}

GDALFileBase::~GDALFileBase() {
	delete m_impl;
}

}
