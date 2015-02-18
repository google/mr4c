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

package com.google.mr4c.nativec.jna;

import com.google.mr4c.algorithm.EnvironmentSet;
import com.google.mr4c.algorithm.LogLevel;
import com.google.mr4c.message.Message;
import com.google.mr4c.nativec.ExternalAlgorithm;
import com.google.mr4c.nativec.ExternalAlgorithmData;
import com.google.mr4c.nativec.ExternalContext;
import com.google.mr4c.nativec.ExternalDataFile;
import com.google.mr4c.nativec.ExternalDataset;
import com.google.mr4c.nativec.ExternalEntry;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalDataFilePtr;
import com.google.mr4c.serialize.SerializerFactory;

import com.sun.jna.ptr.PointerByReference;

import java.util.Arrays;
import java.util.Collection;

public class JnaExternalEntry implements ExternalEntry {

	private SerializerFactory m_serFact;

	static Mr4cLibrary s_lib = Mr4cLibrary.INSTANCE;

	public JnaExternalEntry(SerializerFactory factory) {
		m_serFact = factory;
	}

	public void dumpDataset(ExternalDataset dataset) {

		s_lib.CExternalEntry_dumpDataset(JnaExternalDataset.toNative(dataset));
	}

	public ExternalDataset cloneDataset(ExternalDataset dataset) {
		return JnaExternalDataset.fromNative(s_lib.CExternalEntry_cloneDataset(JnaExternalDataset.toNative(dataset)));
	}

	public ExternalAlgorithmData cloneAlgorithmData(ExternalAlgorithmData data) {
		return JnaExternalAlgorithmData.fromNative(s_lib.CExternalEntry_cloneAlgorithmData(JnaExternalAlgorithmData.toNative(data)));
	}

	public ExternalAlgorithm getAlgorithm(String name) {
		return new JnaExternalAlgorithm(s_lib.CExternalEntry_getAlgorithm(name));
	}

	public boolean executeAlgorithm(String name, ExternalAlgorithmData data, ExternalContext context) {
		byte rc = s_lib.CExternalEntry_executeAlgorithm(
			name,
			JnaExternalAlgorithmData.toNative(data),
			JnaExternalContext.toNative(context)
		);
		return rc==1;
	}

	public void pushEnvironmentProperties(EnvironmentSet envSet, String serializedProperties) {
		s_lib.CExternalEntry_pushEnvironmentProperties(envSet.toString(), serializedProperties);
	}

	public void testLogging(ExternalContext context, LogLevel level, String msg) {
		JnaExternalContext jnaContext = (JnaExternalContext) context;
		s_lib.CExternalEntry_testLogging(JnaExternalContext.toNative(context), level.toString(), msg);
	}

	public void testProgressReporting(ExternalContext context, float percentDone, String msg) {
		s_lib.CExternalEntry_testProgressReporting(JnaExternalContext.toNative(context), percentDone, msg);
	}

	public void testFailureReporting(ExternalContext context, String msg) {
		s_lib.CExternalEntry_testFailureReporting(JnaExternalContext.toNative(context), msg);
	}

	public void testSendMessage(ExternalContext context, Message msg) {
		s_lib.CExternalEntry_testSendMessage(
			JnaExternalContext.toNative(context),
			JnaExternalContext.toNative(msg)
		);
	}

	public void testAddFile(ExternalDataset dataset, ExternalDataFile extFile, boolean stream) {
		s_lib.CExternalEntry_testAddDataFile(
			JnaExternalDataset.toNative(dataset),
			JnaExternalDataFile.toNative(extFile),
			(byte) (stream ? 1 : 0)
		);
	}

	public String testGetFileName(ExternalDataset dataset, String serializedKey) {
		JnaExternalDataset jnaDataset = (JnaExternalDataset) dataset;
		return s_lib.CExternalEntry_testGetDataFileName(
			JnaExternalDataset.toNative(dataset),
			serializedKey
		);
	}

	public ExternalDataFile testFindFile(ExternalDataset dataset, String serializedKey) {
		return JnaExternalDataFile.fromNative(
			s_lib.CExternalEntry_testFindDataFile(
				JnaExternalDataset.toNative(dataset),
				serializedKey
			)
		);
	}

	public boolean testIsQueryOnly(ExternalDataset dataset) {
		JnaExternalDataset jnaDataset = (JnaExternalDataset) dataset;
		byte result = s_lib.CExternalEntry_testIsQueryOnly(
			JnaExternalDataset.toNative(dataset)
		);
		return result==(byte)1;
	}

	public ExternalDataFile testReadFileAsRandomAccess(ExternalDataset dataset, String serializedKey) {
		return JnaExternalDataFile.fromNative(
			s_lib.CExternalEntry_testReadFileAsRandomAccess(
				JnaExternalDataset.toNative(dataset),
				serializedKey
			)
		);
	}

	public void testWriteFileAsRandomAccess(ExternalDataset dataset, ExternalDataFile extFile) {
		s_lib.CExternalEntry_testWriteFileAsRandomAccess(
			JnaExternalDataset.toNative(dataset),
			JnaExternalDataFile.toNative(extFile)
		);
	}

	public void deleteLocalTempFiles() {
		s_lib.CExternalEntry_deleteLocalTempFiles();
	}

	public Collection<String> getLogFiles() {
		PointerByReference ptrRef =  s_lib.CExternalEntry_getLogFilePaths();
		String[] strs = ptrRef.getPointer().getStringArray(0);
		return Arrays.asList(strs);
	}

}
