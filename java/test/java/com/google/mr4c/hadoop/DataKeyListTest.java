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

package com.google.mr4c.hadoop;

import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.dataset.DatasetTestUtils;
import com.google.mr4c.keys.DataKey;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class DataKeyListTest {

	private Dataset m_dataset;

	@Before public void setup() throws Exception {
		m_dataset = DatasetTestUtils.buildDataset1();
	}

	@Test public void testRoundTrip() throws Exception {
		List<DataKey> list1 = new ArrayList<DataKey>(m_dataset.getAllFileKeys());
		DataKeyList dkl1 = new DataKeyList(list1);
		DataKeyList dkl2 = new DataKeyList();
		HadoopTestUtils.copyWritable(dkl1, dkl2);
		assertEquals(list1, dkl2.getKeys());
	}


}
