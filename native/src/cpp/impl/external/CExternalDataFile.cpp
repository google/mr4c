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

#include "dataset/dataset_api.h"
#include "external/external_api.h"

CExternalDataFilePtr CExternalDataFile_newDataFile(const char* key, const char* name) {
	MR4C::ExternalDataFile* file = new MR4C::ExternalDataFile();
	file->init(key, name);
	return wrapExternalDataFile(file);
}

const char* CExternalDataFile_getSerializedKey(CExternalDataFilePtr fileHandle) {
	return fileHandle->file->getSerializedKey();
}

const char* CExternalDataFile_getSerializedFile(CExternalDataFilePtr fileHandle) {
	return fileHandle->file->getSerializedFile();
}

void  CExternalDataFile_setSerializedFile(
	CExternalDataFilePtr fileHandle,
	const char* serializedFile
) {
	fileHandle->file->setSerializedFile(serializedFile);
}

CExternalDataFileSourcePtr CExternalDataFile_getFileSource(CExternalDataFilePtr fileHandle) {
	MR4C::ExternalDataFileSource* extSrc = fileHandle->file->getFileSource();
	return wrapExternalDataFileSource(extSrc);
}

void CExternalDataFile_setFileSource(CExternalDataFilePtr fileHandle, CExternalDataFileSourcePtr srcHandle) {
	fileHandle->file->setFileSource(srcHandle->src);
}

CExternalDataFileSinkPtr CExternalDataFile_getFileSink(CExternalDataFilePtr fileHandle) {
	MR4C::ExternalDataFileSink* extSrc = fileHandle->file->getFileSink();
	return wrapExternalDataFileSink(extSrc);
}

void CExternalDataFile_setFileSink(CExternalDataFilePtr fileHandle, CExternalDataFileSinkPtr sinkHandle) {
	fileHandle->file->setFileSink(sinkHandle->sink);
}

const char* CExternalDataFile_getFileName(CExternalDataFilePtr fileHandle) {
	return fileHandle->file->getFileName();
}

CExternalDataFilePtr wrapExternalDataFile(MR4C::ExternalDataFile* file) {
	if ( file==NULL ) {
		return NULL;
	}
	CExternalDataFilePtr handle = new CExternalDataFile();
	handle->file = file;
	return handle;
}

