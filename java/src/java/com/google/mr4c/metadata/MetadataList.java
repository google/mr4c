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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetadataList implements MetadataElement {

	private List<MetadataElement> m_list;

	public MetadataList() {
		m_list = Collections.synchronizedList( new ArrayList<MetadataElement>() );
	}

	public MetadataList(List<MetadataElement> list) {
		m_list = list;
	}

	public List<MetadataElement> getList() {
		return m_list;
	}

	public MetadataElementType getMetadataElementType() {
		return MetadataElementType.LIST;
	}

	public void accept(MetadataVisitor visitor) {
		visitor.visitListPreIteration(this);
		for ( MetadataElement element : m_list ) {
			element.accept(visitor);
		}
		visitor.visitListPostIteration(this);
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !this.getClass().equals(obj.getClass()) ) return false;
		MetadataList list = (MetadataList) obj;
		return m_list.equals(list.m_list);
	}

	public int hashCode() {
		// might be slow, but this isn't supposed to be a key
		// just here for completeness, since we did equals
		return m_list.hashCode();
	}

}
