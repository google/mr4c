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

package com.google.mr4c.message;

import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.Category;
import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;

public class Messages {

	protected static final Logger s_log = MR4CLogging.getLogger(Messages.class);

	private static Map<String,MessageHandler> s_handlerMap =
		new HashMap<String,MessageHandler>();

	private static MessageHandler s_defaultHandler = new DefaultMessageHandler();

	private static Map<String,Class<? extends MessageHandler>> s_schemeMap =
		Collections.synchronizedMap( new HashMap<String,Class<? extends MessageHandler>>() );

	private static boolean s_configured=false;

	static {
		s_schemeMap.put("http", HttpMessageHandler.class);
	}

	public static void handleMessage(Message msg) {
		configureTopics();
		MessageHandler handler = getHandler(msg.getTopic());
		try {
			handler.handleMessage(msg);
		} catch ( Exception e ) {
			s_log.error("Couldn't handle message", e);
		}
	}

	private static MessageHandler getHandler(String topic) {
		MessageHandler handler = s_handlerMap.get(topic);
		if ( handler==null ) {
			s_log.warn("No message handler for topic [{}]", topic);
			handler = s_defaultHandler;
		}
		return handler;
	}

	private static void configureTopics() {
		if ( s_configured ) {
			return;
		}
		Properties props = MR4CConfig.getDefaultInstance().getCategory(Category.TOPICS).getProperties(false);
		for ( String topic : props.stringPropertyNames() ) {
			URI uri = URI.create(props.getProperty(topic));
			registerTopic(topic, uri);
		}
		s_configured=true;
	}

	public static void registerTopic(String topic, URI uri) {
		Class<? extends MessageHandler> clazz = s_schemeMap.get(uri.getScheme());
		if ( clazz==null ) {
			throw new IllegalArgumentException(String.format("Don't know how to handle scheme [%s] for topic URI [%s]", uri.getScheme(), uri));
		}
		
		MessageHandler handler = null;
		try {
			handler = clazz.newInstance();
		} catch ( Exception e ) {
			throw new IllegalStateException(e);
		}
		handler.setURI(uri);
		s_log.info("Adding message handler for topic = [{}] and URI = [{}]", topic, uri);
		s_handlerMap.put(topic,handler);
	}

}
