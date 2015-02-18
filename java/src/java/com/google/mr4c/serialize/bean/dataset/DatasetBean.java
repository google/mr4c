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

package com.google.mr4c.serialize.bean.dataset;

import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.metadata.MetadataMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatasetBean {

	private DatasetMetadataBean[] metadata;
	private DatasetFileBean[] files;

	public static DatasetBean instance(Dataset dataset) {
		DatasetBean bean = new DatasetBean();
		addFiles(dataset, bean);
		addMetadata(dataset, bean);
		return bean;
	}

	private static void addFiles(Dataset dataset, DatasetBean bean) {
		List<DatasetFileBean> fileBeans = new ArrayList<DatasetFileBean>();
		List<DataKey> sorted = new ArrayList<DataKey>(dataset.getAllFileKeys());
		Collections.sort(sorted);
		for ( DataKey key : sorted ) {
			DataFile file = dataset.getFile(key);
			DatasetFileBean fileBean = DatasetFileBean.instance(key,file);
			fileBeans.add(fileBean);
		}
		bean.files = fileBeans.toArray(new DatasetFileBean[fileBeans.size()]);
	}

	private static void addMetadata(Dataset dataset, DatasetBean bean) {
		List<DatasetMetadataBean> metadataBeans = new ArrayList<DatasetMetadataBean>();
		List<DataKey> sorted = new ArrayList<DataKey>(dataset.getAllMetadataKeys());
		Collections.sort(sorted);
		for ( DataKey key : sorted ) {
			MetadataMap metadata = dataset.getMetadata(key);
			DatasetMetadataBean metadataBean = DatasetMetadataBean.instance(key,metadata);
			metadataBeans.add(metadataBean);
		}
		bean.metadata = metadataBeans.toArray(new DatasetMetadataBean[metadataBeans.size()]);
	}

	public DatasetBean(){}

	public Dataset toDataset() {
		Dataset dataset = new Dataset();
		addFiles(dataset);
		addMetadata(dataset);
		return dataset;
	}

	private void addFiles(Dataset dataset) {
		for ( DatasetFileBean fileBean : files ) {
			DataKey key = fileBean.extractDataKey();
			DataFile file = fileBean.extractDataFile();
			dataset.addFile(key,file);
		}
	}

	private void addMetadata(Dataset dataset) {
		for ( DatasetMetadataBean metadataBean : metadata ) {
			DataKey key = metadataBean.extractDataKey();
			MetadataMap meta = (MetadataMap) metadataBean.extractMetadata();
			dataset.addMetadata(key,meta);
		}
	}

}

