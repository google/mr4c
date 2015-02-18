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

namespace MR4C {

class LocalTempFileImpl {

	friend class LocalTempFile;

	private :

		std::string m_path;
		std::shared_ptr<DataFileSource> m_src;
		std::shared_ptr<DataFileSink> m_sink;


		
		LocalTempFileImpl(
			const std::string& dir,
			const std::string& name
		) {
			m_path = dir + "/" + name;
			m_src = std::shared_ptr<DataFileSource>(new LocalDataFileSource(m_path));
			m_sink = std::shared_ptr<DataFileSink>(new LocalDataFileSink(m_path));
		}

		std::string getPath() const {
			return m_path;
		}

		void deleteFile() {
			remove(m_path.c_str());
		}

		void copyFrom(DataFile& dataFile) {
			Dataset::copySourceToSink(dataFile.getFileSource(), m_sink.get());
			m_sink.get()->close();
		}

		DataFile* toDataFile(const std::string& contentType) {
			std::shared_ptr<DataFileSource> src = m_src;
			return new DataFile(src, contentType);
		}

};

LocalTempFile::LocalTempFile(
	const std::string& dir,
	const std::string& name
) {
	m_impl = new LocalTempFileImpl(dir, name);
}

std::string LocalTempFile::getPath() const {
	return m_impl->getPath();
}

void LocalTempFile::deleteFile() {
	m_impl->deleteFile();
}

DataFile* LocalTempFile::toDataFile(const std::string& contentType) {
	return m_impl->toDataFile(contentType);
}

void LocalTempFile::copyFrom(DataFile& dataFile) {
	m_impl->copyFrom(dataFile);
}

LocalTempFile::~LocalTempFile() {
	delete m_impl;
}

}

