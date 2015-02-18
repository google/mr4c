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

package com.google.mr4c.sources;

import com.google.mr4c.dataset.DataFileFinder;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;

import java.io.IOException;

public interface DatasetSource extends DataFileFinder {

	public enum WriteMode { ALL, FILES_ONLY, SERIALIZED_ONLY }

	public enum SourceType { DATA, LOGS }

	Dataset readDataset() throws IOException;

	// assumes ALL
	void writeDataset(Dataset dataset) throws IOException;

	void writeDataset(Dataset dataset, WriteMode writeMode) throws IOException;

	DataFileSink getDataFileSink(DataKey key) throws IOException;

	void copyToFinal() throws IOException;

	void ensureExists() throws IOException;

	String getDescription();

	void setQueryOnly(boolean queryOnly);

	boolean isQueryOnly();

	SourceType getSourceType();

}

