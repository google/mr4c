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
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.nativec.ExternalDataFile;
import com.google.mr4c.nativec.ExternalDataset;
import com.google.mr4c.nativec.ExternalDatasetSerializer;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalDatasetPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalDataFilePtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalAddFunctionPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalFileNameFunctionPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalFindFunctionPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalQueryOnlyFunctionPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalRandomAccessFileSinkPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalRandomAccessFileSourcePtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalRandomAccessSinkFunctionPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalRandomAccessSourceFunctionPtr;
import com.google.mr4c.nativec.jna.lib.CExternalDatasetCallbacksStruct;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.util.MR4CLogging;

import com.sun.jna.Pointer;

import java.io.FileNotFoundException;

import org.slf4j.Logger;

public class JnaExternalDataset implements ExternalDataset {

	protected static final Logger s_log = MR4CLogging.getLogger(JnaExternalDataset.class);

	static Mr4cLibrary s_lib = Mr4cLibrary.INSTANCE;

	private Dataset m_dataset;
	private CExternalDatasetPtr m_nativeDataset;
	private CExternalDatasetCallbacksStruct m_callbacks;
	private DatasetSerializer m_serializer;
	private ExternalDatasetSerializer m_extSerializer;


	/*package*/ JnaExternalDataset(CExternalDatasetPtr nativeDataset) {
		m_nativeDataset = nativeDataset;
	}

	public JnaExternalDataset(String name, Dataset dataset, DatasetSerializer serializer, ExternalDatasetSerializer extSerializer) {
		m_dataset = dataset;
		m_serializer = serializer;
		m_extSerializer = extSerializer;
		m_callbacks = new CExternalDatasetCallbacksStruct();
		buildAddFileCallback();
		buildFileNameCallback();
		buildFindFileCallback();
		buildQueryOnlyCallback();
		buildRandomAccessSourceCallback();
		buildRandomAccessSinkCallback();
		m_nativeDataset = s_lib.CExternalDataset_newDataset(name, new CExternalDatasetCallbacksStruct[] {m_callbacks});
		JnaUtils.protectDataset(m_dataset.getToken(), this); 
	}


	public String getName() {
		return s_lib.CExternalDataset_getName(m_nativeDataset);
	}

	public String getSerializedDataset() {
		return s_lib.CExternalDataset_getSerializedDataset(m_nativeDataset);
	}

	public void setSerializedDataset(String serializedDataset) {
		s_lib.CExternalDataset_setSerializedDataset(m_nativeDataset, serializedDataset);
	}

	public void addDataFile(ExternalDataFile file) {
		s_lib.CExternalDataset_addDataFile(m_nativeDataset, JnaExternalDataFile.toNative(file));
	}

	public ExternalDataFile getDataFile(int index) {
		return JnaExternalDataFile.fromNative(s_lib.CExternalDataset_getDataFile(m_nativeDataset, index));
	}

	public int getFileCount() {
		return s_lib.CExternalDataset_getFileCount(m_nativeDataset).intValue();
	}

	/*package*/ CExternalDatasetPtr getNativeDataset() {
		return m_nativeDataset;
	}

	/*package*/ static CExternalDatasetPtr toNative(ExternalDataset dataset) {
		JnaExternalDataset jnaDataset = (JnaExternalDataset) dataset;
		return jnaDataset==null ? null : jnaDataset.getNativeDataset();
	}
	
	/*package*/ static JnaExternalDataset fromNative(CExternalDatasetPtr nativeDataset) {
		return nativeDataset==null ? null : new JnaExternalDataset(nativeDataset);
	}


	private void buildAddFileCallback() {
		m_callbacks.addFileCallback = new CExternalAddFunctionPtr() {
			public byte apply(CExternalDataFilePtr filePtr) {
				return doAddFile(filePtr);
			}
		};
	}

	private synchronized byte doAddFile(CExternalDataFilePtr filePtr) {
		try {
			JnaExternalDataFile extFile = new JnaExternalDataFile(filePtr);
			String serKey = extFile.getSerializedKey();
			DataKey key = m_serializer.deserializeDataKey(serKey);
			DataFile file = m_extSerializer.deserializeDataFile(extFile);
			m_dataset.addFile(key, file);
			if ( file.getFileSink()!=null ) {
				JnaDataFileSink jnaSink = new JnaDataFileSink(file.getFileSink());
				s_lib.CExternalDataFile_setFileSink(filePtr, jnaSink.getNativeSink());
			}
			return (byte)1;
		} catch ( Exception e ) {
			s_log.error("Error adding file", e);
			return (byte)0;
		}
	}


	private void buildFileNameCallback() {
		m_callbacks.getFileNameCallback = new CExternalFileNameFunctionPtr() {
			public String apply(Pointer serKey) {
				return doGetFileName(serKey);
			}
		};
	}

	private String doGetFileName(Pointer serKey) {
		try {
			DataKey key = m_serializer.deserializeDataKey(serKey.getString(0));
			return m_dataset.getFileName(key);
		} catch ( Exception e ) {
			s_log.error("Error getting file name", e);
			return null;
		}
	}

	private void buildFindFileCallback() {
		m_callbacks.findFileCallback = new CExternalFindFunctionPtr() {
			public CExternalDataFilePtr apply(Pointer serializedKey) {
				return doFind(serializedKey);
			}
		};
	}

	private synchronized CExternalDataFilePtr doFind(Pointer serializedKey) {
		try {
			DataKey key = m_serializer.deserializeDataKey(serializedKey.getString(0));
			DataFile file = m_dataset.getFile(key);
			if ( file==null ) {
				return null;
			}
			ExternalDataFile extFile = m_extSerializer.serializeDataFile(key, file);
			addDataFile(extFile);
			return JnaExternalDataFile.toNative(extFile);
		} catch ( Exception e ) {
			s_log.error("Error finding file", e);
			return null;
		}
	}
		
	private void buildQueryOnlyCallback() {
		m_callbacks.isQueryOnlyCallback = new CExternalQueryOnlyFunctionPtr() {
			public byte apply() {
				return doIsQueryOnly();
			}
		};
	}

	private byte doIsQueryOnly() {
		return (byte) (m_dataset.isQueryOnly() ? 1 : 0);
	}

	private void buildRandomAccessSourceCallback() {
		m_callbacks.randomAccessSourceCallback = new CExternalRandomAccessSourceFunctionPtr() {
			public CExternalRandomAccessFileSourcePtr apply(Pointer serializedKey) {
				return doRandomAccessSource(serializedKey);
			}
		};
	}

	private CExternalRandomAccessFileSourcePtr doRandomAccessSource(Pointer serializedKey) {
		try {
			DataKey key = m_serializer.deserializeDataKey(serializedKey.getString(0));
			DataFile file = m_dataset.getFile(key);
			if ( file==null ) {
				throw new FileNotFoundException(String.format("No file with key = [%s]", key));
			}
			JnaRandomAccessFileSource rafSrc = new JnaRandomAccessFileSource(file.getFileSource());
			return rafSrc.getNativeSource();
		} catch ( Exception e ) {
			s_log.error("Error preparing random access source", e);
			return null;
		}
	}
		
	private void buildRandomAccessSinkCallback() {
		m_callbacks.randomAccessSinkCallback = new CExternalRandomAccessSinkFunctionPtr() {
			public CExternalRandomAccessFileSinkPtr apply(Pointer serializedKey) {
				return doRandomAccessSink(serializedKey);
			}
		};
	}

	private CExternalRandomAccessFileSinkPtr doRandomAccessSink(Pointer serializedKey) {
		try {
			DataKey key = m_serializer.deserializeDataKey(serializedKey.getString(0));
			DataFile file = m_dataset.getFile(key);
			if ( file==null ) {
				throw new FileNotFoundException(String.format("No file with key = [%s]", key));
			}
			JnaRandomAccessFileSink rafSink = new JnaRandomAccessFileSink(file.getFileSink());
			return rafSink.getNativeSink();
		} catch ( Exception e ) {
			s_log.error("Error preparing random access sink", e);
			return null;
		}
	}
		
}

