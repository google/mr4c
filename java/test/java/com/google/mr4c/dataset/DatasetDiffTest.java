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
import com.google.mr4c.metadata.MetadataField;
import com.google.mr4c.metadata.MetadataKey;
import com.google.mr4c.metadata.MetadataMap;
import com.google.mr4c.metadata.PrimitiveType;

import org.junit.*;
import static org.junit.Assert.*;

public class DatasetDiffTest {

	private DataKeyDimension m_dim;

	@Before public void setup() throws Exception {
		m_dim = new DataKeyDimension("dim1");
	}

	@Test public void testDifferent() {
		DatasetDiff diff = new DatasetDiff(
			buildDataset1(),
			buildDataset2()
		);
		diff.computeDiff();
		assertEquals(buildSame(), diff.getSame());
		assertEquals(buildOnly1(), diff.getOnlyInDataset1());
		assertEquals(buildOnly2(), diff.getOnlyInDataset2());
		assertEquals(buildDiff1(), diff.getDifferentInDataset1());
		assertEquals(buildDiff2(), diff.getDifferentInDataset2());
	}

	@Test public void testSame() {
		Dataset dataset = buildDataset1();
		DatasetDiff diff = new DatasetDiff(
			dataset,
			dataset
		);
		diff.computeDiff();
		Dataset empty = new Dataset();
		assertEquals(dataset, diff.getSame());
		assertEquals(empty, diff.getOnlyInDataset1());
		assertEquals(empty, diff.getOnlyInDataset2());
		assertEquals(empty, diff.getDifferentInDataset1());
		assertEquals(empty, diff.getDifferentInDataset2());
	}


	private DataKey buildKey(int num) {
		DataKeyElement ele = new DataKeyElement("val"+num, m_dim);
		return DataKeyFactory.newKey(ele);
	}

	private DataFile buildFile(int num) {
		byte[] bytes = new byte[5];
		for ( int i=0; i<5; i++ ) {
			bytes[i] = (byte)num;
		}
		return new DataFile(bytes, "image/png");
	}

	private MetadataMap buildMetadata(int num) {
		MetadataField field = new MetadataField(num, PrimitiveType.INTEGER);
		MetadataMap map = new MetadataMap();
		map.getMap().put("name", field);
		return map;
	}

	private Dataset buildDataset1() {
		Dataset dataset = new Dataset();
		dataset.addFile(buildKey(1), buildFile(10));
		dataset.addFile(buildKey(2), buildFile(21));
		dataset.addFile(buildKey(3), buildFile(31));
		dataset.addMetadata(buildKey(5), buildMetadata(50));
		dataset.addMetadata(buildKey(6), buildMetadata(61));
		dataset.addMetadata(buildKey(7), buildMetadata(71));
		return dataset;
	}

	private Dataset buildDataset2() {
		Dataset dataset = new Dataset();
		dataset.addFile(buildKey(1), buildFile(10));
		dataset.addFile(buildKey(2), buildFile(22));
		dataset.addFile(buildKey(4), buildFile(42));
		dataset.addMetadata(buildKey(5), buildMetadata(50));
		dataset.addMetadata(buildKey(6), buildMetadata(62));
		dataset.addMetadata(buildKey(8), buildMetadata(82));
		return dataset;
	}

	private Dataset buildSame() {
		Dataset dataset = new Dataset();
		dataset.addFile(buildKey(1), buildFile(10));
		dataset.addMetadata(buildKey(5), buildMetadata(50));
		return dataset;
	}
		
	private Dataset buildOnly1() {
		Dataset dataset = new Dataset();
		dataset.addFile(buildKey(3), buildFile(31));
		dataset.addMetadata(buildKey(7), buildMetadata(71));
		return dataset;
	}
		
	private Dataset buildOnly2() {
		Dataset dataset = new Dataset();
		dataset.addFile(buildKey(4), buildFile(42));
		dataset.addMetadata(buildKey(8), buildMetadata(82));
		return dataset;
	}
		
	private Dataset buildDiff1() {
		Dataset dataset = new Dataset();
		dataset.addFile(buildKey(2), buildFile(21));
		dataset.addMetadata(buildKey(6), buildMetadata(61));
		return dataset;
	}
		
	private Dataset buildDiff2() {
		Dataset dataset = new Dataset();
		dataset.addFile(buildKey(2), buildFile(22));
		dataset.addMetadata(buildKey(6), buildMetadata(62));
		return dataset;
	}

}
