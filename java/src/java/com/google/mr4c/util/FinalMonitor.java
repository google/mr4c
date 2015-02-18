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

import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;

/**
	Diagnostic tool to track when objects are being garbage collected
*/
public class FinalMonitor<T> {

	private String m_desc;
	private ReferenceQueue<T> m_refQ = new ReferenceQueue<T>();
	private Thread m_thread;

	public FinalMonitor(String desc) {
		m_desc = desc;
		m_thread = new Thread(
			new Runnable() {
				public void run() {
					try {
						monitor();
					} catch (InterruptedException e ) {
						e.printStackTrace();
					}
				}
			}
		);
		m_thread.setDaemon(true);
		m_thread.start();
	}

	public WeakReference<T> monitorObject(T obj, String desc) {
		return new MonitoredReference(obj, desc);
	}


	private class MonitoredReference extends WeakReference<T> {

		String m_desc;

		MonitoredReference(T obj, String desc) {
			super(obj, m_refQ);
			m_desc = desc;
		}

	}

	private void monitor() throws InterruptedException {
		for (;;) {
			System.out.println(String.format("Waiting for %s ...", m_desc));
			MonitoredReference ref = (MonitoredReference) m_refQ.remove();
			System.out.println(String.format("%s cleanup for %s" , m_desc, ref.m_desc));
		}
	}

}

