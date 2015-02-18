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

import com.google.mr4c.nativec.ExternalAlgorithm;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalAlgorithmPtr;

public class JnaExternalAlgorithm implements ExternalAlgorithm {

	static Mr4cLibrary s_lib = Mr4cLibrary.INSTANCE;

	private CExternalAlgorithmPtr m_nativeAlgo;


	/*package*/ JnaExternalAlgorithm(CExternalAlgorithmPtr nativeAlgo) {
		m_nativeAlgo = nativeAlgo;
	}

	public JnaExternalAlgorithm(String name) {
		m_nativeAlgo = s_lib.CExternalAlgorithm_newAlgorithm(name);
	}

	public String getName() {
		return s_lib.CExternalAlgorithm_getName(m_nativeAlgo);
	}

	public String getSerializedAlgorithm() {
		return s_lib.CExternalAlgorithm_getSerializedAlgorithm(m_nativeAlgo);
	}

	public void setSerializedAlgorithm(String serializedAlgo) {
		s_lib.CExternalAlgorithm_setSerializedAlgorithm(m_nativeAlgo, serializedAlgo);
	}

	/*package*/ CExternalAlgorithmPtr getNativeAlgorithm() {
		return m_nativeAlgo;
	}

	/*package*/ static CExternalAlgorithmPtr toNative(ExternalAlgorithm algorithm) {
		JnaExternalAlgorithm jnaAlgorithm = (JnaExternalAlgorithm) algorithm;
		return jnaAlgorithm==null ? null : jnaAlgorithm.getNativeAlgorithm();
	}
	
	/*package*/ static JnaExternalAlgorithm fromNative(CExternalAlgorithmPtr nativeAlgorithm) {
		return nativeAlgorithm==null ? null : new JnaExternalAlgorithm(nativeAlgorithm);
	}

}

