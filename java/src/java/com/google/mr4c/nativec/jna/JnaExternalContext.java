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

import com.google.mr4c.algorithm.AlgorithmContext;
import com.google.mr4c.algorithm.LogLevel;
import com.google.mr4c.message.Message;
import com.google.mr4c.nativec.ExternalContext;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalContextPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalFailureFunctionPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalLogFunctionPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalMessageFunctionPtr;
import com.google.mr4c.nativec.jna.lib.Mr4cLibrary.CExternalProgressFunctionPtr;
import com.google.mr4c.nativec.jna.lib.CExternalContextCallbacksStruct;
import com.google.mr4c.nativec.jna.lib.CExternalContextMessageStruct;
import com.google.mr4c.util.MR4CLogging;

import com.sun.jna.Pointer;

import org.slf4j.Logger;

public class JnaExternalContext implements ExternalContext {

	protected static final Logger s_log = MR4CLogging.getLogger(JnaExternalContext.class);

	static Mr4cLibrary s_lib = Mr4cLibrary.INSTANCE;

	private AlgorithmContext m_context;
	private CExternalContextCallbacksStruct m_callbacks;
	private CExternalContextPtr m_nativeContext;

	/*package*/ JnaExternalContext(
		CExternalContextPtr nativeContext
	) {
		m_nativeContext = nativeContext;
	}

	public JnaExternalContext(
		AlgorithmContext context
	) {
		m_context = context;
		m_callbacks = new CExternalContextCallbacksStruct();
		buildLogCallback();
		buildProgressCallback();
		buildFailureCallback();
		buildMessageCallback();
		m_nativeContext = s_lib.CExternalContext_newContext(m_callbacks);
	}

	public AlgorithmContext getContext() {
		return m_context;
	}

	/*package*/ CExternalContextPtr getNativeContext() {
		return m_nativeContext;
	}

	/*package*/ static CExternalContextPtr toNative(ExternalContext context) {
		JnaExternalContext jnaContext = (JnaExternalContext) context;
		return jnaContext==null ? null : jnaContext.getNativeContext();
	}
	
	/*package*/ static JnaExternalContext fromNative(CExternalContextPtr nativeContext) {
		return nativeContext==null ? null : new JnaExternalContext(nativeContext);
	}

	private void buildLogCallback() {
		m_callbacks.logCallback = new CExternalLogFunctionPtr() {
			public void apply(Pointer lvl, Pointer msg) {
				LogLevel level = LogLevel.valueOf(lvl.getString(0));
				m_context.log(level, msg.getString(0));
			}
		};
	}

	private void buildProgressCallback() {
		m_callbacks.progressCallback = new CExternalProgressFunctionPtr() {
			public void apply(float percentDone, Pointer msg) {
				m_context.progress(percentDone,msg.getString(0));
			}
		};
	}

	private void buildFailureCallback() {
		m_callbacks.failureCallback = new CExternalFailureFunctionPtr() {
			public void apply(Pointer msg) {
				m_context.failure(msg.getString(0));
			}
		};
	}

	private void buildMessageCallback() {
		m_callbacks.messageCallback = new CExternalMessageFunctionPtr() {
			public void apply(CExternalContextMessageStruct msg) {
				m_context.sendMessage(fromNative(msg));
			}
		};
	}

	/*package*/ static CExternalContextMessageStruct toNative(Message msg) {
		CExternalContextMessageStruct nativeMsg = new CExternalContextMessageStruct();
		nativeMsg.topic = JnaUtils.toNative(msg.getTopic());
		nativeMsg.content = JnaUtils.toNative(msg.getContent());
		nativeMsg.contentType = JnaUtils.toNative(msg.getContentType());
		return nativeMsg;
	}

	/*package*/ static Message fromNative(CExternalContextMessageStruct nativeMsg) {
		return new Message(
			nativeMsg.topic.getString(0),
			nativeMsg.content.getString(0),
			nativeMsg.contentType.getString(0)
		);
	}

}
