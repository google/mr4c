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

package com.google.mr4c.stats;

/**
  * Timer starts on object creation.  Call success() or failure() to stop timer and report timing
*/
public class StatsTimer {

	StatsClient m_client;
	private String m_success;
	private String m_fail;
	private long m_start;

	public StatsTimer(StatsClient client, String name) {
		this(client, name, name);
	}

	public StatsTimer(StatsClient client, String success, String fail) {
		m_client = client;
		m_success = success;
		m_fail = fail;
		m_start = System.currentTimeMillis();
	}

	public void done(boolean success) {
		if ( success ) {
			success();
		} else {
			failure();
		}
	}

	public void success() {
		report(m_success);
	}

	public void failure() {
		report(m_fail);
	}

	private void report(String name) {
		if ( m_client==null ) {
			return;
		}
		long time = System.currentTimeMillis() - m_start;
		m_client.timing(name,(int)time);
	}

}
