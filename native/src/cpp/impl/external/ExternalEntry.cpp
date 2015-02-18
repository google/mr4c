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

#include <iostream>
#include <string>
#include <exception>
#include <stdexcept>
#include <memory>
#include "algorithm/algorithm_api.h"
#include "context/context_api.h"
#include "dataset/dataset_api.h"
#include "external/external_api.h"
#include "serialize/serialize_api.h"
#include "util/util_api.h"

namespace MR4C {

ExternalEntry::ExternalEntry() {}
ExternalEntry::~ExternalEntry() {}

ExternalDatasetSerializer* buildDatasetSerializer() {
	SerializerFactory* factory = SerializerRegistry::instance().getSerializerFactory("application/json");
	return new ExternalDatasetSerializer(*factory);
}

ExternalAlgorithmDataSerializer* buildAlgoDataSerializer() {
	SerializerFactory* factory = SerializerRegistry::instance().getSerializerFactory("application/json");
	return new ExternalAlgorithmDataSerializer(*factory);
}

ExternalAlgorithmSerializer* buildAlgorithmSerializer() {
	SerializerFactory* factory = SerializerRegistry::instance().getSerializerFactory("application/json");
	return new ExternalAlgorithmSerializer(*factory);
}

void ExternalEntry::dumpDataset(ExternalDataset* extDataset) {
	ExternalDatasetSerializer* serializer = buildDatasetSerializer();
	Dataset* dataset = serializer->deserializeDataset(*extDataset, false);
	SerializerFactory* factory = SerializerRegistry::instance().getSerializerFactory("application/json");
	DatasetSerializer* jsonSerializer = factory->createDatasetSerializer();
	std::string json = jsonSerializer->serializeDataset(*dataset);
	std::cout << json << std::endl;
	delete serializer;
	delete jsonSerializer;
	delete dataset;
}

ExternalDataset* ExternalEntry::cloneDataset(ExternalDataset* extDataset) {
	ExternalDatasetSerializer* serializer = buildDatasetSerializer();
	Dataset* dataset = serializer->deserializeDataset(*extDataset, false);
	ExternalDataset* extDataset2 = new ExternalDataset();
	extDataset2->init("clone");
	serializer->serializeDataset(extDataset2, *dataset);
	delete serializer;
	delete dataset;
	return extDataset2;
}

ExternalAlgorithmData* ExternalEntry::cloneAlgorithmData(ExternalAlgorithmData* data) {
	ExternalAlgorithmDataSerializer* serializer = buildAlgoDataSerializer();

	AlgorithmData* algoData = new AlgorithmData();
	serializer->deserializeInputData(*algoData, *data);
	serializer->deserializeOutputData(*algoData, *data);

	ExternalAlgorithmData* data2 = new ExternalAlgorithmData();
	serializer->serializeInputData(*algoData, *data2);
	serializer->serializeOutputData(*algoData, *data2);

	delete serializer;
	delete algoData; 
	return data2;
}

ExternalAlgorithm* ExternalEntry::getAlgorithm(const char* algoName) {
	if ( !AlgorithmRegistry::instance().hasAlgorithm(algoName) ) {
		return NULL;
	}
	try {
		Algorithm* algo = AlgorithmRegistry::instance().getAlgorithm(algoName);
		ExternalAlgorithmSerializer* serializer = buildAlgorithmSerializer();
 		ExternalAlgorithm* extAlgo = new ExternalAlgorithm(algoName);
		serializer->serializeAlgorithm(extAlgo, *algo);
		delete serializer;
		return extAlgo;
	} catch ( std::exception& e ) {
		return NULL;
	}
}

bool ExternalEntry::executeAlgorithm(const char* algoName, ExternalAlgorithmData* extData, ExternalContext* extContext) {
	try {
		ExternalAlgorithmDataSerializer* serializer = buildAlgoDataSerializer();
		AlgorithmData* algoData = new AlgorithmData();
		serializer->deserializeInputData(*algoData, *extData);
		serializer->deserializeOutputData(*algoData, *extData);

		AlgorithmRunner runner(algoName);
		AlgorithmContext* context = new AlgorithmContext(algoName);
		extContext->initContext(*context);
		runner.executeAlgorithm(*algoData, *context);
		serializer->serializeOutputData(*algoData, *extData);
		delete serializer;
		delete algoData;
		delete context;
		return true;
	} catch ( std::exception& e ) {
		extContext->reportFailure(e.what());
		return false;
	}
}

void ExternalEntry::pushEnvironmentProperties(const char* setName, const char* serializedProps) {
	MR4CEnvironment::EnvSet envSet = MR4CEnvironment::enumFromString(setName);
	SerializerFactory* factory = SerializerRegistry::instance().getSerializerFactory("application/json");
	PropertiesSerializer* serializer = factory->createPropertiesSerializer();
	Properties props = serializer->deserializeProperties(serializedProps);
	MR4CEnvironment::instance().addPropertySet(envSet, props);
	delete serializer;
}

void ExternalEntry::testLogging(ExternalContext* extContext, const char* level, const char* msg) {
	AlgorithmContext context("testing");
	extContext->initContext(context);
	context.log( Logger::enumFromString(level), msg);
}

void ExternalEntry::testProgressReporting(ExternalContext* extContext, float percentDone, const char* msg) {
	AlgorithmContext context("testing");
	extContext->initContext(context);
	context.progress( percentDone, msg);
}

void ExternalEntry::testFailureReporting(ExternalContext* extContext, const char* msg) {
	extContext->reportFailure(msg);
}

void ExternalEntry::testSendMessage(ExternalContext* extContext, const Message& msg) {
	AlgorithmContext context("testing");
	extContext->initContext(context);
	context.sendMessage(msg);
}

void ExternalEntry::testAddDataFile(ExternalDataset* extDataset, ExternalDataFile* extFile, bool stream) {
	ExternalDatasetSerializer* serializer = buildDatasetSerializer();
	DataKey key = serializer->deserializeDataKey(extFile->getSerializedKey());
	DataFile* file1 = serializer->deserializeDataFile(*extFile);
	Dataset* dataset = serializer->deserializeDataset(*extDataset, true);
	char* data = copyArray(file1->getBytes(), file1->getSize());
	DataFileSource* src = new SimpleDataFileSource(data, file1->getSize());
	std::shared_ptr<DataFileSource> srcPtr(src);
	if ( !stream ) {
		DataFile* file2 = new DataFile(srcPtr, file1->getContentType());
		dataset->addDataFile(key, file2);
	} else {
		DataFile* file2 = new DataFile(file1->getContentType());
		dataset->addDataFile(key, file2);
		Dataset::copySourceToSink(srcPtr, file2->getFileSink());
	}
}

const char* ExternalEntry::testGetDataFileName(ExternalDataset* extDataset, const char* serializedKey) {
	ExternalDatasetSerializer* serializer = buildDatasetSerializer();
	DataKey key = serializer->deserializeDataKey(serializedKey);
	Dataset* dataset = serializer->deserializeDataset(*extDataset, true);
	return dataset->getDataFileName(key).c_str();
}

ExternalDataFile* ExternalEntry::testFindDataFile(ExternalDataset* extDataset, const char* serializedKey) {
	ExternalDatasetSerializer* serializer = buildDatasetSerializer();
	DataKey key = serializer->deserializeDataKey(serializedKey);
	Dataset* dataset = serializer->deserializeDataset(*extDataset, false);
	if ( dataset->hasDataFile(key) ) {
		DataFile* file = dataset->getDataFile(key);
		return serializer->serializeDataFile(key, *file);
	} else {
		return NULL;
	}
}

bool ExternalEntry::testIsQueryOnly(ExternalDataset* extDataset) {
	ExternalDatasetSerializer* serializer = buildDatasetSerializer();
	Dataset* dataset = serializer->deserializeDataset(*extDataset, true);
	return dataset->isQueryOnly();
}

ExternalDataFile* ExternalEntry::testReadFileAsRandomAccess(ExternalDataset* extDataset, const char* serializedKey) {
	ExternalDatasetSerializer* serializer = buildDatasetSerializer();
	DataKey key = serializer->deserializeDataKey(serializedKey);
	Dataset* dataset = serializer->deserializeDataset(*extDataset, false);

	if ( !dataset->hasDataFile(key) ) {
		MR4C_THROW(std::logic_error, "No file found for key " << key.str());
	}
 
	// pull the file to get the type
	DataFile* origFile = dataset->getDataFile(key);

	RandomAccessFile* rand = dataset->getDataFileForRandomAccess(key);

	size_t size = rand->getFileSize();
	if ( size<100 ) {
		MR4C_THROW(std::logic_error, "File for test must be at least 100 bytes; passed file has " << size << " bytes");
	}

	char* data = new char[size];

	size_t size0 = 0.10 * size;
	size_t size1 = 0.15 * size;
	size_t size2 = 0.25 * size;
	size_t size3 = 0.30 * size;
	size_t size4 = 0.20 * size;

	size_t start0 = 0;
	size_t start1 = start0 + size0;
	size_t start2 = start1 + size1;
	size_t start3 = start2 + size2;
	size_t start4 = start3 + size3;

	// 2: abs from start
	rand->setLocation(start2);
	size_t read2 = rand->read(data+start2, size2);

	// 4: skip forward
	rand->skipForward(size3);
	size_t read4 = rand->read(data+start4, size4);

	// 3: from end 
	rand->setLocationFromEnd(size3+size4);
	size_t read3 = rand->read(data+start3, size3);

	// 1: skip back
	rand->skipBackward(size1 + size2 + size3);
	size_t read1 = rand->read(data+start1, size1);

	// 0: back to start, read it
	rand->setLocation(0);
	size_t read0 = rand->read(data, size0);

	rand->close();
	delete rand;

	// return what we read
	DataFile* newFile = new DataFile(data, size, origFile->getContentType());
	return serializer->serializeDataFile(key, *newFile);
}


void ExternalEntry::testWriteFileAsRandomAccess(ExternalDataset* extDataset, ExternalDataFile* extFile) {

	ExternalDatasetSerializer* serializer = buildDatasetSerializer();
	DataKey key = serializer->deserializeDataKey(extFile->getSerializedKey());
	DataFile* file1 = serializer->deserializeDataFile(*extFile);
	Dataset* dataset = serializer->deserializeDataset(*extDataset, true);

	char* data = file1->getBytes();
	size_t size = file1->getSize();
	if ( size<100 ) {
		MR4C_THROW(std::logic_error, "File for test must be at least 100 bytes; passed file has " << size << " bytes");
	}

	DataFile* file2 = new DataFile(file1->getContentType());
	WritableRandomAccessFile* rand = dataset->addDataFileForRandomAccess(key, file2);

	size_t middle = size/2;
	rand->setFileSize(size);

	// write 2nd half
	rand->setLocation(middle);
	rand->write(data+middle, size-middle);

	// write 1st half
	rand->setLocation(0);
	rand->write(data, middle);

	rand->close();
}

void ExternalEntry::deleteLocalTempFiles() {
    MR4CTempFiles::instance().deleteAllocatedDirectories();
}


const char** ExternalEntry::getLogFilePaths() {
	// NOTE: this leaks a tiny bit of memory
	std::set<std::string> files = MR4CLogging::extractLogFiles();
	const char** result = new const char*[files.size()+1];

	std::set<std::string>::iterator iter = files.begin();
	int index=0;
	for ( ; iter!=files.end(); iter++ ) {
		result[index]=iter->c_str();
		index++;
	}
	result[index]=NULL;
	
	return result;
}

}
