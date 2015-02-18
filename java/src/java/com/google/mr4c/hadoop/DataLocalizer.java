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

package com.google.mr4c.hadoop;

import com.google.mr4c.sources.DataFileSource;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.net.NetworkTopology;

/**
  * Computes the hosts that best localize a set of DataFiles.  The calculation is
  * delegated to Hadoop's FileInputFormat class.  A single instance should be
  * used for multiple localize calls.  The instance will cache information
  * discovered about the network topology.
*/
public class DataLocalizer {

	private NetworkTopology m_topo = new NetworkTopology();
	private BlockCalc m_calc = new BlockCalc();
	
	public List<String> localize(Collection<DataFileSource> sources) throws IOException {
		List<BlockLocation> allBlocks = new ArrayList<BlockLocation>();
		long totalSize=0;
		for ( DataFileSource src : sources ) {	
			BlockLocation[] blocks = src.getBlockLocation();
			allBlocks.addAll(Arrays.asList(blocks));
			for ( BlockLocation block : blocks ) {
				totalSize+=block.getLength();
			}
		}

		return Arrays.asList(m_calc.calcSplitHosts(allBlocks.toArray(new BlockLocation[allBlocks.size()]), 0, totalSize, m_topo));

	}

	// Extending FileInputFormat so we can access its not so well located
	// protected method for calculating hosts
	private static class BlockCalc extends FileInputFormat<Text,Text> {

		public RecordReader<Text,Text> getRecordReader(InputSplit split, JobConf job, Reporter reporter) {
			throw new RuntimeException("Don't call me!!!!");
		}

		public String[] calcSplitHosts(BlockLocation[] blkLocations, long offset, long splitSize, NetworkTopology clusterMap) throws IOException {
			return blkLocations.length==0 ?
				new String[0] :
				getSplitHosts(blkLocations, offset, splitSize, clusterMap);
		}
	}


}


