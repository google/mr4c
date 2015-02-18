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

import org.junit.*;
import static org.junit.Assert.*;

public class CompoundDataKeyTest {

	private DataKeyDimension m_dim1;
	private DataKeyDimension m_dim2;
	private DataKeyDimension m_dim3;

	private DataKeyElement m_ele1;
	private DataKeyElement m_ele2a; 
	private DataKeyElement m_ele2b; 
	private DataKeyElement m_ele3; 

	private DataKey m_key1a;
	private DataKey m_key1b;
	private DataKey m_key2; // different ids
	private DataKey m_key3; // different dims

	@Before public void setUp() {
		m_dim1 = buildDimension1(); 
		m_dim2 = buildDimension2(); 
		m_dim3 = buildDimension3(); 
		m_ele1 = buildElement1(); 
		m_ele2a = buildElement2a();
		m_ele2b = buildElement2b();
		m_ele3 = buildElement3();
		m_key1a = buildKey1(); 
		m_key1b = buildKey1(); 
		m_key2 = buildKey2(); 
		m_key3 = buildKey3(); 
	} 

	@Test public void testEquals() {
		assertEquals(m_key1a, m_key1b);
	}

	@Test public void testNotEqualId() {
		assertFalse(m_key1a.equals(m_key2));
	}

	@Test public void testNotEqualDim() {
		assertFalse(m_key1a.equals(m_key3));
	}

	@Test public void testGetElement() {
		assertEquals(m_ele1, m_key1a.getElement(m_dim1));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetElementFail() {
		m_key1a.getElement(m_dim3);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructorFail() {
		new CompoundDataKey( m_ele1, m_ele2a, m_ele2b, m_ele3 );
	}

	private DataKeyDimension buildDimension1() {
		return new DataKeyDimension("dim1");
	}

	private DataKeyDimension buildDimension2() {
		return new DataKeyDimension("dim2");
	}

	private DataKeyDimension buildDimension3() {
		return new DataKeyDimension("dim3");
	}

	private DataKeyElement buildElement1() {
		return new DataKeyElement("ele1", m_dim1);
	}

	private DataKeyElement buildElement2a() {
		return new DataKeyElement("ele2a", m_dim2);
	}

	private DataKeyElement buildElement2b() {
		return new DataKeyElement("ele2b", m_dim2);
	}

	private DataKeyElement buildElement3() {
		return new DataKeyElement("ele3", m_dim3);
	}

	private DataKey buildKey1() {
		return new CompoundDataKey( m_ele1, m_ele2a );
	}

	private DataKey buildKey2() {
		return new CompoundDataKey( m_ele1, m_ele2b );
	}

	private DataKey buildKey3() {
		return new CompoundDataKey( m_ele1, m_ele3 );
	}

}
