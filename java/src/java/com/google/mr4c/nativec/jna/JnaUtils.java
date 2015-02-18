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

import com.google.mr4c.stats.MR4CStats;
import com.google.mr4c.stats.StatsClient;
import com.google.mr4c.stats.StatsTimer;

import com.sun.jna.Callback;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

abstract class JnaUtils {

	private static Protector<Callback> s_cbProt = new Protector<Callback>();
	private static Protector<Pointer> s_ptrProt = new Protector<Pointer>();
	private static Protector<Buffer> s_bufProt = new Protector<Buffer>();
	private static Protector<JnaDataFileSource> s_srcProt = new Protector<JnaDataFileSource>();
	private static Protector<JnaDataFileSink> s_sinkProt = new Protector<JnaDataFileSink>();
	private static Protector<JnaExternalDataset> s_datasetProt = new Protector<JnaExternalDataset>();
	private static Protector<JnaRandomAccessFileSource> s_randSrcProt = new Protector<JnaRandomAccessFileSource>();
	private static Protector<JnaRandomAccessFileSink> s_randSinkProt = new Protector<JnaRandomAccessFileSink>();

	// Algorithm can be loaded and GC'ed multiple times in the same JVM
	// Keep static lib reference here so the lib doesn't reload and try to register twice
	private static Map<String,NativeLibrary> s_loadedLibs = new HashMap<String,NativeLibrary>();

	public static void protectPointer(Object scope, Pointer ptr) {
		s_ptrProt.protect(scope,ptr);
	}

	public static void protectBuffer(Object scope, Buffer buf) {
		s_bufProt.protect(scope,buf);
	}

	public static void protectCallback(Object scope, Callback cb) {
		s_cbProt.protect(scope,cb);
	}

	public static void protectDataset(Object scope, JnaExternalDataset dataset) {
		s_datasetProt.protect(scope,dataset);
	}

	public static void protectSource(Object scope, JnaDataFileSource src) {
		s_srcProt.protect(scope,src);
	}

	public static void releaseSource(Object scope, JnaDataFileSource src) {
		s_srcProt.release(scope,src);
	}

	public static void protectSink(Object scope, JnaDataFileSink sink) {
		s_sinkProt.protect(scope,sink);
	}

	public static void releaseSink(Object scope, JnaDataFileSink sink) {
		s_sinkProt.release(scope,sink);
	}

	public static void protectSource(Object scope, JnaRandomAccessFileSource src) {
		s_randSrcProt.protect(scope,src);
	}

	public static void releaseSource(Object scope, JnaRandomAccessFileSource src) {
		s_randSrcProt.release(scope,src);
	}

	public static void protectSink(Object scope, JnaRandomAccessFileSink sink) {
		s_randSinkProt.protect(scope,sink);
	}

	public static void releaseSink(Object scope, JnaRandomAccessFileSink sink) {
		s_randSinkProt.release(scope,sink);
	}

	public synchronized static NativeLibrary loadLibrary(String name) {
		NativeLibrary lib = s_loadedLibs.get(name);
		if ( lib==null ) {
			lib = loadLibraryWithTiming(name);
			s_loadedLibs.put(name,lib);
		}
		return lib;
	}

	private static NativeLibrary loadLibraryWithTiming(String name) {
		String successName = String.format("mr4c.loadlib.%s.success", name);
		String failureName = String.format("mr4c.loadlib.%s.failure", name);
		StatsTimer timer = new StatsTimer(
			MR4CStats.getClient(),
			successName,
			failureName
		);
		boolean success = false;
		try {
			NativeLibrary result = doLoadLibrary(name);
			success = true;
			return result;
		} finally {
			timer.done(success);
		}
	}

	private static NativeLibrary doLoadLibrary(String name) {
		return NativeLibrary.getInstance(name);
	}

	public static Pointer toNative(String str) {
		Memory mem = new Memory(str.length()+1);
		mem.setString(0, str);
		return mem;
	}

	private static class Protector<T> {

		WeakHashMap<Object,List<T>> s_targetMap = new WeakHashMap<Object,List<T>>();

		void protect(Object scope, T target) {
			List<T> targets = s_targetMap.get(scope);
			if ( targets==null ) {
				targets = new ArrayList<T>();
				s_targetMap.put(scope,targets);
			}
			targets.add(target);
		}

		void release(Object scope, T target) {
			List<T> targets = s_targetMap.get(scope);
			if ( targets==null ) {
				return;
			}
			targets.remove(target);
		}

	}		
		
}

