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

import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;

/**
  * Source for optional datasets that have no source provided
*/
public class NullDatasetSource extends AbstractDatasetSource {

	private SourceType m_type;

	public NullDatasetSource() {
		this(SourceType.DATA);
	}

	public NullDatasetSource(SourceType type) {
		m_type = type;
	}

	public Dataset readDataset() {
		return null;
	}

	public void writeDataset(Dataset dataset) {}

	public void writeDataset(Dataset dataset, WriteMode writeMode) {}

	public DataFileSink getDataFileSink(DataKey key) {
		return new NullDataFileSink();
	}

	public DataFile findDataFile(DataKey key) {
		return null;
	}

	public String getDescription() {
		return "null dataset source";
	}

	public SourceType getSourceType() {
		return m_type;
	}

}
