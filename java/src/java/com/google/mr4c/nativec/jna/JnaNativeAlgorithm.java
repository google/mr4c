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

import com.google.mr4c.nativec.ExternalAlgorithmDataSerializer;
import com.google.mr4c.nativec.ExternalAlgorithmSerializer;
import com.google.mr4c.nativec.ExternalFactory;
import com.google.mr4c.nativec.NativeAlgorithm;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary;
import com.google.mr4c.serialize.PropertiesSerializer;
import com.google.mr4c.util.MR4CLogging;

import com.sun.jna.NativeLibrary;


import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JnaNativeAlgorithm extends NativeAlgorithm {

	protected static final Logger s_log = MR4CLogging.getLogger(JnaNativeAlgorithm.class);

	private NativeLibrary m_lib; 
	private List<NativeLibrary> m_extras = new ArrayList<NativeLibrary>(); 

	public JnaNativeAlgorithm(
		ExternalAlgorithmSerializer algoSerializer,
		ExternalAlgorithmDataSerializer dataSerializer,
		PropertiesSerializer propsSerializer,
		ExternalFactory factory
	) {
		super( algoSerializer, dataSerializer, propsSerializer, factory);
	}

	protected void loadNativeLibraries() {
		s_log.info("Begin loading native libraries");
		s_log.info("jna.library.path={}", System.getProperty("jna.library.path"));
		s_log.info("jna.platform.library.path={}", System.getProperty("jna.platform.library.path"));
		s_log.info("LD_LIBRARY_PATH={}", System.getenv("LD_LIBRARY_PATH"));
		s_log.info("MR4C native library found at [{}]", Mr4cLibrary.JNA_NATIVE_LIB.getFile().getAbsolutePath());
		String libName = getAlgorithmConfig().getArtifact();
		s_log.info("Loading native algorithm library [{}]", libName);
		m_lib = JnaUtils.loadLibrary(libName);
		s_log.info("Native algorithm library found at [{}]", m_lib.getFile().getAbsolutePath());
		for ( String name : getAlgorithmConfig().getExtras() ) {
			s_log.info("Loading extra native library [{}]", name);
			NativeLibrary lib = JnaUtils.loadLibrary(name);
			s_log.info("Extra native library found at [{}]", lib.getFile().getAbsolutePath());
			m_extras.add(lib);
		}
		s_log.info("End loading native libraries");
	}

	public Collection<File> getRequiredFiles() {
		List<File> files = new ArrayList();
		files.add(Mr4cLibrary.JNA_NATIVE_LIB.getFile());
		files.add( m_lib.getFile());
		for ( NativeLibrary lib : m_extras ) {
			files.add(lib.getFile());
		}
		return files;
	}
	
}

