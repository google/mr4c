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

package com.google.mr4c.dataset;

import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.Category;
import com.google.mr4c.config.category.CategoryConfig;
import com.google.mr4c.config.category.CustomConfig;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;
import com.google.mr4c.metadata.MetadataField;
import com.google.mr4c.metadata.MetadataMap;
import com.google.mr4c.metadata.PrimitiveType;
import com.google.mr4c.sources.DataFileSource;
import com.google.mr4c.sources.URIDataFileSource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class LogsDatasetBuilder {

	private MR4CConfig m_bbConf;
	private Dataset m_dataset;

	private String m_task;
	private String m_job;
	private String m_host;
	private DataKeyDimension m_nameDim;
	private DataKeyDimension m_taskDim;

	public static final DataKeyDimension NAME = new DataKeyDimension("NAME");
	public static final DataKeyDimension TASKID = new DataKeyDimension("TASKID");

	public LogsDatasetBuilder(MR4CConfig bbConf) {
		m_bbConf = bbConf;
	}

	public void init() {
		CategoryConfig catConf = m_bbConf.getCategory(Category.CUSTOM);
		m_task = catConf.getProperty(CustomConfig.PROP_TASKID);
		m_job = catConf.getProperty(CustomConfig.PROP_JOBID);
		m_host = catConf.getProperty(CustomConfig.PROP_HOST);
		m_dataset = new Dataset();
		addMetadata();
	}

	public Dataset getDataset() {
		return m_dataset;
	}

	public void addFiles(File... files) {
		addFiles(Arrays.asList(files));
	}

	public void addFiles(Collection<File> files) {
		for ( File file : files ) {
			addFile(file);
		}
	}

	public void addFile(File file) {
		DataKey key = makeFileKey(file);
		DataFile dataFile = makeFile(file);
		m_dataset.addFile(key,dataFile);
	}

	private DataKey makeFileKey(File file) {
		List<DataKeyElement> elements = new ArrayList<DataKeyElement>();
		elements.add(new DataKeyElement(file.getName(), NAME));
		if ( m_task!=null ) {
			elements.add(new DataKeyElement(m_task, TASKID));
		}
		return DataKeyFactory.newKey(elements);
	}

	private DataFile makeFile(File file) {
		DataFileSource src = new URIDataFileSource(file.toURI(), file.getName());
		return new DataFile(src, "text/plain");
	}

	private void addMetadata() {
		boolean hasTask = !StringUtils.isEmpty(m_task);
		boolean hasJob = !StringUtils.isEmpty(m_job);
		boolean hasHost = !StringUtils.isEmpty(m_host);

		if ( !hasTask ) {
			// no key
			return;
		}

		if ( !(hasJob || hasHost) ) {
			// no data
			return;
		}
		
		DataKey key = DataKeyFactory.newKey(new DataKeyElement(m_task, TASKID));
		MetadataMap metaMap = new MetadataMap();

		if ( hasJob ) {
			MetadataField field = new MetadataField(m_job, PrimitiveType.STRING);
			metaMap.getMap().put("job", field);
		}
			
		if ( hasHost ) {
			MetadataField field = new MetadataField(m_host, PrimitiveType.STRING);
			metaMap.getMap().put("host", field);
		}
		
		m_dataset.addMetadata(key, metaMap);	
	}
}

