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

package com.google.mr4c.keys;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class KeyspaceTest {

	private DataKeyDimension m_dim1;
	private DataKeyDimension m_dim2;
	private DataKeyDimension m_dim3;
	private Set<DataKeyDimension> m_dims;

	private DataKeyElement m_ele1a;
	private DataKeyElement m_ele1b;
	private DataKeyElement m_ele1c;
	private DataKeyElement m_ele2a;
	private DataKeyElement m_ele2b;
	private DataKeyElement m_ele3;

	private List<DataKeyElement> m_eles1;
	private List<DataKeyElement> m_eles2;
	private List<DataKeyElement> m_eles3;

	private Keyspace m_ks1a;
	private Keyspace m_ks1b;
	private Keyspace m_ks2;

	@Before public void setUp() {
		m_dim1 = new DataKeyDimension("dim1");
		m_dim2 = new DataKeyDimension("dim2");
		m_dim3 = new DataKeyDimension("dim3");
		m_dims = new HashSet<DataKeyDimension>(Arrays.asList(m_dim1, m_dim2, m_dim3));

		m_ele1a = new DataKeyElement("ele1a", m_dim1);
		m_ele1b = new DataKeyElement("ele1b", m_dim1);
		m_ele1c = new DataKeyElement("ele1c", m_dim1);
		m_ele2a = new DataKeyElement("ele2a", m_dim2);
		m_ele2b = new DataKeyElement("ele2b", m_dim2);
		m_ele3 = new DataKeyElement("ele3", m_dim3);

		m_eles1 = Arrays.asList(m_ele1a, m_ele1b, m_ele1c);
		m_eles2 = Arrays.asList(m_ele2a, m_ele2b);
		m_eles3 = Arrays.asList(m_ele3);

		m_ks1a = buildKeyspace1(); 
		m_ks1b = buildKeyspace1(); 
		m_ks2 = buildKeyspace2(); 
	} 

	@Test
	public void testDimensions() {
		assertEquals(m_dims, m_ks1a.getDimensions());
	}
		
	@Test
	public void testElements() {
		assertEquals(m_eles1, m_ks1a.getKeyspaceDimension(m_dim1).getElements());
		assertEquals(m_eles2, m_ks1a.getKeyspaceDimension(m_dim2).getElements());
		assertEquals(m_eles3, m_ks1a.getKeyspaceDimension(m_dim3).getElements());
	}

	@Test
	public void testEquals() {
		assertEquals(m_ks1a, m_ks1b);
	}

	@Test
	public void testNotEqual() {
		assertFalse(m_ks1a.equals(m_ks2));
	}


	private Keyspace buildKeyspace1() {
		Keyspace ks = new Keyspace();
		Collection<DataKey> keys = Arrays.asList(
			DataKeyFactory.newKey(m_ele2b, m_ele1a),
			DataKeyFactory.newKey(m_ele1b, m_ele2a),
			DataKeyFactory.newKey(m_ele1c, m_ele3, m_ele2b),
			DataKeyFactory.newKey(m_ele1b)
		);
		ks.addKeys(keys);
		return ks;
	}

	private Keyspace buildKeyspace2() {
		Keyspace ks = new Keyspace();
		Collection<DataKey> keys = Arrays.asList(
			DataKeyFactory.newKey(m_ele2a, m_ele1a),
			DataKeyFactory.newKey(m_ele1b, m_ele3, m_ele2b),
			DataKeyFactory.newKey(m_ele2a)
		);
		ks.addKeys(keys);
		return ks;
	}

}
