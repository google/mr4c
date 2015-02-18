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

import java.io.IOException;

public class StagedDatasetSource implements DatasetSource {

	private DatasetSource m_actualSrc;
	private DatasetSource m_stagingSrc;

	public StagedDatasetSource( DatasetSource actualSrc, DatasetSource stagingSrc) {
		m_actualSrc = actualSrc;
		m_stagingSrc = stagingSrc;
	}
	
	public Dataset readDataset() throws IOException {
		return m_actualSrc.readDataset();
	}

	public void writeDataset(Dataset dataset) throws IOException {
		m_stagingSrc.writeDataset(dataset);
	}

	public void writeDataset(Dataset dataset, WriteMode writeMode) throws IOException {
		m_stagingSrc.writeDataset(dataset, writeMode);
	}

	public DataFileSink getDataFileSink(DataKey key) throws IOException {
		return m_stagingSrc.getDataFileSink(key);
	}

	public DataFile findDataFile(DataKey key) throws IOException {
		return m_actualSrc.findDataFile(key);
	}

	public void copyToFinal() throws IOException {
		SourceUtils.copySource(m_stagingSrc, m_actualSrc);
	}

	public void ensureExists() throws IOException {
		m_stagingSrc.ensureExists();
		m_actualSrc.ensureExists();
	}

	public void setQueryOnly(boolean queryOnly) {
		m_actualSrc.setQueryOnly(queryOnly);
	}

	public boolean isQueryOnly() {
		return m_actualSrc.isQueryOnly();
	}

	public String getDescription() {
		return String.format("staged dataset source: actual is [%s] and staged is [%s]", m_actualSrc.getDescription(), m_stagingSrc.getDescription());
	}

	public SourceType getSourceType() {
		return m_actualSrc.getSourceType();
	}
		
}

