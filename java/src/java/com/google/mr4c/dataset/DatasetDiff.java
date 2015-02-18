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

import com.google.mr4c.keys.BasicDataKeyFilter;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.metadata.MetadataMap;
import com.google.mr4c.util.SetAnalysis;

import java.util.HashSet;
import java.util.Set;

public class DatasetDiff {
	
	private static class DatasetInfo {
		private Dataset m_ds1;
		private Dataset m_ds2;
		private Dataset m_same;
		private Dataset m_only1;
		private Dataset m_only2;
		private Dataset m_diff1;
		private Dataset m_diff2;
		// rest not valid for "all"
		private SetAnalysis<DataKey> m_keyAnalysis;
		private Set<DataKey> m_sameKeys = new HashSet<DataKey>();
		private Set<DataKey> m_diffKeys = new HashSet<DataKey>();
	}

	private DatasetInfo m_all = new DatasetInfo();
	private DatasetInfo m_files = new DatasetInfo();
	private DatasetInfo m_meta = new DatasetInfo();
	private boolean m_diff = false;

	public DatasetDiff(
		Dataset dataset1,
		Dataset dataset2
	) {
		m_all.m_ds1 = dataset1;
		m_all.m_ds2 = dataset2;
	}

	public Dataset getDataset1() {
		return m_all.m_ds1;
	}

	public Dataset getDataset2() {
		return m_all.m_ds2;
	}

	public synchronized Dataset getSame() {
		return m_all.m_same;
	}

	public synchronized Dataset getOnlyInDataset1() {
		return m_all.m_only1;
	}

	public synchronized Dataset getOnlyInDataset2() {
		return m_all.m_only2;
	}

	public synchronized Dataset getDifferentInDataset1() {
		return m_all.m_diff1;
	}

	public synchronized Dataset getDifferentInDataset2() {
		return m_all.m_diff2;
	}

	public synchronized boolean different() {
		return m_diff;
	}

	public synchronized void computeDiff() {
		analyzeFileKeys();
		analyzeMetaKeys();
		checkFiles();
		checkMetadata();
		combine();
		checkForDiff();
	}

	private void analyzeFileKeys() {
		m_files.m_keyAnalysis = SetAnalysis.analyze(
			m_all.m_ds1.getAllFileKeys(),
			m_all.m_ds2.getAllFileKeys()
		);
	}

	private void analyzeMetaKeys() {
		m_meta.m_keyAnalysis = SetAnalysis.analyze(
			m_all.m_ds1.getAllMetadataKeys(),
			m_all.m_ds2.getAllMetadataKeys()
		);
	}

	private void checkFiles() {
		m_files.m_ds1 = m_all.m_ds1.toFilesOnly();
		m_files.m_ds2 = m_all.m_ds2.toFilesOnly();
		for ( DataKey key : m_files.m_keyAnalysis.getInBothSets() ) {
			DataFile file1 = m_files.m_ds1.getFile(key);
			DataFile file2 = m_files.m_ds2.getFile(key);
			if ( file1.equals(file2) ) {
				m_files.m_sameKeys.add(key);
			} else {
				m_files.m_diffKeys.add(key);
			}
		}
		process(m_files);
	}

	private void checkMetadata() {
		m_meta.m_ds1 = m_all.m_ds1.toMetadataOnly();
		m_meta.m_ds2 = m_all.m_ds2.toMetadataOnly();
		for ( DataKey key : m_meta.m_keyAnalysis.getInBothSets() ) {
			MetadataMap map1 = m_meta.m_ds1.getMetadata(key);
			MetadataMap map2 = m_meta.m_ds2.getMetadata(key);
			if ( map1.equals(map2) ) {
				m_meta.m_sameKeys.add(key);
			} else {
				m_meta.m_diffKeys.add(key);
			}
		}
		process(m_meta);
	}

	private void combine() {
		m_all.m_same = Dataset.combineSlices(m_files.m_same, m_meta.m_same);
		m_all.m_only1 = Dataset.combineSlices(m_files.m_only1, m_meta.m_only1);
		m_all.m_only2 = Dataset.combineSlices(m_files.m_only2, m_meta.m_only2);
		m_all.m_diff1 = Dataset.combineSlices(m_files.m_diff1, m_meta.m_diff1);
		m_all.m_diff2 = Dataset.combineSlices(m_files.m_diff2, m_meta.m_diff2);
	}

	private void checkForDiff() {
		if ( !m_all.m_only1.isEmpty() ) { m_diff = true; return; }
		if ( !m_all.m_only2.isEmpty() ) { m_diff = true; return; }
		if ( !m_all.m_diff1.isEmpty() ) { m_diff = true; return; }
		if ( !m_all.m_diff2.isEmpty() ) { m_diff = true; return; }
	}

	private void process(DatasetInfo info) {
		info.m_same = slice(info.m_ds1, info.m_sameKeys);
		info.m_only1 = slice(info.m_ds1, info.m_keyAnalysis.getOnlyInSet1());
		info.m_only2 = slice(info.m_ds2, info.m_keyAnalysis.getOnlyInSet2());
		info.m_diff1 = slice(info.m_ds1, info.m_diffKeys);
		info.m_diff2 = slice(info.m_ds2, info.m_diffKeys);
	}

	private Dataset slice(Dataset dataset, Set<DataKey> keys) {
		BasicDataKeyFilter filter = new BasicDataKeyFilter();
		filter.addKeys(keys);
		return dataset.slice(filter);
	}

}

