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
import com.google.mr4c.keys.ElementTransformer;
import com.google.mr4c.keys.KeyTransformer;
import com.google.mr4c.metadata.MetadataField;
import com.google.mr4c.metadata.MetadataKey;
import com.google.mr4c.metadata.MetadataList;
import com.google.mr4c.metadata.MetadataMap;
import com.google.mr4c.metadata.PrimitiveType;

import org.junit.*;
import static org.junit.Assert.*;

public class DatasetTransformerTest {

	private DataKeyDimension m_dim1a;
	private DataKeyDimension m_dim1b;
	private DataKeyDimension m_dim2a;
	private DataKeyDimension m_dim2b;
	private DataKeyDimension m_dim3;
	private DataKeyDimension m_dim4a;
	private DataKeyDimension m_dim4b;

	private DataKey m_key1a;
	private DataKey m_key1b;
	private DataKey m_key2a;
	private DataKey m_key2b;
	private DataKey m_key3;
	private DataKey m_key4a;
	private DataKey m_key4b;

	private Dataset m_dataset;
	private Dataset m_datasetCopy;
	private Dataset m_transDataset;
	private DatasetTransformer m_trans;


	@Before public void setUp() {
		buildDimensions();
		buildKeys();
		buildTransform();
		m_dataset = buildDataset();
		m_datasetCopy = buildDataset();
		m_transDataset = buildTransformedDataset();
	} 

	@Test public void testTransform() {
		Dataset transformed = m_trans.transformDataset(m_dataset, true);
		assertEquals(m_transDataset, transformed );
	}

	@Test public void testReadOnlyTransform() {
		Dataset transformed = m_trans.transformDataset(m_dataset, true);
		assertEquals(m_transDataset, transformed );
		assertEquals(m_dataset, m_datasetCopy ); // make sure the original wasn't changed
	}

	private void buildDimensions() {
		m_dim1a = new DataKeyDimension("dim1a");
		m_dim1b = new DataKeyDimension("dim1b");
		m_dim2a = new DataKeyDimension("dim2a");
		m_dim2b = new DataKeyDimension("dim2b");
		m_dim3 = new DataKeyDimension("dim3");
		m_dim4a = new DataKeyDimension("dim4a");
		m_dim4b = new DataKeyDimension("dim4b");
	}

	private void buildKeys() {
		m_key1a = DataKeyFactory.newKey( new DataKeyElement("val1", m_dim1a));
		m_key1b = DataKeyFactory.newKey( new DataKeyElement("val1", m_dim1b));
		m_key2a = DataKeyFactory.newKey( new DataKeyElement("val2", m_dim1a));
		m_key2b = DataKeyFactory.newKey( new DataKeyElement("val2", m_dim1b));
		m_key3 = DataKeyFactory.newKey( new DataKeyElement("val3", m_dim3));
		m_key4a = DataKeyFactory.newKey( new DataKeyElement("val4", m_dim4a));
		m_key4b = DataKeyFactory.newKey( new DataKeyElement("val4", m_dim4b));
	}

	private void buildTransform() {
		KeyTransformer keyTrans = new KeyTransformer();
		ElementTransformer et1 = new ElementTransformer(m_dim1a, m_dim1b);
		ElementTransformer et2 = new ElementTransformer(m_dim2a, m_dim2b);
		ElementTransformer et4 = new ElementTransformer(m_dim4a, m_dim4b);
		keyTrans.addDimension(et1);
		keyTrans.addDimension(et2);
		keyTrans.addDimension(et4);
		m_trans = new DatasetTransformer(keyTrans);
	}

	private Dataset buildDataset() {
		Dataset dataset = new Dataset();
		dataset.addFile(m_key1a, DatasetTestUtils.buildDataFile1());
		dataset.addFile(m_key2a, DatasetTestUtils.buildDataFile2());
		dataset.addFile(m_key3, DatasetTestUtils.buildDataFile3());
		MetadataMap metaMap = new MetadataMap();
		MetadataList metaList = new MetadataList();
		metaList.getList().add(new MetadataKey(m_key1a));
		metaList.getList().add(new MetadataKey(m_key2a));
		metaList.getList().add(new MetadataKey(m_key3));
		metaMap.getMap().put("keys", metaList);
		metaMap.getMap().put("field", new MetadataField(7, PrimitiveType.INTEGER));
		dataset.addMetadata(m_key4a, metaMap);
		return dataset;
	}

	private Dataset buildTransformedDataset() {
		Dataset dataset = new Dataset();
		dataset.addFile(m_key1b, DatasetTestUtils.buildDataFile1());
		dataset.addFile(m_key2b, DatasetTestUtils.buildDataFile2());
		dataset.addFile(m_key3, DatasetTestUtils.buildDataFile3());
		MetadataMap metaMap = new MetadataMap();
		MetadataList metaList = new MetadataList();
		metaList.getList().add(new MetadataKey(m_key1b));
		metaList.getList().add(new MetadataKey(m_key2b));
		metaList.getList().add(new MetadataKey(m_key3));
		metaMap.getMap().put("keys", metaList);
		metaMap.getMap().put("field", new MetadataField(7, PrimitiveType.INTEGER));
		dataset.addMetadata(m_key4b, metaMap);
		return dataset;
	}

	

}

