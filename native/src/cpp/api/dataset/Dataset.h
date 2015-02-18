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

#ifndef __MR4C_DATASET_H__
#define __MR4C_DATASET_H__

#include <map>
#include <memory>

#include "DataFile.h"
#include "DatasetContext.h"
#include "keys/keys_api.h"
#include "metadata/metadata_api.h"

namespace MR4C {


class DatasetImpl;

/**
  * A Dataset is a bundle of conceptually related files and metadata.  It
  * consists of two maps:
  *    - DataKey to DataFile
  *    - DataKey to MetadataMap
  *    .
  * Each key may have at most one file, and at most one metadata map.
  * The metadata map is the root of a metadata structure of arbitrary
  * complexity.  To associate multiple metadata elements to a key, just add
  * them to the map.
*/

class Dataset {

	public:

		Dataset();

		DataFile* getDataFile(const DataKey& key) const;

		
		/**
		  * Same as getDataFile, but a copy of the file has been staged
		  * on local disk to allow random access.
		*/
		RandomAccessFile* getDataFileForRandomAccess(const DataKey& key) const;

		/**
		  * Retrieves metadata that had been stored as a DataFile.
		  * The caller takes ownership of the MetadataMap objects in the returned map.
		  * The MetadataMap objects can be freed by calling freeMetadata()
		*/
		std::map<DataKey,MetadataMap*> getDataFileAsMetadata(const DataKey& key) const;

		/**
		  * Associates a key to a file.  The file must be allocated on
		  * the heap with new.  This class takes ownership of the file
		  * object.  The file object should not be associated to any
		  * other key.  An exception will be thrown if this key already
		  * has a file.  If the file has content, this dataset
		  * will attempt to write the file to storage.  If the write
		  * succeeds, the dataset will call release() on the file.
		*/
		void addDataFile(const DataKey& key, DataFile* file);

		/**
		  * Same as addDataFile with no content in the file.  Instead
		  * of doing chunked writing, the returned WritableRandomAccessFile
		  * can be used to do the write with more flexibility.  The cost
		  * is that the file must be staged on local disk, and then
		  * copied after the write is completed.
		*/
		WritableRandomAccessFile* addDataFileForRandomAccess(const DataKey& key, DataFile* file);

		/**
		  * Stores metadata as a DataFile.  The format of the file is JSON.
		  * This class takes ownership of the MetadataMap objects in the map
		  * The metadata can be recovered by calling getDataFileAsMetadata()
		*/
		void addDataFileAsMetadata(const DataKey& key, const std::map<DataKey,MetadataMap*>& meta);

		bool hasDataFile(const DataKey& key) const;

		std::set<DataKey> getAllFileKeys() const;

		MetadataMap* getMetadata(const DataKey& key) const;

		/**
		  * Associates a key to a metadata map.  The map must be
		  * allocated on the heap with new.  This class takes ownership
		  * of the metadata object.  The map object should not be
		  * associated to any other key.  An exception will be thrown
		  * if this key already has metadata.
		*/
		void addMetadata(const DataKey& key, MetadataMap* meta);

		bool hasMetadata(const DataKey& key) const;

		std::set<DataKey> getAllMetadataKeys() const;

		void release();

		/**
		  * For use by the framework.
		*/
		void setContext(DatasetContext* context);

		/**
		  * For use by the framework.
		*/
		void setGlobalKeyspace(const Keyspace& keyspace);

		/**
		  * Tries to get the name of the data file.
		  * @return empty string if the name is not available
		*/
		std::string getDataFileName(const DataKey& key) const;


		/**
		  * Returns the keyspace containing all the elements found in
		  * this dataset.  Return value is a reference valid for
		  * the lifetime of this object.
		*/
		const Keyspace& getKeyspace() const;

		/**
		  * Returns the keyspace containing all the elements found in
		  * file keys of this dataset.  Return value is a
		  * reference valid for the lifetime of this object.
		*/
		const Keyspace& getFilesKeyspace() const;

		/**
		  * Returns the keyspace containing all the elements found in
		  * metadata keys of this dataset.  Return value is a
		  * reference valid for the lifetime of this object.
		*/
		const Keyspace& getMetadataKeyspace() const;
	
		/**
		  * This will overwrite any keyspaces already in this object
		*/
		void generateKeyspaces();

		bool isQueryOnly();

		bool operator==(const Dataset& dataset) const;

		bool operator!=(const Dataset& dataset) const;

		~Dataset();

		static void copySourceToSink(std::shared_ptr<DataFileSource> src, DataFileSink* sink);

		/**
		  * Free memory for metadata obtained from getDataFileAsMetadata()
		*/
		static void freeMetadata(std::map<DataKey,MetadataMap*> meta);

	private:

		DatasetImpl* m_impl;

		// prevent calling these
		Dataset(const Dataset& dataset);
		Dataset& operator=(const Dataset& dataset);

};

}
#endif


