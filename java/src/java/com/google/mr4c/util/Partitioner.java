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

package com.google.mr4c.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
  * Partitioning in multiple dimensions according to proportions and constraints.
*/
public class Partitioner<K> {

	private List<DimInfo> m_dims = new ArrayList<DimInfo>();
	private Map<K,Integer> m_result;

	private int m_min; // min # of total partitions
	private int m_max; // max # of total partitions

	public Partitioner(int min, int max) {
		m_min = min;
		m_max = max;
		if ( min<1 ) {
			throw new IllegalStateException(String.format("Minimum # of partitions is %d; must be > 0", min));
		}
		if ( max<min ) {
			throw new IllegalStateException(String.format("Maximum # of partitions is %d; must be >= minimum, which is %d", max, min));
		}
	}

	public void addDimension(K key, int size, int minPartitions) {
		addDimension(key, size, minPartitions, null);
	}
 
	public synchronized void addDimension(K key, int size, int minPartitions, Integer maxPartitions) {
		m_dims.add(new DimInfo<K>(key, size, minPartitions, maxPartitions));
	}

	public synchronized Map<K,Integer> getComputedPartitions() {
		return m_result;
	}

	public synchronized void computePartitions() {
		computeMixed(m_dims, m_min, m_max);
		validateSolution();
		extractSolution();
	}

	private void extractSolution() {
		m_result = new HashMap<K,Integer>();
		for ( DimInfo<K> dim : m_dims ) {
			m_result.put(dim.m_key, dim.m_num);
		}
	}

	private void computeMixed(List<DimInfo> dims, int min, int max) {
		List<DimInfo> fixed = extractFixed(dims);
		List<DimInfo> nonFixed = extractNonFixed(dims);
		if ( nonFixed.isEmpty() ) {
			return;
		}
		int fixedNum = computeNumPartitions(fixed);
		int subMin = (int) Math.ceil(min/ fixedNum); 
		int subMax = (int) Math.floor(max / fixedNum );
		if ( subMin>subMax ) {
			throw new IllegalStateException(String.format("Min partitions [%d] > max partitions [%d]", subMin, subMax));
		}
		computeNonFixed(nonFixed, subMin, subMax);
	}

	private void computeNonFixed(List<DimInfo> dims, int min, int max) {
		assertNoneFixed(dims);
		computeByScaling(dims, min, max);
		boolean fixedNeeded = fixIfNecessary(dims);
		if ( fixedNeeded ) {
			computeMixed(dims, min, max);
		} // otherwise, take current sol'n
	}

	private void computeByScaling(List<DimInfo> dims, int min, int max) {
		int totalSize = computeTotalSize(dims);
		sortBySizeAscending(dims);
		double scale = computeScale(totalSize,dims.size(), max);
		int total=1;
		for ( int i=0; i<dims.size()-1; i++ ) {
			DimInfo dim = dims.get(i);
			dim.scale(scale);
			total *= dim.m_num;
		}
		DimInfo lastDim = dims.get(dims.size()-1);
		lastDim.m_num = computeLastNum(max, total);
	}

	// returns true if any of the dims had to be fixed to a value
	private boolean fixIfNecessary(List<DimInfo> dims) {
		boolean fixed = false;
		for ( DimInfo dim : dims ) {
			if ( dim.m_num < dim.m_min ) {
				dim.m_num = dim.m_min;
				dim.m_fixed = true;
				fixed = true;
			}
			if ( dim.m_max!=null && dim.m_num > dim.m_max ) {
				dim.m_num = dim.m_max;
				dim.m_fixed = true;
				fixed = true;
			}
			if ( dim.m_num > dim.m_size ) {
				dim.m_num = dim.m_size;
				dim.m_fixed = true;
				fixed = true;
			}
		}
		return fixed;
	}

	private void assertNoneFixed(List<DimInfo> dims) {
		for ( DimInfo dim : dims ) {
			if ( dim.m_fixed ) {
				throw new IllegalStateException(String.format("Value for dim [%s] unexpectedly fixed at %d", dim.m_key, dim.m_num));
			}
		}
	}

	private List<DimInfo> extractFixed(List<DimInfo> dims) {
		return extract(dims,true);
	}

	private List<DimInfo> extractNonFixed(List<DimInfo> dims) {
		return extract(dims,false);
	}

	private List<DimInfo> extract(List<DimInfo> dims, boolean fixed) {
		List<DimInfo> result = new ArrayList<DimInfo>();
		for ( DimInfo dim : dims ) {
			if ( dim.m_fixed==fixed ) {
				result.add(dim);
			}
		}
		return result;
	}

	private int computeTotalSize(List<DimInfo> dims) {
		int size=1;
		for ( DimInfo dim : dims ) {
			size*=dim.m_size;
		}
		return size;
	}

	private int computeNumPartitions(List<DimInfo> dims) {
		int num=1;
		for ( DimInfo dim : dims ) {
			num*=dim.m_num;
		}
		return num;
	}

	private double computeScale(int totalSize,int numDims, int max) {
		double ratio = totalSize / (0.0+max);
		double power = 1.0 / numDims;
		return Math.pow(ratio, power);
	}

	private int computeLastNum(int max, int total) {
		int num = max / total;
		if ( num==0 ) {
			num=1;
		}
		return num;
	}

	private void sortBySizeAscending(List<DimInfo> dims) {
		Collections.sort(dims, new Comparator<DimInfo>() {
			public int compare(DimInfo dim1, DimInfo dim2) {
				return new Integer(dim1.m_size).compareTo(dim2.m_size);
			}
		});
	}

	private void validateSolution() {
		int num = computeNumPartitions(m_dims);
		if ( num < m_min ) {
			throw new IllegalStateException(String.format("Number of partitions [%d] is less than min [%d]", num, m_min));
		}
		if ( num > m_max ) {
			throw new IllegalStateException(String.format("Number of partitions [%d] is greater than max [%d]", num, m_max));
		}
	}

	// solving: minimize(max-product(xi))
	// constraints:
	//	min <= product(xi) <= max
	//	min_i <= xi <= max_i

	private static class DimInfo<K> {
		K m_key;
		int m_size=1; // # of elements in the dimension
		int m_min=1; // min # of partitions
		Integer m_max=null; // optional max # of partitions
		Integer m_num=null; // computed # of partitions
		boolean m_fixed=false;

		// NOTE: May want to add these in the future:
		//	min partition size
		//	max partition size
	
		DimInfo(K key, int size, int min, Integer max) {
			m_key = key;
			m_size = size;
			m_min = min;
			m_max = max;
			validate();
			checkFixed();
		}
	
		void validate() {
			if ( m_size<1 ) {
				throw new IllegalStateException(String.format("Size of dimension [%s] is %d; must be > 0", m_key, m_size));
			}
			if ( m_min<1 ) {
				throw new IllegalStateException(String.format("Minimum # of partitions for dimension [%s] is %d; must be > 0", m_key, m_min));
			}
			if ( m_min>m_size ) {
				throw new IllegalStateException(String.format("Minimum # of partitions for dimension [%s] is %d; must be <= dimension size, which is %d", m_key, m_min, m_size));
			}
			if ( m_max!=null && m_max<m_min ) {
				throw new IllegalStateException(String.format("Maximum # of partitions for dimension [%s] is %d; must be >= minimum, which is %d", m_key, m_max, m_min));
			}
		}

		void checkFixed() {
			if ( m_max!=null && m_max==m_min ) {
				m_fixed=true;
				m_num = m_min;
			}
		}

		void scale(double scale) {
			int val = (int) ((m_size +0.0)/ scale);
			if ( val==0 ) {
				val=1;
			}
			m_num = val;
		}

	}

}
