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

import com.google.mr4c.nativec.ExternalDataset;
import com.google.mr4c.nativec.ExternalAlgorithmData;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalAlgorithmDataPtr;

public class JnaExternalAlgorithmData implements ExternalAlgorithmData {

	static Mr4cLibrary s_lib = Mr4cLibrary.INSTANCE;

	private CExternalAlgorithmDataPtr m_nativeAlgoData;


	/*package*/ JnaExternalAlgorithmData(CExternalAlgorithmDataPtr nativeAlgoData) {
		m_nativeAlgoData = nativeAlgoData;
	}

	public JnaExternalAlgorithmData() {
		m_nativeAlgoData = s_lib.CExternalAlgorithmData_newAlgorithmData();
	}

	public String getSerializedKeyspace() {
		return s_lib.CExternalAlgorithmData_getSerializedKeyspace(m_nativeAlgoData);
	}

	public void setSerializedKeyspace(String keyspace) {
		s_lib.CExternalAlgorithmData_setSerializedKeyspace(m_nativeAlgoData, keyspace);
	}

	public String getSerializedConfig() {
		return s_lib.CExternalAlgorithmData_getSerializedConfig(m_nativeAlgoData);
	}

	public void setSerializedConfig(String config) {
		s_lib.CExternalAlgorithmData_setSerializedConfig(m_nativeAlgoData, config);
	}

	public void addInputDataset(ExternalDataset dataset) {
		s_lib.CExternalAlgorithmData_addInputDataset(m_nativeAlgoData, JnaExternalDataset.toNative(dataset));
	}

	public ExternalDataset getInputDataset(int index) {
		return JnaExternalDataset.fromNative(s_lib.CExternalAlgorithmData_getInputDataset(m_nativeAlgoData, index));
	}

	public int getInputDatasetCount() {
		return s_lib.CExternalAlgorithmData_getInputDatasetCount(m_nativeAlgoData).intValue();
	}

	public void addOutputDataset(ExternalDataset dataset) {
		s_lib.CExternalAlgorithmData_addOutputDataset(m_nativeAlgoData, JnaExternalDataset.toNative(dataset));
	}

	public ExternalDataset getOutputDataset(int index) {
		return JnaExternalDataset.fromNative(s_lib.CExternalAlgorithmData_getOutputDataset(m_nativeAlgoData, index));
	}

	public int getOutputDatasetCount() {
		return s_lib.CExternalAlgorithmData_getOutputDatasetCount(m_nativeAlgoData).intValue();
	}


	/*package*/ CExternalAlgorithmDataPtr getNativeAlgorithmData() {
		return m_nativeAlgoData;
	}

	/*package*/ static CExternalAlgorithmDataPtr toNative(ExternalAlgorithmData data) {
		JnaExternalAlgorithmData jnaData = (JnaExternalAlgorithmData) data;
		return jnaData==null ? null : jnaData.getNativeAlgorithmData();
	}
	
	/*package*/ static JnaExternalAlgorithmData fromNative(CExternalAlgorithmDataPtr nativeData) {
		return nativeData==null ? null : new JnaExternalAlgorithmData(nativeData);
	}

}

