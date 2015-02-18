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

import com.google.mr4c.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;

import org.slf4j.Logger;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class HadoopTestUtils {

	public static final String DFS_DIR = "output/minidfs";

	private static MiniDFSCluster s_dfsCluster;

	/**
	  * Copies writable using the readFields() and write() methods
	*/
	public static void copyWritable(Writable src, Writable target) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		try {
			src.write(oos);
		} finally {
			oos.close();
		}
		byte[] bytes = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		try {
			target.readFields(ois);
		} finally {
			ois.close();
		}
	}

	public static void assertEquals(Configuration expected, Configuration actual) {
		org.junit.Assert.assertEquals(
			CollectionUtils.toProperties(expected),
			CollectionUtils.toProperties(actual)
		);
	}

	public static void addParserUsedProperty(Configuration conf) {
		conf.set("mapreduce.client.genericoptionsparser.used", "true");
	}

	/**
	  * Returns a lightweight temporary instance of HDFS for test use.  There is one instance that expires when the JVM exits.
	*/
	public static synchronized FileSystem getTestDFS() throws IOException {
		if ( s_dfsCluster==null ) {
			startDFSCluster();
		}
		return s_dfsCluster.getFileSystem();
	}

	public static URI toTestDFSURI(String path) throws IOException {
		FileSystem fs = getTestDFS();
		Path root = new Path(fs.getUri());
		return new Path(root, path).toUri();
	}
		
	private static void startDFSCluster() throws IOException {

		// wipe out the space if it exists
		FileSystem localFS = FileSystem.get(new Configuration());
		Path dfsRoot = new Path(DFS_DIR);
		if ( localFS.exists(dfsRoot) ) {
			if (  localFS.exists(dfsRoot) && !localFS.delete(dfsRoot,true) ) {
				throw new IOException(String.format("Couldn't delete DFS root [%s]", dfsRoot));
			}
		}

		// create the name and data directories
		File nameDir = createDFSDir("name");
		File dataDir = createDFSDir("data");
		File tmpDir = createDFSDir("tmp");

		Configuration conf = new Configuration();
		conf.set("dfs.name.dir", nameDir.getAbsolutePath());
		conf.set("dfs.data.dir", dataDir.getAbsolutePath());
		conf.set("hadoop.tmp.dir", tmpDir.getAbsolutePath());
		// Namespace ID is stored in the "tmp" directory.
		// If we don't get it inside our DFS, we'll get an error on restart when the id doesn't match.
		// See //www.michael-noll.com/tutorials/running-hadoop-on-ubuntu-linux-multi-node-cluster/#caveats

		if (System.getProperty("hadoop.log.dir") == null) {
			System.setProperty("hadoop.log.dir", "/tmp/mr4c/hadoop/log");
		}

		MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(conf);
		builder.manageNameDfsDirs(false); // we created the name node directory
		builder.manageDataDfsDirs(false); // we created the data node directory
		builder.startupOption(StartupOption.FORMAT); // might not matter, just in case
		// take defaults on everything else
		s_dfsCluster = builder.build();

	}

	private static File createDFSDir(String name) throws IOException {
		File dir = new File(DFS_DIR, name);
		if ( !dir.mkdirs() ) {
			throw new IOException(String.format("Couldn't create DFS %s directory [%s]", name, dir));
		}
		return dir;
	}

	/**
	  * Returns a JobConf that can be used to submit jobs to a lightweight
	  * temporary instance of MapReduce.  There is one instance that
	  * expires when the JVM exits.
	*/
	public static synchronized JobConf createTestMRJobConf() throws IOException {
		return getHadoopTestBinding().createTestMRJobConf();
	}

	public static void runMiniMRJob(String name, MR4CMRJob bbJob) throws IOException {
		getHadoopTestBinding().runMiniMRJob(name, bbJob);
	}

	public synchronized static void shutdownClusters() {
		getHadoopTestBinding().shutdownMRCluster();
		if ( s_dfsCluster!=null ) {
			s_dfsCluster.shutdown();
			s_dfsCluster=null;
		}
	}

	private static HadoopTestBinding s_binding;

	public synchronized static HadoopTestBinding getHadoopTestBinding() {
		if ( s_binding==null ) {
			s_binding = StaticHadoopTestBinder.createBinding();
		}
		return s_binding;
	}

}
