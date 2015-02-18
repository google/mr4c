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

package com.google.mr4c.metadata;

import java.util.Arrays;
import java.util.List;

public class MetadataArray implements MetadataElement {

	private List m_vals;
	private PrimitiveType m_type;

	public MetadataArray(Object[] vals, PrimitiveType type) {
		this(Arrays.asList(vals), type);
	}

	public MetadataArray(List vals, PrimitiveType type) {
		m_vals = vals;
		m_type = type;
	}

	public List getValues() {
		return m_vals;
	}

	public PrimitiveType getType() {
		return m_type;
	}

	public MetadataElementType getMetadataElementType() {
		return MetadataElementType.ARRAY;
	}

	public void accept(MetadataVisitor visitor) {
		visitor.visitArray(this);
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !this.getClass().equals(obj.getClass()) ) return false;
		MetadataArray array = (MetadataArray) obj;
		return m_vals.equals(array.m_vals) && m_type.equals(array.m_type);
	}

	public int hashCode() {
		// might be slow, but this isn't supposed to be a key
		// just here for completeness, since we did equals
		return m_vals.hashCode() + m_type.hashCode();
	}

	public String toString() {
		return m_vals.toString();
	}

}
