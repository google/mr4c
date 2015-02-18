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

#include "external/external_api.h"

void CExternalEntry_dumpDataset(CExternalDatasetPtr datasetHandle) {
	MR4C::ExternalEntry entry;
	entry.dumpDataset(datasetHandle->dataset);
}

CExternalDatasetPtr CExternalEntry_cloneDataset(CExternalDatasetPtr datasetHandle) {
	MR4C::ExternalEntry entry;
	MR4C::ExternalDataset* dataset = entry.cloneDataset(datasetHandle->dataset);
	return wrapExternalDataset(dataset);
}

CExternalAlgorithmDataPtr CExternalEntry_cloneAlgorithmData(CExternalAlgorithmDataPtr algoDataHandle) {
	MR4C::ExternalEntry entry;
	MR4C::ExternalAlgorithmData* algoData = entry.cloneAlgorithmData(algoDataHandle->algoData);
	return wrapExternalAlgorithmData(algoData);
}

CExternalAlgorithmPtr CExternalEntry_getAlgorithm(const char* algoName) {
	MR4C::ExternalEntry entry;
	MR4C::ExternalAlgorithm* algo = entry.getAlgorithm(algoName);
	return wrapExternalAlgorithm(algo);
}

bool CExternalEntry_executeAlgorithm(const char* algoName, CExternalAlgorithmDataPtr inputDataHandle, CExternalContextPtr contextHandle) {
	MR4C::ExternalEntry entry;
	return entry.executeAlgorithm(algoName, inputDataHandle->algoData, contextHandle->context);
}

void CExternalEntry_pushEnvironmentProperties(const char* setName, const char* serializedProps) {
	MR4C::ExternalEntry entry;
	entry.pushEnvironmentProperties(setName, serializedProps);
}

void CExternalEntry_testLogging(CExternalContextPtr contextHandle, const char* level, const char* msg) {
	MR4C::ExternalEntry entry;
	entry.testLogging(contextHandle->context, level, msg);
}

void CExternalEntry_testProgressReporting(CExternalContextPtr contextHandle, float percentDone, const char* msg) {
	MR4C::ExternalEntry entry;
	entry.testProgressReporting(contextHandle->context, percentDone, msg);
}

void CExternalEntry_testFailureReporting(CExternalContextPtr contextHandle, const char* msg) {
	MR4C::ExternalEntry entry;
	entry.testFailureReporting(contextHandle->context, msg);
}

void CExternalEntry_testSendMessage(CExternalContextPtr contextHandle, const CExternalContextMessage& extMsg) {
	MR4C::ExternalEntry entry;
	MR4C::Message msg(
		extMsg.topic,
		extMsg.content,
		extMsg.contentType
	);
	entry.testSendMessage(contextHandle->context, msg);
}


void CExternalEntry_testAddDataFile(CExternalDatasetPtr datasetHandle, CExternalDataFilePtr fileHandle, bool stream) {
	MR4C::ExternalEntry entry;
	entry.testAddDataFile(datasetHandle->dataset, fileHandle->file, stream);
}

const char* CExternalEntry_testGetDataFileName(CExternalDatasetPtr datasetHandle, const char* serializedKey) {
	MR4C::ExternalEntry entry;
	return entry.testGetDataFileName(datasetHandle->dataset, serializedKey);
}

CExternalDataFilePtr CExternalEntry_testFindDataFile(CExternalDatasetPtr datasetHandle, const char* serializedKey) {
	MR4C::ExternalEntry entry;
	MR4C::ExternalDataFile* file = entry.testFindDataFile(datasetHandle->dataset, serializedKey);
	return wrapExternalDataFile(file);
}

bool CExternalEntry_testIsQueryOnly(CExternalDatasetPtr datasetHandle) {
	MR4C::ExternalEntry entry;
	return entry.testIsQueryOnly(datasetHandle->dataset);
}

CExternalDataFilePtr CExternalEntry_testReadFileAsRandomAccess(CExternalDatasetPtr datasetHandle, const char* serializedKey) {
	MR4C::ExternalEntry entry;
	MR4C::ExternalDataFile* file = entry.testReadFileAsRandomAccess(datasetHandle->dataset, serializedKey);
	return wrapExternalDataFile(file);
}

void CExternalEntry_testWriteFileAsRandomAccess(CExternalDatasetPtr datasetHandle, CExternalDataFilePtr fileHandle) {
	MR4C::ExternalEntry entry;
	entry.testWriteFileAsRandomAccess(datasetHandle->dataset, fileHandle->file);
}

void CExternalEntry_deleteLocalTempFiles() {
    MR4C::ExternalEntry entry;
    entry.deleteLocalTempFiles();
}

const char** CExternalEntry_getLogFilePaths() {
	MR4C::ExternalEntry entry;
	return entry.getLogFilePaths();
}

