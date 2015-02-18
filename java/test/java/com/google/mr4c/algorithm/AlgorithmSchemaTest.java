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

package com.google.mr4c.algorithm;

import com.google.common.collect.Lists;

import com.google.mr4c.keys.DataKeyDimension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class AlgorithmSchemaTest {

	private AlgorithmSchema m_algoSchema;

	private Set<String> m_requiredInputs;
	private Set<String> m_optionalInputs;
	private Set<String> m_excludedInputs;

	@Before public void setup() throws Exception {
		m_algoSchema = buildSchema();
		buildInputSets();
	}

	@Test public void testEquals() {
		AlgorithmSchema algoSchema = buildSchema();
		assertEquals(m_algoSchema, algoSchema);
	}

	@Test public void testNotEqualInput() {
		AlgorithmSchema algoSchema = buildSchema();
		algoSchema.addInputDataset("xxxx");
		assertFalse(m_algoSchema.equals(algoSchema));
	}

	@Test public void testNotEqualOptionalInput() {
		AlgorithmSchema algoSchema = buildSchema();
		algoSchema.addInputDataset("xxxx", true);
		assertFalse(m_algoSchema.equals(algoSchema));
	}

	@Test public void testNotEqualExcludedInput() {
		AlgorithmSchema algoSchema = buildSchema();
		algoSchema.addInputDataset("xxxx", false, true);
		assertFalse(m_algoSchema.equals(algoSchema));
	}

	@Test public void testNotEqualOutput() {
		AlgorithmSchema algoSchema = buildSchema();
		algoSchema.addOutputDataset("xxxx");
		assertFalse(m_algoSchema.equals(algoSchema));
	}

	@Test public void testNotEqualDimension() {
		AlgorithmSchema algoSchema = buildSchema();
		algoSchema.addExpectedDimension(new DataKeyDimension("xxxx"));
		assertFalse(m_algoSchema.equals(algoSchema));
	}

	@Test public void testRequiredInputs() {
		assertEquals(m_requiredInputs, m_algoSchema.getRequiredInputDatasets());
	}

	@Test public void testOptionalInputs() {
		assertEquals(m_optionalInputs, m_algoSchema.getOptionalInputDatasets());
	}

	@Test public void testExcludedInputs() {
		assertEquals(m_excludedInputs, m_algoSchema.getExcludedInputDatasets());
	}

	private AlgorithmSchema buildSchema() {
		return AlgorithmDataTestUtils.buildAlgorithmSchema();
	}

	private void buildInputSets() {
		m_requiredInputs = new HashSet<String>(Arrays.asList("input1", "input2", "input4"));
		m_optionalInputs = new HashSet<String>(Arrays.asList("input3", "input5"));
		m_excludedInputs = new HashSet<String>(Arrays.asList("input4", "input5"));
	}


}
