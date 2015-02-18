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

import com.google.mr4c.config.execution.DimensionConfig;
import com.google.mr4c.config.execution.MapConfig;
import com.google.mr4c.config.execution.ValueConfig;
import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.dataset.DatasetTransformer;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.ElementTransformer;
import com.google.mr4c.keys.KeyTransformer;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

// wraps a source with a specified key transformation
public class TransformedDatasetSource implements DatasetSource {

	private DatasetSource m_src;
	private MapConfig m_config;
	private KeyTransformer m_readKeyTrans;
	private KeyTransformer m_writeKeyTrans;
	private DatasetTransformer m_readTrans;
	private DatasetTransformer m_writeTrans;

	public TransformedDatasetSource(DatasetSource src, MapConfig config) {
		m_src = src;
		m_config = config;
		buildReadTransformers();
		buildWriteTransformers();
		
	}

	public Dataset readDataset() throws IOException {
		Dataset origDataset = m_src.readDataset();
		return m_readTrans.transformDataset(origDataset, false);
	}

	public void writeDataset(Dataset dataset) throws IOException {
		writeDataset(dataset, WriteMode.ALL);
	}

	public void writeDataset(Dataset dataset, WriteMode writeMode) throws IOException {
		Dataset newDataset = m_writeTrans.transformDataset(dataset, true);
		m_src.writeDataset(newDataset, writeMode);
	}

	public DataFileSink getDataFileSink(DataKey key) throws IOException {
		DataKey newKey = m_writeKeyTrans.transformKey(key);
		return m_src.getDataFileSink(newKey);
	}

	public DataFile findDataFile(DataKey key) throws IOException {
		DataKey newKey = m_writeKeyTrans.transformKey(key);
		return m_src.findDataFile(newKey);
	}

	public void ensureExists() throws IOException {
		m_src.ensureExists();
	}

	public void copyToFinal() throws IOException {
		m_src.copyToFinal();
	}

	public void setQueryOnly(boolean queryOnly) {
		m_src.setQueryOnly(queryOnly);
	}

	public boolean isQueryOnly() {
		return m_src.isQueryOnly();
	}
	
	private void buildReadTransformers() {
		m_readKeyTrans = new KeyTransformer();
		for ( DimensionConfig dimConfig : m_config.getDimensions() ) {
			DataKeyDimension dim = new DataKeyDimension(dimConfig.getName());
			DataKeyDimension target = StringUtils.isEmpty(dimConfig.getMapTo()) ?
				null :
				new DataKeyDimension(dimConfig.getMapTo());
			ElementTransformer eleTrans = new ElementTransformer(dim,target);
			for ( ValueConfig valConfig : dimConfig.getValues() ) {
				eleTrans.addValueTransform(valConfig.getName(), valConfig.getMapTo());
			}
			m_readKeyTrans.addDimension(eleTrans);
		}
		m_readTrans = new DatasetTransformer(m_readKeyTrans);
	}

	private void buildWriteTransformers() {
		m_writeKeyTrans = new KeyTransformer();
		for ( DimensionConfig dimConfig : m_config.getDimensions() ) {
			DataKeyDimension target = new DataKeyDimension(dimConfig.getName());
			DataKeyDimension dim = StringUtils.isEmpty(dimConfig.getMapTo()) ?
				target :
				new DataKeyDimension(dimConfig.getMapTo());
			ElementTransformer eleTrans = new ElementTransformer(dim,target);
			for ( ValueConfig valConfig : dimConfig.getValues() ) {
				eleTrans.addValueTransform(valConfig.getMapTo(), valConfig.getName());
			}
			m_writeKeyTrans.addDimension(eleTrans);
		}
		m_writeTrans = new DatasetTransformer(m_writeKeyTrans);
	}

	public String getDescription() {
		return String.format("transformed dataset source wrapping [%s]", m_src.getDescription());
	}

	public SourceType getSourceType() {
		return m_src.getSourceType();
	}

}

