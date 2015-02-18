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

import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;
import com.google.mr4c.metadata.MetadataArray;
import com.google.mr4c.metadata.MetadataField;
import com.google.mr4c.metadata.MetadataKey;
import com.google.mr4c.metadata.MetadataList;
import com.google.mr4c.metadata.MetadataMap;
import com.google.mr4c.metadata.PrimitiveType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class DatasetTestUtils {

	private static DataKeyDimension m_dim1;
	private static DataKeyDimension m_dim2;

	static {
		buildDimensions();
	}

	private static void buildDimensions() {
		m_dim1 = new DataKeyDimension("dim1");
		m_dim2 = new DataKeyDimension("dim2");
	}

	public static DataFile buildDataFile1() {
		return new DataFile(
			new byte[] { 5, 6, 7, 8 },
			"image/png"
		);
	}

	public static DataFile buildDataFile2() {
		return new DataFile(
			new byte[] { 32,-6, 0},
			"image/png"
		);
	}

	public static DataFile buildDataFile3() {
		return new DataFile(
			new byte[] { 0, 66, 127},
			"image/png"
		);
	}

	public static Dataset buildDataset1() {
		Dataset dataset = new Dataset();
		addSlice1(dataset);
		addSlice2(dataset);
		addSlice3(dataset);
		return dataset;
	}

	public static Collection<Dataset> buildDataset1Slices() {
		Collection<Dataset> datasets = new ArrayList<Dataset>();
		Dataset dataset1 = new Dataset();
		addSlice1(dataset1);
		datasets.add(dataset1);
		Dataset dataset2 = new Dataset();
		addSlice2(dataset2);
		datasets.add(dataset2);
		Dataset dataset3 = new Dataset();
		addSlice3(dataset3);
		datasets.add(dataset3);
		return datasets;
	}

	private static void addSlice1(Dataset dataset) {
		DataKeyElement ele1 = new DataKeyElement("val1", m_dim1);
		DataKey key1 = DataKeyFactory.newKey(ele1);
		dataset.addFile(key1, buildDataFile1());
	}


	private static void addSlice2(Dataset dataset) {
		DataKeyElement ele11 = new DataKeyElement("val11", m_dim1);
		DataKey key11 = DataKeyFactory.newKey(ele11);
		dataset.addFile(key11, buildDataFile2());
		DataKeyElement ele2 = new DataKeyElement("val2", m_dim2);
		DataKey key2 = DataKeyFactory.newKey(ele2);
		MetadataField field = new MetadataField("some_data", PrimitiveType.STRING);
		MetadataKey metakey = new MetadataKey(key11);
		MetadataArray array = new MetadataArray(Arrays.asList(true, false, false), PrimitiveType.BOOLEAN);
		MetadataMap map = new MetadataMap();
		map.getMap().put("some_name", field);
		map.getMap().put("whateva", array);
		map.getMap().put("a_key", metakey);
		dataset.addMetadata(key2,map);
		DataKeyElement ele22 = new DataKeyElement("val22", m_dim2);
		DataKey key22 = DataKeyFactory.newKey(ele22);
		MetadataField field2 = new MetadataField("some more data", PrimitiveType.STRING);
		MetadataList list = new MetadataList();
		list.getList().add(field2);
		MetadataMap map2 = new MetadataMap();
		map2.getMap().put("another_name", list);
		dataset.addMetadata(key22,map2);
	}

	private static void addSlice3(Dataset dataset) {
		DataKeyElement ele1 = new DataKeyElement("val1", m_dim1);
		DataKeyElement ele2 = new DataKeyElement("val2", m_dim2);
		DataKey key3 = DataKeyFactory.newKey(ele1, ele2);
		dataset.addFile(key3, buildDataFile3());
	}

	public static Dataset buildDataset2() {
		Dataset dataset = new Dataset();
		DataKeyElement ele1 = new DataKeyElement("val1", m_dim1);
		DataKey key1 = DataKeyFactory.newKey(ele1);
		dataset.addFile(key1, buildDataFile1());
		DataKeyElement ele2 = new DataKeyElement("val2", m_dim2);
		DataKey key2 = DataKeyFactory.newKey(ele2);
		MetadataField field = new MetadataField("whatever", PrimitiveType.STRING);
		MetadataMap map = new MetadataMap();
		map.getMap().put("name", field);
		dataset.addMetadata(key2,map);
		return dataset;
	}

}
