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

package com.google.mr4c.mbtiles;

import java.util.Arrays;

public class Tile {

	private TileKey m_key;
	private byte[] m_data;

	public Tile(TileKey key, byte[] data) {
		if ( key==null ) {
			throw new IllegalStateException("Tile key cannot be null");
		}
		if ( data==null ) {
			throw new IllegalStateException("Tile data cannot be null");
		}
		m_key = key;
		m_data = data;
	}

	public TileKey getKey() {
		return m_key;
	}

	public byte[] getData() {
		return m_data;
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		Tile tile = (Tile) obj;
		if ( !m_key.equals(tile.m_key) ) return false;
		if ( !Arrays.equals(m_data, tile.m_data) ) return false;
		return true; 
	}

}
