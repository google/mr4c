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

package com.google.mr4c;

import com.google.mr4c.sources.DatasetSource.WriteMode;
import com.google.mr4c.testing.TestDataManager;

import java.util.Arrays;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class AlgoRunnerTest {

	private List<String> m_inputs = Arrays.asList("input1", "input2", "input3");
	private List<String> m_outputs = Arrays.asList("output1", "output2");
	private TestDataManager m_mgr = new TestDataManager();

	@Before public void setUp() {

		m_mgr.addDimension("dim1", Arrays.asList("d1v1", "d1v2", "d1v3"));
		m_mgr.addDimension("dim2", Arrays.asList("d2v1", "d2v2", "d2v3"));
		for( String input : m_inputs ) {
			m_mgr.addInputDataset(input);
		}
		for( String output : m_outputs ) {
			m_mgr.addOutputDataset(output);
		}
		m_mgr.readyToTest();
	} 

	@Test public void testRunStandalone() throws Exception {
		AlgoRunner runner = new AlgoRunner(m_mgr.getExecutionSource());
		runner.executeStandalone();
		for ( String  name : m_outputs ) {
			m_mgr.assertWriteCalled(name, WriteMode.SERIALIZED_ONLY);
			m_mgr.assertSerializedContentCorrect(name);
			m_mgr.assertFileContentCorrect(name);
		}
	}

	

}

