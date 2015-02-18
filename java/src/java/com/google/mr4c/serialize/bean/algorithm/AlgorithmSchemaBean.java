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

package com.google.mr4c.serialize.bean.algorithm;

import com.google.mr4c.algorithm.AlgorithmSchema;
import com.google.mr4c.keys.DataKeyDimension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AlgorithmSchemaBean {

	private List<String> inputs;
	private List<String> optionalInputs;
	private List<String> excludedInputs;
	private List<String> outputs;
	private List<String> dimensions;

	public static AlgorithmSchemaBean instance(AlgorithmSchema algo) {
		AlgorithmSchemaBean bean = new AlgorithmSchemaBean();
		bean.inputs = toSortedList(algo.getInputDatasets());
		bean.optionalInputs = toSortedList(algo.getOptionalInputDatasets());
		bean.excludedInputs = toSortedList(algo.getExcludedInputDatasets());
		bean.outputs = toSortedList(algo.getOutputDatasets());
		bean.dimensions = extractDimensions(algo);
		return bean;
	}

	private static List<String> toSortedList(Collection<String> col) {
		List<String> list = new ArrayList<String>(col);
		Collections.sort(list);
		return list;
	}
	private static List<String> extractDimensions(AlgorithmSchema algo) {
		List<String> result = new ArrayList<String>();
		for ( DataKeyDimension dim : algo.getExpectedDimensions() ) {
			result.add(dim.getName());
		}
		Collections.sort(result);
		return result;
	}

	public AlgorithmSchemaBean(){}

	public AlgorithmSchema toAlgorithmSchema() {
		AlgorithmSchema algo = new AlgorithmSchema();
		addInputs(algo);
		addOutputs(algo);
		addDimensions(algo);
		return algo;
	}

	private void addInputs(AlgorithmSchema algo) {
		List<String> inputs = this.inputs==null ? Collections.<String>emptyList() : this.inputs;
		List<String> optional = this.optionalInputs==null ? Collections.<String>emptyList() : this.optionalInputs;
		List<String> excluded = this.excludedInputs==null ? Collections.<String>emptyList() : this.excludedInputs;
		for ( String input : inputs ) {
			algo.addInputDataset(input, optional.contains(input), excluded.contains(input));
		}
	}

	private void addOutputs(AlgorithmSchema algo) {
		List<String> outputs = this.outputs==null ? Collections.<String>emptyList() : this.outputs;
		for ( String output : outputs ) {
			algo.addOutputDataset(output);
		}
	}

	private void addDimensions(AlgorithmSchema algo) {
		List<String> dims = this.dimensions==null ? Collections.<String>emptyList() : this.dimensions;
		for ( String dim : dims ) {
			algo.addExpectedDimension(new DataKeyDimension(dim));
		}
	}

}

