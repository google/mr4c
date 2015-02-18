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

package com.google.mr4c.nativec;

import com.google.mr4c.algorithm.EnvironmentSet;
import com.google.mr4c.algorithm.LogLevel;
import com.google.mr4c.message.Message;

import java.util.Collection;

public interface ExternalEntry {

	void dumpDataset(ExternalDataset dataset);

	ExternalDataset cloneDataset(ExternalDataset dataset);

	ExternalAlgorithmData cloneAlgorithmData(ExternalAlgorithmData data);

	ExternalAlgorithm getAlgorithm(String name);

	boolean executeAlgorithm(String name, ExternalAlgorithmData data, ExternalContext context);

	void pushEnvironmentProperties(EnvironmentSet envSet, String serializedProperties);

	void testLogging(ExternalContext context, LogLevel level, String msg);
	
	void testProgressReporting(ExternalContext context, float percentDone, String msg);
	
	void testFailureReporting(ExternalContext context, String msg);
	
	void testSendMessage(ExternalContext context, Message msg);
	
	void testAddFile(ExternalDataset dataset, ExternalDataFile extFile, boolean stream);

	String testGetFileName(ExternalDataset dataset, String serializedKey);

	ExternalDataFile testFindFile(ExternalDataset dataset, String serializedKey);

	boolean testIsQueryOnly(ExternalDataset dataset);

	ExternalDataFile testReadFileAsRandomAccess(ExternalDataset dataset, String serializedKey);

	void testWriteFileAsRandomAccess(ExternalDataset dataset, ExternalDataFile extFile);

	void deleteLocalTempFiles();

	Collection<String> getLogFiles();
}
