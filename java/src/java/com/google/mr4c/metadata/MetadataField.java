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

public class MetadataField implements MetadataElement {

	private Object m_val;
	private PrimitiveType m_type;

	public MetadataField(Object val, PrimitiveType type) {
		m_val = val;
		m_type = type;
	}

	public Object getValue() {
		return m_val;
	}

	public PrimitiveType getType() {
		return m_type;
	}

	public MetadataElementType getMetadataElementType() {
		return MetadataElementType.FIELD;
	}

	public void accept(MetadataVisitor visitor) {
		visitor.visitField(this);
	}

	public String toString() {
		return m_val.toString();
	}
	
	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !this.getClass().equals(obj.getClass()) ) return false;
		MetadataField field = (MetadataField) obj;
		return m_val.equals(field.m_val) && m_type.equals(field.m_type);
	}

	public int hashCode() {
		return m_val.hashCode() + m_type.hashCode();
	}

}
