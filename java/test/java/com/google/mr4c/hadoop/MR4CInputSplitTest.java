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
import java.util.Arrays;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class MR4CInputSplitTest {

	private Dataset m_dataset;

	@Before public void setup() throws Exception {
		m_dataset = DatasetTestUtils.buildDataset1();
	}

	@Test public void testRoundTrip() throws Exception {
		List<DataKey> keys = new ArrayList<DataKey>(m_dataset.getAllFileKeys());
		List<String> hosts = Arrays.asList("host1", "host2", "host3");
		int seqNum = 666;
		MR4CInputSplit split1 = new MR4CInputSplit(seqNum, keys, hosts);
		MR4CInputSplit split2 = new MR4CInputSplit();
		HadoopTestUtils.copyWritable(split1, split2);
		assertEquals(seqNum, split2.getSequenceNumber());
		assertEquals(keys, split2.getKeys().getKeys());
		assertEquals(hosts, Arrays.asList(split2.getLocations()));
	}


}
