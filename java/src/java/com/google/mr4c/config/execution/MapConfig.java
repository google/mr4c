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

package com.google.mr4c.config.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapConfig {

	private List<DimensionConfig> dimensions = Collections.synchronizedList( new ArrayList<DimensionConfig>() );

	public MapConfig() {}

	public void addDimension(DimensionConfig conf) {
		this.dimensions.add(conf);
	}

	public List<DimensionConfig> getDimensions() {
		return this.dimensions;
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		MapConfig config = (MapConfig) obj;
		if ( !dimensions.equals(config.dimensions) ) return false;
		return true; 
	}

	public int hashCode() {
		return dimensions.hashCode();
	}

}
