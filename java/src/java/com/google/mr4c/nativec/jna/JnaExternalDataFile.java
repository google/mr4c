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

import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.nativec.ExternalDataFile;
import com.google.mr4c.nativec.ExternalDataFileSink;
import com.google.mr4c.nativec.ExternalDataFileSource;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalDataFilePtr;

public class JnaExternalDataFile implements ExternalDataFile {

	static Mr4cLibrary s_lib = Mr4cLibrary.INSTANCE;

	private CExternalDataFilePtr m_nativeFile;

	public JnaExternalDataFile(String serializedKey, DataFile file) {
		m_nativeFile = s_lib.CExternalDataFile_newDataFile(serializedKey, file.getFileName());
	}

	/*package*/ JnaExternalDataFile(CExternalDataFilePtr nativeFile) {
		m_nativeFile = nativeFile;
	}

	public String getSerializedKey() {
		return s_lib.CExternalDataFile_getSerializedKey(m_nativeFile);
	}

	public String getFileName() {
		return s_lib.CExternalDataFile_getFileName(m_nativeFile);
	}

	public String getSerializedFile() {
		return s_lib.CExternalDataFile_getSerializedFile(m_nativeFile);
	}

	public void setSerializedFile(String serializedFile) {
		s_lib.CExternalDataFile_setSerializedFile(m_nativeFile, serializedFile);
	}

	public ExternalDataFileSource getDataFileSource() {
		return JnaDataFileSource.fromNative(s_lib.CExternalDataFile_getFileSource(m_nativeFile));
	}

	public void setDataFileSource(ExternalDataFileSource src) {
		s_lib.CExternalDataFile_setFileSource(m_nativeFile, JnaDataFileSource.toNative(src));
	}

	public ExternalDataFileSink getDataFileSink() {
		return JnaDataFileSink.fromNative(s_lib.CExternalDataFile_getFileSink(m_nativeFile));
	}

	public void setDataFileSink(ExternalDataFileSink sink) {
		s_lib.CExternalDataFile_setFileSink(m_nativeFile, JnaDataFileSink.toNative(sink));
	}


	/*package*/ CExternalDataFilePtr getNativeFile() {
		return m_nativeFile;
	}

	/*package*/ static CExternalDataFilePtr toNative(ExternalDataFile file) {
		JnaExternalDataFile jnaFile = (JnaExternalDataFile) file;
		return jnaFile==null ? null : jnaFile.getNativeFile();
	}
	
	/*package*/ static JnaExternalDataFile fromNative(CExternalDataFilePtr nativeFile) {
		return nativeFile==null ? null : new JnaExternalDataFile(nativeFile);
	}

}

