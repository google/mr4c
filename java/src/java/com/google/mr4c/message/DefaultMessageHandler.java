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

import com.google.mr4c.util.MR4CLogging;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;

public class DefaultMessageHandler implements MessageHandler {

	protected static final Logger s_log = MR4CLogging.getLogger(DefaultMessageHandler.class);

	public void setURI(URI uri) {}
		
	public void handleMessage(Message msg) throws IOException {
		s_log.info("Message sent to default handler for topic [{}] : [{}]", msg.getTopic(), msg.getContent());
	}

}

