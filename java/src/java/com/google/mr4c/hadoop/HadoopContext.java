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

import com.google.mr4c.algorithm.AlgorithmContext;
import com.google.mr4c.algorithm.LogLevel;
import com.google.mr4c.message.Message;
import com.google.mr4c.message.Messages;
import com.google.mr4c.util.MR4CLogging;

import org.apache.hadoop.mapred.Reporter;

import org.slf4j.Logger;

public class HadoopContext implements AlgorithmContext {

	protected final Logger m_log = MR4CLogging.getLogger(HadoopContext.class);

	private Reporter m_reporter;
	private boolean m_failed = false;
	private String m_failureMsg;

	public HadoopContext(Reporter reporter) {
		m_reporter = reporter;
	}

	public void log(LogLevel level, String msg) {
		m_log.debug("Logging at level {} : {}", level, msg);

	}

	public void progress(float percentDone, String msg) {
		String status = String.format("%f%% done : %s", percentDone, msg);
		m_log.info("Progress reported: {}", status);
		m_reporter.progress(); // we are still alive!
		m_reporter.setStatus(status);
	}

	public String getEnvironmentDescription() {
		return "Hadoop";
	}

	public void failure(String msg) {
		m_failed = true;
		m_failureMsg = msg;
		String status = String.format("Algorithm failed: %s", msg);
		m_reporter.setStatus(status);
	}

	public boolean isFailed() {
		return m_failed;
	}

	public String getFailureMessage() {
		return m_failureMsg;
	}

	public void sendMessage(Message msg) {
		Messages.handleMessage(msg);
	}
	
}
