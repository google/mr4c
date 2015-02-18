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

import com.google.mr4c.config.algorithm.AlgorithmConfig;
import com.google.mr4c.config.algorithm.DimensionConfig;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;
import com.google.mr4c.testing.TestDataManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.mapred.InputSplit;

import org.junit.*;
import static org.junit.Assert.*;

public class MR4CInputFormatTest {

	private List<String> m_inputs = Arrays.asList("input1", "input2");
	private List<String> m_frames = Arrays.asList( "frame1", "frame2", "frame3", "frame4", "frame5", "frame6", "frame7", "frame8");
	private List<String> m_types = Arrays.asList( "PAN", "MS");
	private DataKeyDimension m_frameDim = new DataKeyDimension("frame");
	private DataKeyDimension m_typeDim = new DataKeyDimension("type");
	private TestDataManager m_mgr = new TestDataManager();

	@Before public void setUp() {
		m_mgr.addDimension("frame", m_frames);
		m_mgr.addDimension("type", m_types);
		for( String input : m_inputs ) {
			m_mgr.addInputDataset(input);
		}
		m_mgr.readyToTest();
	} 

	private Set<Set<DataKey>> buildExpectedSplits(List<List<String>> frameSplits) {
		Set<Set<DataKey>> expectedKeySplits = new HashSet<Set<DataKey>>();
		for ( List<String> frameSplit : frameSplits ) {
			Set<DataKey> keys = new HashSet<DataKey>();
			for ( String frameName : frameSplit ) {
				for ( String typeName : m_types ) {
					DataKeyElement frameElement = new DataKeyElement(frameName, m_frameDim);
					DataKeyElement typeElement = new DataKeyElement(typeName, m_typeDim);
					keys.add(DataKeyFactory.newKey(frameElement, typeElement));
				}
			}
			expectedKeySplits.add(keys);
		}
		return expectedKeySplits;
	}
		
	@Test public void testSimpleSplit() throws Exception {
		List<List<String>> frameSplits = Arrays.asList(
			Arrays.asList( "frame1", "frame2" ),
			Arrays.asList( "frame3", "frame4" ),
			Arrays.asList( "frame5", "frame6" ),
			Arrays.asList( "frame7", "frame8" )
		);
		doTest(frameSplits, 0, 0, null);

	}

	@Test public void testSplitWithOverlap() throws Exception {
		List<List<String>> frameSplits = Arrays.asList(
			Arrays.asList( "frame1", "frame2", "frame3" ),
			Arrays.asList( "frame2", "frame3", "frame4", "frame5" ),
			Arrays.asList( "frame4", "frame5", "frame6", "frame7" ),
			Arrays.asList( "frame6", "frame7", "frame8" )
		);
		doTest(frameSplits, 1, 1, null);

	}

	@Test public void testSplitWithChunkSize() throws Exception {
		List<List<String>> frameSplits = Arrays.asList(
			Arrays.asList( "frame1", "frame2", "frame3", "frame4" ),
			Arrays.asList( "frame5", "frame6", "frame7", "frame8" )
		);
		doTest(frameSplits, 0, 0, 4);

	}

	private void doTest(List<List<String>> frameSplits, int overlapBefore, int overlapAfter, Integer chunkSize) throws Exception {
		AlgorithmConfig algoConfig = m_mgr.getExecutionSource().getAlgorithmConfig();
		algoConfig.addDimension(new DimensionConfig("frame", true, overlapBefore, overlapAfter, null, chunkSize, false));
		algoConfig.addDimension(new DimensionConfig("type", false, 0, 0, null, null, false));

		Set<Set<DataKey>> expectedKeySplits = buildExpectedSplits(frameSplits);
		MR4CInputFormat format = new MR4CInputFormat();
		InputSplit[] splits = format.getSplits( m_mgr.getExecutionSource(), 4);

		Set<Set<DataKey>> actualKeySplits=  new HashSet<Set<DataKey>>();
		for ( InputSplit split : splits ) {
			MR4CInputSplit bbSplit = (MR4CInputSplit) split;
			actualKeySplits.add(new HashSet<DataKey>(bbSplit.getKeys().getKeys()));
		}
		assertEquals(expectedKeySplits, actualKeySplits);
	}


}

