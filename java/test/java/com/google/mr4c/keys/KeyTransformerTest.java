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

import org.junit.*;
import static org.junit.Assert.*;

public class KeyTransformerTest {

	private DataKeyDimension m_dim1a;
	private DataKeyDimension m_dim1b;
	private DataKeyDimension m_dim2a;
	private DataKeyDimension m_dim2b;
	private DataKeyDimension m_dim3;

	// simple, no transform
	private DataKey m_key1;

	// simple, transform
	private DataKey m_key2a;
	private DataKey m_key2b;

	// compound, partial transform
	private DataKey m_key3a;
	private DataKey m_key3b;

	private KeyTransformer m_trans;


	@Before public void setUp() {
		buildDimensions();
		buildKeys();
		buildTransform();
	} 

	@Test public void testSimpleNoTransform() {
		DataKey key = m_trans.transformKey(m_key1);
		assertEquals(m_key1, key);
	}

	@Test public void testSimpleTransform() {
		DataKey key = m_trans.transformKey(m_key2a);
		assertEquals(m_key2b, key);
	}

	@Test public void testCompoundPartialTransform() {
		DataKey key = m_trans.transformKey(m_key3a);
		assertEquals(m_key3b, key);
	}


	@Test(expected=IllegalArgumentException.class)
	public void testDuplicateDimension() {
		ElementTransformer et = new ElementTransformer(m_dim1a, m_dim1b);
		m_trans.addDimension(et);
	}

	private void buildDimensions() {
		m_dim1a = new DataKeyDimension("dim1a");
		m_dim1b = new DataKeyDimension("dim1b");
		m_dim2a = new DataKeyDimension("dim2a");
		m_dim2b = new DataKeyDimension("dim2b");
		m_dim3 = new DataKeyDimension("dim3");
	}

	private void buildKeys() {

		// simple, no transform
		m_key1 = DataKeyFactory.newKey( new DataKeyElement("val", m_dim3) );
	
		// simple, transform
		m_key2a = DataKeyFactory.newKey( new DataKeyElement("val", m_dim1a) );
		m_key2b = DataKeyFactory.newKey( new DataKeyElement("val", m_dim1b) );

		// compound, partial transform
		m_key3a = DataKeyFactory.newKey(
			new DataKeyElement("val1", m_dim1a), 
			new DataKeyElement("val3", m_dim3) 
		);
		m_key3b = DataKeyFactory.newKey(
			new DataKeyElement("val1", m_dim1b), 
			new DataKeyElement("val3", m_dim3) 
		);

	}

	private void buildTransform() {
		m_trans = new KeyTransformer();
		ElementTransformer et1 = new ElementTransformer(m_dim1a, m_dim1b);
		ElementTransformer et2 = new ElementTransformer(m_dim2a, m_dim2b);
		m_trans.addDimension(et1);
		m_trans.addDimension(et2);
	}

}
