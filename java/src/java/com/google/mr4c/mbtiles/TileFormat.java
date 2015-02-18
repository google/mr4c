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

import java.util.Map;

public enum TileFormat {

	JPG(
		"jpg",
		"image/jpg"
	),

	PNG(
		"png",
		"image/png"
	);

	public static final String METADATA_NAME = "format";

	private String m_name;
	private String m_contentType;

	private static TileFormat s_default = PNG;

	private TileFormat(
		String name,
		String contentType
	) {
		m_name = name;
		m_contentType = contentType;
	}

	public String getName() {
		return m_name;
	}

	public String getContentType() {
		return m_contentType;
	}

	public boolean isDefaultFormat() {
		return this==s_default;
	}

	public static TileFormat getDefaultFormat() {
		return s_default;
	}

	public static TileFormat getByName(String name) {
		for ( TileFormat format : values() ) {
			if ( format.getName().equals(name) ) {
				return format;
			}
		}
		throw new IllegalStateException(String.format("No tile format named [%s]", name));
	}

	public static TileFormat extractFromMetadata(Map<String,String> meta) {
		String val = meta.get(METADATA_NAME);
		return val==null ? null : getByName(val);
	}

}
