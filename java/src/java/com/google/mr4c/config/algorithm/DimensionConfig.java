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

package com.google.mr4c.config.algorithm;

import org.apache.commons.lang3.ObjectUtils;

public class DimensionConfig {

	private String name;
	private boolean canSplit=false;
	private int overlapBefore=0;
	private int overlapAfter=0;
	private Integer maxSize=null;
	private Integer chunkSize=null;
	private boolean dependent=false;


	// for gson	
	private DimensionConfig() {}

	public DimensionConfig(String name, boolean canSplit, int overlapBefore, int overlapAfter, Integer maxSize, Integer chunkSize, boolean dependent) {
		this.name = name;
		this.canSplit = canSplit;
		this.overlapBefore = overlapBefore;
		this.overlapAfter = overlapAfter;
		this.maxSize = maxSize;
		this.chunkSize = chunkSize;
		this.dependent = dependent;
	}

	public String getName() {
		return this.name;
	}

	public boolean canSplit() {
		return this.canSplit;
	}

	public int getOverlapBefore() {
		return this.overlapBefore;
	}

	public int getOverlapAfter() {
		return this.overlapAfter;
	}

	public Integer getMaxSize() {
		return this.maxSize;
	}

	public Integer getChunkSize() {
		return this.chunkSize;
	}

	public boolean isDependent() {
		return this.dependent;
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		DimensionConfig config = (DimensionConfig) obj;
		if ( !name.equals(config.name) ) return false;
		if ( canSplit!=config.canSplit ) return false;
		if ( overlapBefore!=config.overlapBefore ) return false;
		if ( overlapAfter!=config.overlapAfter ) return false;
		if ( !ObjectUtils.equals(maxSize, config.maxSize) ) return false;
		if ( !ObjectUtils.equals(chunkSize, config.chunkSize) ) return false;
		if ( dependent!=config.dependent ) return false;
		return true; 
	}

	public int hashCode() {
		// this isn't a key, don't bother getting involved
		return name.hashCode();
	}

}

