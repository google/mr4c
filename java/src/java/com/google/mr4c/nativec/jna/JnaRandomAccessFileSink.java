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

import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.ochafik.lang.jnaerator.runtime.NativeSizeByReference;

import com.google.mr4c.nativec.ExternalRandomAccessFileSink;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalRandomAccessFileSinkPtr;
import com.google.mr4c.sources.DataFileSink;
import com.google.mr4c.sources.RandomAccessFileSink;
import com.google.mr4c.util.MR4CLogging;

import org.slf4j.Logger;

public class JnaRandomAccessFileSink implements ExternalRandomAccessFileSink {

	protected static final Logger s_log = MR4CLogging.getLogger(JnaRandomAccessFileSink.class);

	static Mr4cLibrary s_lib = Mr4cLibrary.INSTANCE;

	private RandomAccessFileSink m_randSink;
	private JnaRandomAccessFile m_jnaRand;
	private CExternalRandomAccessFileSinkPtr m_nativeSink;

	/*package*/ JnaRandomAccessFileSink(DataFileSink sink) {
		m_randSink = new RandomAccessFileSink(sink);	
		m_jnaRand = new JnaRandomAccessFile(m_randSink);
		m_nativeSink = s_lib.CExternalRandomAccessFile_newRandomAccessFileSink(m_jnaRand.getCallbacks());

		JnaUtils.protectSink(sink, this);
	}

	public RandomAccessFileSink getSink() {
		return m_randSink;
	}

	/*package*/ CExternalRandomAccessFileSinkPtr getNativeSink() {
		return m_nativeSink;
	}

	/*package*/ static CExternalRandomAccessFileSinkPtr toNative(ExternalRandomAccessFileSink sink) {
		JnaRandomAccessFileSink jnaSink = (JnaRandomAccessFileSink) sink;
		return jnaSink==null ? null : jnaSink.getNativeSink();
	}
	
}

