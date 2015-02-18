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

import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.dataset.DatasetTestUtils;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.sources.DataFileSource;
import com.google.mr4c.sources.URIDataFileSource;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class DataLocalizerTest {

	private String m_localRoot;
	private String m_hdfsRoot;
	private List<String> m_localFiles;
	private List<String> m_hdfsFiles;
	private List<String> m_localExpected;
	private List<String> m_hdfsExpected;
	private DataLocalizer m_localizer = new DataLocalizer();


	@Before public void setup() throws Exception {
		buildUriRoots();
		buildLocalFileList();
		buildHDFSFileList();
		buildExpectedLocalHosts();
		buildExpectedHDFSHosts();
	}

	@Test public void testLocal() throws Exception {
		URI root = new File(m_localRoot).toURI();
		test(root, m_localFiles, m_localExpected);
	}

	/*@Test public void testHDFS() throws Exception {
		test(new URI(m_hdfsRoot), m_hdfsFiles, m_hdfsExpected);
	}*/
	// NOTE: this class isn't properly testable due to external dependencies
	// Need to factor out the Hadoop class, or maybe get rid of the test entirely

	private void test(URI root, List<String> files, List<String> expected) throws Exception {
		List<DataFileSource> sources = new ArrayList<DataFileSource>();
		for ( String file : files ) {
			URI uri = new URI(root+file);
			DataFileSource src = new URIDataFileSource(uri);
			sources.add(src);
		}
		
		List<String> result = new ArrayList<String>(m_localizer.localize(sources));

		Collections.sort(result);
		Collections.sort(expected);
		assertEquals(expected,result);
	}

	private void buildUriRoots() {
		m_localRoot = "input/data/images/";
		m_hdfsRoot = "hdfs://myhadoopcluster/data";
	}

	private void buildExpectedLocalHosts() {
		m_localExpected = Arrays.asList("localhost");
	}

	private void buildExpectedHDFSHosts() {
		m_hdfsExpected = Arrays.asList("192.168.157.140", "192.168.157.40", "192.168.157.80");
	}

	private void buildHDFSFileList() {
		m_hdfsFiles = Arrays.asList(
			"a00_s0_MS.sr.tif",
			"a00_s0_PAN.sr.tif",
			"a00_s0_mask.sr.tif",
			"a00_s1_MS.sr.tif",
			"a00_s1_PAN.sr.tif",
			"a00_s1_mask.sr.tif",
			"a00_s2_MS.sr.tif",
			"a00_s2_PAN.sr.tif",
			"a00_s2_mask.sr.tif",
			"a07_s0_MS.sr.tif",
			"a07_s0_PAN.sr.tif",
			"a07_s0_mask.sr.tif",
			"a07_s1_MS.sr.tif",
			"a07_s1_PAN.sr.tif",
			"a07_s1_mask.sr.tif",
			"a07_s2_MS.sr.tif",
			"a07_s2_PAN.sr.tif",
			"a07_s2_mask.sr.tif",
			"a14_s0_MS.sr.tif",
			"a14_s0_PAN.sr.tif",
			"a14_s0_mask.sr.tif",
			"a14_s1_MS.sr.tif",
			"a14_s1_PAN.sr.tif",
			"a14_s1_mask.sr.tif",
			"a14_s2_MS.sr.tif",
			"a14_s2_PAN.sr.tif",
			"a14_s2_mask.sr.tif",
			"a21_s0_MS.sr.tif",
			"a21_s0_PAN.sr.tif",
			"a21_s0_mask.sr.tif",
			"a21_s1_MS.sr.tif",
			"a21_s1_PAN.sr.tif",
			"a21_s1_mask.sr.tif",
			"a21_s2_MS.sr.tif",
			"a21_s2_PAN.sr.tif",
			"a21_s2_mask.sr.tif",
			"a28_s0_MS.sr.tif",
			"a28_s0_PAN.sr.tif",
			"a28_s0_mask.sr.tif",
			"a28_s1_MS.sr.tif",
			"a28_s1_PAN.sr.tif",
			"a28_s1_mask.sr.tif",
			"a28_s2_MS.sr.tif",
			"a28_s2_PAN.sr.tif",
			"a28_s2_mask.sr.tif",
			"a35_s0_MS.sr.tif",
			"a35_s0_PAN.sr.tif",
			"a35_s0_mask.sr.tif",
			"a35_s1_MS.sr.tif",
			"a35_s1_PAN.sr.tif",
			"a35_s1_mask.sr.tif",
			"a35_s2_MS.sr.tif",
			"a35_s2_PAN.sr.tif",
			"a35_s2_mask.sr.tif",
			"a42_s0_MS.sr.tif",
			"a42_s0_PAN.sr.tif",
			"a42_s0_mask.sr.tif",
			"a42_s1_MS.sr.tif",
			"a42_s1_PAN.sr.tif",
			"a42_s1_mask.sr.tif",
			"a42_s2_MS.sr.tif",
			"a42_s2_PAN.sr.tif",
			"a42_s2_mask.sr.tif",
			"a49_s0_MS.sr.tif",
			"a49_s0_PAN.sr.tif",
			"a49_s0_mask.sr.tif",
			"a49_s1_MS.sr.tif",
			"a49_s1_PAN.sr.tif",
			"a49_s1_mask.sr.tif",
			"a49_s2_MS.sr.tif",
			"a49_s2_PAN.sr.tif",
			"a49_s2_mask.sr.tif",
			"a56_s0_MS.sr.tif",
			"a56_s0_PAN.sr.tif",
			"a56_s0_mask.sr.tif",
			"a56_s1_MS.sr.tif",
			"a56_s1_PAN.sr.tif",
			"a56_s1_mask.sr.tif",
			"a56_s2_MS.sr.tif",
			"a56_s2_PAN.sr.tif",
			"a56_s2_mask.sr.tif",
			"a63_s0_MS.sr.tif",
			"a63_s0_PAN.sr.tif",
			"a63_s0_mask.sr.tif",
			"a63_s1_MS.sr.tif",
			"a63_s1_PAN.sr.tif",
			"a63_s1_mask.sr.tif",
			"a63_s2_MS.sr.tif",
			"a63_s2_PAN.sr.tif",
			"a63_s2_mask.sr.tif",
			"a70_s0_MS.sr.tif",
			"a70_s0_PAN.sr.tif",
			"a70_s0_mask.sr.tif",
			"a70_s1_MS.sr.tif",
			"a70_s1_PAN.sr.tif",
			"a70_s1_mask.sr.tif",
			"a70_s2_MS.sr.tif",
			"a70_s2_PAN.sr.tif",
			"a70_s2_mask.sr.tif",
			"a77_s0_MS.sr.tif",
			"a77_s0_PAN.sr.tif",
			"a77_s0_mask.sr.tif",
			"a77_s1_MS.sr.tif",
			"a77_s1_PAN.sr.tif",
			"a77_s1_mask.sr.tif",
			"a77_s2_MS.sr.tif",
			"a77_s2_PAN.sr.tif",
			"a77_s2_mask.sr.tif",
			"a84_s0_MS.sr.tif",
			"a84_s0_PAN.sr.tif",
			"a84_s0_mask.sr.tif",
			"a84_s1_MS.sr.tif",
			"a84_s1_PAN.sr.tif",
			"a84_s1_mask.sr.tif",
			"a84_s2_MS.sr.tif",
			"a84_s2_PAN.sr.tif",
			"a84_s2_mask.sr.tif",
			"a91_s0_MS.sr.tif",
			"a91_s0_PAN.sr.tif",
			"a91_s0_mask.sr.tif",
			"a91_s1_MS.sr.tif",
			"a91_s1_PAN.sr.tif",
			"a91_s1_mask.sr.tif",
			"a91_s2_MS.sr.tif",
			"a91_s2_PAN.sr.tif",
			"a91_s2_mask.sr.tif",
			"a98_s0_MS.sr.tif",
			"a98_s0_PAN.sr.tif",
			"a98_s0_mask.sr.tif",
			"a98_s1_MS.sr.tif",
			"a98_s1_PAN.sr.tif",
			"a98_s1_mask.sr.tif",
			"a98_s2_MS.sr.tif",
			"a98_s2_PAN.sr.tif",
			"a98_s2_mask.sr.tif"
		);
	}

	private void buildLocalFileList() {
		m_localFiles =  Arrays.asList(
			"ss01_c1_2455874.21556848_MS_1.5bps.jpc",
			"ss01_c1_2455874.21556848_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21556871_MS_1.5bps.jpc",
			"ss01_c1_2455874.21556871_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21556894_MS_1.5bps.jpc",
			"ss01_c1_2455874.21556894_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21556917_MS_1.5bps.jpc",
			"ss01_c1_2455874.21556917_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21556941_MS_1.5bps.jpc",
			"ss01_c1_2455874.21556941_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21556964_MS_1.5bps.jpc",
			"ss01_c1_2455874.21556964_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21556987_MS_1.5bps.jpc",
			"ss01_c1_2455874.21556987_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21557010_MS_1.5bps.jpc",
			"ss01_c1_2455874.21557010_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21557033_MS_1.5bps.jpc",
			"ss01_c1_2455874.21557033_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21557056_MS_1.5bps.jpc",
			"ss01_c1_2455874.21557056_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21557079_MS_1.5bps.jpc",
			"ss01_c1_2455874.21557079_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21557103_MS_1.5bps.jpc",
			"ss01_c1_2455874.21557103_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21557126_MS_1.5bps.jpc",
			"ss01_c1_2455874.21557126_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21557149_MS_1.5bps.jpc",
			"ss01_c1_2455874.21557149_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21557172_MS_1.5bps.jpc",
			"ss01_c1_2455874.21557172_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21557195_MS_1.5bps.jpc",
			"ss01_c1_2455874.21557195_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21557218_MS_1.5bps.jpc",
			"ss01_c1_2455874.21557218_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21557242_MS_1.5bps.jpc",
			"ss01_c1_2455874.21557242_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21557265_MS_1.5bps.jpc",
			"ss01_c1_2455874.21557265_PAN_1.5bps.jpc",
			"ss01_c1_2455874.21557288_MS_1.5bps.jpc",
			"ss01_c1_2455874.21557288_PAN_1.5bps.jpc"
		);
	}


}
