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

public class TileKey {

	private int m_zoom;
	private int m_x;
	private int m_y;

	public TileKey(int zoom, int x, int y) {
		m_zoom = zoom;
		m_x = x;
		m_y = y;
	}

	public int getZoom() {
		return m_zoom;
	}

	public int getX() {
		return m_x;
	}

	public int getY() {
		return m_y;
	}

	public int getColumn() {
		return m_x;
	}

	public int getRow() {
		return m_y;
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		TileKey key = (TileKey) obj;
		if ( m_zoom!=key.m_zoom ) return false;
		if ( m_x!=key.m_x ) return false;
		if ( m_y!=key.m_y ) return false;
		return true; 
	}

	public int hashCode() {
		return m_zoom + m_x + m_y;
	}

	public String toString() {
		return String.format(
			"zoom=[%s]; " +
			"x=[%s]; " +
			"y=[%s]",
			m_zoom,
			m_x,
			m_y
		);
	}


}
