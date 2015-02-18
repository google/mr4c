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

package com.google.mr4c.sources;

import com.google.mr4c.keys.DataKey;

import java.util.ArrayList;
import java.util.List;

public class CompositeKeyFileMapper implements DataKeyFileMapper {

	private List<DataKeyFileMapper> m_mappers = new ArrayList<DataKeyFileMapper>();

	public synchronized void addMapper(DataKeyFileMapper mapper) {
		m_mappers.add(mapper);
	}

	public synchronized String getFileName(DataKey key) {
		for ( DataKeyFileMapper mapper : m_mappers ) {
			if ( mapper.canMapKey(key) ) {
				return mapper.getFileName(key);
			}
		}
		throw new IllegalArgumentException(String.format("No mapper found for key [%s]", key));
	}

	public synchronized boolean canMapKey(DataKey key) {
		for ( DataKeyFileMapper mapper : m_mappers ) {
			if ( mapper.canMapKey(key) ) {
				return true;
			}
		}
		return false;
	}

	public synchronized DataKey getKey(String name) {
		for ( DataKeyFileMapper mapper : m_mappers ) {
			if ( mapper.canMapName(name) ) {
				return mapper.getKey(name);
			}
		}
		throw new IllegalArgumentException(String.format("No mapper found for file name [%s]", name));
	}

	public synchronized boolean canMapName(String name) {
		for ( DataKeyFileMapper mapper : m_mappers ) {
			if ( mapper.canMapName(name) ) {
				return true;
			}
		}
		return false;
	}

}
