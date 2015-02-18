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

import com.google.mr4c.AlgoRunner;
import com.google.mr4c.algorithm.AlgorithmData;
import com.google.mr4c.config.algorithm.AlgorithmConfig;
import com.google.mr4c.config.algorithm.DimensionConfig;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFilter;
import com.google.mr4c.keys.DataKeyUtils;
import com.google.mr4c.keys.DimensionPartition;
import com.google.mr4c.keys.KeyDimensionPartitioner;
import com.google.mr4c.keys.Keyspace;
import com.google.mr4c.keys.KeyspaceDimension;
import com.google.mr4c.keys.KeyspacePartition;
import com.google.mr4c.keys.KeyspacePartitioner;
import com.google.mr4c.sources.DataFileSource;
import com.google.mr4c.sources.DatasetSource.WriteMode;
import com.google.mr4c.sources.ExecutionSource;
import com.google.mr4c.stats.MR4CStats;
import com.google.mr4c.stats.StatsClient;
import com.google.mr4c.stats.StatsTimer;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.io.Text;

import org.slf4j.Logger;

public class MR4CInputFormat {

	protected Logger m_log = MR4CLogging.getLogger(MR4CInputFormat.class);

	protected StatsClient m_statsClient = MR4CStats.getClient();

	private DataLocalizer m_localizer = new DataLocalizer();

	public static RecordReader<Text,DataKeyList> getRecordReader(MR4CInputSplit split) throws IOException {
		return new MR4CRecordReader(split);
	}

	public InputSplit[] getSplits(ExecutionSource exeSrc, int numSplits) throws IOException {
		StatsTimer timer = new StatsTimer(
			m_statsClient, 
			"mr4c.hadoop.splits.success",
			"mr4c.hadoop.splits.failure"
		);
		boolean success = false;
		try {
			InputSplit[] result = doGetSplits(exeSrc, numSplits);
			success = true;
			return result;
		} finally {
			timer.done(success);
		}
	}

	private InputSplit[] doGetSplits(ExecutionSource exeSrc, int numSplits) throws IOException {
		m_log.info("Asked for {} splits", numSplits );

		AlgoRunner runner = new AlgoRunner(exeSrc);
		boolean success = false;
		try {
			InputSplit[] result = doGetSplits(runner, numSplits);
			success = true;
			return result;
		} catch ( Exception e ) {
			m_log.error("Error getting splits", e);
			throw new IOException(e);
		} finally {
			try {
				writeLogs(runner);
			} catch ( Exception e ) {
				m_log.error("Error saving logs", e);
				// Only throw log failure if it won't mask another exception
				if ( success ) {
					throw new IOException(e);
				}
			}
		}
	}


	
	private InputSplit[] doGetSplits(AlgoRunner runner, int numSplits) throws IOException {

		runner.loadInputData();

		AlgorithmData algoData = runner.getAlgorithmData();

		algoData.generateKeyspaceFromInputDatasets();

		Keyspace keyspace = algoData.getKeyspace();

		AlgorithmConfig algoConfig = runner.getAlgorithmConfig();

		List<KeyspacePartition> partitions = generatePartitions(algoConfig, keyspace, numSplits);

		m_log.info("Generated {} partitions", partitions.size() );

		for ( KeyspacePartition partition : partitions ) {
			handleDependentDimensions(partition, algoConfig, algoData);
		}

		MR4CInputSplit[] splits = new MR4CInputSplit[partitions.size()];

		for ( int i=0; i<partitions.size(); i++ ) {
			splits[i] = generateSplit(i+1, partitions.get(i), algoConfig, algoData);
		}

		return splits;
	}


	private List<KeyspacePartition> generatePartitions(AlgorithmConfig algoConfig, Keyspace keyspace, int numSplits) {
		KeyDimensionPartitioner dimPartitioner = new KeyDimensionPartitioner(keyspace, numSplits);
		for ( DimensionConfig dimConfig : algoConfig.getDimensions() ) {
			DataKeyDimension dim = new DataKeyDimension(dimConfig.getName());
			if ( dimConfig.canSplit() ) {
				dimPartitioner.canSplit(dim);
			}
			if ( dimConfig.isDependent() ) {
				dimPartitioner.dependent(dim);
			}
		}
		dimPartitioner.partition();

		m_log.info("Dim partition is {}", dimPartitioner.getComputedPartitions());

		KeyspacePartitioner keyspacePartitioner = new KeyspacePartitioner(keyspace, dimPartitioner.getComputedPartitions());
		for ( DimensionConfig dimConfig : algoConfig.getDimensions() ) {
			DataKeyDimension dim = new DataKeyDimension(dimConfig.getName());
			if ( !keyspace.hasDimension(dim) ) {
				throw new IllegalStateException(String.format("Dimension [%s] is not in the keyspace. It should not have a splitting configuration.  If you haven't specified queryOnly in a dataset config, or registered the algorithm with a dataset excluded from the keyspace, then there is probably a typo somewhere.", dim));
			}
			if ( !dimConfig.isDependent() ) {
				keyspacePartitioner.addOverlaps(dim, dimConfig.getOverlapBefore(), dimConfig.getOverlapAfter());
				if ( dimConfig.getChunkSize()!=null ) {
					keyspacePartitioner.specifyChunkSize(dim, dimConfig.getChunkSize());
				}
			}
		}
		keyspacePartitioner.partition();

		return keyspacePartitioner.getKeyPartitions();
	}

	// finds all the elements needed from dependent dimensions
	private void handleDependentDimensions(KeyspacePartition partition, AlgorithmConfig algoConfig, AlgorithmData algoData) throws IOException {
		DataKeyFilter filter = partition.getIndependentFilter();
		algoData = algoData.slice(filter);
		for ( DimensionConfig dimConfig : algoConfig.getDimensions() ) {
			if ( dimConfig.isDependent() ) {
				DataKeyDimension dim = new DataKeyDimension(dimConfig.getName());
				Set<DataKey> depKeys = algoData.getDependentKeys(dim);
				DimensionPartition dimPart = new DimensionPartition(dim);
				for ( DataKey key : depKeys ) {
					DataKeyElement ele = key.getElement(dim);
					dimPart.addElement(ele);
				}
				partition.addDependentDimension(dimPart);
				
			}
		}
	}

	private MR4CInputSplit generateSplit(int seqNum, KeyspacePartition partition, AlgorithmConfig algoConfig, AlgorithmData algoData) throws IOException {
		DataKeyFilter filter = partition.getExtraDimensionsFilter();
		algoData = algoData.slice(filter);
		logKeyspace(seqNum, algoData, partition);
		Set<DataKey> keys = algoData.getAllInputKeys();
		Collection<DataFileSource> sources = algoData.getInputDataFileSources(true);
		List<String> hosts = m_localizer.localize(sources);
		m_log.info("Hosts for split #{} are {}" , seqNum, hosts);
		return new MR4CInputSplit(seqNum, keys, hosts);
	}

	private void logKeyspace(int seqNum, AlgorithmData algoData, KeyspacePartition partition) {
		// for each dim, just list the elements
		m_log.info("Begin keyspace for split #{}", seqNum);
		Keyspace keyspace = algoData.getKeyspace();
		for ( DataKeyDimension dim : keyspace.getDimensions() ) {
			KeyspaceDimension ksd = keyspace.getKeyspaceDimension(dim);
			List<String> foundIDs = new ArrayList<String>();
			for ( DataKeyElement element : ksd.getElements() ) {
				foundIDs.add(element.getIdentifier());
			}
			List<String> expectedIDs = new ArrayList<String>();
			for ( DataKeyElement element : partition.getPartition(dim).getElements() ) {
				expectedIDs.add(element.getIdentifier());
			}
			List<String> missingIDs = new ArrayList<String>(expectedIDs);
			missingIDs.removeAll(foundIDs);
			m_log.info("{} elements for dimension [{}] are {}", new Object[] {foundIDs.size(), dim.getName(), foundIDs.toString()});
			if ( missingIDs.isEmpty() ) {
				m_log.info("No missing elements for dimension [{}]", dim.getName());
			} else {
				m_log.info("{} expected elements for dimension [{}] are {}", new Object[] {expectedIDs.size(), dim.getName(), expectedIDs.toString()});
				m_log.info("{} possibly missing elements for dimension [{}] are {}", new Object[] {missingIDs.size(), dim.getName(), missingIDs.toString()});
			}
		}
		m_log.info("End keyspace for split #{}", seqNum);
	}

	private void writeLogs(AlgoRunner runner) throws IOException {
		// this captures the logs of a launcher job
		runner.buildLogsDatasets();
		runner.saveLogs(WriteMode.FILES_ONLY);
	}

}
