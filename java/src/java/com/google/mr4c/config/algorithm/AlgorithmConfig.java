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

import com.google.mr4c.algorithm.AlgorithmType;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmConfig {

	private String name;
	private String description;
	private AlgorithmType type;
	private String artifact;
	private List<String> inputs = new ArrayList<String>();
	private List<String> outputs = new ArrayList<String>();
	private List<String> optionalInputs = new ArrayList<String>();
	private List<DimensionConfig> dimensions = new ArrayList<DimensionConfig>();
	private List<String> extras = new ArrayList<String>();


	// for gson	
	private AlgorithmConfig() {}

	public AlgorithmConfig(String name, String description, AlgorithmType type, String artifact) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.artifact = artifact;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public AlgorithmType getType() {
		return this.type;
	}

	public String getArtifact() {
		return this.artifact;
	}

	public List<String> getAllInputDatasets() {
		List<String> result = new ArrayList<String>();
		result.addAll(this.inputs);
		result.addAll(this.optionalInputs);
		return result;
	}

	public List<String> getRequiredInputDatasets() {
		return this.inputs;
	}

	public List<String> getOptionalInputDatasets() {
		return this.optionalInputs;
	}

	public void addInputDataset(String input, boolean optional) {
		if ( optional ) {
			this.optionalInputs.add(input);
		} else {
			this.inputs.add(input);
		}
	}

	public List<String> getOutputDatasets() {
		return this.outputs;
	}
		
	public void addOutputDataset(String output) {
		this.outputs.add(output);
	}

	public List<DimensionConfig> getDimensions() {
		return this.dimensions;
	}
		
	public void addDimension(DimensionConfig dim) {
		this.dimensions.add(dim);
	}

	public List<String> getExtras() {
		return this.extras;
	}
		
	public void addExtras(String extra) {
		this.extras.add(extra);
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		AlgorithmConfig config = (AlgorithmConfig) obj;
		if ( !name.equals(config.name) ) return false;
		if ( !description.equals(config.description) ) return false;
		if ( !type.equals(config.type) ) return false;
		if ( !artifact.equals(config.artifact) ) return false;
		if ( !inputs.equals(config.inputs) ) return false;
		if ( !outputs.equals(config.outputs) ) return false;
		if ( !optionalInputs.equals(config.optionalInputs) ) return false;
		if ( !dimensions.equals(config.dimensions) ) return false;
		if ( !extras.equals(config.extras) ) return false;
		return true; 
	}

	public int hashCode() {
		return name.hashCode() +
			description.hashCode() +
			type.hashCode() +
			artifact.hashCode() +
			inputs.hashCode() +
			outputs.hashCode();
	}

}

