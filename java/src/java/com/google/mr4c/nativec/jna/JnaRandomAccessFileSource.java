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

import com.google.mr4c.nativec.ExternalRandomAccessFileSource;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalRandomAccessFileSourcePtr;
import com.google.mr4c.sources.DataFileSource;
import com.google.mr4c.sources.RandomAccessFileSource;
import com.google.mr4c.util.MR4CLogging;

import org.slf4j.Logger;

public class JnaRandomAccessFileSource implements ExternalRandomAccessFileSource {

	protected static final Logger s_log = MR4CLogging.getLogger(JnaRandomAccessFileSource.class);

	static Mr4cLibrary s_lib = Mr4cLibrary.INSTANCE;

	private RandomAccessFileSource m_randSrc;
	private JnaRandomAccessFile m_jnaRand;
	private CExternalRandomAccessFileSourcePtr m_nativeSrc;

	/*package*/ JnaRandomAccessFileSource(DataFileSource src) {
		m_randSrc = new RandomAccessFileSource(src);	
		m_jnaRand = new JnaRandomAccessFile(m_randSrc);
		m_nativeSrc = s_lib.CExternalRandomAccessFile_newRandomAccessFileSource(m_jnaRand.getCallbacks());

		JnaUtils.protectSource(src, this);
	}

	public RandomAccessFileSource getSource() {
		return m_randSrc;
	}

	/*package*/ CExternalRandomAccessFileSourcePtr getNativeSource() {
		return m_nativeSrc;
	}

	/*package*/ static CExternalRandomAccessFileSourcePtr toNative(ExternalRandomAccessFileSource src) {
		JnaRandomAccessFileSource jnaSrc = (JnaRandomAccessFileSource) src;
		return jnaSrc==null ? null : jnaSrc.getNativeSource();
	}
	
}

